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

import java.util.EventListener;

/**
 * Define a listener to be used with CheckBoxList. It enables to figure
 * out whether or not an item has been selected.
 * 
 * @author Patrick G. Durand
 * */
public interface CheckBoxListSelectionListener extends EventListener {
  /**
   * Method called when the used click on a check box contained in a 
   * CheckBoxList component.
   * 
   * @param item the clicked CheckBoxList item
   */
  public void itemSelected(CheckBoxListItem item);
}
