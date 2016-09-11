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

import java.util.EventObject;
import java.util.List;

import bzh.plealog.bioinfo.api.data.sequence.DLocation;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;

/**
 * This is a selection event fired by the DSequence viewer.
 * 
 * @author Patrick G. Durand
 */
public class DSequenceSelectionEvent extends EventObject {
  private static final long serialVersionUID = -4499545865123385871L;
  private DSequence       _seq;
  private int             _selFrom = -1;
  private int             _selTo = -1;
  private int             _type = SIMPLE_RANGE;
  private List<DLocation> _locs;

  public static final int SIMPLE_RANGE = 1;
  public static final int MULTIPLE_RANGE = 2;

  public DSequenceSelectionEvent(Object src){
    super(src);
  }

  public DSequenceSelectionEvent(Object src, DSequence fullSeq, int from, int to) {
    super(src);
    setEntireSequence(fullSeq);
    setSelFrom(from);
    setSelTo(to);
  }

  public DSequenceSelectionEvent(Object src, DSequence fullSeq, List<DLocation> locs) {
    super(src);
    setEntireSequence(fullSeq);
    setLocs(locs);
    _type = MULTIPLE_RANGE;
  }
  public void setEntireSequence(DSequence seq){
    _seq = seq;
  }
  /**
   * Returns the full sequence displayed in the viewer.
   */
  public DSequence getEntireSequence(){
    return _seq;
  }
  /**
   * Returns the position corresponding to the beginning of the selected region.
   * Returns -1 if nothing is selected. Value is absolute, so use the DSequence
   * DRulerModel to get a sequence coordinate. Use this method only if 
   * SelectionType is SIMPLE_RANGE.
   */
  public int getSelFrom() {
    return _selFrom;
  }
  /**
   * Returns the position corresponding to the ending of the selected region.
   * Returns -1 if nothing is selected. Value is absolute, so use the DSequence
   * DRulerModel to get a sequence coordinate. Use this method only if 
   * SelectionType is SIMPLE_RANGE.
   * 
   */
  public int getSelTo() {
    return _selTo;
  }
  public void setSelFrom(int selFrom) {
    _selFrom = selFrom;
  }
  public void setSelTo(int selTo) {
    _selTo = selTo;
  }
  public List<DLocation> getLocs() {
    return _locs;
  }
  /**
   *  Sets the selection ranges. Values within DLocation objects have to be absolute.
   *  Use this method only if SelectionType is MULTIPLE_RANGE.
   *  */
  public void setLocs(List<DLocation> locs) {
    this._locs = locs;
  }
  /**
   * Return the selection type. One of SIMPLE_RANGE or MULTIPLE_RANGE.
   */
  public int getSelectionType(){
    return _type;
  }
}
