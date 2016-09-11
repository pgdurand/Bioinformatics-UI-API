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

import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.ui.sequence.basic.DPatternSearchFacility;

/**
 * This is a pattern matching event fired by the PatternSearchFacility.
 * 
 * @author Patrick G. Durand
 */
public class DPatternEvent extends EventObject {
  private static final long serialVersionUID = -1794151404124705220L;
  private DSequence _seq;
  private String    _pattern;
  private int       _selFrom;
  private int       _selTo;

  public DPatternEvent(DPatternSearchFacility src){
    super(src);
  }
  public DPatternEvent(DPatternSearchFacility src, DSequence fullSeq, String pat, int from, int to) {
    super(src);
    setEntireSequence(fullSeq);
    setMatchFrom(from);
    setMatchTo(to);
    setPattern(pat);
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
   * Returns the position corresponding to the beginning of the matched region.
   * Returns -1 if nothing is selected. Value is absolute, so use the DSequence
   * DRulerModel to get a sequence coordinate.
   */
  public int getMatchFrom() {
    return _selFrom;
  }
  /**
   * Returns the position corresponding to the ending of the matched region.
   * Returns -1 if nothing is selected. Value is absolute, so use the DSequence
   * DRulerModel to get a sequence coordinate.
   */
  public int getMatchTo() {
    return _selTo;
  }
  public void setMatchFrom(int selFrom) {
    _selFrom = selFrom;
  }
  public void setMatchTo(int selTo) {
    _selTo = selTo;
  }
  public String getPattern() {
    return _pattern;
  }
  public void setPattern(String pattern) {
    this._pattern = pattern;
  }

}
