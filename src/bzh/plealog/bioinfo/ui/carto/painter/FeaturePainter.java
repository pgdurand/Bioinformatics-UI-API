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

import bzh.plealog.bioinfo.ui.carto.data.FGraphics;

/**
 * This interface defines a feature painter.
 * 
 * @author Patrick G. Durand
 */
public interface FeaturePainter {
  /**
   * Paint a single feature.
   * 
   * @param g the Graphics object
   * @param box the rectangle that contains the feature to draw.
   * @param fg a feature graphics object
   * @param strand the strand orientation. Value is one of Feature.XXX_STRAND.
   */
  public void paintFeature(Graphics2D g, Rectangle box, FGraphics fg, int strand);

  /**
   * Returns the name of this painter.
   */
  public String getName();

  /**
   * Sets a particular user data object to this painter.
   */
  public void setUserData(Object userData);

  /**
   * Gets the user data object. Can be null.
   */
  public Object getUserData();
}
