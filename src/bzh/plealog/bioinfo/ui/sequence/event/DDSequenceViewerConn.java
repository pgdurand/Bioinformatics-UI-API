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

import bzh.plealog.bioinfo.api.data.sequence.DLocation;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;

public interface DDSequenceViewerConn {
  /**
   * Sets the sequence to be displayed by this viewer. Pass in null to reset the viewer content.
   */
  public void setSequence(DSequence sequence);
  /**
   * Returns the sequence currently displayed by this viewer.
   */
  public DSequence getSequence();
  /**
   * Returns the selected region of the sequence currently displayed by this viewer.
   * Returns null if nothing is selected.
   */
  public DSequence getSelectedSequence();
  /**
   * Sets the selected region of the sequence displayed in the viewer.
   * Values have to be zero-based and absolute, so use the DSequence DRulerModel to get
   * an absolute position from a sequence coordinate. Set from and to to -1 to reset
   * selection.
   */
  public void setSelectedSequenceRange(int from, int to);
  /**
   * Sets a list of selected segments over the sequence.  Value are
   * zero-based and absolute: use the DRulerModel from the DSequence to 
   * switch to the sequence coordinate system. 
   */
  public void setSelectionRanges(List<DLocation> locs);
  /**
   * Returns a list of selected segments over the sequence.  Value are
   * zero-based and absolute: use the DRulerModel from the DSequence to 
   * switch to the sequence coordinate system. Returns null if nothing is selected.
   */
  public List<DLocation> getSelectedRanges();

  /**
   * Returned the global selected region of the sequence displayed in the viewer.
   * The method returns null if nothing is selected, otherwise the array contains
   * the selected region. Index zero contains from and index one contains to. Values
   * are zero-based and absolute, so use the DSequence DRulerModel to get
   * an from a sequence coordinate from an absolute position.
   */
  public int[] getSelectedSequenceRange();

}
