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
 * This painter draws a needle.
 * 
 * @author Patrick G. Durand
 */
public class FeatureNeedlePainter extends FeaturePainterBase {
  private int needleHeadSize = 4;

  //Note; the needle in centered in the box
  public void paintFeature(Graphics2D g, Rectangle box, FGraphics fg, int strand) {
    Color clr;
    int   middle;
    clr = g.getColor();
    g.setColor(fg.getLineColor());
    middle = box.x + box.width/2;
    g.drawLine(middle, box.y, middle, box.y+box.height);

    if (fg.isPaintBackground()){
      g.setColor(fg.getBackgroundColor());
      g.fillOval(middle-needleHeadSize/2, box.y, needleHeadSize, needleHeadSize);
    }
    if (fg.isPaintLine()){
      g.setColor(fg.getLineColor());
      g.drawOval(middle-needleHeadSize/2, box.y, needleHeadSize, needleHeadSize);
    }
    g.setColor(clr);
  }
  public String getName(){
    return "needle";
  }

}
