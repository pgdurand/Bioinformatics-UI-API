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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * A panel aims at drawing HCA plots.
 * 
 * @author Patrick G. Durand
 */
public class PanelHca extends JPanel{
  private static final long serialVersionUID = 1733784611357262203L;
  protected BioSeq bioseq;
  protected int taille;
  private int cxChar;
  private int cyChar;
  private double nLineWidthAAs;
  private double nLineHeightAAs;
  private double EtoileX[];
  private double EtoileY[];
  private double amasUnitX[];
  private double amasUnitY[];
  private UnitZigZag unitZZ;
  private int [] xPoints;
  private int [] yPoints;
  private Font f;
  private boolean displaySpecialAsSymbols = true;

  private Color clr3 = UIManager.getDefaults().getColor("Table.selectionBackground");
  private Color clr4 = UIManager.getDefaults().getColor("Table.selectionForeground");

  public PanelHca()
  {
    super();
    BioSeq fakeSeq = new BioSeq("AAAAAAAAAAAAAAA");
    Font f = new Font("SansSerif", 0, 12);
    FontMetrics fm = getFontMetrics(f);
    cxChar = fm.charWidth('W');
    cyChar = fm.getAscent();
    nLineHeightAAs = ((double)cxChar / 2.7000000000000002D) * 3.2000000000000002D;
    nLineWidthAAs = (double)cyChar / 3D;
    creeEtoileUni();
    fakeSeq.computeCoordForHca(cxChar, cyChar, nLineWidthAAs, nLineHeightAAs);
    calcCoorUnitAmas(fakeSeq);
    setBackground(Color.white);
  }

  public PanelHca(String s)
  {
    this();
    setSequence(s);
  }

  public void setSequence(String s){
    if (s==null){
      init(null);
    }
    else{
      init(new BioSeq(s));
    }
  }
  protected void init(BioSeq b){
    bioseq = b;
    if (bioseq!=null){
      taille = bioseq.length();
      bioseq.computeCoordForHca(cxChar, cyChar, nLineWidthAAs, nLineHeightAAs);
      bioseq.computeColorForHca();
      bioseq.initPhiPho();
      bioseq.initZigZag();
      bioseq.initBreakers();
      bioseq.calculTableauBits();
    }
    else{
      taille = 0;
    }
    setPreferredSize(new Dimension(roundFloat((taille+10) * nLineWidthAAs), getPreferredHeight()));
  }
  public int getPreferredHeight(){
    return roundFloat(8.d*nLineHeightAAs);
  }
  private int roundFloat(double fVal)
  {
    int nVal;
    if(Math.abs(fVal) - (double)(int)Math.abs(fVal) > 0.5D)
      nVal = 1 + (int)Math.abs(fVal);
    else
      nVal = (int)Math.abs(fVal);
    if(fVal < 0.0D)
      nVal = -nVal;
    return nVal;
  }

  private void calcCoorUnitAmas(BioSeq fake)
  {
    amasUnitX = new double[6];
    amasUnitY = new double[6];
    amasUnitX[0] = (fake.getBioSeqCoordX(8) - fake.getBioSeqCoordX(5)) / 2D;
    amasUnitY[0] = (fake.getBioSeqCoordY(8) - fake.getBioSeqCoordY(5)) / 2D;
    amasUnitX[1] = (fake.getBioSeqCoordX(9) - fake.getBioSeqCoordX(5)) / 2D;
    amasUnitY[1] = (fake.getBioSeqCoordY(9) - fake.getBioSeqCoordY(5)) / 2D;
    amasUnitX[2] = (fake.getBioSeqCoordX(6) - fake.getBioSeqCoordX(5)) / 2D;
    amasUnitY[2] = (fake.getBioSeqCoordY(6) - fake.getBioSeqCoordY(5)) / 2D;
    amasUnitX[3] = (fake.getBioSeqCoordX(2) - fake.getBioSeqCoordX(5)) / 2D;
    amasUnitY[3] = (fake.getBioSeqCoordY(2) - fake.getBioSeqCoordY(5)) / 2D;
    amasUnitX[4] = (fake.getBioSeqCoordX(1) - fake.getBioSeqCoordX(5)) / 2D;
    amasUnitY[4] = (fake.getBioSeqCoordY(1) - fake.getBioSeqCoordY(5)) / 2D;
    amasUnitX[5] = (fake.getBioSeqCoordX(4) - fake.getBioSeqCoordX(5)) / 2D;
    amasUnitY[5] = (fake.getBioSeqCoordY(4) - fake.getBioSeqCoordY(5)) / 2D;
    xPoints = new int[6];
    yPoints = new int[6];

    unitZZ = new UnitZigZag();
    unitZZ.ZigUpStart.x = amasUnitX[5]+(amasUnitX[0]-amasUnitX[5])/2;
    unitZZ.ZigUpStart.y = amasUnitY[5]+(amasUnitY[0]-amasUnitY[5])/2;
    unitZZ.ZigUpDelta.x = 2*(amasUnitX[0]-amasUnitX[5])/2;
    unitZZ.ZigUpDelta.y = 3*(fake.getBioSeqCoordY(6)-fake.getBioSeqCoordY(8))/4;
  }

  private void creeEtoileUni()
  {
    double tabXEtoile[] = {
        55D, 67D, 109D, 73D, 83D, 55D, 27D, 37D, 1.0D, 43D
    };
    double tabYEtoile[] = {
        0, 36D, 36D, 54D, 96D, 72D, 96D, 54D, 36D, 36D
    };
    EtoileX = new double[10];
    EtoileY = new double[10];
    for(int i = 0; i < 10; i++)
    {
      EtoileX[i] = tabXEtoile[i] / 6.5D;
      EtoileY[i] = tabYEtoile[i] / 6.5D - (double)(cyChar / 2);
    }

  }

  private void drawRuler(Graphics g)
  {
    int y1 = cyChar + cyChar / 3;
    int y2 = 2 * cyChar;
    int fin = taille + 1;
    int j, seqPos;
    for(j = 1; j < fin; j++){
      seqPos = bioseq.getBioSeqPos(j-1);
      if (seqPos==-1)
        seqPos = j;
      if(j == 1 || j != 1 && seqPos % 10 == 0)
      {
        int x = roundFloat(bioseq.getBioSeqCoordX(j - 1)) + cxChar / 2;
        String s = Integer.toString(seqPos);
        int decal = (s.length() * cxChar) / 2;
        g.drawString(s, x - decal, cyChar);
        g.drawLine(x, y1, x, y2);
      }
    }
  }
  private void drawZZ(Graphics g, double centreX, double centreY)
  {
    int x, x2, y, y2;
    x = roundFloat(centreX+unitZZ.ZigUpStart.x);
    y = roundFloat(centreY+unitZZ.ZigUpStart.y);
    x2 = roundFloat(centreX+unitZZ.ZigUpDelta.x);
    y2 = roundFloat(centreY+unitZZ.ZigUpDelta.y);
    g.drawLine(x, y, x2, y2);

    x2 = roundFloat(centreX+3*unitZZ.ZigUpDelta.x/2);
    y2 = roundFloat(centreY-3*unitZZ.ZigUpDelta.y/2);
    g.drawLine(x, y, x2, y2);

  }
  private void drawAmas(Graphics g, double centreX, double centreY, int idx)
  {
    byte hydro = bioseq.getBioSeqTabHydro(idx);
    centreY -= (double)cyChar / 3D + 2D;
    centreX += (double)cxChar / 3D;

    //chaque aa est entoure de 6 lignes formant un hexagone dont un sommet est au Nord.
    if((hydro & 1) != 0)//sommet NO
    g.drawLine(roundFloat(centreX + amasUnitX[5]), roundFloat(centreY + amasUnitY[5]), roundFloat(centreX + amasUnitX[4]), roundFloat(centreY + amasUnitY[4]));
    if((hydro & 2) != 0)//sommet O
    g.drawLine(roundFloat(centreX + amasUnitX[4]), roundFloat(centreY + amasUnitY[4]), roundFloat(centreX + amasUnitX[3]), roundFloat(centreY + amasUnitY[3]));
    if((hydro & 4) != 0)//sommet SO
      g.drawLine(roundFloat(centreX + amasUnitX[3]), roundFloat(centreY + amasUnitY[3]), roundFloat(centreX + amasUnitX[2]), roundFloat(centreY + amasUnitY[2]));
    if((hydro & 8) != 0)//sommet NE
      g.drawLine(roundFloat(centreX + amasUnitX[5]), roundFloat(centreY + amasUnitY[5]), roundFloat(centreX + amasUnitX[0]), roundFloat(centreY + amasUnitY[0]));
    if((hydro & 0x10) != 0)//sommet E
      g.drawLine(roundFloat(centreX + amasUnitX[0]), roundFloat(centreY + amasUnitY[0]), roundFloat(centreX + amasUnitX[1]), roundFloat(centreY + amasUnitY[1]));
    if((hydro & 0x20) != 0)//sommet SE
      g.drawLine(roundFloat(centreX + amasUnitX[1]), roundFloat(centreY + amasUnitY[1]), roundFloat(centreX + amasUnitX[2]), roundFloat(centreY + amasUnitY[2]));
  }
  private void fillAmas(Graphics g, double centreX, double centreY){
    centreY -= (double)cyChar / 3D + 2D;
    centreX += (double)cxChar / 3D;
    for(int i=0;i<amasUnitX.length;i++){
      xPoints[i] = roundFloat(centreX + amasUnitX[i]);
      yPoints[i] = roundFloat(centreY + amasUnitY[i]);
    }
    g.fillPolygon(xPoints, yPoints, xPoints.length);
  }
  private void drawProline(Graphics g, int centreX, int centreY)
  {
    int x[] = new int[10];
    int y[] = new int[10];
    centreY -= cyChar / 2;
    centreX -= cxChar / 2;
    for(int i = 0; i < 10; i++)
    {
      x[i] = roundFloat(EtoileX[i] + (double)centreX);
      y[i] = roundFloat(EtoileY[i] + (double)centreY);
    }

    Polygon poly = new Polygon(x, y, x.length);
    g.fillPolygon(poly);
  }

  private void drawGlycine(Graphics g, int centreX, int centreY)
  {
    int x[] = new int[4];
    int y[] = new int[4];
    centreY -= cyChar / 2;
    centreX += cxChar / 2;
    x[0] = centreX;
    x[1] = centreX + cxChar / 2;
    x[2] = centreX;
    x[3] = centreX - cxChar / 2;
    y[0] = centreY - cxChar / 2;
    y[1] = centreY;
    y[2] = centreY + cxChar / 2;
    y[3] = centreY;
    Polygon poly = new Polygon(x, y, x.length);
    g.fillPolygon(poly);
  }

  private void drawTyrosine(Graphics g, int centreX, int centreY)
  {
    int fcxChar = cxChar / 3;
    int fsize = 2 * fcxChar;
    centreY -= cyChar / 2;
    centreX += cxChar / 2;
    g.drawRect(centreX - fcxChar, centreY - fcxChar, fsize, fsize);
  }

  private void drawSerine(Graphics g, int centreX, int centreY)
  {
    int fcxChar = cxChar / 3;
    int fsize = 2 * fcxChar;
    int fcxCharb = Math.max(1, cxChar / 8);
    int fsizeb = 2 * fcxCharb;
    centreY -= cyChar / 2;
    centreX += cxChar / 2;
    g.drawRect(centreX - fcxChar, centreY - fcxChar, fsize, fsize);
    g.fillRect(centreX - fcxCharb, centreY - fcxCharb, fsizeb, fsizeb);
  }
  protected boolean isAASelected(int idx){
    return false;
  }
  protected int getPos(Point pt){
    return roundFloat(((double)pt.x - 2*nLineWidthAAs) / nLineWidthAAs);
  }
  protected Rectangle getVisibleRect(int seqPos){
    int px;

    px = roundFloat((double)(seqPos) * nLineWidthAAs);
    //approximate a rectangle around the px position
    return new Rectangle(px, 0, px + 5, 120);
  }
  public void setDisplaySpecialAsSymbols(boolean display){
    displaySpecialAsSymbols = display;
  }
  public boolean isDisplaySpecialAsSymbols(){
    return displaySpecialAsSymbols;
  }
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    if (bioseq == null){
      return;
    }
    Graphics2D g2 = ((Graphics2D)g);
    Object old;
    Color  oldClr;
    Font   oldFnt;

    //save DC
    old = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
    oldFnt = g.getFont();
    oldClr = g.getColor();

    //prepare DC
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
        RenderingHints.VALUE_ANTIALIAS_ON);
    g.setFont(f);

    drawRuler(g);
    //zig zag and selected aa
    for(int j = 0; j < bioseq.length(); j++)
    {
      if (bioseq.getBioSeqIsZigZag(j)){
        g.setColor(Color.black);
        drawZZ(g, bioseq.getBioSeqCoordX(j), bioseq.getBioSeqCoordY(j));
        drawZZ(g, bioseq.getBioSeqCoordX(j), bioseq.getBioSeqCoordY2(j));
      }
      if (isAASelected(j)){
        g.setColor(clr3);
        fillAmas(g, bioseq.getBioSeqCoordX(j), bioseq.getBioSeqCoordY(j));
        fillAmas(g, bioseq.getBioSeqCoordX(j), bioseq.getBioSeqCoordY2(j));
      }
      else if (bioseq.getBioSeqIsPho(j)){
        g.setColor(Color.white);
        fillAmas(g, bioseq.getBioSeqCoordX(j), bioseq.getBioSeqCoordY(j));
        fillAmas(g, bioseq.getBioSeqCoordX(j), bioseq.getBioSeqCoordY2(j));
      }

    }

    for(int j = 0; j < bioseq.length(); j++)
    {
      String s = String.valueOf(bioseq.getBioSeqLetter(j));
      int x = roundFloat(bioseq.getBioSeqCoordX(j));
      int y = roundFloat(bioseq.getBioSeqCoordY(j)) - 1;
      int y2 = roundFloat(bioseq.getBioSeqCoordY2(j)) - 1;

      if (isAASelected(j))
        g.setColor(clr4);
      else
        g.setColor(bioseq.getBioSeqLetterColor(j));
      if (displaySpecialAsSymbols){
        switch(bioseq.getBioSeqLetter(j)){
          case 'G': drawGlycine(g, x, y);drawGlycine(g, x, y2); break;
          case 'T': drawTyrosine(g, x, y);drawTyrosine(g, x, y2); break;
          case 'S': drawSerine(g, x, y); drawSerine(g, x, y2); break;
          case 'P': drawProline(g, x, y); drawProline(g, x, y2); break;
          default: g.drawString(s, x, y); g.drawString(s, x, y2); break;
        }
      }
      else{
        g.drawString(s, x, y); g.drawString(s, x, y2);
      }
    }
    g.setColor(Color.black);
    for(int j = 0; j < taille; j++)
    {
      double fx = bioseq.getBioSeqCoordX(j);
      double fy = bioseq.getBioSeqCoordY(j);
      double fy2 = bioseq.getBioSeqCoordY2(j);
      drawAmas(g, fx, fy, j);
      drawAmas(g, fx, fy2, j);
    }
    //restore DC
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, old);
    g.setFont(oldFnt);
    g.setColor(oldClr);
  }

  private class UnitZigZag {
    private Point2D.Double ZigUpStart = new Point2D.Double();
    private Point2D.Double ZigUpDelta = new Point2D.Double();
  }
}
