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
package bzh.plealog.bioinfo.ui.sequence.event;

import java.util.List;

import javax.swing.event.EventListenerList;

import bzh.plealog.bioinfo.api.data.sequence.DLocation;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;

/**
 * This class can be used to handle sequence selection messages between several
 * sequence viewers.
 * 
 * @author Patrick G. Durand
 * */
public class DSelectionListenerSupport {
  private EventListenerList _listenerList;

  /**
   * Default constructor.
   */
  public DSelectionListenerSupport(){
    _listenerList = new EventListenerList();
  }
  /**
   * Select a range of residues.
   * 
   * @param src the object that emitted the selection message
   * @param seq the sequence on which the selection has been made
   * @param from starting position
   * @param to ending position
   */
  public void setSelectedSequenceRange(Object src, DSequence seq, int from, int to){
    fireDSequenceSelectionEvent(new DSequenceSelectionEvent(
        src, seq, from, to));
  }
  /**
   * Select a list of ranges.
   * 
   * @param src the object that emitted the selection message
   * @param seq the sequence on which the selection has been made
   * @param locs the list of ranges that were selected
   */
  public void setSelectionRanges(Object src, DSequence seq, List<DLocation> locs){
    if (locs.size()==1){
      fireDSequenceSelectionEvent(new DSequenceSelectionEvent(
          src, seq, locs.get(0).getFrom(), locs.get(0).getTo()));
    }
    else{
      fireDSequenceSelectionEvent(new DSequenceSelectionEvent(
          src, seq, locs));
    }
  }
  /**
   * Adds a DSequenceSelectionListener on this viewer.
   */
  public void addDSequenceSelectionListener(DSequenceSelectionListener l) {
    _listenerList.add(DSequenceSelectionListener.class, l);
  }

  /**
   * Removes a DSequenceSelectionListener from this viewer.
   */
  public void removeDSequenceSelectionListener(DSequenceSelectionListener l) {
    _listenerList.remove(DSequenceSelectionListener.class, l);
  }
  /**
   * Fire a selection event.
   */
  protected void fireDSequenceSelectionEvent(DSequenceSelectionEvent event) {
    Object[] listeners = _listenerList.getListenerList();
    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==DSequenceSelectionListener.class) {
        ((DSequenceSelectionListener)listeners[i+1]).selectionChanged(event);
      }
    }
  }

}
