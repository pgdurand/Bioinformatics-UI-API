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
package bzh.plealog.bioinfo.ui.carto.data;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.ui.carto.painter.FeaturePainter;

/**
 * Interface to implement to interact with a FeatureOrganizer.
 * 
 * @author Patrick G. Durand
 */
public interface FeatureOrganizerManager {
  
  /**
   * Let FeatureOrganizer know in which order features have to be placed on feature lanes.
   * 
   * @return an array of feature types representing the order to use to display
   * feature lanes. Can be null.
   */
  public String[] getFeatureOrderingNames();
  
  /**
   * This method can be used to overload default FGraphics setup by the FeatureOrganizer
   * for a particular Feature.
   * 
   * @param feat a feature for which FeatureOrganizer wants to know graphics properties
   * @param fg the default graphics the FeatureOrganizer is going to assign to the feature.
   * If you do not want to modify them, simply return this parameter as it is, or null. If
   * you do want to set different graphic properties, MAKE a copy of fg parameter, upgrade
   * it accordingly and return it. But never modify this parameter.
   * 
   * @return feature graphics to assign to a feature. Can return null.
   */
  public FGraphics getFGraphics(Feature feat, FGraphics fg);
  
  /**
   * This method can be used to overload default FeaturePainter setup by the FeatureOrganizer
   * for a particular Feature.
   * 
   * @param feat a feature for which FeatureOrganizer wants to know FeaturePainter
   * @param fp the default painter the FeatureOrganizer is going to assign to the feature.
   * If you do not want to modify it, simply return this parameter as it is, or null. If
   * you do want to set a different painter, create a new painter and return it. But never
   * modify this parameter.
   * 
   * @return feature graphics to assign to a feature. Can return null.
   */
  public FeaturePainter getFeaturePainter(Feature feat, FeaturePainter fp);
  
  /**
   * Return the name of the feature used as the reference.
   * 
   * @return feature name.
   */
  public String getReferenceFeatureName();
  
}
