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
package bzh.plealog.bioinfo.ui.carto.core;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.ui.carto.drawer.BasicFeatureDrawingLane;
import bzh.plealog.bioinfo.ui.carto.drawer.DrawingLane;
import bzh.plealog.bioinfo.ui.carto.drawer.DrawingLaneBase;
import bzh.plealog.bioinfo.ui.carto.drawer.FeatureDrawingLane;
import bzh.plealog.bioinfo.ui.carto.event.SViewerListenerSupport;
import bzh.plealog.bioinfo.ui.carto.event.SViewerSelectionEvent;
import bzh.plealog.bioinfo.ui.carto.event.SViewerSelectionListener;

/**
 * This class is the viewer system capable of displaying a set of DrawingLanes.
 * 
 * @author Patrick G. Durand
 */
public class CartoViewerPanel extends JPanel {
  private static final long serialVersionUID = 2945061544862358154L;
  private ArrayList<DrawingLane> _dLanesDataModel;
  private ArrayList<DrawingLane> _dLanesToDisplay;
  private Point                  _startSelectionPos = new Point();
  private Point                  _stopSelectionPos = new Point();
  private boolean                _zoomable = true;
  private MOUSE_MODE             _mmode = MOUSE_MODE.SELECTION;
  private SViewerListenerSupport _sLiseteners = new SViewerListenerSupport();
  private LaneHeaderList         _jHeader;
  private DSequence              _refSequence;
  private HashSet<String>        _featureTypesToDisplay;
  private boolean                _drawGrid=false;
  
  public static enum MOUSE_MODE {SELECTION, ZOOM};

  private static final Color ZOMMER_CLR = new Color(192, 192, 192, 50);

  /**
   * Standard constructor.
   */
  public CartoViewerPanel(){
    super();
    _dLanesDataModel = new ArrayList<DrawingLane>();
    _dLanesToDisplay = new ArrayList<DrawingLane>();
    this.addMouseListener(new DataMouseListener());
    this.addMouseMotionListener(new DataMouseMotionListener());
    this.setOpaque(true);
    this.setBackground(Color.WHITE);
    this.setAutoscrolls(true);
    _featureTypesToDisplay = new HashSet<String>();
  }
  public void setFeatureTypesToDisplay(String[] featureTypes){
    synchronized (this) {
      _featureTypesToDisplay.clear();
      for(String type : featureTypes){
        _featureTypesToDisplay.add(type);
      }
      prepareDisplayDataModel();
    }
  }
  /**
   * Figures out whether or not the viewer has to show a drawing grid. 
   * Such a grid may help the user to analyze the data.
   */
  public void setDrawGrid(boolean b){
    _drawGrid=b;
    for(DrawingLane dl : _dLanesDataModel){
      dl.setDrawGrid(b);
    }
    for(DrawingLane dl : _dLanesToDisplay){
      dl.setDrawGrid(b);
    }
  }
  private void prepareDisplayDataModel(){
    String fType;
    _dLanesToDisplay.clear();
    for(DrawingLane dl : _dLanesDataModel){
      if ((_featureTypesToDisplay.isEmpty()==false) && 
          (_featureTypesToDisplay.contains("all")==false) && //"all": from FeatureSelectionLisetner in KoriBlast API
          (dl instanceof BasicFeatureDrawingLane) ){
        fType = ((BasicFeatureDrawingLane)dl).getFeatureTable().get(0).getFeature().getKey();
        if (_featureTypesToDisplay.contains(fType)==false){
          continue;
        }
      }
      _dLanesToDisplay.add(dl);
    }
  }
  /**
   * Adds a drawing lane to this viewer.
   */
  public synchronized void addDrawingLane(DrawingLane dl){
    dl.setDrawGrid(_drawGrid);
    _dLanesDataModel.add(dl);
    //super.setPreferredSize(null);
    addSViewerSelectionListener(dl);
    prepareDisplayDataModel();
  }
  /**
   * Adds a list of drawing lanes to this viewer.
   */
  public synchronized void addDrawingLanes(List<DrawingLane> dls){
    for(DrawingLane dl : dls){
      dl.setDrawGrid(_drawGrid);
      _dLanesDataModel.add(dl);
      //super.setPreferredSize(null);
      addSViewerSelectionListener(dl);
    }
    prepareDisplayDataModel();
  }
  /**
   * Reset the content of this viewer.
   */
  public synchronized void clear(){
    for(DrawingLane dl : _dLanesDataModel){
      removeSViewerSelectionListener(dl);
    }
    _dLanesToDisplay.clear();
    _dLanesDataModel.clear();
    _featureTypesToDisplay.clear();
    //this.revalidate();
    //this.repaint();
    if (_jHeader!=null)
      _jHeader.repaint();
  }
  /**
   * Gets a particular drawing lane.
   */
  public DrawingLane getDrawingLane(int idx){
    if (_dLanesToDisplay.isEmpty())
      return null;
    return _dLanesToDisplay.get(idx);
  }
  
  /**
   * Returns the number of drawing lanes displayed by this viewer.
   */
  public int getDrawingLanes(){
    return _dLanesToDisplay.size();
  }
  
  /**
   * Turn on or off visibility status of all features contained in 
   * this drawing lane.
   * 
   *  @param visible visibility status
   */
  public void setFeaturesVisible(boolean visible){
    for(DrawingLane dl : _dLanesDataModel){
      if (dl instanceof FeatureDrawingLane){
        ((FeatureDrawingLane)dl).setFeaturesVisible(visible);
      }
    }
  }
  
  /**
   * Turn on or off visibility status of a particular feature.
   * 
   * @param feat feature for which to switch visibility status
   * @param visible visibility status
   * 
   * @return true if Feature was found in this lane, false otherwise.
   */
  public void setFeatureVisible(Feature feat, boolean visible){
    BasicFeatureDrawingLane fdl;
    String fType;
    for(DrawingLane dl : _dLanesDataModel){
      if (dl instanceof BasicFeatureDrawingLane){
        fType = ((BasicFeatureDrawingLane)dl).getFeatureTable().get(0).getFeature().getKey();
        fdl = (BasicFeatureDrawingLane)dl;
        //Features are organized by types
        if (feat.getKey().equals(fType)==false){
          continue;
        }
        if (fdl.setFeatureVisible(feat, visible)){
          //feature is contained in a single lane; so if found, we can
          //end loop through all features
          return;
        }
      }
    }
  }
  
  public void setReferenceSequence(DSequence seq){
    _refSequence = seq;
  }
  /**
   * Dispatch an object to all the drawing lanes contained in this viewer to force
   * its highlight.
   */
  public void setSelectedObject(Object obj){
    _sLiseteners.fireSelectionEvent(
        new SViewerSelectionEvent(
            CartoViewerPanel.this,
            SViewerSelectionEvent.SEL_TYPE.OBJECT_ALONE,
            obj));
    if (obj instanceof Feature && _refSequence!=null){
      setVisibleLocation(_refSequence.getRulerModel().getRulerPos(((Feature)obj).getFrom()));
    }
  }
  /**
   * Force the viewer to display a particular location within the viewport.
   * @param loc value is in the range from 0 to DSequence.size()-1.
   */
  public void setVisibleLocation(int loc){
    DrawingLane lane;
    Rectangle   r;
    int         xPos;

    if (_dLanesToDisplay.isEmpty())
      return;
    //all drawing lanes have same xFactor and left/right margins if any
    lane = _dLanesToDisplay.get(0);
    xPos = lane.getLeftMargin() + (int)(lane.computeScaleFactor() * (double) loc);
    JScrollBar hBar = ((JScrollPane)CartoViewerPanel.this.getParent().getParent()).getHorizontalScrollBar();
    r = this.getVisibleRect();
    if (xPos<r.x || xPos>(r.x+r.width)){
      hBar.setValue(xPos - (CartoViewerPanel.this.getParent().getWidth()/2));
    }
  }
  /**
   * Sets the mouse mode.
   */
  public void setMouseMode(MOUSE_MODE mm){
    _mmode = mm;
  }
  /**
   * Returns the current mouse mode.
   */
  public MOUSE_MODE getMouseMode(){
    return _mmode;
  }
  /**
   * Adds a SViewerSelectionListener to this viewer.
   */
  public void addSViewerSelectionListener(SViewerSelectionListener l) {
    _sLiseteners.addSViewerSelectionListener(l);
  }

  /**
   * Removes a SViewerSelectionListener from this viewer.
   */
  public void removeSViewerSelectionListener(SViewerSelectionListener l) {
    _sLiseteners.removeSViewerSelectionListener(l);
  }
  public Dimension getPreferredSize(){
    Dimension dim;
    int       width = 0, height = 0;

    for(DrawingLane dl : _dLanesToDisplay){
      dim = dl.getPreferredSize();
      //get the widest component
      if (dim.width>width)
        width = dim.width;
      //get the total height
      height += dim.height;
    }
    dim = new Dimension(width, height);
    return dim;
  }
  protected int getPanelHeight(){
    Dimension dim;
    int       height = 0;

    for(DrawingLane dl : _dLanesToDisplay){
      dim = dl.getPreferredSize();
      //get the total height
      height += dim.height;
    }
    return height;
  }
  public Dimension getMaximumSize(){
    return this.getPreferredSize();
  }
  public Dimension getMinimumSize(){
    return this.getPreferredSize();
  }
  /**
   * Sets the left and right margin widths of this viewer.
   */
  public void setMargins(int leftMargin, int rightMargin) {
    for(DrawingLane dl : _dLanesDataModel){
      dl.setLeftMargin(leftMargin);
      dl.setRightMargin(rightMargin);
    }
  }

  /**
   * This method is used to extend or reduce the width of the drawing lanes.
   */
  public void setWidth(int width){
    Dimension dim;

    for(DrawingLane dl : _dLanesDataModel){
      dim = dl.getPreferredSize();
      dim.width = width;
    }
  }
  /**
   * Draws the selection zone.
   */
  private void drawSelection(Graphics g){
    int x1, x2;

    if (_startSelectionPos.x<0 || _stopSelectionPos.x<0)
      return;

    x1 = Math.min(_startSelectionPos.x, _stopSelectionPos.x);
    x2 = Math.max(_startSelectionPos.x, _stopSelectionPos.x);
    g.setColor(ZOMMER_CLR);
    g.fillRect(x1, 0, x2-x1+1, this.getBounds().height);
    g.setColor(Color.BLACK);
  }
  public void paintComponent(Graphics g){
    super.paintComponent(g);

    boolean bRet;

    synchronized (this) {
      bRet = _dLanesToDisplay.isEmpty();
    }
    if (bRet)
      return;
    Rectangle paintBounds = g.getClipBounds();
    Rectangle laneBounds, visibleRect;
    Dimension dim;
    int       height = 0;

    visibleRect = this.getVisibleRect();
    laneBounds = new Rectangle();
    laneBounds.x = visibleRect.x; 
    laneBounds.width = visibleRect.width; 
    for(DrawingLane dl : _dLanesToDisplay){
      dim = dl.getPreferredSize();
      laneBounds.y = height; 
      laneBounds.height = dim.height;
      g.setClip(laneBounds.x, laneBounds.y, laneBounds.width,
          laneBounds.height);
      g.clipRect(paintBounds.x, paintBounds.y, paintBounds.width,
          paintBounds.height);
      dl.paintLane((Graphics2D)g, laneBounds);
      height+=dim.height;
    }
    g.setClip(paintBounds.x, 0, paintBounds.width, height);
    g.clipRect(paintBounds.x, 0, paintBounds.width, height);
    drawSelection(g);
  }
  /**
   * Returns the drawing lane index given a mouse location. Returns -1 if that location is
   * not located within a drawing lane.
   */
  private int getClickedLane(int x, int y){
    Rectangle laneBounds;
    Dimension dim;
    int       height = 0, lane = 0;

    laneBounds = new Rectangle(0, 0, 0, 0);
    for(DrawingLane dl : _dLanesToDisplay){
      dim = dl.getPreferredSize();
      laneBounds.x = 0; laneBounds.y = height; laneBounds.width = dim.width; laneBounds.height = dim.height;
      if (laneBounds.contains(x, y)){
        return lane;
      }
      height+=dim.height;
      lane++;
    }
    return -1;
  }
  /**
   * Internal class used to handle mouse actions.
   */
  private class DataMouseListener extends MouseAdapter{
    private void resetPoint(){
      _startSelectionPos.x = 
          _startSelectionPos.y = 
          _stopSelectionPos.x  =
          _stopSelectionPos.y  = -1;
    }
    private void doZoom(MouseEvent me){
      Dimension   dim;
      DrawingLane lane;
      Rectangle   r;
      int         oldWidth, pWidth, delta, lastPos, zoomFact, position, parentWidth, curWidth, h;
      double      xFactor;

      //all drawing lanes have same xFactor and left/right margins if any
      lane = _dLanesDataModel.get(0);
      xFactor = lane.computeScaleFactor();
      oldWidth = curWidth = lane.getPreferredSize().width-(lane.getLeftMargin()+lane.getRightMargin());

      //current sequence drawing viewport area
      pWidth = CartoViewerPanel.this.getVisibleRect().width;
      //selected drawing area
      lastPos = me.getX();
      delta = Math.abs(_startSelectionPos.x-me.getX());

      //current position on the sequence
      position = lane.getRulerPositionAt(lastPos-delta/2);
      if (delta<2){//Mouse does not move a lot
        zoomFact = 5;
      }
      else{
        zoomFact = pWidth / delta;
      }
      if (zoomFact<1)
        zoomFact = 1;

      if ((me.getModifiers() & InputEvent.BUTTON3_MASK) != 0){
        curWidth /= zoomFact; //right click: zoom out
      }
      else{
        curWidth *= zoomFact;//zoom in
      }
      parentWidth = lane.getMinimumSize().width;
      if (curWidth<parentWidth)
        curWidth = parentWidth;
      parentWidth = lane.getMaximumSize().width; 
      if (curWidth>parentWidth){
        curWidth = parentWidth;
      }
      if (oldWidth==curWidth){
        resetPoint();
        CartoViewerPanel.this.repaint();
        return;
      }
      for(DrawingLane dl : _dLanesDataModel){
        dim = dl.getPreferredSize();
        dim.width = curWidth;
      }
      lane = _dLanesDataModel.get(0);
      xFactor = lane.computeScaleFactor();
      h = CartoViewerPanel.this.getSize().height;
      CartoViewerPanel.this.setSize(new Dimension(curWidth, h));
      CartoViewerPanel.this.revalidate();
      parentWidth = CartoViewerPanel.this.getParent().getWidth();
      r = new Rectangle(
          lane.getLeftMargin() + (int)(xFactor * (double) position) 
          - (parentWidth/2),
          0,
          100,
          CartoViewerPanel.this.getSize().height);
      //it appears that the scroll is not yet fully updated: need to do it here!
      JScrollBar hBar = ((JScrollPane)CartoViewerPanel.this.getParent().getParent()).getHorizontalScrollBar();
      hBar.setMaximum(curWidth);
      hBar.setValue(r.x);
      hBar.setUnitIncrement(10);
      resetPoint();
      CartoViewerPanel.this.repaint();
    }
    private Object getClickedObject(Point p){
      Object obj = null;
      int    lane = getClickedLane(p.x, p.y);
      if (lane!=-1){
        obj = _dLanesToDisplay.get(lane).getClickedObject(p.x);
      }
      return obj;
    }
    private Object getObjectFromSelection(Point pFrom, Point pTo){
      Object obj = null;
      int    lane1, lane2;

      lane1 = getClickedLane(pFrom.x, pFrom.y);
      lane2 = getClickedLane(pTo.x, pTo.y);
      if (lane1!=lane2){
        return null;
      }
      obj = _dLanesToDisplay.get(lane1).getObjectFromSection(
          Math.min(pFrom.x, pTo.x), Math.max(pFrom.x, pTo.x));
      /*if (obj!=null){
				System.out.println("Object selected on lane "+lane1+" in range ["+
						(_dLanes.get(lane1).getRulerPositionAt(pFrom.x)+1)+","+
						(_dLanes.get(lane1).getRulerPositionAt(pTo.x)+1)+"]: "+obj);
			}*/
      return obj;
    }
    public void mousePressed(MouseEvent me){
      _startSelectionPos.x = _stopSelectionPos.x = me.getX();
      _startSelectionPos.y = _stopSelectionPos.y = me.getY();
      CartoViewerPanel.this.repaint();
    }
    public void mouseReleased(MouseEvent me){
      DrawingLane dl;
      Object      obj = null;

      if (_mmode==MOUSE_MODE.SELECTION){
        if (Math.abs(_stopSelectionPos.x-_startSelectionPos.x+1)>3){
          obj = getObjectFromSelection(_startSelectionPos, _stopSelectionPos);
        }
        else{
          obj = getClickedObject(me.getPoint());
        }
        if (obj!=null){
          dl = _dLanesToDisplay.get(getClickedLane(_stopSelectionPos.x, _stopSelectionPos.y));
          _sLiseteners.fireSelectionEvent(
              new SViewerSelectionEvent(
                  dl,
                  SViewerSelectionEvent.SEL_TYPE.OBJECT_WITH_RANGE,
                  obj,
                  dl.getRulerPositionAt(Math.min(_startSelectionPos.x, _stopSelectionPos.x)),
                  dl.getRulerPositionAt(Math.max(_startSelectionPos.x, _stopSelectionPos.x))));
        }
        else{
          _sLiseteners.fireSelectionEvent(
              new SViewerSelectionEvent(
                  CartoViewerPanel.this, SViewerSelectionEvent.SEL_TYPE.EMPTY, null, -1, -1));
        }
        resetPoint();
        CartoViewerPanel.this.repaint();
      }
      else if (_zoomable && _mmode==MOUSE_MODE.ZOOM){
        doZoom(me);
      }
      else{
        resetPoint();
        CartoViewerPanel.this.repaint();
      }
    }
  }
  private class DataMouseMotionListener extends MouseMotionAdapter {
    public void mouseDragged (MouseEvent me){
      _stopSelectionPos.x = me.getX();
      _stopSelectionPos.y = me.getY();

      Rectangle visibleRect = CartoViewerPanel.this.getVisibleRect();
      int       from = visibleRect.x;
      int       to = visibleRect.x + visibleRect.width;
      if (_stopSelectionPos.x>to || _stopSelectionPos.x<from){
        CartoViewerPanel.this.scrollRectToVisible(new Rectangle(_stopSelectionPos.x, 0, 1, 1));
      }
      else{
        CartoViewerPanel.this.repaint();
      }
    }
  }
  /**
   * Returns the component that can be used to display the left margin labels.
   */
  public JComponent getLaneHeader(int headerWidth){
    if (_jHeader!=null)
      return _jHeader;
    _jHeader = new LaneHeaderList(new LaneHeaderListModel(_dLanesToDisplay));
    _jHeader.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    _jHeader.setCellRenderer(new MyRenderer());
    _jHeader.setFixedCellWidth(headerWidth);
    _jHeader.setFont(DrawingLaneBase.DEF_FNT);
    return _jHeader;
  }
  private class LaneHeaderList extends JList<String>{
    /**
     * 
     */
    private static final long serialVersionUID = -1033844959295952191L;

    public LaneHeaderList(ListModel<String> dm) {
      super(dm);
    }
  }
  private class LaneHeaderListModel extends AbstractListModel<String>{
    private static final long serialVersionUID = 9141489071961551091L;

    public LaneHeaderListModel(List<DrawingLane> dLanes){
      updateModel(dLanes);
    }
    public DrawingLane getLane(int i){
      if (_dLanesToDisplay==null)
        return null;
      return _dLanesToDisplay.get(i);
    }
    public void updateModel(List<DrawingLane> dLanes){
      //_dl = dLanes;
    }
    public int getSize() {
      if (_dLanesToDisplay==null)
        return 0;
      return _dLanesToDisplay.size();
    }

    public String getElementAt(int index) {
      String val;

      val = _dLanesToDisplay.get(index).getLeftLabel();
      if (val==null)
        val = "";
      return val;
    }
  }
  //renderer mainly used to allow variable row heights
  private class MyRenderer extends DefaultListCellRenderer{  
    /**
     * 
     */
    private static final long serialVersionUID = 913813598267053481L;

    public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list,Object value,  
        int index,boolean isSelected,boolean cellHasFocus) {  
      JLabel lbl = (JLabel)super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);  
      LaneHeaderListModel lhtm = (LaneHeaderListModel) list.getModel();
      DrawingLane dl = lhtm.getLane(index);

      if (dl!=null)
        lbl.setPreferredSize(new Dimension(dl.getPreferredSize().width,dl.getPreferredSize().height));
      else
        lbl.setPreferredSize(new Dimension(100,16));
      lbl.setHorizontalAlignment(SwingConstants.RIGHT);
      return lbl;  
    }
  }
  /*
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(640, 480);
	}
	@Override
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return 50;
	}
	@Override
	public boolean getScrollableTracksViewportHeight() {
		if (getParent() instanceof JViewport){
			return ((JViewport)getParent()).getHeight() > getPreferredSize().height ;
		}
		return false;
	}
	@Override
	public boolean getScrollableTracksViewportWidth() {
		if (getParent() instanceof JViewport){
			return ((JViewport)getParent()).getWidth() > getPreferredSize().width ;
		}
		return false;
	}
	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 1;
	}
   */
}
