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
 * This is an entry for the RadioChooser dialog box. It symbolized an option by
 * way of a radio button and a description.
 * 
 * @author Patrick G. Durand
 */
public class RadioChooserEntry {
  private int    _id;
  private String _categoryName;
  private String _label;
  private String _description;

  /**
   * Constructor.
   * 
   * @param id an id to uniquely identify an option
   * @param label the label displayed in the dialogue
   * @param description the description of the entry. This text is displayed below
   * each radio button as a helper message.
   */
  public RadioChooserEntry(int id, String label, String description) {
    this(id, "none", label, description);
  }
  /**
   * Constructor.
   * 
   * @param id an id to uniquely identify an option
   * @param categoryName the name of the category to which belongs this entry
   * @param label the label displayed in the dialog box
   * @param description the description of the entry. This text is displayed below
   * each radio button as a helper message.
   */
  public RadioChooserEntry(int id, String categoryName, String label, String description) {
    super();
    this._id = id;
    this._categoryName = categoryName;
    this._label = label;
    this._description = description;
  }
  /**
   * @return option ID
   * */
  public int getId() {
    return _id;
  }
  /**
   * @param id an id to uniquely identify an option
   * */
  public void setId(int id) {
    this._id = id;
  }
  /**
   * @return the name of the category to which belongs this entry
   * */
  public String getCategoryName() {
    return _categoryName;
  }
  /**
   * @param categoryName the name of the category to which belongs this entry
   * */
  public void setCategoryName(String categoryName) {
    this._categoryName = categoryName;
  }
  /**
   * @return label the label displayed in the dialog box
   * */
  public String getLabel() {
    return _label;
  }
  /**
   * @return description the description of the entry. This text is displayed below
   * each radio button as a helper message.
   */
  public String getDescription() {
    return _description;
  }
  /**
   * @param label the label displayed in the dialog box
   * */
  public void setLabel(String label) {
    this._label = label;
  }
  /**
   * @param description description the description of the entry. This text is displayed below
   * each radio button as a helper message.
   * */
  public void setDescription(String description) {
    this._description = description;
  }
}
