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

/**
 * Element data to be used with CheckBoxList.
 * 
 * @author Patrick G. Durand
 * */
public class CheckBoxListItem {
  private String label;
  private boolean isSelected = false;

  /**
   * Constructor.
   * 
   * @param label a label for this check box
   */
  public CheckBoxListItem(String label) {
    this.label = label;
  }

  /**
   * Figures out whether or not this check box is selected.
   * 
   * @return true or false
   */
  public boolean isSelected() {
    return isSelected;
  }

  /**
   * Sets the selection status of this check box.
   * 
   * @param isSelected true or false
   */
  public void setSelected(boolean isSelected) {
    this.isSelected = isSelected;
  }
  
  /**
   * Return the label of this item.
   * 
   * @return a label
   */
  public String getLabel(){
    return label;
  }

  /**
   * Set the label of this item.
   *
   * @param lbl the label
   **/
  public void setLabel(String lbl){
    label = lbl;
  }
  
  /**
   * Overrides default toString method.
   * 
   * @return the label of this check box.
   */
  @Override
  public String toString() {
    return label;
  }
}