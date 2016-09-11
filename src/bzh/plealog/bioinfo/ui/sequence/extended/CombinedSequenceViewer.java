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
package bzh.plealog.bioinfo.ui.sequence.extended;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSelectionEvent;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSelectionListener;
import bzh.plealog.bioinfo.api.data.sequence.DAlphabet;
import bzh.plealog.bioinfo.api.data.sequence.DLocation;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.ui.carto.core.CartoViewerControlPanel;
import bzh.plealog.bioinfo.ui.carto.core.CartoViewerPanel;
import bzh.plealog.bioinfo.ui.carto.data.BasicFeatureOrganizer;
import bzh.plealog.bioinfo.ui.carto.drawer.AxisDrawingLane;
import bzh.plealog.bioinfo.ui.carto.drawer.RulerDrawingLane;
import bzh.plealog.bioinfo.ui.carto.drawer.SequenceDrawingLane;
import bzh.plealog.bioinfo.ui.carto.event.SViewerSelectionEvent;
import bzh.plealog.bioinfo.ui.carto.event.SViewerSelectionListener;
import bzh.plealog.bioinfo.ui.feature.FeatureViewer;
import bzh.plealog.bioinfo.ui.hca.DDPanelHCA;
import bzh.plealog.bioinfo.ui.hca.DisplaySpecialsAction;
import bzh.plealog.bioinfo.ui.sequence.basic.DPatternSearchFacility;
import bzh.plealog.bioinfo.ui.sequence.basic.DSequenceTableViewer;
import bzh.plealog.bioinfo.ui.sequence.basic.RowHeaderTable;
import bzh.plealog.bioinfo.ui.sequence.event.DDSequenceViewerConn;
import bzh.plealog.bioinfo.ui.sequence.event.DPatternEvent;
import bzh.plealog.bioinfo.ui.sequence.event.DPatternListener;
import bzh.plealog.bioinfo.ui.sequence.event.DSelectionListenerSupport;
import bzh.plealog.bioinfo.ui.util.ContextMenuElement;
import bzh.plealog.bioinfo.ui.util.ContextMenuManager;
import bzh.plealog.bioinfo.ui.util.ImageManagerAction;
import bzh.plealog.bioinfo.ui.util.SearchField;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;


/**
 * This is a full-featured sequence viewer combining several views. 
 * 
 * <br><br>
 * 
 * Three complementary views are provided to show a sequence: a zoomable graphical view relying on 
 * CartoViewerPanel, a standard sequence view relying on DSequenceTableViewer and an HCA view relying 
 * on DDPanelHCA. This latter viewer is only available when displaying protein sequences. 
 * 
 * <br><br>
 * 
 * You can use this viewer as it is for your own applications. You can also have a look at its
 * source code to see how to use and interact with the many different viewers contained here.
 * 
 * @author Patrick G. Durand
 */
public class CombinedSequenceViewer extends JPanel implements DDSequenceViewerConn{
  private static final long serialVersionUID = -8511200475207932581L;
  private CartoSequenceViewer     _cartoViewer;
  private JComponent              _cartoViewerLaneHeader;
  private CartoViewerControlPanel _cartoCtrlPanel;
  private JScrollPane             _cartoScroller;
  private DSequenceTableViewer    _seqViewer;
  private DDPanelHCA              _hcaViewer;
  private RowHeaderTable          _rowHeaderTable;
  private DPatternSearchFacility  _patternFacility;
  private JScrollPane             _mainScroller;
  private JTextField              _nameField;
  private JTextField              _sizeField;
  private JTextArea               _positionF;
  private JPanel                  _headerPnl;
  private SearchField             _sf;
  private JTabbedPane             _jtp;
  private FeatureViewer           _featureViewer;

  private static final String NAME_FIELD_HDR = "Name:";
  private static final String SIZE_FIELD_HDR = "Size:";
  private static final String NOT_FOUND = " not found.";

  /**
   * Default constructor.
   */
  public CombinedSequenceViewer(){
    super();
    JPanel      seqPnl, patternPnl;

    _seqViewer = new DSequenceTableViewer();
    _hcaViewer = new DDPanelHCA();
    _rowHeaderTable = new RowHeaderTable(null);
    _rowHeaderTable.setColumnSelectionAllowed(false);
    _rowHeaderTable.setRowSelectionAllowed(false);
    initColumnSizeRowHeaderTable();
    Dimension d = _rowHeaderTable.getPreferredScrollableViewportSize();
    d.width = 2 * _rowHeaderTable.getPreferredSize().width;
    _rowHeaderTable.setPreferredScrollableViewportSize(d);
    _rowHeaderTable.setRowHeight(_seqViewer.getRowHeight());

    seqPnl = new JPanel(new BorderLayout());
    seqPnl.add(_seqViewer, BorderLayout.CENTER);

    _mainScroller = new JScrollPane(seqPnl);
    _mainScroller.setRowHeaderView(_rowHeaderTable);

    JScrollBar vsb = _mainScroller.getVerticalScrollBar();
    vsb.setUnitIncrement(_seqViewer.getCellWidth());
    vsb.setBlockIncrement(10*_seqViewer.getCellWidth());
    patternPnl = new JPanel();
    _patternFacility = new DPatternSearchFacility(null);
    _patternFacility.addDPatternListener(new MyDPatternListener());
    _sf = _patternFacility.getSearchForm();
    patternPnl.add(_sf);
    _sf.setEnabled(false);
    _headerPnl = new JPanel(new BorderLayout());
    _headerPnl.add(patternPnl, BorderLayout.WEST);

    _jtp = new JTabbedPane();
    _jtp.setFocusable(false);
    _jtp.setTabPlacement(JTabbedPane.LEFT);
    //jtp.setOpaque(false);
    _jtp.add("Graphic", prepareCartoViewer());
    _jtp.add("Sequence", _mainScroller);
    _jtp.add("HCA", new JScrollPane(_hcaViewer));
    this.setLayout(new BorderLayout());
    this.add(getHeaderpanel(), BorderLayout.NORTH);
    this.add(_jtp, BorderLayout.CENTER);
    this.add(_headerPnl, BorderLayout.SOUTH);

  }

  /**
   * Setup a Carto Viewer.
   */
  private JPanel prepareCartoViewer(){
    JScrollPane scroller;
    JPanel      pnl;

    _cartoViewer = new CartoSequenceViewer();
    _cartoViewer.addSViewerSelectionListener(new MyCartoFeatureListener());
    scroller = new JScrollPane(_cartoViewer);
    scroller.getHorizontalScrollBar().addAdjustmentListener(new MyAdjustmentListener(_cartoViewer));
    _cartoViewerLaneHeader = _cartoViewer.getLaneHeader(100);
    scroller.setRowHeaderView(_cartoViewerLaneHeader);
    _cartoScroller = scroller;

    _cartoCtrlPanel = new CartoViewerControlPanel(_cartoViewer);
    JPanel cmdPnl = new JPanel(new BorderLayout());
    cmdPnl.add(_cartoCtrlPanel, BorderLayout.WEST);

    pnl = new JPanel(new BorderLayout());
    pnl.add(cmdPnl, BorderLayout.NORTH);
    pnl.add(scroller, BorderLayout.CENTER);
    return pnl;
  }

  /**
   * Register a FeatureViewer and connect it to sequence viewers.
   */
  public void registerFeatureViewer(FeatureViewer fv){
    _featureViewer = fv;
    _featureViewer.addFeatureSelectionListener(_cartoViewer);
  }

  /**
   * Add actions to the viewers.
   */
  public void plugActions(JToolBar tBar, boolean displayBtnLabel){
    JToggleButton btn;
    ImageIcon     icon;
    ImageManagerAction imager;

    tBar.addSeparator();
    //Capture
    icon = EZEnvironment.getImageIcon("imager.png");
    if (icon!=null){
      imager = new ImageManagerAction("", icon);
    }
    else{
      imager = new ImageManagerAction("Capture");
    }
    JComponent header = _cartoScroller.getColumnHeader();
    imager.setComponents(new JComponent[]{_cartoScroller.getViewport(),header,_cartoScroller.getRowHeader()});
    JButton button = tBar.add(imager);
    if(displayBtnLabel) {
      button.setText("Capture");
    }
    tBar.addSeparator();
    //Select
    icon = EZEnvironment.getImageIcon("aero_arrow-center_24_24.png");
    btn = _cartoCtrlPanel.getSelectionBtn();
    if (icon!=null){
      btn.setIcon(icon);
      btn.setText("");
    }
    if(displayBtnLabel) {
      btn.setVerticalTextPosition(AbstractButton.BOTTOM);
      btn.setHorizontalTextPosition(AbstractButton.CENTER);
      btn.setText("Select");
    }
    tBar.add(btn);
    //Zoom
    icon = EZEnvironment.getImageIcon("docView.png");
    btn = _cartoCtrlPanel.getZoomBtn();
    if (icon!=null){
      btn.setIcon(icon);
      btn.setText("");
    }
    if(displayBtnLabel) {
      btn.setVerticalTextPosition(AbstractButton.BOTTOM);
      btn.setHorizontalTextPosition(AbstractButton.CENTER);
      btn.setText("Zoom");
    }
    tBar.add(btn);
  }

  /**
   * Adds a FeatureTable to this viewer.
   */
  public void setFeaturesForCartoView(FeatureTable fTable){
    RulerDrawingLane    rdl;
    SequenceDrawingLane sdl;
    AxisDrawingLane     adl;
    Dimension           dim;
    DSequence           sequence;
    int                 labelLength;

    _cartoViewer.clear();
    sequence = this.getSequence();
    if (sequence==null)
      return;

    _cartoViewer.setReferenceSequence(sequence);
    //adds the sequence
    sdl = new SequenceDrawingLane(sequence);
    _cartoViewer.addDrawingLane(sdl);

    //adds the axis
    labelLength = String.valueOf(sequence.getRulerModel().getSeqPos(sequence.size()-1)).length();
    sdl.setReferenceLabelSize(labelLength);


    adl = new AxisDrawingLane(sequence, AxisDrawingLane.TICK_TYPE.BOTH);
    dim = adl.getPreferredSize();
    adl.setTickType(AxisDrawingLane.TICK_TYPE.DOWN);
    dim.height = 10;
    adl.setPreferredSize(dim);
    adl.setReferenceLabelSize(labelLength);
    _cartoViewer.addDrawingLane(adl);
    //adds the ruler
    rdl = new RulerDrawingLane(sequence);
    rdl.setReferenceLabelSize(labelLength);
    _cartoViewer.addDrawingLane(rdl);

    //adds the features if any
    if (fTable!=null){
      String[] featureOrdering = {"source","gap","gene","mRNA","CDS"};
      BasicFeatureOrganizer.organizeFeatures(
          _cartoViewer, 
          fTable, 
          sequence, 
          featureOrdering, 
          false, 
          sequence.size()//use seq size instead of panel width to compact view as more as possible
          );
    }
    //add some space on far right/left sides of the view
    _cartoViewer.setMargins(50,50);

  }
  /**
   * Register the component used by this viewer to broadcast selection events.
   */
  public void registerSelectionListenerSupport(DSelectionListenerSupport lSupport){
    _seqViewer.registerSelectionListenerSupport(lSupport);
    _hcaViewer.registerSelectionListenerSupport(lSupport);
  }

  /**
   * Adds a special command panel on the right of the header of the viewer. 
   * Can be used to add a toolbar, for example.
   */
  public void setCommandPanel(JComponent pnl){
    if (pnl!=null)
      _headerPnl.add(pnl, BorderLayout.EAST);
  }

  private JTextField getTxtField(){
    JTextField txtF = new JTextField();
    txtF.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
    txtF.setEditable(false);
    txtF.setOpaque(false);
    //txtF.setForeground(DDResources.getSystemTextColor());
    return txtF;
  }

  private JPanel getHeaderpanel(){
    JPanel pnl, namePnl, sizePnl, mainPnl;

    _nameField = getTxtField();
    _nameField.setFocusable(false);
    namePnl = new JPanel(new BorderLayout());
    namePnl.add(new JLabel(NAME_FIELD_HDR), BorderLayout.WEST);
    namePnl.add(_nameField, BorderLayout.CENTER);
    namePnl.setBorder(BorderFactory.createEmptyBorder(2, 5, 1, 0));

    _sizeField = getTxtField(); 
    _sizeField.setFocusable(false);
    sizePnl = new JPanel(new BorderLayout());
    sizePnl.add(new JLabel(SIZE_FIELD_HDR), BorderLayout.WEST);
    sizePnl.add(_sizeField, BorderLayout.CENTER);
    sizePnl.setBorder(BorderFactory.createEmptyBorder(2, 5, 1, 0));

    pnl = new JPanel(new BorderLayout());
    pnl.add(namePnl, BorderLayout.NORTH);
    pnl.add(sizePnl, BorderLayout.SOUTH);
    mainPnl = new JPanel(new BorderLayout());
    mainPnl.add(pnl, BorderLayout.CENTER);

    _positionF = new JTextArea();
    _positionF.setText("");
    _positionF.setEditable(false);
    _positionF.setOpaque(false);
    _positionF.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    Dimension dim = _positionF.getPreferredSize();
    FontMetrics fm = _positionF.getFontMetrics(_positionF.getFont());
    dim.width = fm.stringWidth("01234567890");
    _positionF.setPreferredSize(dim);
    _positionF.setMaximumSize(dim);
    _positionF.setMinimumSize(dim);

    _seqViewer.setPositionField(_positionF);
    _hcaViewer.setPositionField(_positionF);
    mainPnl.add(_positionF, BorderLayout.EAST);
    return mainPnl;
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

  /**
   * Sets the sequence to be displayed by this viewer. Pass null to reset the viewer content.
   */
  public void setSequence(DSequence sequence){
    StringBuffer buf;

    this.cleaSelection();
    _seqViewer.setSequence(sequence);
    _rowHeaderTable.updateModel(sequence);
    _patternFacility.setSequence(sequence);
    _sf.setEnabled(sequence!=null);
    if (sequence==null){
      _nameField.setText("");
      _sizeField.setText("");
      _hcaViewer.setSequence((DSequence)null);
    }
    else{
      if (sequence.getSequenceInfo()!=null){
        if (sequence.getSequenceInfo().getName()!=null){
          _nameField.setText(sequence.getSequenceInfo().getId()+": "+sequence.getSequenceInfo().getName());
        }
        else{
          _nameField.setText(sequence.getSequenceInfo().getId());
        }
      }
      else{
        _nameField.setText("-");
      }
      buf = new StringBuffer();
      buf.append(sequence.size());
      switch(sequence.getAlphabet().getType()){
        case DAlphabet.PROTEIN_ALPHABET:
          buf.append(" ");
          buf.append(DAlphabet.AA_STR);
          break;
        case DAlphabet.DNA_ALPHABET:
        case DAlphabet.RNA_ALPHABET:
          buf.append(" ");
          buf.append(DAlphabet.NUC_STR);
          break;
        default:
          buf.append(" ");
          buf.append(DAlphabet.OTHER_STR);
          break;
      }

      _sizeField.setText(buf.toString());
      //reset scroll at top position
      Rectangle rect = _seqViewer.getCellRect(0, 0, true);
      _seqViewer.scrollRectToVisible(rect);
      if (sequence.getAlphabet().getType()==DAlphabet.PROTEIN_ALPHABET){
        _hcaViewer.setSequence(sequence);
        _jtp.setEnabledAt(2, true);
      }
      else{
        _hcaViewer.setSequence((DSequence)null);
        _jtp.setEnabledAt(2, false);
      }
    }
  }
  /**
   * Returns the sequence currently displayed by this viewer.
   */
  public DSequence getSequence(){
    return _seqViewer.getSequence();
  }
  /**
   * Returns the selected region of the sequence currently displayed by this viewer.
   * Returns null if nothing is selected.
   */
  public DSequence getSelectedSequence(){
    return _seqViewer.getSelectedSequence();
  }
  /**
   * Sets the selected region of the sequence displayed in the viewer.
   * Values have to be zero-based and absolute, so use the DSequence DRulerModel to get
   * an absolute position from a sequence coordinate. Set from and to to -1 to reset
   * selection.
   */
  public void setSelectedSequenceRange(int from, int to){
    _seqViewer.setSelectedSequenceRange(from, to);
    _hcaViewer.setSelectedSequenceRange(from, to);
  }

  /**
   * Sets a list of selected segments over the sequence.  Value are
   * zero-based and absolute: use the DRulerModel from the DSequence to 
   * switch to the sequence coordinate system. 
   */
  public void setSelectionRanges(List<DLocation> locs){
    _seqViewer.setSelectionRanges(locs);
    _hcaViewer.setSelectionRanges(locs);
  }
  /**
   * Returns a list of selected segments over the sequence.  Value are
   * zero-based and absolute: use the DRulerModel from the DSequence to 
   * switch to the sequence coordinate system. Returns null if nothing is selected.
   */
  public List<DLocation> getSelectedRanges(){
    return _seqViewer.getSelectedRanges();
  }

  public void cleaSelection(){
    _seqViewer.setSelectedSequenceRange(-1, -1);
    _hcaViewer.setSelectedSequenceRange(-1, -1);
  }
  /**
   * Returns the global selected region of the sequence displayed in the viewer.
   * The method returns null if nothing is selected, otherwise the array contains
   * the selected region. Index zero contains from and index one contains to. Values
   * are zero-based and absolute, so use the DSequence DRulerModel to get a sequence 
   * coordinate.
   */
  public int[] getSelectedSequenceRange(){
    return _seqViewer.getSelectedSequenceRange();
  }
  /**
   * Sets a contextual popup menu to this viewer.
   */
  public void setContextMenu(List<ContextMenuElement> actions){
    //set menu to the sequence viewer
    _seqViewer.setContextMenu(new ContextMenuManager(_seqViewer, actions));

    //make a copy of standard commands, then add a save image action
    //for the HCA panel
    ArrayList<ContextMenuElement> actions2;
    actions2 = new ArrayList<ContextMenuElement>();
    for(ContextMenuElement cme : actions){
      actions2.add(cme);
    }
    //save image 
    ImageManagerAction imager;
    imager = new ImageManagerAction(
        "Capture",
        EZEnvironment.getImageIcon("imager_s.png"));
    imager.setComponent(_hcaViewer);
    actions2.add(null);
    actions2.add(new ContextMenuElement(imager));
    //display on/off special symbols
    DisplaySpecialsAction dsa = new DisplaySpecialsAction(DisplaySpecialsAction.DISP_OFF);
    dsa.setPanelHca(_hcaViewer);
    actions2.add(null);
    actions2.add(new ContextMenuElement(dsa));
    _hcaViewer.setContextMenu(new ContextMenuManager(_hcaViewer, actions2));
  }
  private class MyDPatternListener implements DPatternListener{
    public void patternMatched(DPatternEvent event){
      int from, to;
      from = event.getMatchFrom();
      to = event.getMatchTo();
      if (from==-1 && to==-1){
        JOptionPane.showMessageDialog(
            EZEnvironment.getParentFrame(),
            event.getPattern()+NOT_FOUND,
            EZApplicationBranding.getAppName(),
            JOptionPane.INFORMATION_MESSAGE);
      }
      else{
        CombinedSequenceViewer.this.setSelectedSequenceRange(event.getMatchFrom(), event.getMatchTo());
      }
    }
  }
  private class MyCartoFeatureListener implements SViewerSelectionListener{
    public void objectSelected(SViewerSelectionEvent event){
      if (_featureViewer!=null && event.getType()==SViewerSelectionEvent.SEL_TYPE.EMPTY)
        _featureViewer.selectFeature(null);

      if (_featureViewer!=null && (event.getSelectionObject() instanceof Feature)
          && event.getSource()!=_cartoViewer){
        _featureViewer.selectFeature((Feature)event.getSelectionObject());
        //System.out.println("MyCartoFeatureListener");
      }
    }
  }
  /**
   * Local implementation of the SViewerPanel.
   */
  private class CartoSequenceViewer extends CartoViewerPanel implements FeatureSelectionListener{
    /**
     * 
     */
    private static final long serialVersionUID = 8332595722485293649L;
    public CartoSequenceViewer(){
      super();
    }
    public void featureSelected(FeatureSelectionEvent event){
      this.setSelectedObject(event.getFeature());
      this.repaint();
    }
    public void featureTypesSelected(String[] types) {
      this.setFeatureTypesToDisplay(types);
      this.repaint();
      _cartoViewerLaneHeader.repaint();
      _cartoScroller.updateUI();
    }
  }
  /**
   * Local implementation of a scrollbar listener. Added to fix the repaint of the viewer
   * panel during scrolling.
   */
  private class MyAdjustmentListener implements AdjustmentListener {
    private CartoViewerPanel spnl;
    private MyAdjustmentListener(CartoViewerPanel pnl){
      spnl = pnl;
    }
    // This method is called whenever the value of a scrollbar is changed,
    // either by the user or programmatically.
    public void adjustmentValueChanged(AdjustmentEvent evt) {
      spnl.repaint();
    }
  }

}
