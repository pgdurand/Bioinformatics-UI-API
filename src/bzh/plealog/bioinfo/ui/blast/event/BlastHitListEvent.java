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
import java.util.List;

import bzh.plealog.bioinfo.ui.blast.core.BlastHitHSP;

/**
 * This event is fired to notify about the selection of a some Blast Hits.
 * 
 * @author Patrick G. Durand
 *
 */
public class BlastHitListEvent extends EventObject {
  private static final long serialVersionUID = -3994004831520249000L;

  public static final int HIT_CHANGED = 1;

  protected int _type;
  protected List<BlastHitHSP> _hitHsp;

  /**
   * Constructor.
   * 
   * @param src
   *          source of this event
   * @param hitHsp
   *          the BlastHitHsp concerned by this event
   * @param type
   *          the type of event. See HIT_XXX constants from this class.
   */
  public BlastHitListEvent(Object src, List<BlastHitHSP> hitHsp, int type) {
    super(src);
    _type = type;
    _hitHsp = hitHsp;
  }

  public List<BlastHitHSP> getHitHsps() {
    return _hitHsp;
  }

  public int getType() {
    return _type;
  }
}
