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
package bzh.plealog.bioinfo.ui.carto.event;

import javax.swing.event.EventListenerList;

/**
 * This class can be used to handle sequence selection messages between several
 * sequence viewers.
 * 
 * @author Patrick G. Durand
 * */
public class SViewerListenerSupport {
  private EventListenerList _listenerList;

  /**
   * Default constructor.
   */
  public SViewerListenerSupport(){
    _listenerList = new EventListenerList();
  }
  /**
   * Adds a SViewerSelectionListener on this viewer.
   */
  public void addSViewerSelectionListener(SViewerSelectionListener l) {
    _listenerList.add(SViewerSelectionListener.class, l);
  }

  /**
   * Removes a SViewerSelectionListener from this viewer.
   */
  public void removeSViewerSelectionListener(SViewerSelectionListener l) {
    _listenerList.remove(SViewerSelectionListener.class, l);
  }
  /**
   * Fire a selection event.
   */
  public void fireSelectionEvent(SViewerSelectionEvent event) {
    Object[] listeners = _listenerList.getListenerList();
    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==SViewerSelectionListener.class) {
        ((SViewerSelectionListener)listeners[i+1]).objectSelected(event);
      }
    }
  }

}
