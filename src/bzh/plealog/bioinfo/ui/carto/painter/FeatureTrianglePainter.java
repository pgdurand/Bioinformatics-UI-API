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

import bzh.plealog.bioinfo.ui.carto.data.FGraphics;

/**
 * This painter draws a triangle.
 * 
 * @author Patrick G. Durand
 */
public class FeatureTrianglePainter extends FeaturePainterBase {

  public Polygon computeFeatureShape(Rectangle box){
    Polygon p;
    int     width;
    p = new Polygon();
    width = box.width/2;
    p.addPoint(box.x, box.y+box.height);
    p.addPoint(box.x+width, box.y);
    p.addPoint(box.x+2*width, box.y+box.height);
    return p;
  }
  public void paintFeature(Graphics2D g, Rectangle box, FGraphics fg, int strand) {
    Color   clr;
    Polygon p;

    clr = g.getColor();
    p = computeFeatureShape(box);
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
  public String getName(){
    return "triangle";
  }

}
