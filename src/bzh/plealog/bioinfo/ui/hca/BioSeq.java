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
 * A simple representation of a protein sequence.
 * 
 * @author Patrick G. Durand
 */
public class BioSeq extends AminoAcid {
  protected AminoAcid bioseq[];
  protected int length;

  public BioSeq(){}

  public BioSeq(String sequence)
  {
    String s = sequence.toUpperCase();
    int len = s.length();
    int nlet = 0;
    int j = 0;
    char c;

    for(int i = 0; i < len; i++)
    {
      c = s.charAt(i);
      if(c >= 'A' && c <= 'Z')
        nlet++;
    }

    bioseq = new AminoAcid[nlet];
    for(int i = 0; i < len; i++)
    {
      c = s.charAt(i);
      if(c >= 'A' && c <= 'Z')
      {
        bioseq[j] = new AminoAcid(c);
        j++;
      }
    }

    length = nlet;
  }
  private double fmod(double a, double b)
  {
    long i = (long)(a / b);
    return a - (double)i * b;
  }

  public void computeCoordForHca(int cxChar, int cyChar, double nLineWidthAAs, double nLineHeightAAs)
  {
    double t = 3.6000000000000001D;
    double nr = 18D;
    int decal = 2 * cxChar + (3 * cyChar) / 2;
    int taille2 = length + 1;
    double decal2 = 3.6000000000000001D * nLineHeightAAs;
    for(int j = 1; j < taille2; j++)
    {
      double x = (double)j * nLineWidthAAs;
      double y = fmod(((double)j - 1.0D) + nr, t) * nLineHeightAAs + (double)decal;
      double y2 = y + decal2;
      bioseq[j - 1].setCoordHca(x, y, y2);
    }

  }

  public void computeColorForHca()
  {
    String pho = new String("VILFMWY");
    String acid = new String("DE");
    String basic = new String("KR");
    String breaker = new String("P");
    for(int j = 0; j < length; j++)
      if(pho.indexOf(bioseq[j].getLetter()) != -1)
        bioseq[j].setColorHca(Color.green);
      else
        if(acid.indexOf(bioseq[j].getLetter()) != -1)
          bioseq[j].setColorHca(Color.red);
        else
          if(basic.indexOf(bioseq[j].getLetter()) != -1)
            bioseq[j].setColorHca(Color.blue);
          else
            if(breaker.indexOf(bioseq[j].getLetter()) != -1)
              bioseq[j].setColorHca(Color.red);
            else
              bioseq[j].setColorHca(Color.black);

  }

  public boolean IsZigZag(int i)
  {

    if (getBioSeqIsPho(i)){
      if ((i-1)>=0 && (i+3)<bioseq.length){
        if (getBioSeqIsPho(i+2) &&
            !getBioSeqIsPho(i-1) &&
            !getBioSeqIsPho(i+3) &&
            !getBioSeqIsPho(i+1)){
          return(true);
        }
      }
    }
    return(false);
  }

  public void initZigZag(){
    for(int j = 0; j < bioseq.length; j++){
      bioseq[j].setIsZigZag(IsZigZag(j));
    }
  }
  public void initPhiPho()
  {
    String pho = new String("VILFMWY");
    for(int j = 0; j < length; j++)
      if(pho.indexOf(bioseq[j].getLetter()) != -1)
        bioseq[j].setIsPho(true);

  }

  public void initBreakers()
  {
    String pho = new String("P");
    for(int j = 0; j < length; j++)
      if(pho.indexOf(bioseq[j].getLetter()) != -1)
        bioseq[j].setIsBreaker(true);

  }
  //cf methode drawAmas() de PanelHca pour l'explication des 6 cotes de chaque aa
  public void calculTableauBits()
  {
    int maxLength3 = length + 20;
    byte pBits[] = new byte[maxLength3];
    boolean pPropAA[] = new boolean[maxLength3];
    boolean szSeqTmp[] = new boolean[maxLength3];
    for(int n = 0; n < length; n++)
    {
      pPropAA[n + 6] = bioseq[n].getIsPho();
      szSeqTmp[n + 6] = bioseq[n].getIsBreaker();
    }

    int maxLength = length + 12;
    int maxLength2 = length + 6;
    for(int n = 0; n < maxLength; n++)
      if(n >= 6 && n < maxLength2)
      {
        if(pPropAA[n])
        {
          if(pPropAA[n - 1])
            pBits[n - 6] |= 0;
          else
            if(pPropAA[n - 4])
            {
              if(szSeqTmp[n - 3] || szSeqTmp[n - 2] || szSeqTmp[n - 1])
                pBits[n - 6] |= 1;
              else
                pBits[n - 6] |= 0;
            } else
            {
              pBits[n - 6] |= 1;
            }
        } else
          if(pPropAA[n - 4])
            if(pPropAA[n - 1])
            {
              if(szSeqTmp[n - 3] || szSeqTmp[n - 2])
                pBits[n - 6] |= 0;
              else
                pBits[n - 6] |= 1;
            } else
            {
              pBits[n - 6] |= 0;
            }
        if(pPropAA[n])
        {
          if(pPropAA[n - 4])
          {
            if(szSeqTmp[n - 3] || szSeqTmp[n - 2] || szSeqTmp[n - 1])
              pBits[n - 6] |= 2;
            else
              pBits[n - 6] |= 0;
          } else
            if(pPropAA[n - 3])
            {
              if(szSeqTmp[n - 2] || szSeqTmp[n - 1])
                pBits[n - 6] |= 2;
              else
                pBits[n - 6] |= 0;
            } else
            {
              pBits[n - 6] |= 2;
            }
        } else
          if(pPropAA[n - 4])
          {
            if(pPropAA[n - 3])
              pBits[n - 6] |= 2;
            else
              pBits[n - 6] |= 0;
          } else
          {
            pBits[n - 6] |= 0;
          }
        if(pPropAA[n])
        {
          if(pPropAA[n + 1])
            pBits[n - 6] |= 0;
          else
            if(pPropAA[n - 3])
            {
              if(szSeqTmp[n - 2] || szSeqTmp[n - 1])
                pBits[n - 6] |= 4;
              else
                pBits[n - 6] |= 0;
            } else
            {
              pBits[n - 6] |= 4;
            }
        } else
          if(pPropAA[n - 3])
          {
            if(pPropAA[n + 1])
            {
              if(szSeqTmp[n - 2] || szSeqTmp[n - 1] || szSeqTmp[n])
                pBits[n - 6] |= 0;
              else
                pBits[n - 6] |= 4;
            } else
            {
              pBits[n - 6] |= 0;
            }
          } else
          {
            pBits[n - 6] |= 0;
          }
        if(pPropAA[n])
        {
          if(pPropAA[n - 1])
            pBits[n - 6] |= 0;
          else
            if(pPropAA[n + 3])
            {
              if(szSeqTmp[n + 1] || szSeqTmp[n + 2])
                pBits[n - 6] |= 8;
              else
                pBits[n - 6] |= 0;
            } else
            {
              pBits[n - 6] |= 8;
            }
        } else
          if(pPropAA[n - 1])
          {
            if(pPropAA[n + 3])
            {
              if(szSeqTmp[n] || szSeqTmp[n + 1] || szSeqTmp[n + 2])
                pBits[n - 6] |= 0;
              else
                pBits[n - 6] |= 8;
            } else
            {
              pBits[n - 6] |= 0;
            }
          } else
          {
            pBits[n - 6] |= 0;
          }
        if(pPropAA[n])
        {
          if(pPropAA[n + 3])
          {
            if(szSeqTmp[n + 1] || szSeqTmp[n + 2])
              pBits[n - 6] |= 0x10;
            else
              pBits[n - 6] |= 0;
          } else
            if(pPropAA[n + 4])
            {
              if(szSeqTmp[n + 1] || szSeqTmp[n + 2] || szSeqTmp[n + 3])
                pBits[n - 6] |= 0x10;
              else
                pBits[n - 6] |= 0;
            } else
            {
              pBits[n - 6] |= 0x10;
            }
        } else
          if(pPropAA[n + 3])
          {
            if(pPropAA[n + 4])
              pBits[n - 6] |= 0x10;
            else
              pBits[n - 6] |= 0;
          } else
          {
            pBits[n - 6] |= 0;
          }
        if(pPropAA[n])
        {
          if(pPropAA[n + 1])
            pBits[n - 6] |= 0;
          else
            if(pPropAA[n + 4])
            {
              if(szSeqTmp[n + 1] || szSeqTmp[n + 2] || szSeqTmp[n + 3])
                pBits[n - 6] |= 0x20;
              else
                pBits[n - 6] |= 0;
            } else
            {
              pBits[n - 6] |= 0x20;
            }
        } else
          if(pPropAA[n + 4])
          {
            if(pPropAA[n + 1])
            {
              if(szSeqTmp[n + 2] || szSeqTmp[n + 3])
                pBits[n - 6] |= 0;
              else
                pBits[n - 6] |= 0x20;
            } else
            {
              pBits[n - 6] |= 0;
            }
          } else
          {
            pBits[n - 6] |= 0;
          }
      }

    for(int n = 0; n < length; n++)
      bioseq[n].setTabHydro(pBits[n]);

  }

  public int length()
  {
    return length;
  }

  public char getBioSeqLetter(int idx)
  {
    return bioseq[idx].getLetter();
  }

  public double getBioSeqCoordX(int idx)
  {
    return bioseq[idx].getCoordX();
  }

  public double getBioSeqCoordY(int idx)
  {
    return bioseq[idx].getCoordY();
  }

  public double getBioSeqCoordY2(int idx)
  {
    return bioseq[idx].getCoordY2();
  }

  public Color getBioSeqLetterColor(int idx)
  {
    return bioseq[idx].getLetterColor();
  }

  public byte getBioSeqTabHydro(int idx)
  {
    return bioseq[idx].getTabHydro();
  }

  public boolean getBioSeqIsPho(int idx)
  {
    return bioseq[idx].getIsPho();
  }
  public boolean getBioSeqIsZigZag(int idx)
  {
    return bioseq[idx].getIsZigZag();
  }
  public boolean getBioSeqIsBreaker(int idx)
  {
    return bioseq[idx].getIsBreaker();
  }
  public int getBioSeqPos(int idx){
    return bioseq[idx].getSeqPos();
  }
}
