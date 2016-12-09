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

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Renderer of data to be used with CheckBoxList.
 * 
 * @author Patrick G. Durand
 * */
public class CheckboxListItemRenderer extends JCheckBox implements ListCellRenderer<CheckBoxListItem> {

  private static final long serialVersionUID = -9141288252841413560L;

  @Override
  public Component getListCellRendererComponent(JList<? extends CheckBoxListItem> list, CheckBoxListItem value,
      int index, boolean isSelected, boolean cellHasFocus) {
    setEnabled(list.isEnabled());
    setSelected(value.isSelected());
    setFont(list.getFont());
    setBackground(list.getBackground());
    setForeground(list.getForeground());
    setText(value.toString());
    return this;
  }
}