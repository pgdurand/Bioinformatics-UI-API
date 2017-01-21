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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LogoCell {

  private double                value_;
  private ArrayList<LogoLetter> logoLetters_;
  private boolean               ordered_;
  private LogoLetterSorter      llSorter_;

  public LogoCell() {
    logoLetters_ = new ArrayList<>();
    llSorter_ = new LogoLetterSorter();
  }

  public LogoCell(double mxVal) {
    this();
    setMaxValue(mxVal);
  }

  public double getMaxValue() {
    return value_;
  }

  public void setMaxValue(double mxVal) {
    value_ = mxVal;
  }

  public void addLogoLetter(LogoLetter ll) {
    logoLetters_.add(ll);
  }

  public int size() {
    return logoLetters_.size();
  }

  public LogoLetter getLogoLetter(int idx) {
    return ((LogoLetter) logoLetters_.get(idx));
  }

  public void clear() {
    logoLetters_.clear();
    value_ = 0.0;
    ordered_ = false;
  }

  public void orderLogoLetter() {
    if (ordered_)
      return;
    Collections.sort(logoLetters_, llSorter_);
    ordered_ = true;
  }

  private class LogoLetterSorter implements Comparator<LogoLetter> {
    public int compare(LogoLetter o1, LogoLetter o2) {
      double ll1, ll2;
      int ret;

      ll1 = o1.getValue();
      ll2 = o2.getValue();
      if (ll1 < ll2) {
        ret = 1;
      } else if (ll1 > ll2) {
        ret = -1;
      } else {
        ret = 0;
      }
      return ret;
    }
  }
}
