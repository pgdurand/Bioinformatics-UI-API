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
package bzh.plealog.bioinfo.ui.hca;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import bzh.plealog.bioinfo.api.data.sequence.DLocation;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DViewerSystem;
import bzh.plealog.bioinfo.ui.sequence.event.DDSelectionRange;
import bzh.plealog.bioinfo.ui.sequence.event.DDSequenceViewerConn;
import bzh.plealog.bioinfo.ui.sequence.event.DSelectionListenerSupport;
import bzh.plealog.bioinfo.ui.sequence.event.DSequenceSelectionEvent;
import bzh.plealog.bioinfo.ui.sequence.event.DSequenceSelectionListener;
import bzh.plealog.bioinfo.ui.util.ContextMenuManager;
/**
 * This class adds event handling stuff to the basic HCA viewer panel PanelHCA.
 * 
 * @author Patrick G. Durand
 */
public class DDPanelHCA extends PanelHca implements DDSequenceViewerConn, DSequenceSelectionListener{
  private static final long serialVersionUID = -7582219723036182626L;
  private DSequence                 _sequence;
  private TDMouseListener           _mListener;
  private DSelectionListenerSupport _lSupport;
  private JTextArea                 _positionF;
  private ContextMenuManager        _mnuManager;

  /**
   * Default constructor.
   */
  public DDPanelHCA(){
    super();
    _mListener = new TDMouseListener();
    this.addMouseListener(_mListener);
    this.addMouseMotionListener(_mListener);

  }
  /**
   * Register the component used by this viewer to broadcast selection events.
   */
  public void registerSelectionListenerSupport(DSelectionListenerSupport lSupport){
    _lSupport = lSupport;
    _lSupport.addDSequenceSelectionListener(this);
  }
  /**
   * Implementation of DDSequenceViewerConn.
   */
  public void setSequence(DSequence sequence){
    _sequence = sequence;
    if (sequence==null){
      init(null);
      _mListener.setSequence(0);
    }
    else{
      init(new DDBioSeq(sequence));
      _mListener.setSequence(sequence.size());
    }
    if (_positionF!=null)
      _positionF.setText("");
  }
  /**
   * Implementation of DDSequenceViewerConn.
   */
  public DSequence getSequence(){
    return _sequence;
  }
  /**
   * Implementation of DDSequenceViewerConn.
   */
  public DSequence getSelectedSequence(){
    List<DLocation> lst;
    DLocation       loc;
    StringBuffer    buf;
    DSequence       seq, curSeq;
    int             i, size;

    lst = getSelectedRanges();
    if (lst==null)
      return null;
    curSeq = this.getSequence();
    if (curSeq==null)
      return null;
    size = lst.size();
    buf = new StringBuffer();
    for(i=0;i<size;i++){
      loc = lst.get(i);
      buf.append(curSeq.getSubSequence(loc.getFrom(), loc.getTo()+1, false));
    }
    seq = DViewerSystem.getSequenceFactory().getSequence(new StringReader(buf.toString()), curSeq.getAlphabet());
    seq.createRulerModel(1, 1);
    seq.setSequenceInfo(curSeq.getSequenceInfo());
    return seq;
  }
  /**
   * Implementation of DDSequenceViewerConn.
   */
  public void setSelectedSequenceRange(int from, int to){
    if (_sequence==null)
      return;
    if (from==-1 && to==-1){
      _mListener.resetSelection();
      this.repaint();
      return;
    }
    from = Math.max(0, from);
    to = Math.min(_sequence.size()-1, to);
    this.repaint();
    _mListener.setSelectedSequence(from, to);
  }
  /**
   * Implementation of DDSequenceViewerConn.
   */
  public void setSelectionRanges(List<DLocation> locs){
    DLocation loc;
    int       i, size;

    size = locs.size();
    for(i=0;i<size;i++){
      loc = locs.get(i);
      if (i==0){
        _mListener.setSelectedSequence(loc.getFrom(), loc.getTo());
      }
      else{
        _mListener.addSelectedSequence(loc.getFrom(), loc.getTo());
      }
    }
    this.repaint();
  }
  /**
   * Implementation of DDSequenceViewerConn.
   */
  public List<DLocation> getSelectedRanges(){
    return _mListener.getSelectedRanges();
  }

  /**
   * Implementation of DDSequenceViewerConn.
   */
  public int[] getSelectedSequenceRange(){
    int from, to;

    from = _mListener.getStartSelection();
    to = _mListener.getStopSelection();
    if (from==-1 || to==-1)
      return null;
    else
      return new int[]{from, to};
  }
  /**
   * Sets the field displaying the mouse position mapped in sequence coordinate. Use this
   * field to display that information wherever you want.
   */
  public void setPositionField(JTextArea positionF){
    _positionF = positionF;
  }
  /**
   * Sets a contextual popup menu to this viewer.
   */
  public void setContextMenu(ContextMenuManager contextMnu){
    if (contextMnu!=null){
      contextMnu.setParent(this);
    }
    _mnuManager = contextMnu;
  }
  protected boolean isAASelected(int idx){
    return _mListener.isCellMySelected(idx);
  }
  /**
   * Implementation of DSequenceSelectionListener.
   */
  public void selectionChanged(DSequenceSelectionEvent event){
    if (event.getSource() == this){
      return;
    }
    else{
      if (event.getSelectionType()==DSequenceSelectionEvent.SIMPLE_RANGE)
        setSelectedSequenceRange(event.getSelFrom(), event.getSelTo());
      else
        setSelectionRanges(event.getLocs());
    }
  }
  /**
   * This class has been designed to overload the standard JTable selection system that only allows 
   * rectangular selection of cells. This selection system behaves as a standard sequence viewers
   * to extend selection of cells (i.e. letters) through table rows.
   */
  private class TDMouseListener implements MouseListener, MouseMotionListener {
    private DDSelectionRange previousRange;//use to manage mouse selection
    private DDSelectionRange currentRange;//use to manage mouse selection
    private BitSet           selectionStates;//keep the selection state
    private int              lastPos, firstPos, seqSize;

    public TDMouseListener(){
      previousRange = new DDSelectionRange();
      currentRange = new DDSelectionRange();
    }
    private void setSequence(int size){
      seqSize = size;
      if (size==0)
        selectionStates = null;
      else
        selectionStates = new BitSet(size);
    }
    /**
     * Figures out if a cell is selected.
     */
    private boolean isCellMySelected(int col){
      if (selectionStates == null)
        return false;
      else
        return selectionStates.get(col);
    }
    /**
     * Gets the global starting position of the selected region. Value is
     * are zero-based and absolute: use the DRulerModel
     * from the DSequence to switch to the sequence coordinate system. If selection
     * is empty, then -1 is returned.
     */
    private int getStartSelection(){
      return selectionStates.nextSetBit(0);
    }
    private int getNextSegment(int start){
      int stop = -1;
      for(int i=start; i>=0; i=selectionStates.nextSetBit(i+1)) { 
        stop=i;
      }
      return stop;
    }
    /**
     * Gets the global ending position of the selected region. Value is
     * zero-based and absolute: use the DRulerModel
     * from the DSequence to switch to the sequence coordinate system.
     */
    private int getStopSelection(){
      int i, stop=-1;

      i = selectionStates.nextSetBit(0);
      if (i==-1)
        return i;
      while(i>=0){
        stop = getNextSegment(i);
        i = selectionStates.nextSetBit(stop+1);
      }
      return stop;
    }
    /**
     * Returns a list of selected segments over the sequence.  Value are
     * zero-based and absolute: use the DRulerModel from the DSequence to 
     * switch to the sequence coordinate system. Returns null if nothing is selected.
     */
    private List<DLocation> getSelectedRanges(){
      ArrayList<DLocation> lst;
      int                  i, stop=-1;

      i = selectionStates.nextSetBit(0);
      if (i==-1)
        return null;
      lst = new ArrayList<DLocation>();
      while(i>=0){
        stop = selectionStates.nextClearBit(i+1)-1;
        lst.add(new DLocation(i, stop));
        i = selectionStates.nextSetBit(stop+1);
      }
      return lst;
    }
    /**
     * Clears the current selected region if any.
     */
    private void resetSelection(){
      if (selectionStates==null)
        return;
      selectionStates.clear();
      previousRange.reset();
      currentRange.reset();
    }
    /**
     * Sets a selection on the viewer. Values are zero-based and absolute: use 
     * the DRulerModel from the DSequence to switch to the sequence coordinate 
     * system.
     */
    private void setSelectedSequence(int from, int to){
      if (selectionStates==null)
        return;
      previousRange.reset();
      currentRange.reset();
      selectionStates.clear();
      if (from>=selectionStates.size()-1)
        return;
      if (to>=selectionStates.size()-1)
        return;
      if (from!=-1 && to!=-1){
        if (from==to)
          selectionStates.set(from);
        else
          selectionStates.set(Math.min(from, to), Math.max(from, to)+1, true);
        DDPanelHCA.this.scrollRectToVisible(getVisibleRect(from));
      }
      DDPanelHCA.this.repaint();
    }
    private void addSelectedSequence(int from, int to){
      if (selectionStates==null)
        return;
      if (from>=selectionStates.size()-1)
        return;
      if (to>=selectionStates.size()-1)
        return;
      if (from==to)
        selectionStates.set(from);
      else
        selectionStates.set(Math.min(from, to), Math.max(from, to)+1, true);
      DDPanelHCA.this.repaint();
    }

    public void mousePressed(MouseEvent e){
      Point pt;
      int   max;
      if (seqSize==0)
        return;
      if (SwingUtilities.isRightMouseButton(e)){
        return;
      }
      pt = e.getPoint();
      //extended selection with shift keyboard key
      if ((e.getModifiers() & MouseEvent.SHIFT_MASK)!=0){
        currentRange.startCol = previousRange.startCol;
        currentRange.lastCol = getPos(pt);
      }
      else{
        currentRange.startCol = currentRange.lastCol = getPos(pt);
      }
      firstPos = currentRange.lastCol;
      max = seqSize - 1;
      if (firstPos > max)
        firstPos = max;
      DDPanelHCA.this.repaint();
    }

    public void mouseReleased(MouseEvent e){
      if (seqSize==0)
        return;
      if (SwingUtilities.isRightMouseButton(e)){
        if (_mnuManager!=null)
          _mnuManager.showContextMenu(e.getX(), e.getY());
        return;
      }
      if (currentRange.equals(previousRange)){
        selectionStates.clear();
        currentRange.reset();
        previousRange.reset();
      }
      else{
        previousRange.set(currentRange);
        updateSelectionStates();
      }
      firstPos = lastPos = -1;
      DDPanelHCA.this.repaint();
      fireSelectionEvent();
    }
    private void updateSelectionStates(){
      //store selection in the bitset
      selectionStates.clear();
      int from = currentRange.startCol;
      int to = currentRange.lastCol;
      if (from<0 || to<0)
        return;
      int a, b;
      a = Math.min(from, to);
      b = Math.max(from, to);
      if (b>=seqSize)
        b = seqSize-1;
      selectionStates.set(a, b+1, true);
    }
    public void mouseDragged(MouseEvent e){
      Point                pt;

      if (SwingUtilities.isRightMouseButton(e)){
        return;
      }
      if(currentRange.startCol==-1)
        return;
      pt = e.getPoint();
      currentRange.lastCol = getPos(pt);
      displayMouseLocation(e, true);
      updateSelectionStates();
      DDPanelHCA.this.repaint();
    }
    private void fireSelectionEvent(){
      if (_lSupport!=null){
        _lSupport.setSelectedSequenceRange(
            DDPanelHCA.this, 
            DDPanelHCA.this.getSequence(), 
            getStartSelection(), 
            getStopSelection());
      }
    }
    private void displayMouseLocation(MouseEvent e, boolean displayRange){
      Point                pt;
      DSequence            seq;
      int                  row, col, pos;

      if (_positionF==null)
        return;
      seq = DDPanelHCA.this.getSequence();
      if (seq==null || seq.getRulerModel()==null)
        return;
      pt = e.getPoint();
      pos = getPos(pt);
      if (pos==lastPos)
        return;
      lastPos = pos;
      if (pos>=0 &&pos<seq.size()){
        if (displayRange){
          row = Math.min(firstPos, pos);
          col = Math.max(firstPos, pos);
          _positionF.setText(
              String.valueOf(seq.getRulerModel().getSeqPos(row))+" -\n"+
                  String.valueOf(seq.getRulerModel().getSeqPos(col)));
        }
        else{
          _positionF.setText(String.valueOf(seq.getRulerModel().getSeqPos(pos)));
        }
      }
      else{
        _positionF.setText("");
      }	

    }
    public void mouseMoved(MouseEvent e){
      displayMouseLocation(e, false);
    }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){
      if (_positionF!=null)
        _positionF.setText("");
    }
    public void mouseClicked(MouseEvent e){}

  }

}
