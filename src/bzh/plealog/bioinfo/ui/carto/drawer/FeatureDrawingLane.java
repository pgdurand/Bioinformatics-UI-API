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
package bzh.plealog.bioinfo.ui.carto.drawer;

import java.util.List;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.ui.carto.core.FeatureGraphics;

public interface FeatureDrawingLane {
  
  /**
   * Return the list of features contained in this drawing lane.
   */
  public List<FeatureGraphics> getFeatureTable();

  /**
   * Set the list of features contained in this drawing lane.
   */
  public void setFeatureTable(List<FeatureGraphics> features);
  
  /**
   * Turn on or off visibility status of all features contained in 
   * this drawing lane.
   * 
   *  @param visible visibility status
   */
  public void setFeaturesVisible(boolean visible);
  
  /**
   * Turn on or off visibility status of a particular feature.
   * 
   * @param feat feature for which to switch visibility status
   * @param visible visibility status
   * 
   * @return true if Feature was found in this lane, false otherwise.
   */
  public boolean setFeatureVisible(Feature feat, boolean visible);
}
