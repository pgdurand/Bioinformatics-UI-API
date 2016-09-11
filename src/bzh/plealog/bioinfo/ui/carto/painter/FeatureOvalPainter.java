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
 * This painter draws an oval.
 * 
 * @author Patrick G. Durand
 */
public class FeatureOvalPainter extends FeaturePainterBase {

  public void paintFeature(Graphics2D g, Rectangle box, FGraphics fg, int strand) {
    Color clr;

    clr = g.getColor();
    if (fg.isPaintBackground()){
      g.setColor(fg.getBackgroundColor());
      g.fillOval(box.x, box.y, box.width, box.height);
    }
    if (fg.isPaintLine()){
      g.setColor(fg.getLineColor());
      g.drawOval(box.x, box.y, box.width, box.height);
    }
    g.setColor(clr);
  }
  public String getName(){
    return "oval";
  }

}
