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
package test;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import bzh.plealog.bioinfo.api.data.sequence.DSymbol;
import bzh.plealog.bioinfo.api.data.sequence.DSymbolGraphics;

/**
 * This class implements a default symbol renderer used by the sequence viewer.
 * 
 * @author Patrick G. Durand
 */
public class DSymbolRendererTest extends JLabel implements TableCellRenderer{
  private static final long serialVersionUID = 3285084176605597639L;

  /*To do: DSequenceGraphics: structure de donnees de type DSymbolGraphics, mais
   * pour une sequence complete. COntient les donnees graphiques pour des positions
   * particulieres. Permettre d'associer un DSequenceGraphics a ce renderer, auquel
   * cas les infos de celui-ci surpasse le graphics d'un symbole. Note: via le param
   * 'list' du renderer, il est possible de recuperer le DSequenceModel et donc la
   * position actuelle dans la sequence.*/

  /*to do: astuce pour gras, ital, souligne: utiliser html!*/

  public Component getTableCellRendererComponent(
      JTable list, Object value,  boolean isSelected,
      boolean cellHasFocus,int row, int col) {

    Color           bkClr, txClr;
    DSymbol         symbol;
    DSymbolGraphics graphics;

    symbol = (DSymbol) value;
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

}
