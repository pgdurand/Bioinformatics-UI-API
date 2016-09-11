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
package test;

import java.awt.Dimension;
import java.io.StringReader;

import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bzh.plealog.bioinfo.api.data.sequence.DAlphabet;
import bzh.plealog.bioinfo.api.data.sequence.DRulerModel;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSequenceModel;
import bzh.plealog.bioinfo.api.data.sequence.DSymbol;
import bzh.plealog.bioinfo.api.data.sequence.DViewerSystem;
import bzh.plealog.bioinfo.ui.sequence.basic.DRulerViewer;
import bzh.plealog.bioinfo.ui.sequence.basic.MyListSelectionModel;
import bzh.plealog.bioinfo.ui.sequence.event.DSequenceSelectionEvent;
import bzh.plealog.bioinfo.ui.sequence.event.DSequenceSelectionListener;

/**
 * This is a convenient class wrapping a DSequenceViewer and
 * implements DScrollable interface.
 * 
 * @author Patrick G. Durand
 */
public class DSequenceViewerTest extends JPanel {
  private static final long serialVersionUID = 2282431812087029197L;
  private DSequenceListViewerTest _list;
  private DRulerViewer            _ruler;
  private EventListenerList       _listenerList;

  /**
   * No default constructor allowed
   */
  private DSequenceViewerTest(){
    _listenerList = new EventListenerList();
  }

  /**
   * Creates a new viewer panel.
   * 
   * @param list the viewer
   * @param reverse sets to true, the ruler will be displayed on top of
   * the sequence. Sets to false, the ruler will be displayed at the bottom of
   * the sequence.
   */
  public DSequenceViewerTest(DSequenceListViewerTest list, DRulerViewer ruler, boolean reverse){
    this();
    _list = list;
    _list.getColumnModel().getSelectionModel().addListSelectionListener(new MyListSelectionListener());
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
  public void setModel(DSequenceModelTest seqModel, int rulerStartPos, int increment){
    _list.setModel(seqModel);
    if (_ruler!=null)
      _ruler.setRulerModel(seqModel.getSequence().createRulerModel(rulerStartPos, increment));
  }
  public void setModel(DSequenceModelTest seqModel){
    _list.setModel(seqModel);
    if (_ruler!=null)
      _ruler.setRulerModel(seqModel.getSequence().getRulerModel());
  }
  /**
   * Returns the current sequence viewer component.
   */
  public DSequenceListViewerTest getSequenceList(){
    return _list;
  }
  /**
   * Returns the DSequence made of selected symbols. Returns null if nothing is
   * selected on the sequence.
   */
  public DSequence getSelectedSequence(){
    StringBuffer buf;
    DSequence    seq, curSeq;
    DAlphabet    alphabet;
    DSymbol      symbol, gap;
    String       chain;
    int[]        indices;
    int          i;

    indices = _list.getSelectedIndices();
    if (indices.length==0)
      return null;
    curSeq = ((DSequenceModel)_list.getModel()).getSequence();
    alphabet = curSeq.getAlphabet();
    buf = new StringBuffer();
    gap = alphabet.getSymbol(DSymbol.GAP_SYMBOL_CODE);
    for(i=0;i<indices.length;i++){
      symbol = curSeq.getSymbol(indices[i]);
      if (!symbol.equals(gap)){
        buf.append(symbol.getChar());
      }
    }
    chain = buf.toString();
    if (chain.length()==0)
      return null;
    seq = DViewerSystem.getSequenceFactory().getSequence(new StringReader(chain), alphabet);

    return seq;
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

  protected void fireFeatureSelectionEvent(DSequenceSelectionEvent event){
    Object[] observers = _listenerList.getListenerList();
    int i;
    for (i = observers.length - 2; i >= 0; i -= 2) {
      if (observers[i] == DSequenceSelectionListener.class) {
        ((DSequenceSelectionListener)observers[i+1]).selectionChanged(event);
      }
    }
  }

  /**
   * Registers a Sequence Selection Listener.
   */
   public void addDSequenceSelectionListener(DSequenceSelectionListener listener){
     if (listener!=null)
       _listenerList.add(DSequenceSelectionListener.class, listener);
   }

   /**
    * Removes a Sequence Selection Listener.
    */
   public void removeDSequenceSelectionListener(DSequenceSelectionListener listener){
     if (listener!=null)
       _listenerList.remove(DSequenceSelectionListener.class, listener);
   }

   private class MyListSelectionListener implements ListSelectionListener{
     @SuppressWarnings("unused")
     public void valueChanged(ListSelectionEvent event) {
       if (event.getValueIsAdjusting())
         return;
       DSequence          dseq;
       DRulerModel        rModel;
       int                idxFrom, idxTo, seqFrom, seqTo;
       ListSelectionModel collsm = _list.getColumnModel().getSelectionModel();

       if (collsm.isSelectionEmpty()){
         fireFeatureSelectionEvent(new DSequenceSelectionEvent(DSequenceViewerTest.this));
         return;
       }
       else{
         idxFrom = collsm.getMinSelectionIndex();
         idxTo = collsm.getMaxSelectionIndex();
       }
       dseq = ((DSequenceModel)_list.getModel()).getSequence();
       rModel = dseq.getRulerModel();
       if (rModel!=null){
         seqFrom = rModel.getSeqPos(idxFrom);
         seqTo = rModel.getSeqPos(idxTo);
       }
       else{
         seqFrom = seqTo = -1;
       }
       fireFeatureSelectionEvent(new DSequenceSelectionEvent(DSequenceViewerTest.this, dseq, idxFrom, idxTo));
     }
   }
}
