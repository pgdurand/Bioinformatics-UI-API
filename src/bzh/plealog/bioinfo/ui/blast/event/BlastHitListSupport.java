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
 * Defines a notification system for the Blast Hit Viewer.
 *
 * @author Patrick G. Durand
 */
public class BlastHitListSupport {

  protected EventListenerList _listenerList;

  public BlastHitListSupport() {
    _listenerList = new EventListenerList();
  }

  public void fireHitChange(BlastHitListEvent mge) {
    // Guaranteed to return a non-null array
    Object[] listeners = _listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == BlastHitListListener.class) {
        ((BlastHitListListener) listeners[i + 1]).hitChanged(mge);
      }
    }
  }

  public void addBlastHitListListener(BlastHitListListener listener) {
    if (listener == null)
      return;
    _listenerList.add(BlastHitListListener.class, listener);
  }

  public void removeBlastHitListListener(BlastHitListListener listener) {
    if (listener == null)
      return;
    _listenerList.remove(BlastHitListListener.class, listener);
  }
}
