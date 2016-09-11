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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import javax.swing.table.TableModel;

import bzh.plealog.bioinfo.api.data.sequence.DSymbol;
import bzh.plealog.bioinfo.api.data.sequence.DSymbolFamily;
import bzh.plealog.bioinfo.api.data.sequence.DSymbolFamilySystem;
import bzh.plealog.bioinfo.api.data.sequence.DSymbolGraphics;
import bzh.plealog.bioinfo.api.data.sequence.stat.AlphabetCounter;
import bzh.plealog.bioinfo.api.data.sequence.stat.SymbolCounter;

public class AlphabetCounterSingleViewer extends JPanel {
  private static final long serialVersionUID = 1L;
  private AlphabetCounterTable          _alphViewer;
  private AlphabetCounterRowHeaderTable _rowHeaderTable;
  private JScrollPane                   _mainScroller;
  private RowHeaderEntry[]              _rowHeaderVals;

  private static final String COUNTER_COLUMN_HDR[] = {
    "Count", 
  "%"}; //space added to avoid problem with ResultTableHeaderPanel system

  private static final String COUNTERH_COLUMN_HDR = "Symbol";
  private static final DecimalFormat PCT_FORMATTER = new DecimalFormat("###.#");

  public AlphabetCounterSingleViewer(){
    JTableHeader corner;

    _alphViewer = new AlphabetCounterTable(new AlphabetCounterTableModel());
    _alphViewer.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    _alphViewer.getTableHeader().setReorderingAllowed(false);

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

    this.setLayout(new BorderLayout());
    this.add(_mainScroller, BorderLayout.CENTER);
  }

  public void setFullSeqAlphabetCounter(AlphabetCounter alphC){
    updateModel(alphC);
    _alphViewer.setModel(new AlphabetCounterTableModel(_rowHeaderVals));
    _rowHeaderTable.setModel(new AlphabetCounterRowHeaderTableModel(_rowHeaderVals));
  }
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
  private void updateModel(AlphabetCounter alphC){
    ArrayList<RowHeaderEntry> data;
    SymbolCounter[] counters;
    int             i, count, max, tot;
    float           pct, totf;

    _rowHeaderVals = null;

    if (alphC == null)
      return;

    counters = alphC.getAllCounters(AlphabetCounter.SORT_TYPE.LETTER_SORT);
    _rowHeaderVals = new RowHeaderEntry[counters.length];
    max = alphC.getMaxCounter();
    tot = 0;
    totf = 0f;
    data = new ArrayList<RowHeaderEntry>();
    for(i=0;i<counters.length;i++){
      count = counters[i].getCounter();
      pct = (float)count*100f/(float)max;
      data.add(new RowHeaderEntry(counters[i].getSymbol().toString(), 
          counters[i].getSymbol().getGraphics(), count, pct));
      tot += counters[i].getCounter();
      totf += pct;
    }
    addCountsForFamilies(alphC, data);
    _rowHeaderVals = data.toArray(new RowHeaderEntry[0]);
    System.out.println("Tot: "+tot+", Pct: "+totf);
  }
  /**
   * Initializes columns size for RowHeader MSA table to default values.
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

  private class AlphabetCounterTable extends JTable {
    private static final long serialVersionUID = 1L;
    public AlphabetCounterTable(TableModel dm) {
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
      return tcr;
    }
  }

  private class AlphabetCounterTableModel extends AbstractTableModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private RowHeaderEntry[]      _data;

    public AlphabetCounterTableModel(){
    }

    public AlphabetCounterTableModel(RowHeaderEntry[] d){
      _data = d;
    }
    public String getColumnName(int column){
      return COUNTER_COLUMN_HDR[column];
    }

    public int getColumnCount() { 
      return COUNTER_COLUMN_HDR.length;
    }

    public int getRowCount() {
      if (_data==null)
        return 0;
      return _data.length;
    }

    public Object getValueAt(int row, int col) {
      if (_data == null)
        return "";
      switch(col){
        case 0:
          return _data[row].getCount();
        case 1:
          return PCT_FORMATTER.format(_data[row].getPercentage());
      }
      return "";
    }
  }

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
}
