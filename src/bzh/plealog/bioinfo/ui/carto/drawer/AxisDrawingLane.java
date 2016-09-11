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
import java.awt.Graphics2D;
import java.awt.Rectangle;

import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.ui.carto.event.SViewerSelectionEvent;

/**
 * This class is used to draw a ruler axis.
 * 
 * @author Patrick G. Durand
 */
public class AxisDrawingLane extends DrawingLaneBase {
  /**Available types of axis ticks*/
  public static enum TICK_TYPE {UP, DOWN, BOTH};

  private TICK_TYPE tickType = TICK_TYPE.UP;

  /**
   * Standard constructor.
   * 
   * @param seq the sequence used to prepare the axis.
   */
  public AxisDrawingLane(DSequence seq){
    super(seq);
  }

  /**
   * Standard constructor.
   * 
   * @param seq the sequence used to prepare the axis.
   * @param tt tick type
   */
  public AxisDrawingLane(DSequence seq, TICK_TYPE tt){
    this(seq);
    setTickType(tt);
  }

  public TICK_TYPE getTickType() {
    return tickType;
  }

  public void setTickType(TICK_TYPE tickType) {
    this.tickType = tickType;
  }

  private void paintTicks(Graphics2D g, double xFactor, Rectangle drawingArea){
    int i, x3, yBase, xTick, from, to, sFrom, sTo, tickFrom, tickTo, x1, x2, sSize;

    xTick = getTickSpacer(drawingArea.width, xFactor);

    yBase = drawingArea.y + drawingArea.height / 2;

    switch(tickType){
      case UP:
        tickFrom = yBase-3;
        tickTo = yBase;
        break;
      case DOWN:
        tickFrom = yBase;
        tickTo = yBase+3;
        break;
      case BOTH:
      default:
        tickFrom = yBase-3;
        tickTo = yBase+3;
        break;
    }

    sSize = this.getSequence().size()-1;

    //these are panel coordinates
    from = drawingArea.x;
    to = from + drawingArea.width;// - this.getRightMargin();
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

    if (sFrom<0)
      x1 = 0;
    else
      x1 = sFrom;
    if (sTo>sSize)
      x2 = sSize;
    else
      x2 = sTo;
    g.setColor(Color.black);
    g.drawLine(this.getLeftMargin() + (int)(xFactor * (double) x1), yBase, 
        this.getLeftMargin() + (int)(xFactor * (double) x2), yBase);
    //draw ticks
    for(i=sFrom;i<=sTo;i+=xTick){
      if (i<0) 
        continue;
      if (i>sSize)
        break;
      x3 = this.getLeftMargin() + (int)(xFactor * (double) (i));
      g.drawLine(x3, tickFrom, x3, tickTo);
    }
    //starting tick
    if (0>=sFrom && 0<=sTo){
      x3 = this.getLeftMargin() /*+ (int)(xFactor * (double) (0))*/;
      g.drawLine(x3, tickFrom, x3, tickTo);
    }
    //ending tick
    if (sSize>=sFrom && sSize<=sTo){
      x3 = this.getLeftMargin() + (int)(xFactor * (double) sSize);
      g.drawLine(x3, tickFrom, x3, tickTo);
    }
    //draw additional ticks
    switch(tickType){
      case UP:
        tickFrom = yBase-1;
        tickTo = yBase;
        break;
      case DOWN:
        tickFrom = yBase;
        tickTo = yBase+1;
        break;
      case BOTH:
      default:
        tickFrom = yBase-1;
        tickTo = yBase+1;
        break;
    }
    xTick = xTick/10;
    for(i=sFrom;i<=sTo;i+=xTick){
      if (i<0) 
        continue;
      if (i>sSize)
        break;
      x3 = this.getLeftMargin() + (int)(xFactor * (double) (i));
      g.drawLine(x3, tickFrom, x3, tickTo);
    }
  }

  public void paintLane(Graphics2D g, Rectangle drawingArea) {
    super.paintLane(g, drawingArea);
    paintTicks(g, this.computeScaleFactor(), drawingArea);
  }
  public void objectSelected(SViewerSelectionEvent event){
  }
}
