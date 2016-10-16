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
package bzh.plealog.bioinfo.ui.util;

import javax.swing.SwingConstants;

/**
 * Define a way to handle column selection in a JTable.
 * 
 * @author Patrick G. Durand
 * @since 2003
 */
public class TableHeaderColumnItem {
  private int _iID;
  private String _sID;
  private boolean _required;
  private boolean _visible;
  private boolean _isLargest;
  private Class<?> _class;
  private int _horzAlignment = SwingConstants.CENTER;
  
  public TableHeaderColumnItem(String sid, int iid, boolean required, boolean visible) {
    setIID(iid);
    setSID(sid);
    setRequired(required);
    setVisible(visible);
    setDataType(String.class);
  }

  public TableHeaderColumnItem(Class<?> dataType, String sid, int iid, boolean required, boolean visible) {
    setIID(iid);
    setSID(sid);
    setRequired(required);
    setVisible(visible);
    setDataType(dataType);
  }

  public int getIID() {
    return _iID;
  }

  public void setIID(int iid) {
    _iID = iid;
  }

  public String getSID() {
    return _sID;
  }

  public void setSID(String sid) {
    _sID = sid;
  }

  public boolean isRequired() {
    return _required;
  }
  
  public void setRequired(boolean required) {
    this._required = required;
  }

  public boolean isLargest() {
    return _isLargest;
  }

  public void setLargest(boolean largest) {
    this._isLargest = largest;
  }

  public boolean isVisible() {
    return _visible;
  }

  public void setVisible(boolean visible) {
    this._visible = visible;
  }

  public Class<?> getDataType() {
    return _class;
  }

  public void setDataType(Class<?> dType) {
    _class = dType;
  }

  public int getHorizontalAlignment(){
    return _horzAlignment;
  }

  public void setHorizontalAlignment(int horzAlignment){
    this._horzAlignment = horzAlignment;
  }

}
