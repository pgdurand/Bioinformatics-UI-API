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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import bzh.plealog.bioinfo.api.data.sequence.DRulerModel;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.ui.carto.event.SViewerSelectionEvent;

/**
 * This class is used to draw the values associated to a ruler axis.
 * 
 * @author Patrick G. Durand
 */
public class RulerDrawingLane extends DrawingLaneBase {

  public RulerDrawingLane(DSequence seq){
    super(seq);
  }

  private void paintRuler(Graphics2D g, double xFactor, Rectangle drawingArea){
    DRulerModel rModel;
    String      str;
    FontMetrics fm;
    int         i, x2, x3, yBase, xTick, from, to, sFrom, sTo, sSize, lastX, w, firstX;

    rModel = this.getSequence().getRulerModel();
    g.setColor(Color.black);
    xTick = getTickSpacer(drawingArea.width, xFactor);
    if (xTick==0)
      return;
    sSize = this.getSequence().size()-1;
    fm = g.getFontMetrics(this.getFont());
    yBase = drawingArea.y + drawingArea.height / 2+fm.getHeight()/2;
    //these are panel coordinates
    from = drawingArea.x;
    to = from + drawingArea.width;
    //these are ruler absolute positions in the range [0..sSize] 
    sFrom = (int)((double)from / xFactor); 
    sFrom = Math.min(Math.max(0,sFrom), sSize);
    sFrom = sFrom - (sFrom%xTick) - 2*xTick;
    sTo = (int)((double)to / xFactor);
    sTo = Math.min(Math.max(0,sTo), sSize);
    sTo = sTo - (sTo%xTick) + 2*xTick;
    lastX=0;firstX=-1;
    for(i=sFrom;i<=sTo;i+=xTick){
      if (i<0) 
        continue;
      if (i>(this.getSequence().size()-1))
        break;
      x2 = rModel.getSeqPos(i);
      x3 = this.getLeftMargin() + (int)(xFactor * (double) (i));
      str = String.valueOf(x2);
      w = fm.stringWidth(str)/2;
      lastX = x3+w;
      if (firstX==-1)
        firstX=lastX;
      if (x2!=-1)
        g.drawString(str, x3 - w, yBase);
    }
    if (0>=sFrom && 0<=sTo){
      x3 = this.getLeftMargin() + (int)(xFactor * (double) (0));
      str = String.valueOf(rModel.getStartPos());
      w = x3-fm.stringWidth(str)/2;
      if (w>firstX)
        g.drawString(str, w, yBase);
      g.drawString(str, x3 - fm.stringWidth(str)/2, yBase);
    }
    if (sSize>=sFrom && sSize<=sTo){
      x3 = this.getLeftMargin() + (int)(xFactor * (double) sSize);
      str = String.valueOf(rModel.getSeqPos(sSize));
      w = x3-fm.stringWidth(str)/2;
      if (w>lastX)
        g.drawString(str, w, yBase);
    }
  }

  public void paintLane(Graphics2D g, Rectangle drawingArea) {
    super.paintLane(g, drawingArea);
    paintRuler(g, this.computeScaleFactor(), drawingArea);
  }
  public void objectSelected(SViewerSelectionEvent event){
  }

}
