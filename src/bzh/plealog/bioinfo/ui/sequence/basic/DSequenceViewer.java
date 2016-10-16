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

import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bzh.plealog.bioinfo.api.data.sequence.DLocation;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSequenceModel;
import bzh.plealog.bioinfo.ui.sequence.event.DSelectionListenerSupport;
import bzh.plealog.bioinfo.ui.sequence.event.DSequenceSelectionEvent;
import bzh.plealog.bioinfo.ui.sequence.event.DSequenceSelectionListener;

import com.plealog.genericapp.ui.common.ContextMenuManager;

/**
 * This is a convenient class wrapping a DSequenceViewer and
 * implements DScrollable interface.
 * 
 * @author Patrick G. Durand
 */
public class DSequenceViewer extends JPanel implements DSequenceSelectionListener{
  private static final long serialVersionUID = 3465190754406719433L;
  private DSequenceListViewer       _list;
  private DRulerViewer              _ruler;
  private DSelectionListenerSupport _lSupport;
  private boolean                   _bLockSelection;

  /**
   * No default constructor allowed
   */
  private DSequenceViewer(){
  }

  /**
   * Creates a new viewer panel.
   * 
   * @param list the viewer
   * @param reverse sets to true, the ruler will be displayed on top of
   * the sequence. Sets to false, the ruler will be displayed at the bottom of
   * the sequence.
   */
  public DSequenceViewer(DSequenceListViewer list, DRulerViewer ruler, boolean reverse){
    this();
    _list = list;
    _list.getSelectionModel().addListSelectionListener(new MyListSelectionListener());
    _ruler = ruler;
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    if (reverse){
      this.add(list);
      if (ruler!=null)
        this.add(ruler);
    }
    else{
      if (ruler!=null)
        this.add(ruler);
      this.add(list);
    }
  }

  /**
   * Returns the ruler viewer.
   */
  public DRulerViewer getRuler(){
    return (_ruler);
  }

  /**
   * Sets a new sequence model.
   * @param seqModel the new sequence model
   * @param rulerStartPos the starting value of the ruler (sequence coordinate)
   * @param increment the increment of the ruler. It is either 1 or 3 (for translated
   * DNA sequence displayed as protein).
   */
  public void setModel(DSequenceModel seqModel, int rulerStartPos, int increment){
    _list.setModel(seqModel);
    if (_ruler!=null)
      _ruler.setRulerModel(seqModel.getSequence().createRulerModel(rulerStartPos, increment));
  }
  public void setModel(DSequenceModel seqModel){
    _list.setModel(seqModel);
    if (_ruler!=null)
      _ruler.setRulerModel(seqModel.getSequence().getRulerModel());
  }
  /**
   * Returns the current sequence viewer component.
   */
  public DSequenceListViewer getSequenceList(){
    return _list;
  }
  public DSequence getSequence(){
    return _list.getSequence();
  }
  /**
   * Returns the DSequence made of selected symbols. Returns null if nothing is
   * selected on the sequence.
   */
  public DSequence getSelectedSequence(){
    return _list.getSelectedSequence();
  }
  /**
   * Returns the first and last positions of the selection. Values correspond to absolute
   * position within the viewer. Returns null if nothing is selected on the sequence.
   */
  public int[] getSelectedRegion(){
    int[] indices;
    int[] intervals;

    indices = _list.getSelectedIndices();
    if (indices.length==0)
      return null;
    intervals = new int[2];
    intervals[0] = indices[0];
    intervals[1] = indices[indices.length-1];
    return intervals;
  }
  public void setSelectionEnabled(boolean enable){
    if (!enable)
      _list.setSelectionModel(new MyListSelectionModel());
    else
      _list.setSelectionModel(new DefaultListSelectionModel());
  }
  public Dimension getPreferredSize(){
    Dimension dim, dim2;

    dim = _list.getPreferredSize();

    dim2 = new Dimension(dim.width, dim.height);
    if (_ruler!=null){
      dim2.height = dim2.height + _ruler.getPreferredSize().height;
    }
    return dim2;
  }

  public void setContextMenu(ContextMenuManager contextMenu){
    _list.setContextMenu(contextMenu);
  }
  private void setSelectedSequenceRange(int from, int to){
    _bLockSelection = true;
    if (from==-1 && to==-1){
      _list.clearSelection();
    }
    else{
      _list.getSelectionModel().setSelectionInterval(from, to);
    }
    _bLockSelection = false;
  }
  private void setSelectionRanges(List<DLocation> locs){
    DLocation loc;
    int       i, size;

    size = locs.size();
    ListSelectionModel lModel = _list.getSelectionModel();
    _bLockSelection = true;
    lModel.setValueIsAdjusting(true);
    for(i=0;i<size-1;i++){
      loc = locs.get(i);
      if (i==0){
        lModel.setSelectionInterval(loc.getFrom(), loc.getTo());
      }
      else{
        lModel.addSelectionInterval(loc.getFrom(), loc.getTo());
      }
    }
    lModel.setValueIsAdjusting(false);
    loc = locs.get(size-1);
    lModel.addSelectionInterval(loc.getFrom(), loc.getTo());
    _bLockSelection=false;
  }
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
   * Register the component used by this viewer to broadcast selection events.
   */
  public void registerSelectionListenerSupport(DSelectionListenerSupport lSupport){
    _lSupport = lSupport;
    _lSupport.addDSequenceSelectionListener(this);
  }
  private void fireSelectionEvent(int from, int to){
    if (_lSupport!=null){
      _lSupport.setSelectedSequenceRange(
          this, 
          this.getSequence(), 
          from, 
          to);
    }
  }
  private class MyListSelectionListener implements ListSelectionListener{
    public void valueChanged(ListSelectionEvent event) {
      if (event.getValueIsAdjusting())
        return;
      int                idxFrom, idxTo;
      ListSelectionModel collsm = _list.getSelectionModel();

      if (_bLockSelection)
        return;
      if (collsm.isSelectionEmpty()){
        fireSelectionEvent(-1, -1);
        return;
      }
      else{
        idxFrom = collsm.getMinSelectionIndex();
        idxTo = collsm.getMaxSelectionIndex();
      }
      fireSelectionEvent(idxFrom, idxTo);
    }
  }
}
