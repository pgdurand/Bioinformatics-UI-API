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
package bzh.plealog.bioinfo.ui.carto.painter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.ui.carto.data.FGraphics;

/**
 * This painter draws an arrow.
 * 
 * @author Patrick G. Durand
 */
public class FeatureArrowPainter extends FeaturePainterBase {
  private Polygon computeLeftArrow(Rectangle box){
    Polygon p;
    int     ybase, ydecal, left, right, ydecal2;

    ybase = box.y+box.height/2;
    ydecal = Math.max(1, box.height/4);
    ydecal2 = 2*ydecal;
    left=box.x+4;
    right = box.x+box.width;
    if (box.width<5){
      p = new Polygon();
      //left middle
      p.addPoint(box.x, ybase);
      //right top
      p.addPoint(right, ybase-ydecal2);
      //right bottom
      p.addPoint(right, ybase+ydecal2);
    }
    else{
      p = new Polygon();
      //arrow (top half part)
      p.addPoint(box.x, ybase);//left end
      p.addPoint(left, ybase-ydecal2);
      //box
      p.addPoint(left, ybase-ydecal);
      p.addPoint(right, ybase-ydecal);
      p.addPoint(right, ybase+ydecal);
      p.addPoint(left, ybase+ydecal);
      //arrow (bottom half part)
      p.addPoint(left, ybase+ydecal2);
    }
    return p;
  }

  private Polygon computeRightArrow(Rectangle box){
    Polygon p;
    int     ybase, ydecal, right, ydecal2;
    ybase = box.y+box.height/2;
    ydecal = Math.max(1, box.height/4);
    ydecal2 = 2*ydecal;
    if (box.width<5){
      p = new Polygon();
      right=box.x+box.width;
      //top left
      p.addPoint(box.x, ybase-ydecal2);
      //middle right
      p.addPoint(right, ybase);
      //bottom left
      p.addPoint(box.x, ybase+ydecal2);
    }
    else{
      right=box.x+box.width-4;
      p = new Polygon();
      //arrow (top half part)
      p.addPoint(right+4, ybase);//right end
      p.addPoint(right, ybase-ydecal2);//top
      //box
      p.addPoint(right, ybase-ydecal);
      p.addPoint(box.x, ybase-ydecal);
      p.addPoint(box.x, ybase+ydecal);
      p.addPoint(right, ybase+ydecal);
      //arrow (bottom half part)
      p.addPoint(right, ybase+ydecal2);//bottom
    }
    return p;
  }
  public void paintFeature(Graphics2D g, Rectangle box, FGraphics fg, int strand) {
    Color   clr;
    Polygon p;

    clr = g.getColor();
    if (strand==Feature.PLUS_STRAND)
      p = computeRightArrow(box);
    else
      p = computeLeftArrow(box);
    if (fg.isPaintBackground()){
      g.setColor(fg.getBackgroundColor());
      g.fillPolygon(p);
    }
    if (fg.isPaintLine()){
      g.setColor(fg.getLineColor());
      g.drawPolygon(p);
    }
    g.setColor(clr);
  }

  @Override
  public String getName(){
    return "arrow";
  }

}
