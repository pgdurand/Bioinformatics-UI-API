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
package bzh.plealog.bioinfo.ui.seqinfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import bzh.plealog.bioinfo.api.data.sequence.BankSequenceInfo;
import bzh.plealog.bioinfo.ui.feature.TableCellButtonEditor;
import bzh.plealog.bioinfo.ui.feature.TableCellButtonRenderer;

/**
 * This is a basic SequenceInfo viewer.
 * 
 * @author Patrick G. Durand
 */
public class SequenceInfoViewer extends JPanel {
  private static final long serialVersionUID = 195118850504045104L;
  private SeqInfoTable                _siTable;
  private ArrayList<SeqInfoInspector> _inspectorTable;
  private TableCellButtonRenderer     _cellRenderer;
  private TableCellButtonEditor       _cellEditor;

  public static final int COL_NAME   = 0;
  public static final int VALUE_NAME = 1;
  public static final int VALUE_EDIT = 2;

  private static final String[] COL_HEADERS = 
    {   "Field",
    "Value",
    " "
    };
  private static final Color QUERY_CELL_BK_COLOR = new Color(184,207,229);

  private static final SimpleDateFormat DATE_FOMATTER1 = 
      new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);

  private static final SimpleDateFormat DATE_FOMATTER2 = 
      new SimpleDateFormat("yyyyMMdd");

  public SequenceInfoViewer(){
    JPanel      mainPanel;
    JScrollPane scroll;

    _cellRenderer = new TableCellButtonRenderer();
    _cellEditor = new TableCellButtonEditor(new JCheckBox());
    initInspector();
    _siTable = createSeqInfoTable();

    mainPanel = new JPanel(new BorderLayout());
    scroll = new JScrollPane(_siTable);
    scroll.setPreferredSize(new Dimension(130,40));
    scroll.setMinimumSize(new Dimension(130,40));
    mainPanel.add(scroll, BorderLayout.CENTER);
    mainPanel.addComponentListener(new SeqInfoTableComponentAdapter());
    this.setLayout(new BorderLayout());
    this.add(mainPanel, BorderLayout.CENTER);
  }
  /**
   * Resets the viewer.
   */
  public void clear(){
    setData(null);
  }

  /**
   * Sets a new SequenceInfo.
   */
  public void setData(BankSequenceInfo si){
    SeqInfoTableModel model;

    model = (SeqInfoTableModel)_siTable.getModel();
    model.resetModel();

    if (si==null){
      return;
    }
    model.updateModel(si, null);
    _siTable.updateUI();
  }

  /**
   * Initializes the Inspector Table. It is used to generically defines
   * a list of method calls that can be used to (1) count the number of rows
   * to display in the table, (2) gets the row header names for the first column
   * of the SequenceInfo Table and (3) gets the corresponding values from a 
   * SequenceInfo object. When modifying SequenceInfo, you just need to add a 
   * new SeqInfoInspector object in the Inspector table.
   */
  private void initInspector(){
    DateConvertor    dc = new DateConvertor();
    SeqInfoInspector sii;
    _inspectorTable = new ArrayList<SeqInfoInspector>();
    _inspectorTable.add(new SeqInfoInspector(
        "Organism", "getOrganism"));
    _inspectorTable.add(new SeqInfoInspector(
        "Taxonomy", "getTaxonomy"));
    _inspectorTable.add(new SeqInfoInspector(
        "Moltype", "getMoltype"));
    _inspectorTable.add(new SeqInfoInspector(
        "Topology", "getTopology"));
    _inspectorTable.add(new SeqInfoInspector(
        "Size", "getSequenceSize"));
    _inspectorTable.add(new SeqInfoInspector(
        "Division", "getDivision"));
    sii = new SeqInfoInspector(
        "Create date", "getCreationDate");
    sii.setConvertor(dc);
    _inspectorTable.add(sii);
    sii = new SeqInfoInspector(
        "Update date", "getUpdateDate");
    sii.setConvertor(dc);
    _inspectorTable.add(sii);
  }

  /**
   * Creates the table used to display the content of a SequenceInfo object.
   */
  private SeqInfoTable createSeqInfoTable(){
    SeqInfoTable qTable;

    qTable = new SeqInfoTable();
    qTable.setModel(new SeqInfoTableModel());
    qTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    qTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
    qTable.getTableHeader().setReorderingAllowed(false);
    qTable.setColumnSelectionAllowed(false);
    qTable.setRowSelectionAllowed(false);
    qTable.setGridColor(Color.LIGHT_GRAY);
    return qTable;
  }

  /**
   * The SequenceInfo table.
   */
  private class SeqInfoTable extends JTable {
    private static final long serialVersionUID = 1857329474161656402L;
    private SeqInfoTable() {
      super();
    }

    public TableCellEditor getCellEditor(int row, int column){
      if (column==VALUE_EDIT)
        return _cellEditor;
      else
        return super.getCellEditor(row, column);
    }
    public TableCellRenderer getCellRenderer(int row, int column) {
      TableCellRenderer tcr;

      if (column==VALUE_EDIT)
        return _cellRenderer;
      tcr = super.getCellRenderer(row, column);
      if (tcr instanceof JLabel){
        JLabel lbl;
        lbl = (JLabel)tcr;
        //adjust alignment
        if (column == 0)
          lbl.setBackground(QUERY_CELL_BK_COLOR);
        else
          lbl.setBackground(Color.WHITE);
      }
      return tcr;
    }
  }
  /**
   * The SequenceInfo table model.
   */
  private class SeqInfoTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -8757587425654305753L;
    private int[]        _columnIds;
    private BankSequenceInfo _seqInfo;

    public SeqInfoTableModel(){
      createStandardColHeaders();
    }

    @SuppressWarnings("unused")
    public SeqInfoTableModel(BankSequenceInfo si, int[] colIds){
      updateModel(si, colIds);
    }

    public void updateModel(BankSequenceInfo si, int[] colIds){
      if (colIds==null || colIds.length==0)
        createStandardColHeaders();
      if (si!=null)
        createTable(si);
    }

    public void resetModel(){
      _siTable.getSelectionModel().clearSelection();
      _seqInfo = null;
      fireTableChanged(new TableModelEvent(this,0,0,0,TableModelEvent.DELETE));
    }

    private void createTable(BankSequenceInfo si){
      _seqInfo = si;
      fireTableChanged(new TableModelEvent(this,0,0,0,TableModelEvent.UPDATE));
    }

    private void createStandardColHeaders(){
      _columnIds = new int[COL_HEADERS.length];
      _columnIds[0] = COL_NAME;
      _columnIds[1] = VALUE_NAME;
      _columnIds[2] = VALUE_EDIT;
    }

    public boolean isCellEditable(int row, int col){
      if (col==VALUE_EDIT)
        return true;
      else
        return false;
    }
    public String getColumnName(int column){
      return COL_HEADERS[_columnIds[column]];
    }

    public int getColumnCount() { 
      return _columnIds.length; 
    }

    public int getRowCount() { 
      return _seqInfo==null?0:_inspectorTable.size();
    }

    public Object getValueAt(int row, int col) { 
      SeqInfoInspector sii;
      Convertor        conv;
      Object           val = null;

      switch(_columnIds[col]){
        case COL_NAME:
          val = _inspectorTable.get(row).getHeaderName();
          break;
        case VALUE_NAME:
          if (_seqInfo!=null){
            sii = _inspectorTable.get(row);
            conv = sii.getConvertor();
            val = getValue(sii.getMethodName(), _seqInfo);
            if (conv!=null)
              val = conv.convert(val);
          }
          break;
        case VALUE_EDIT:
          val = "...";
          break;
      }
      if (val==null)
        val = "";
      return (val); 
    }
  }

  /**
   * A generic method to get a value from data using one of its method call.
   * This method uses the Java Reflection System to query data.
   * 
   * @return an object or null if reflection call fails
   */
  private Object getValue(String methodName, Object data) {
    Object ret = null;
    Method method;

    try {
      method = data.getClass().getMethod(methodName, (Class[]) null);
      ret = method.invoke(data, (Object[]) null);
    } catch (Exception e) {
    }
    return ret;
  }

  /**
   * A SequenceInfo Inspector.
   */
  private class SeqInfoInspector {
    /**
     * Header name used to display some data in the firt column
     * of the SequenceInfo table. Required.*/
    private String    headerName;
    /**
     * A method name that is used to query a SequnceInfo object using
     * the Java Reflection System. Required.*/
    private String    methodName;
    /**
     * A Convertor that can be used to make additional stuff on a value
     * retrieved from a SequnceInfo object. Optional. When not null, this
     * Convertor will be automatically called by the Inspector System.*/
    private Convertor convertor;

    public SeqInfoInspector(String header, String method){
      setHeaderName(header);
      setMethodName(method);
    }
    public String getHeaderName() {
      return headerName;
    }
    public void setHeaderName(String headerName) {
      this.headerName = headerName;
    }
    public String getMethodName() {
      return methodName;
    }
    public void setMethodName(String methodName) {
      this.methodName = methodName;
    }
    public Convertor getConvertor() {
      return convertor;
    }
    public void setConvertor(Convertor convertor) {
      this.convertor = convertor;
    }

  }

  private class SeqInfoTableComponentAdapter extends ComponentAdapter{
    private void initColumnSize(int width){
      FontMetrics      fm;
      TableColumnModel tcm;
      TableColumn      tc;
      String           header;
      int              i, size, tot, val;

      fm = _siTable.getFontMetrics(_siTable.getFont());
      size = _inspectorTable.size();
      val=0;
      for (i=0;i<size;i++){
        header = _inspectorTable.get(i).getHeaderName();
        tot = fm.stringWidth(header)+20;
        if (tot>val)
          val=tot;
      }
      tcm = _siTable.getColumnModel();
      tc = tcm.getColumn(COL_NAME);
      tc.setPreferredWidth(val);
      tc = tcm.getColumn(VALUE_NAME);
      tc.setPreferredWidth(width-val-10);
      tc = tcm.getColumn(VALUE_EDIT);
      tc.setPreferredWidth(10);
    }
    public void componentResized(ComponentEvent e){
      Component parent;

      int   width;
      parent = (Component) e.getSource();
      width = parent.getBounds().width;
      initColumnSize(width);
    }
  }

  /**
   * A date convertor. Converts an integer date (19980904) to an INSDseq date 
   * (such as 04-SEP-1998).
   * */
  public static String prepareDate(String d){
    String  date="-";

    try {
      date = DATE_FOMATTER1.format(DATE_FOMATTER2.parse(d.toString()));
    } catch (Exception e) {
    }
    return date;
  }
  /**
   * A date convertor. Converts an integer date (19980904) to an INSDseq date 
   * (such as 04-SEP-1998).
   * */
  private class DateConvertor implements Convertor{
    private String transformDate(Object d){
      return SequenceInfoViewer.prepareDate(d.toString());
    }
    public Object convert(Object obj) {
      return transformDate(obj);
    }
  }
  /**
   * The Convertor Interface.
   */
  private interface Convertor{
    /**
     * @param obj this is usually a value retrieved from a SequenceInfo
     * Object.*/
    public Object convert(Object obj);
  }

}
