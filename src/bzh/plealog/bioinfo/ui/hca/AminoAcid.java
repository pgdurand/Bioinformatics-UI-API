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
package bzh.plealog.bioinfo.ui.hca;
//This code from: Turtle. A Java2 applet to display a HCA plot.
//(c) January 2000, Patrick Durand.

import java.awt.Color;

/**
 * A simple representation of an amino acid.
 * 
 * @author Patrick G. Durand
 */
public class AminoAcid{
  private char letter;
  private boolean isPho;
  private boolean isBreaker;
  private boolean isZigZag;
  private double x_pos;
  private double y_pos;
  private double y_pos2;
  private Color clrLetter;
  private int  seqPos = -1;
  private byte TabHydro;

  public AminoAcid()
  {
    this(' ', false, 0, 0, 0);
  }

  public AminoAcid(char c)
  {
    this(c, false, 0, 0, 0);
  }

  public AminoAcid(char c, boolean prop, int x, int y, int y2)
  {
    letter = c;
    isPho = prop;
    setCoordHca(x, y, y2);
  }

  public void setIsPho(boolean pho)
  {
    isPho = pho;
  }

  public boolean getIsPho()
  {
    return isPho;
  }
  public void setIsZigZag(boolean zz)
  {
    isZigZag = zz;
  }

  public boolean getIsZigZag()
  {
    return isZigZag;
  }

  public void setIsBreaker(boolean breaker)
  {
    isBreaker = breaker;
  }

  public boolean getIsBreaker()
  {
    return isBreaker;
  }

  public void setTabHydro(byte tab)
  {
    TabHydro = tab;
  }

  public byte getTabHydro()
  {
    return TabHydro;
  }

  public void setCoordHca(double x, double y, double y2)
  {
    x_pos = x;
    y_pos = y;
    y_pos2 = y2;
  }

  public void setColorHca(Color clr)
  {
    clrLetter = clr;
  }

  public void setTabHydro(int idx, int val)
  {
    TabHydro |= val << idx;
  }

  public double getCoordX()
  {
    return x_pos;
  }

  public double getCoordY()
  {
    return y_pos;
  }

  public double getCoordY2()
  {
    return y_pos2;
  }

  public char getLetter()
  {
    return letter;
  }

  public Color getLetterColor()
  {
    return clrLetter;
  }

  public int getSeqPos() {
    return seqPos;
  }

  public void setSeqPos(int seqPos) {
    this.seqPos = seqPos;
  }

}
