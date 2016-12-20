/* Copyright (C) 2003-2016 Patrick G. Durand
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
package bzh.plealog.bioinfo.ui.blast.event;

import javax.swing.event.EventListenerList;

/**
 * Defines a notification system for the BlastIteration viewer.
 *
 * @author Patrick G. Durand
 */
public class BlastIterationListSupport {

  protected EventListenerList _listenerList;

  public BlastIterationListSupport() {
    _listenerList = new EventListenerList();
  }

  public void fireHitChange(BlastIterationListEvent mge) {
    // Guaranteed to return a non-null array
    Object[] listeners = _listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == BlastIterationListListener.class) {
        ((BlastIterationListListener) listeners[i + 1]).iterationChanged(mge);
      }
    }
  }

  public void addBlastIterationListListener(BlastIterationListListener listener) {
    if (listener == null)
      return;
    _listenerList.add(BlastIterationListListener.class, listener);
  }

  public void removeBlastIterationListListener(BlastIterationListListener listener) {
    if (listener == null)
      return;
    _listenerList.remove(BlastIterationListListener.class, listener);
  }
}
