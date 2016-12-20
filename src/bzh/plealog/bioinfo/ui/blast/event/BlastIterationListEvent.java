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

import java.util.EventObject;

import bzh.plealog.bioinfo.ui.blast.core.BlastIteration;

/**
 * This event is fired by a component displaying a list of BlastIterations
 * contained in a BlastOutput.
 * 
 * @author Patrick G. Durand
 *
 */
public class BlastIterationListEvent extends EventObject {

  private static final long serialVersionUID = -6139337144317561297L;

  public static final int ITERATION_CHANGED = 1;

  protected int _type;
  protected BlastIteration _iteration;

  /**
   * Constructor.
   * 
   * @param list
   *          source of this event
   * @param it
   *          the BlastIteration concerned by this event
   * @param type
   *          the type of event. See ITERATION_XXX constants from this class.
   */
  public BlastIterationListEvent(Object list, BlastIteration it, int type) {
    super(list);
    _type = type;
    _iteration = it;
  }

  public BlastIteration getBlastIteration() {
    return _iteration;
  }

  public int getType() {
    return _type;
  }
}
