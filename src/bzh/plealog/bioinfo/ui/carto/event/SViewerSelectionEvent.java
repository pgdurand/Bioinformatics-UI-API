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

import java.util.EventObject;

/**
 * This is a selection event fired by the sequence viewer.
 * 
 * @author Patrick G. Durand
 */
public class SViewerSelectionEvent extends EventObject {
  private static final long serialVersionUID = -6792296706333497370L;
  //source object is a DrawingLane or SViewerPanel 
  private Object       _object;
  private int          _from = -1;//ruler coordinates [0..seqSize-1]. -1 if undefined
  private int          _to = -1;
  private SEL_TYPE     _type;

  public static enum SEL_TYPE {EMPTY, OBJECT_WITH_RANGE, OBJECT_ALONE};

  public SViewerSelectionEvent(Object src){
    super(src);
  }

  public SViewerSelectionEvent(Object src, SEL_TYPE type, Object object, int from, int to) {
    super(src);
    _object = object;
    _from = from;
    _to = to;
    _type = type;
  }
  public SViewerSelectionEvent(Object src, SEL_TYPE type, Object object) {
    super(src);
    _object = object;
    _type = type;
  }
  /**
   * Return the selection object.
   */
  public Object getSelectionObject(){
    return _object;
  }

  public int getFrom() {
    return _from;
  }

  public int getTo() {
    return _to;
  }

  public void setFrom(int from) {
    this._from = from;
  }

  public void setTo(int to) {
    this._to = to;
  }

  public SEL_TYPE getType() {
    return _type;
  }

  public void setType(SEL_TYPE type) {
    this._type = type;
  }

}
