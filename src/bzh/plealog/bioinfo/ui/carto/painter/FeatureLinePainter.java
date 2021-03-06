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
import java.awt.Rectangle;

import bzh.plealog.bioinfo.ui.carto.data.FGraphics;

/**
 * This painter draws a box.
 * 
 * @author Patrick G. Durand
 */
public class FeatureLinePainter extends FeaturePainterBase {

  public void paintFeature(Graphics2D g, Rectangle box, FGraphics fg, int strand) {
    Color clr;
    int   center;

    center = box.height/2;
    clr = g.getColor();
    if (fg.isPaintBackground()){
      g.setColor(fg.getBackgroundColor());
      g.fillRect(box.x, box.y+center-2, box.width, 4);
    }
    if (fg.isPaintLine()){
      g.setColor(fg.getLineColor());
      g.drawRect(box.x, box.y+center-2, box.width, 4);
    }
    g.setColor(clr);
  }
  public String getName(){
    return "line";
  }

}
