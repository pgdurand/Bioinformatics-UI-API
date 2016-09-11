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
package bzh.plealog.bioinfo.ui.sequence.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import bzh.plealog.bioinfo.api.data.sequence.DSymbol;
import bzh.plealog.bioinfo.api.data.sequence.DSymbolGraphics;

/**
 * This class implements a default symbol renderer used by the sequence viewer.
 * 
 * @author Patrick G. Durand
 */
public class DSymbolRenderer extends JLabel implements ListCellRenderer<DSymbol>{
  private static final long serialVersionUID = -6504059708601507627L;

  public Component getListCellRendererComponent(
      JList<? extends DSymbol> list, DSymbol value,  int row, boolean isSelected,
      boolean cellHasFocus) {

    Color           bkClr, txClr;
    DSymbol         symbol;
    DSymbolGraphics graphics;

    symbol = value;
    graphics = symbol.getGraphics();
    setText(value.toString());
    if (graphics!=null){
      bkClr = graphics.getBkColor();
      txClr = graphics.getTextColor();
    }
    else{
      bkClr = list.getBackground();
      txClr = list.getForeground();
    }

    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    }
    else {
      setBackground(bkClr);
      setForeground(txClr);
    }
    setEnabled(list.isEnabled());
    setFont(list.getFont());
    setOpaque(true);
    return this;
  }

  public Dimension getPreferredSize(){
    return new Dimension(30,15);
  }

}
