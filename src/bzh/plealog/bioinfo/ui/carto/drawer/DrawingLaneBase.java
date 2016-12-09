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
package bzh.plealog.bioinfo.ui.carto.drawer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSequenceInfo;

/**
 * This is a default implementation of the DrawingLane interface.
 */
public abstract class DrawingLaneBase implements DrawingLane{
  private DSequence   sequence;
  private String      leftLabel;
  private String      rightLabel;
  private Dimension   dim;
  private Color       bkClr;
  private Font        fnt;
  private int         leftMargin;
  private int         rightMargin;
  private int         zoomFactor;
  private int         topMargin;
  private int         bottomMargin;
  private int         refLabelWidth;
  private boolean     zoomable;
  private boolean     drawGrid=false;
  
  //when updating font size, please update dimension in the constructor
  //this is not good code, but we do not have access to a FontMetrics at this stage
  public static final Font DEF_FNT = new Font("Arial",Font.PLAIN,11);

  private static final Color GRID_CLR = Color.LIGHT_GRAY;

  private DrawingLaneBase(){
    super();
    zoomFactor = 1;
    zoomable = true;
    bkClr = Color.WHITE;
    fnt = DEF_FNT;
    dim = new Dimension(800, 15);
  }
  /**
   * Standard constructor.
   */
  public DrawingLaneBase(DSequence seq){
    this();
    sequence = seq;
  }

  public void setDrawGrid(boolean b){
    drawGrid=b;
  }
  public void setTopMargin(int top){
    topMargin = top;
  }
  public int getTopMargin(){
    return topMargin;
  }
  public void setBottomMargin(int bottom){
    bottomMargin = bottom;
  }
  public int getBottomMargin(){
    return bottomMargin;
  }

  public void setInsets(int top, int bottom){
    topMargin = top;
    bottomMargin = bottom;
  }
  public Color getBackgroundClr() {
    return bkClr;
  }

  public void setBackgroundClr(Color bkClr) {
    this.bkClr = bkClr;
  }

  public Font getFont() {
    return fnt;
  }

  public void setFont(Font fnt) {
    this.fnt = fnt;
  }

  public Dimension getPreferredSize(){
    return dim;
  }
  public void setPreferredSize(Dimension dim){
    this.dim = dim;
  }
  public Dimension getMaximumSize(){
    return new Dimension(this.getSequence().size()*8+leftMargin+rightMargin, dim.height);
  }
  public Dimension getMinimumSize(){
    return new Dimension(1024, dim.height);
  }
  private double evaluateScaleFactor(int width){
    double xFactor;
    if (zoomable){
      xFactor = (double)(width-(leftMargin+rightMargin)) / (double) (sequence.size());
      xFactor *= ((double) zoomFactor);
    }
    else{
      xFactor = 1.0;
    }
    return xFactor;
  }
  public double computeScaleFactor(){
    return evaluateScaleFactor(dim.width);
  }

  public String getLeftLabel() {
    return leftLabel;
  }

  public String getRightLabel() {
    return rightLabel;
  }

  public int getLeftMargin() {
    return leftMargin;
  }

  public int getRightMargin() {
    return rightMargin;
  }

  public void setLeftLabel(String leftLabel) {
    this.leftLabel = leftLabel;
  }

  public void setRightLabel(String rightLabel) {
    this.rightLabel = rightLabel;
  }

  public void setLeftMargin(int leftMargin) {
    this.leftMargin = leftMargin;
  }

  public void setRightMargin(int rightMargin) {
    this.rightMargin = rightMargin;
  }

  public DSequence getSequence(){
    return sequence;
  }

  public Object getClickedObject(int x){
    return null;
  }
  public Object getObjectFromSection(int xFrom, int xTo){
    int  from, to;

    from = Math.min(xFrom, xTo);
    to = Math.max(xFrom, xTo);
    from = Math.max(0, getRulerPositionAt(from));
    to = Math.min(getRulerPositionAt(to), this.getSequence().size()-1);
    //System.out.println("getObjectFromSection: "+from+", "+to);
    DSequence seq = sequence.getSubSequence(from, to+1, false);
    DSequenceInfo dsi = sequence.getSequenceInfo();
    if (dsi!=null){
      seq.setSequenceInfo(
          new DSequenceInfo(
              "["+sequence.getRulerModel().getSeqPos(from)+".."+
                  sequence.getRulerModel().getSeqPos(to)+"]", 
                  dsi.getId()));
    }
    return seq;
  }
  public int getRulerPositionAt(int x){
    int    val;
    double xFactor;
    xFactor = computeScaleFactor();
    if (x<this.getLeftMargin())
      x = this.getLeftMargin();
    if (x>(this.getPreferredSize().width-this.getRightMargin()))
      x = (this.getPreferredSize().width-this.getRightMargin());
    val = (int) (((double)x-(double)this.leftMargin)/xFactor);
    if (val<0) 
      return 0;
    else if (val>sequence.size()-1)
      return val;
    else
      return val;
  }
  public void paintLane(Graphics2D g, Rectangle drawingArea){
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    Color clr = g.getBackground();
    g.setColor(this.getBackgroundClr());
    g.fillRect(drawingArea.x-(leftMargin+rightMargin), drawingArea.y, 
        drawingArea.width+(leftMargin+rightMargin), drawingArea.height);
    g.setColor(clr);
  }
  public void setReferenceLabelSize(int size){
    refLabelWidth = size;
  }
  private double log10(double x) {
    return Math.log(x)/Math.log(10);
  }
  protected int getTickSpacer(int width, double xFactor){
    int xTick;
    double labelWidth = (8.0d*(double)(refLabelWidth)) / xFactor;
    xTick = (int) Math.pow(10,  ((int)log10(labelWidth)) + 1);
    return xTick;
  }

  //Grid is adjusted as we were drawing axis or ruler
  protected void drawGrid(Graphics2D g, double xFactor, Rectangle drawingArea){
    Color clr;
    int i, x3, xTick, from, to, sFrom, sTo, tickFrom, tickTo, sSize;
    
    if (!drawGrid)
      return;
    
    xTick = getTickSpacer(drawingArea.width, xFactor);
    if (xTick==0)
      return;
    tickFrom = drawingArea.y;
    tickTo = drawingArea.y + drawingArea.height;
    sSize = this.getSequence().size()-1;
    //these are panel coordinates
    from = drawingArea.x;
    to = from + drawingArea.width;
    //these are ruler absolute positions in the range [0..seqSize]
    sFrom = (int)((double)from / xFactor);
    //switch to absolute coord in the range [0..sSize]
    sFrom = Math.min(Math.max(0,sFrom), sSize);
    //adjust sFrom so that we are in the xTick numbering
    sFrom = sFrom - (sFrom%xTick) - 2*xTick;
    //do the same conversion for To
    sTo = (int)((double)to / xFactor);
    sTo = Math.min(Math.max(0,sTo), sSize);
    sTo = sTo - (sTo%xTick) + 2*xTick;

    //draw ticks
    clr = g.getColor();
    g.setColor(GRID_CLR);
    xTick = xTick/10;
    for(i=sFrom;i<=sTo;i+=xTick){
      if (i<0) 
        continue;
      if (i>sSize)
        break;
      x3 = this.getLeftMargin() + (int)(xFactor * (double) (i));
      g.drawLine(x3, tickFrom, x3, tickTo);
    }
    g.setColor(clr);
  }

}
