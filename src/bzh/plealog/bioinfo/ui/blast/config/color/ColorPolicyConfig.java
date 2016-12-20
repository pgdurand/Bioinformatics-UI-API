/* Copyright (C) 2003-2016 Patrick G. Durand
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
package bzh.plealog.bioinfo.ui.blast.config.color;

import java.awt.Color;

import javax.swing.ImageIcon;

import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.ui.blast.config.AbstractPropertiesConfig;

/**
 * This class contains the color policy used to display colored Blast alignment,
 * scores and so on.
 * 
 * @author Patrick G. Durand
 */
public abstract class ColorPolicyConfig extends AbstractPropertiesConfig {
  public static final String      NAME                  = "ColorPolicyConfig";

  public static Color             BK_COLOR              = Color.WHITE;
  public static final int         TRANSPARENCY_FACTOR   = 128;

  /**
   * Check if color transparency is used.
   * 
   * @return true or false
   */
  public abstract boolean isUsingColorTransparency();

  /**
   * Check if anti-alias is used.
   * 
   * @return true of false
   */
  public abstract boolean isUsingAntialias();

  /**
   * Given a bitScore return the color defined by this color policy.
   * 
   * @param hsp
   *          a SRHsp object
   * @param alpha
   *          use alpha transparency or not
   * 
   * @return a Color
   */
  public abstract Color getHitColor(SRHsp hsp, boolean alpha);

  /**
   * Given a bitScore return the quality defined by this color policy.
   * 
   * @param hsp
   *          a SRHsp object
   * 
   * @return a quality value
   */
  public abstract int getQualityValue(SRHsp hsp);

  /**
   * Given a bitScore returns the quality icon defined by this color policy.
   * 
   * @param hsp
   *          a SRHsp object
   * 
   * @return a quality icon
   */
  public abstract ImageIcon getQualityIcon(SRHsp hsp);


}
