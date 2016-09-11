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

import java.awt.Graphics2D;
import java.awt.Rectangle;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.ui.carto.data.FGraphics;

/**
 * This painter is used to display an arrow that starts with a needle. Basically this is a compound
 * FeaturePainter made of a FeatureArrowPainter and a FeatureNeedlePainter.
 * 
 * @author Patrick G. Durand
 */
public class FeatureArrowNeedlePainter extends FeatureArrowPainter {
  private FeatureNeedlePainter needlePainter = new FeatureNeedlePainter();
  public void paintFeature(Graphics2D g, Rectangle box, FGraphics fg, int strand){
    super.paintFeature(g, box, fg, strand);
    Rectangle box2 = new Rectangle(box);
    if (strand==Feature.PLUS_STRAND){
      box2.x -= 3;
      box2.width = 6;
    }
    else{
      box2.x = box2.x+box2.width-3;
      box2.width = 6;
    }
    needlePainter.paintFeature(g, box2, fg, strand);
  }
  public String getName(){
    return "arrowNeedle";
  }

}
