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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.ui.blast.config.ConfigManager;
import bzh.plealog.bioinfo.ui.blast.config.color.ColorPolicyConfig;

/**
 * This is a dedicated renderer to apply a ColorPolicyConfig.
 * 
 * @author Patrick G. Durand
 */
public class HitQualityCellRenderer extends JLabel implements TableCellRenderer {
  private static final long serialVersionUID = -5076625168038771221L;

  public HitQualityCellRenderer() {
    this.setOpaque(true);
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
      int row, int column) {
    if (isSelected) {
      setBackground(table.getSelectionBackground());
      setForeground(table.getSelectionForeground());
    } else {
      if (row % 2 == 0) {
        setBackground(ColorPolicyConfig.BK_COLOR);
      } else {
        setBackground(table.getBackground());
      }
      setForeground(table.getForeground());
    }
    ColorPolicyConfig nc;
    ImageIcon icon = null;

    nc = (ColorPolicyConfig) ConfigManager.getConfig(ColorPolicyConfig.NAME);
    if (nc != null && value != null && value instanceof SRHit) {
      SRHsp hsp = ((SRHit) value).getHsp(0);
      icon = nc.getQualityIcon(hsp);
      if (icon != null)
        this.setIcon(icon);
    }
    return this;
  }
}