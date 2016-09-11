/* Copyright (C) 2006-2016 Patrick G. Durand
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/agpl-3.0.txt
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 */
package bzh.plealog.bioinfo.ui.sequence.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import bzh.plealog.bioinfo.api.data.sequence.DLocation;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSymbol;
import bzh.plealog.bioinfo.api.data.sequence.DSymbolFamily;
import bzh.plealog.bioinfo.api.data.sequence.DSymbolFamilySystem;
import bzh.plealog.bioinfo.api.data.sequence.DSymbolGraphics;
import bzh.plealog.bioinfo.api.data.sequence.stat.AlphabetCounter;
import bzh.plealog.bioinfo.api.data.sequence.stat.StatUtils;
import bzh.plealog.bioinfo.api.data.sequence.stat.SymbolCounter;
import bzh.plealog.bioinfo.ui.sequence.event.DSequenceSelectionEvent;
import bzh.plealog.bioinfo.ui.sequence.event.DSequenceSelectionListener;
import bzh.plealog.bioinfo.ui.util.JPercentLabel;
import bzh.plealog.bioinfo.ui.util.ResultTableHeaderPanel;

/**
 * This class can be used to display the sequence composition. It uses a dual table to 
 * display both the composition of the full sequence and from a selected region.
 * 
 * @author Patrick G. Durand
 */
public class AlphabetCounterDualViewer extends JPanel implements DSequenceSelectionListener {
  private static final long serialVersionUID = 1L;
  private AlphabetCounterTable          _alphViewer;
  private AlphabetCounterRowHeaderTable _rowHeaderTable;
  private JScrollPane                   _mainScroller;
  private PercentRenderer               _pctRenderer;
  private boolean                       _displayEmptyRow;

  private static final String COUNTER_COLUMN_HDR[] = {
    "Count", 
    "%", 
    " Count ", //space added to avoid problem with ResultTableHeaderPanel system
  " % "}; //space added to avoid problem with ResultTableHeaderPanel system

  private static final String COUNTERH_COLUMN_HDR = "Symbol";
  private static final DecimalFormat PCT_FORMATTER = new DecimalFormat("##0.00");

  /**
   * Default constructor.
   */
  public AlphabetCounterDualViewer(){
    JTableHeader corner;
    HashSet<String> leftHeaders, rightHeaders;

    _pctRenderer = new PercentRenderer();
    _alphViewer = new AlphabetCounterTable(new AlphabetCounterTableModel());
    _alphViewer.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    _alphViewer.getTableHeader().setReorderingAllowed(false);

    _alphViewer.setGridColor(Color.LIGHT_GRAY);
    _rowHeaderTable = new AlphabetCounterRowHeaderTable(new AlphabetCounterRowHeaderTableModel());
    _rowHeaderTable.setColumnSelectionAllowed(false);
    _rowHeaderTable.setRowSelectionAllowed(false);
    initColumnSizeRowHeaderTable();
    Dimension d = _rowHeaderTable.getPreferredScrollableViewportSize();
    d.width = _rowHeaderTable.getPreferredSize().width;
    _rowHeaderTable.setPreferredScrollableViewportSize(d);
    _rowHeaderTable.setRowHeight(_alphViewer.getRowHeight());

    corner = _rowHeaderTable.getTableHeader();
    corner.setReorderingAllowed(false);
    corner.setResizingAllowed(false);

    _mainScroller = new JScrollPane(_alphViewer);
    _mainScroller.setRowHeaderView(_rowHeaderTable);
    _mainScroller.setColumnHeaderView(_alphViewer.getTableHeader());
    _mainScroller.setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);

    leftHeaders = new HashSet<String>();
    initSet(leftHeaders, 0, 1);
    rightHeaders = new HashSet<String>();
    initSet(rightHeaders, 2, COUNTER_COLUMN_HDR.length-1);
    ResultTableHeaderPanel rtHead = new ResultTableHeaderPanel("Full Sequence","Selection Only",leftHeaders,rightHeaders);
    rtHead.registerResultTable(_alphViewer);
    rtHead.setLeftMargin(d.width);

    this.setLayout(new BorderLayout());
    this.add(rtHead, BorderLayout.NORTH);
    this.add(_mainScroller, BorderLayout.CENTER);
  }
  /**
   * Utility method used to set up the table header.
   */
  private void initSet(Set<String> s, int from, int to){
    while(from<=to){
      s.add(COUNTER_COLUMN_HDR[from]);
      from++;
    }
  }
  /**
   * Sets the AlphabetCounter of the full sequence. Such an AlphabetCounter object
   * contains the sequence composition.
   */
  public void setFullSeqAlphabetCounter(AlphabetCounter alphC){
    RowHeaderEntry[] rowHeaderVals; 
    rowHeaderVals = getDataModel(alphC);
    _alphViewer.setModel(new AlphabetCounterTableModel(rowHeaderVals));
    _rowHeaderTable.setModel(new AlphabetCounterRowHeaderTableModel(rowHeaderVals));
  }
  /**
   * Figures out if the viewer has to display alphabet symbols not present in the
   * sequence.
   */
  public void setDisplayEmptyRow(boolean b){
    _displayEmptyRow = b;
  }
  public boolean isDisplayEmptyRow(){
    return _displayEmptyRow;
  }
  /**
   * Utility method used to add the family composition to the symbol composition.
   */
  private void addCountsForFamilies(AlphabetCounter alphC, ArrayList<RowHeaderEntry> data){
    List<String>      protFamilies;
    Iterator<String>  famNames;
    Iterator<DSymbol> symbols;
    DSymbolFamily     fm;
    int               famCount, max;

    protFamilies = DSymbolFamilySystem.getFamilyNames(alphC.getAlphabet().getType());
    if (protFamilies.isEmpty())
      return;
    famNames = protFamilies.iterator();
    max = alphC.getMaxCounter();
    while(famNames.hasNext()){
      fm = DSymbolFamilySystem.getFamily(famNames.next());
      symbols = fm.getSymbols();
      famCount = 0;
      while(symbols.hasNext()){
        famCount += alphC.getCounter(symbols.next());
      }
      data.add(new RowHeaderEntry(
          fm.getSymbolsRepr(), 
          fm.getSymbols().next().getGraphics(), 
          famCount, 
          (float)famCount*100f/(float)max));
    }
  }
  /**
   * Utility method used to prepare a table data model from a AlphabetCounter object.
   */
  private RowHeaderEntry[] getDataModel(AlphabetCounter alphC){
    ArrayList<RowHeaderEntry> data;
    RowHeaderEntry[]          rowHeaderVals; 
    SymbolCounter[]           counters;
    int                       i, count, max;
    float                     pct;

    if (alphC == null)
      return null;

    counters = alphC.getAllCounters(AlphabetCounter.SORT_TYPE.LETTER_SORT);
    rowHeaderVals = new RowHeaderEntry[counters.length];
    max = alphC.getMaxCounter();
    data = new ArrayList<RowHeaderEntry>();
    for(i=0;i<counters.length;i++){
      count = counters[i].getCounter();
      pct = (float)count*100f/(float)max;
      data.add(new RowHeaderEntry(counters[i].getSymbol().toString(), 
          counters[i].getSymbol().getGraphics(), count, pct));
    }
    addCountsForFamilies(alphC, data);
    rowHeaderVals = data.toArray(new RowHeaderEntry[0]);
    return rowHeaderVals; 
  }
  /**
   * Initializes columns size for RowHeader table to default values.
   */
  private void initColumnSizeRowHeaderTable(){
    FontMetrics      fm;
    TableColumnModel tcm;
    TableColumn      tc;

    fm = _rowHeaderTable.getFontMetrics(_rowHeaderTable.getFont());
    tcm = _rowHeaderTable.getColumnModel();
    tc = tcm.getColumn(0);
    tc.setPreferredWidth(2*fm.stringWidth(tc.getHeaderValue().toString()));
  }

  /**
   * The table used to display the composition.
   */
  private class AlphabetCounterTable extends JTable {
    private static final long serialVersionUID = 1L;
    public AlphabetCounterTable(AlphabetCounterTableModel dm) {
      super(dm);
    }
    public TableCellRenderer getCellRenderer(int row, int column) {
      TableCellRenderer tcr;
      JLabel            lbl;

      if (column==1 || column==3){
        tcr = _pctRenderer;
      }
      else{
        tcr = super.getCellRenderer(row, column);
      }
      if (tcr instanceof JLabel){
        lbl = (JLabel) tcr;
        lbl.setOpaque(true);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
      }
      return tcr;
    }
  }
  private class PercentRenderer extends JPercentLabel implements TableCellRenderer{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public PercentRenderer(){
      super(false);
      setDrawZeroValue(false);
    }
    public Component getTableCellRendererComponent(
        JTable table, Object value,
        boolean isSelected, boolean hasFocus,
        int row, int column) {
      if (isSelected) {
        setBackground(table.getSelectionBackground());
      }
      else {
        setBackground(table.getBackground());
      }
      this.setValue(value.toString());
      this.setColor(RowHeaderTable.QUERY_CELL_BK_COLOR);
      return this;
    }
  }

  /**
   * The table model that must be used with AlphabetCounterTable.
   */
  private class AlphabetCounterTableModel extends AbstractTableModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private RowHeaderEntry[]      _dataFullSeq;
    private RowHeaderEntry[]      _dataSelectedSeq;

    public AlphabetCounterTableModel(){
    }

    public AlphabetCounterTableModel(RowHeaderEntry[] d){
      _dataFullSeq = d;
    }
    protected void setSelectedSeqModel(RowHeaderEntry[] d){
      _dataSelectedSeq = d;
    }
    public String getColumnName(int column){
      return COUNTER_COLUMN_HDR[column];
    }

    public int getColumnCount() { 
      return COUNTER_COLUMN_HDR.length;
    }

    public int getRowCount() {
      if (_dataFullSeq==null)
        return 0;
      return _dataFullSeq.length;
    }

    public Object getValueAt(int row, int col) {

      if (_dataFullSeq == null)
        return "";
      int val;
      switch(col){
        case 0:
          val = _dataFullSeq[row].getCount();
          if (val!=0 || (val==0 && _displayEmptyRow))
            return val;
          break;
        case 1:
          val = _dataFullSeq[row].getCount();
          if (val!=0 || (val==0 && _displayEmptyRow))
            return PCT_FORMATTER.format((double) _dataFullSeq[row].getPercentage());
          break;
        case 2:
          if (_dataSelectedSeq!=null){
            val = _dataSelectedSeq[row].getCount();
            if (val!=0 || (val==0 && _displayEmptyRow))
              return val;
          }
          break;
        case 3:
          if (_dataSelectedSeq!=null){
            val = _dataSelectedSeq[row].getCount();
            if (val!=0 || (val==0 && _displayEmptyRow))
              return PCT_FORMATTER.format((double)_dataSelectedSeq[row].getPercentage());
          }
          break;
      }
      return "";
    }
  }

  /**
   * The table used to display AlphabetCounterTable row header.
   */
  private class AlphabetCounterRowHeaderTable extends JTable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public AlphabetCounterRowHeaderTable(AlphabetCounterRowHeaderTableModel dm) {
      super(dm);
    }
    public TableCellRenderer getCellRenderer(int row, int column) {
      TableCellRenderer tcr;
      JLabel            lbl;

      tcr = super.getCellRenderer(row, column);
      if (!(tcr instanceof JLabel))
        return tcr;
      lbl = (JLabel) tcr;
      lbl.setOpaque(true);
      lbl.setHorizontalAlignment(SwingConstants.CENTER);
      RowHeaderEntry entry = (RowHeaderEntry) getModel().getValueAt(row, column);
      if (entry.getGraphics()!=null){
        lbl.setForeground(entry.getGraphics().getTextColor());
      }
      else{
        lbl.setForeground(Color.BLACK);
      }
      lbl.setBackground(RowHeaderTable.QUERY_CELL_BK_COLOR);
      return tcr;
    }
  }
  /**
   * The table model that must be used with a AlphabetCounterRowHeaderTable.
   */
  private class AlphabetCounterRowHeaderTableModel extends AbstractTableModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private RowHeaderEntry[]   _data;

    public AlphabetCounterRowHeaderTableModel(){
    }

    public AlphabetCounterRowHeaderTableModel(RowHeaderEntry[] d){
      _data = d;
    }

    public String getColumnName(int column){
      return COUNTERH_COLUMN_HDR;
    }

    public int getColumnCount() { 
      return 1;
    }

    public int getRowCount() {
      if (_data==null)
        return 0;
      return _data.length;
    }

    public Object getValueAt(int row, int col) {
      if (_data == null)
        return "";

      return _data[row];
    }
  }

  /**
   * Private class used to maintain data displayed in the tables.
   */
  private class RowHeaderEntry{
    private String          name;
    private DSymbolGraphics graphics;
    private int             count;
    private float           percentage;

    public RowHeaderEntry(String name, DSymbolGraphics graphics, int count, float percentage) {
      super();
      this.name = name;
      this.graphics = graphics;
      this.count = count;
      this.percentage = percentage;
    }
    @SuppressWarnings("unused")
    public String getName() {
      return name;
    }
    @SuppressWarnings("unused")
    public void setName(String name) {
      this.name = name;
    }
    public DSymbolGraphics getGraphics() {
      return graphics;
    }
    @SuppressWarnings("unused")
    public void setGraphics(DSymbolGraphics graphics) {
      this.graphics = graphics;
    }
    public int getCount() {
      return count;
    }

    public float getPercentage() {
      return percentage;
    }
    public String toString(){
      return name;
    }

  }

  /**
   * Implementation of interface DSequenceSelectionListener.
   */
  public void selectionChanged(DSequenceSelectionEvent event){
    AlphabetCounter alphC = null;
    DSequence       fullSeq;
    List<DLocation> locs;
    int             from, to;

    fullSeq = event.getEntireSequence();
    if (event.getSelectionType()==DSequenceSelectionEvent.SIMPLE_RANGE){
      from = event.getSelFrom();
      to = event.getSelTo();
      if (from!=-1 && to!=-1 && fullSeq!=null){
        alphC = StatUtils.computeComposition(fullSeq, from, to);
      }
    }
    else{
      locs = event.getLocs();
      if (locs!=null && fullSeq!=null){
        alphC = StatUtils.computeComposition(fullSeq, locs);
      }
    }
    ((AlphabetCounterTableModel)_alphViewer.getModel()).setSelectedSeqModel(getDataModel(alphC));
    _alphViewer.repaint();
  }
}
