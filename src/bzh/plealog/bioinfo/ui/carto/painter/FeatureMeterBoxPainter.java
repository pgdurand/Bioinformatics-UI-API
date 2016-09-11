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

import bzh.plealog.bioinfo.ui.carto.core.MeterData;
import bzh.plealog.bioinfo.ui.carto.data.FGraphics;

/**
 * This painter draws a box.
 * 
 * @author Patrick G. Durand
 */
public class FeatureMeterBoxPainter extends FeatureBoxPainter {
  private MeterData data;
  private double    fract;

  public void paintFeature(Graphics2D g, Rectangle box, FGraphics fg, int strand) {
    if (data==null){
      super.paintFeature(g, box, fg, strand);
      return;
    }
    Color clr;
    int   barHeight;

    barHeight = (int) ((double) box.height * fract);
    if (barHeight>box.height)
      barHeight = box.height;
    clr = g.getColor();
    if (fg.isPaintBackground()){
      g.setColor(fg.getBackgroundColor());
      if (barHeight>0)
        g.fillRect(box.x, box.y+box.height-barHeight, box.width, barHeight);
    }
    if (fg.isPaintLine()){
      g.setColor(fg.getLineColor());
      g.drawRect(box.x, box.y, box.width, box.height);
    }
    g.setColor(clr);
  }

  public void setUserData(Object data) {
    if (data instanceof MeterData){
      this.data = (MeterData) data;
      fract = (double) this.data.getValue() / (double) this.data.getMaximum();
    }
    else{
      this.data = null;
      fract = 1.0f;
    }
  }

  public Object getUserData() {
    return data;
  }

}
