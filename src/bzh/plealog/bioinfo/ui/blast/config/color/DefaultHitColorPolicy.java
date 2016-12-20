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

/**
 * This class is responsible for providing a default color framework to display
 * Blast data with colors based upon alignment bit scores. It is used in case no
 * ColorPolicyConfig is available.
 * 
 * @author Patrick G. Durand
 *
 */
public class DefaultHitColorPolicy {

  /**
   * Given a bit score, returned the appropriate color.
   * 
   * @param score a score
   * 
   * @return the color to use to that score
   */
  public static Color getColor(int score) {
    if (score >= 200)
      return (Color.red);
    else if (score >= 80)
      return (Color.magenta);
    else if (score >= 50)
      return (Color.green.darker());
    else if (score >= 40)
      return (Color.blue);
    else
      return (Color.black);
  }
}
