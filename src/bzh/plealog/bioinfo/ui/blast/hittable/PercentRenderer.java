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
package bzh.plealog.bioinfo.ui.blast.hittable;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import bzh.plealog.bioinfo.ui.blast.config.color.ColorPolicyConfig;
import bzh.plealog.bioinfo.ui.util.JPercentLabel;

/**
 * This is a dedicated renderer to display percent values.
 * 
 * @author Patrick G. Durand
 */
public class PercentRenderer extends JPercentLabel implements TableCellRenderer {
  private static final long serialVersionUID = -4120334236613533424L;

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
      int row, int column) {
    if (isSelected) {
      setBackground(table.getSelectionBackground());
    } else {
      if (row % 2 == 0) {
        setBackground(ColorPolicyConfig.BK_COLOR);
      } else {
        setBackground(table.getBackground());
      }
    }
    this.setValue(value.toString());
    return this;
  }
}