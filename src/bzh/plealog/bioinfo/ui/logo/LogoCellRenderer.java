/* Copyright (C) 2003-2017 Patrick G. Durand
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
package bzh.plealog.bioinfo.ui.logo;

import java.awt.Component;
import java.util.Enumeration;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import bzh.plealog.bioinfo.api.data.sequence.DAlphabet;
import bzh.plealog.bioinfo.api.data.sequence.DSymbol;
import bzh.plealog.bioinfo.api.data.sequence.DSymbolGraphics;
import bzh.plealog.bioinfo.api.data.sequence.stat.AlphabetCounter;

public class LogoCellRenderer extends LogoPanel implements TableCellRenderer {
  private static final long serialVersionUID = 5025470788986369253L;
  private LogoCell          lCell_;
  private LogoLetter[]      letters_;
  private int               alphabetSize_;
  private int               nbAlignedSeq_;

  public LogoCellRenderer(int alphabetSize, int nbSeq) {
    super();
    int i;

    this.setHorizontal(false);
    this.setFillAllPanel(true);
    if (alphabetSize == 0)
      return;
    alphabetSize_ = alphabetSize;
    nbAlignedSeq_ = nbSeq;
    lCell_ = new LogoCell();
    letters_ = new LogoLetter[alphabetSize];
    for (i = 0; i < alphabetSize; i++) {
      letters_[i] = new LogoLetter();
    }
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    AlphabetCounter aCounter;
    LogoLetter letter;
    DAlphabet alph;
    DSymbol symbol;
    Enumeration<DSymbol> enumSb;
    Integer val;
    DSymbolGraphics dg;
    int i;
    double d;

    if (value instanceof AlphabetCounter && alphabetSize_ != 0) {
      aCounter = (AlphabetCounter) value;
      alph = aCounter.getAlphabet();
      lCell_.clear();
      enumSb = alph.symbols();
      i = 0;
      while (enumSb.hasMoreElements()) {
        symbol = (DSymbol) enumSb.nextElement();
        val = aCounter.getCounter(symbol);
        if (val == null)
          continue;
        d = val.doubleValue();
        letter = letters_[i];
        letter.setValue(d);
        letter.setSymbol(symbol.toString());
        dg = symbol.getGraphics();
        if (dg != null) {
          letter.setBarColor(dg.getTextColor());
          letter.setSymbFgColor(dg.getBkColor());
        }
        lCell_.addLogoLetter(letter);
        i++;
      }
      lCell_.setMaxValue(nbAlignedSeq_);
      lCell_.orderLogoLetter();
      this.setLogoCell(lCell_);
    } else {
      this.setLogoCell(null);
    }

    return this;
  }
}