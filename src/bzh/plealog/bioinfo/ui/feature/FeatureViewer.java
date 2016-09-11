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
package bzh.plealog.bioinfo.ui.feature;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.plealog.genericapp.api.EZEnvironment;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.feature.Qualifier;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSelectionEvent;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSelectionListener;
import bzh.plealog.bioinfo.ui.resources.SVMessages;

/**
 * This class implements a FeatureTable viewer.
 * 
 * @author Patrick G. Durand
 */
public class FeatureViewer extends JPanel {
  private static final long serialVersionUID = -4528182274441928087L;
  private JComboBox<FeatureType>  _featTypes;
  private JList<Feature>          _list;
  private JTextField              _nFeatures;
  private DefaultListModel<Feature> _model;
  private QualifierTable          _qTable;
  private EventListenerList       _listenerList;
  private TableCellButtonRenderer _cellRenderer;
  private TableCellButtonEditor   _cellEditor;
  private TableCellButtonLinker   _cellLinker;
  private FeatureTable            _featTable;
  private String                  _lastSelectedFeatType = FeatureSelectionListener.ALL_TYPE;
  private boolean                 _showQualTable;
  private boolean                 _updating;
  private boolean                 _autoSelectFirstFeature = true;

  public static final int QUAL_NAME  = 0;
  public static final int QUAL_VALUE = 1;
  public static final int QUAL_LINK  = 2;
  public static final int QUAL_EDIT  = 3;

  protected static final String[] QUAL_HEADERS = {
    SVMessages.getString("FeatureViewer.0"),
    SVMessages.getString("FeatureViewer.1"),
    " ", //for the JButton Link: nothing to show
    " "  //for the JButton Edit: nothing to show
  };
  private static final Color QUERY_CELL_BK_COLOR = new Color(184,207,229);

  /**
   * Default constructor.
   */
  public FeatureViewer(FeatureWebLinker fwl){
    this(fwl, true);
  }

  public FeatureViewer(FeatureWebLinker fwl, boolean showQualTable){
    JPanel      featPanel, featCountPanel, mainPanel;
    JSplitPane  jsp;
    JScrollPane scroll;
    FontMetrics fm;

    _showQualTable = showQualTable;
    _featTypes = new JComboBox<FeatureType>();
    _featTypes.addActionListener(new FeatureTypeDisplayComboListener());
    _featTypes.setEnabled(false);
    _cellRenderer = new TableCellButtonRenderer();
    _cellEditor = new TableCellButtonEditor(new JCheckBox());
    _cellLinker = new TableCellButtonLinker(fwl, new JCheckBox());
    _listenerList = new EventListenerList();
    featCountPanel = new JPanel();
    featCountPanel.add(new JLabel(SVMessages.getString("FeatureViewer.2")));
    _nFeatures = createTextField();
    featCountPanel.add(_nFeatures);
    featPanel = new JPanel(new BorderLayout());
    featPanel.add(featCountPanel, BorderLayout.WEST);

    _model = new DefaultListModel<Feature>();
    _list = new JList<Feature>(_model);
    fm = _list.getFontMetrics(_list.getFont());
    _list.setFixedCellHeight(fm.getHeight());
    _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    //_list.setVisibleRowCount(10);

    if (_showQualTable)
      _qTable = createQualTable();
    else
      _qTable = null;

    _list.addListSelectionListener(new FeatListSelListener());

    mainPanel = new JPanel(new BorderLayout());
    scroll = new JScrollPane(_list);
    scroll.setPreferredSize(new Dimension(130,40));
    scroll.setMinimumSize(new Dimension(130,40));
    mainPanel.add(scroll, BorderLayout.CENTER);

    this.setLayout(new BorderLayout());
    mainPanel.add(_featTypes, BorderLayout.NORTH);
    if (_showQualTable){
      jsp = new JSplitPane(
          JSplitPane.HORIZONTAL_SPLIT,
          mainPanel,
          new JScrollPane(_qTable));
      this.add(jsp, BorderLayout.CENTER);
      this.addComponentListener(new QualTableComponentAdapter());
    }
    else{
      this.add(mainPanel, BorderLayout.CENTER);
    }
  }
  /**
   * Registers a Feature Selection Listener.
   */
  public void addFeatureSelectionListener(FeatureSelectionListener listener){
    if (listener!=null)
      _listenerList.add(FeatureSelectionListener.class, listener);
  }

  public void setAutoSelectFirstFeature(boolean b){
    _autoSelectFirstFeature = b;
  }
  protected class FeatureTypeDisplayComboListener implements ActionListener{
    private void fireFeatureTypesSelectedEvent(String[] types){
      Object[] observers = _listenerList.getListenerList();
      int i;
      for (i = observers.length - 2; i >= 0; i -= 2) {
        if (observers[i] == FeatureSelectionListener.class) {
          ((FeatureSelectionListener)observers[i+1]).featureTypesSelected(types);
        }
      }
    }
    public void actionPerformed(ActionEvent e){
      Object obj;

      if (_updating)
        return;
      JComboBox<?> cb = (JComboBox<?>) e.getSource();
      obj = cb.getSelectedItem();
      if (obj==null)
        return;
      _lastSelectedFeatType = ((FeatureType)obj).getName(); 
      updateFeatureList(_lastSelectedFeatType);
      fireFeatureTypesSelectedEvent(new String[]{_lastSelectedFeatType});
    }
  }
  protected void updateFeatureTypeCombo(List<FeatureType> featNames, FeatureTable ft){
    FeatureType type;
    int         i, size, selIdx = 0;

    if (featNames.isEmpty()){
      _featTypes.setEnabled(false);
      return;
    }
    _updating = true;
    _featTypes.setEnabled(true);
    featNames.add(0, new FeatureType(FeatureSelectionListener.ALL_TYPE, ft.features()));
    size = featNames.size();
    for(i=0;i<size;i++){
      type = featNames.get(i);
      _featTypes.addItem(type);
      if (type.getName().equals(_lastSelectedFeatType)){
        selIdx = i;
      }
    }
    _updating = false;
    _featTypes.setSelectedIndex(selIdx);
  }
  /**
   * Returns the list of Feature found in the FeatureTable.
   */
   protected List<FeatureType> getFeatureNamesList(FeatureTable ft){
    ArrayList<FeatureType>        list;
    Hashtable<String,FeatureType> hs;
    Enumeration<Feature>          ftEnum;
    Feature                       feat;
    String                        featName;
    FeatureType                   featT;

    list = new ArrayList<FeatureType>();
    if (ft==null)
      return list;
    hs = new Hashtable<String, FeatureType>();
    ftEnum = ft.enumFeatures();
    while(ftEnum.hasMoreElements()){
      feat = (Feature) ftEnum.nextElement();
      featName = feat.getKey();
      featT = hs.get(featName);
      if (featT!=null){
        featT.setCount(featT.getCount()+1);
      }
      else{
        featT = new FeatureType(featName, 1);
        hs.put(featName, featT);
        list.add(featT);
      }
    }
    Collections.sort(list);
    return list;
   }
   private class FeatureType implements Comparable<FeatureType>{
     private String name;
     private int    count;
     private String repr;

     public FeatureType(String name, int count) {
       super();
       this.name = name;
       this.count = count;
     }
     public String getName() {
       return name;
     }
     public int getCount() {
       return count;
     }
     /*public void setName(String name) {
			this.name = name;
			repr = null;
		}*/
     public void setCount(int count) {
       this.count = count;
       repr = null;
     }
     public String toString(){
       if (repr!=null)
         return repr;
       StringBuffer buf = new StringBuffer(name);
       buf.append(" [");
       buf.append(count);
       buf.append("]");
       repr = buf.toString();
       return repr;
     }
     public int compareTo(FeatureType ft){
       return this.getName().compareTo(ft.getName());
     }
   }
   /**
    * Removes a Feature Selection Listener.
    */
   public void removeFeatureSelectionListener(FeatureSelectionListener listener){
     if (listener!=null)
       _listenerList.remove(FeatureSelectionListener.class, listener);
   }

   protected void fireFeatureSelectionEvent(FeatureSelectionEvent event){
     Object[] observers = _listenerList.getListenerList();
     int i;
     for (i = observers.length - 2; i >= 0; i -= 2) {
       if (observers[i] == FeatureSelectionListener.class) {
         ((FeatureSelectionListener)observers[i+1]).featureSelected(event);
       }
     }
   }

   private QualifierTable createQualTable(){
     QualifierTable qTable;

     qTable = new QualifierTable();
     qTable.setModel(new QualifierTableModel());
     qTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
     qTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
     qTable.getTableHeader().setReorderingAllowed(false);
     qTable.setColumnSelectionAllowed(false);
     qTable.setRowSelectionAllowed(false);
     qTable.setGridColor(Color.LIGHT_GRAY);
     return qTable;
   }

   private class QualifierTable extends JTable {
     /**
      * 
      */
      private static final long serialVersionUID = 4088568517949375876L;
      private QualifierTable() {
        super();
      }

      public TableCellEditor getCellEditor(int row, int column){
        if (column==QUAL_EDIT)
          return _cellEditor;
        else if (column==QUAL_LINK)
          return _cellLinker;
        else
          return super.getCellEditor(row, column);
      }
      public TableCellRenderer getCellRenderer(int row, int column) {
        TableCellRenderer tcr;

        if (column==QUAL_EDIT){
          _cellRenderer.setIcon(null);
          return _cellRenderer;
        }
        else if (column==QUAL_LINK){
          FeatureWebLinker linker = _cellLinker.getFeatureWebLinker();
          String           qName = getValueAt(row, FeatureViewer.QUAL_NAME).toString(); 
          String           qVal = getValueAt(row, FeatureViewer.QUAL_VALUE).toString(); 
          if (linker!=null && linker.isLinkable(qName, qVal)){
            _cellRenderer.setIcon(EZEnvironment.getImageIcon("small_earth.png"));
            return _cellRenderer;
          }
        }
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

   private class FeatListSelListener implements ListSelectionListener{
     public void valueChanged(ListSelectionEvent e){
       Feature feat=null;
       int     idx;

       if (e.getValueIsAdjusting())
         return;
       idx = ((JList<?>)e.getSource()).getSelectedIndex();
       if (idx>=0){
         feat = (Feature) _list.getModel().getElementAt(idx);
         if (_showQualTable)
           ((QualifierTableModel)_qTable.getModel()).updateModel(feat, null);
       }
       else{
         if (_showQualTable)
           ((QualifierTableModel)_qTable.getModel()).resetModel();

       }
       if (_showQualTable)
         _qTable.updateUI();
       fireFeatureSelectionEvent(new FeatureSelectionEvent(feat));
     }
   }

   private class QualifierTableModel extends AbstractTableModel {
     /**
      * 
      */
     private static final long serialVersionUID = -9100479266314202824L;
     private int[]        _columnIds;
     private int          _rowCount;
     private Feature      _feat;

     public QualifierTableModel(){
       createStandardColHeaders();
     }

     /*public QualifierTableModel(Feature  f, int[] colIds){
            updateModel(f, colIds);
        }*/

     public void updateModel(Feature  f, int[] colIds){
       if (colIds==null || colIds.length==0)
         createStandardColHeaders();
       if (f!=null)
         createTable(f);
     }

     public void resetModel(){
       _qTable.getSelectionModel().clearSelection();
       //clear data model
       _feat = null;
       _rowCount = 0;
       fireTableChanged(new TableModelEvent(this,0,0,0,TableModelEvent.DELETE));
     }
     public boolean isCellEditable(int row, int col){
       if (col==QUAL_EDIT){
         return true;
       }
       else if (col==QUAL_LINK){
         FeatureWebLinker linker = _cellLinker.getFeatureWebLinker();
         String           qName = getValueAt(row, FeatureViewer.QUAL_NAME).toString(); 
         String           qVal = getValueAt(row, FeatureViewer.QUAL_VALUE).toString(); 
         return (linker!=null && linker.isLinkable(qName, qVal));
       }
       else{
         return false;
       }
     }
     private void createTable(Feature f){
       int size;

       size = f.qualifiers();
       if (size==0){
         _feat = null;
       }
       else{
         _feat = f;
       }
       _rowCount = size;
       fireTableChanged(new TableModelEvent(this,0,0,0,TableModelEvent.UPDATE));
     }

     private void createStandardColHeaders(){
       _columnIds = new int[QUAL_HEADERS.length];
       _columnIds[0] = QUAL_NAME;
       _columnIds[1] = QUAL_VALUE;
       _columnIds[2] = QUAL_LINK;
       _columnIds[3] = QUAL_EDIT;
     }

     public String getColumnName(int column){
       return QUAL_HEADERS[_columnIds[column]];
     }

     public int getColumnCount() { 
       return _columnIds.length; 
     }

     public int getRowCount() { 
       return _rowCount;
     }

     public Object getValueAt(int row, int col) { 
       Qualifier qual;
       Object    val = "";

       if (_feat==null)
         return val; 
       qual = _feat.getQualifier(row);
       if (qual==null)
         return val; 
       switch(_columnIds[col]){
         case QUAL_NAME:
           val = qual.getName();
           break;
         case QUAL_VALUE:
           val = qual.getValue();
           break;
         case QUAL_EDIT:
           val = "...";
           break;
       }
       return (val); 
     }
   }
   /**
    * Resets the viewer.
    */
   public void clear(){
     setData(null);
   }

   protected void updateFeatureList(String featType){
     DefaultListModel<Feature> model;
     Enumeration<Feature>      myEnum;
     Feature                   feat;

     model = new DefaultListModel<Feature>();
     myEnum = _featTable.enumFeatures();

     while(myEnum.hasMoreElements()){
       feat = (Feature) myEnum.nextElement();
       if (featType.equals(FeatureSelectionListener.ALL_TYPE) || feat.getKey().equals(featType))
         model.addElement(feat);
     }
     _list.clearSelection();
     _list.setModel(model);
     setNbFeature(_featTable.features());
     _model = model;
     if (_autoSelectFirstFeature){
       _list.setSelectedIndex(0);
       _list.ensureIndexIsVisible(0);
     }
   }
   /**
    * Sets a new FeatureTable.
    * 
    */
    public void setData(FeatureTable fTable){

      _list.clearSelection();
      _model.clear();
      _featTypes.removeAllItems();
      _featTypes.setEnabled(false);
      if(_showQualTable){
        _qTable.clearSelection();
        ((QualifierTableModel)_qTable.getModel()).resetModel();
      }
      setNbFeature(0);

      _featTable = fTable;

      if (fTable==null || fTable.features()==0){
        return;
      }
      updateFeatureTypeCombo(getFeatureNamesList(fTable), fTable);
    }

    private void setNbFeature(int val){
      _nFeatures.setText(String.valueOf(val));
    }

    private JTextField createTextField(){
      JTextField tf;

      tf = new JTextField();
      tf.setEditable(false);
      tf.setBorder(null);
      return tf;
    }
    private class QualTableComponentAdapter extends ComponentAdapter{
      private void initColumnSize(int width){
        FontMetrics      fm;
        TableColumnModel tcm;
        TableColumn      tc;
        int              val;

        fm = _qTable.getFontMetrics(_qTable.getFont());
        tcm = _qTable.getColumnModel();
        tc = tcm.getColumn(QUAL_NAME);
        val = 3*fm.stringWidth(QUAL_HEADERS[QUAL_NAME])+20;
        tc.setPreferredWidth(val);
        tc = tcm.getColumn(QUAL_VALUE);
        tc.setPreferredWidth(width-val-20);
        tc = tcm.getColumn(QUAL_EDIT);
        tc.setPreferredWidth(10);
        tc = tcm.getColumn(QUAL_LINK);
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
    public void selectFeature(Feature feat){
      Feature f;
      int  i, size;

      size = _list.getModel().getSize();
      for(i=0;i<size;i++){
        f = (Feature) _list.getModel().getElementAt(i);
        if(f == feat){
          _list.getSelectionModel().setSelectionInterval(i, i);
          _list.ensureIndexIsVisible(i);
          return;
        }
      }
      _list.clearSelection();
    }
}
