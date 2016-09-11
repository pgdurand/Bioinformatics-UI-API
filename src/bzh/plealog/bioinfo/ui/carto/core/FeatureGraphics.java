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
package bzh.plealog.bioinfo.ui.carto.core;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.ui.carto.data.FGraphics;
import bzh.plealog.bioinfo.ui.carto.painter.FeaturePainter;

/**
 * This class can be used to associate a feature to a graphical object.
 * 
 * @author Patrick G. Durand
 */
public class FeatureGraphics {
  private Feature feature;
  private FGraphics fGraphics;
  private FeaturePainter fPainter;

  /**
   * Standard constructor.
   * 
   * @param feature a feature
   * @param graphics the associated graphic object
   */
  public FeatureGraphics(Feature feature, FGraphics graphics, FeaturePainter painter) {
    super();
    this.feature = feature;
    fGraphics = graphics;
    fPainter = painter;
  }
  public Feature getFeature() {
    return feature;
  }
  public FGraphics getFGraphics() {
    return fGraphics;
  }
  public void setFeature(Feature feature) {
    this.feature = feature;
  }
  public void setFGraphics(FGraphics graphics) {
    fGraphics = graphics;
  }
  public FeaturePainter getFPainter() {
    return fPainter;
  }
  public void setFPainter(FeaturePainter fPainter) {
    this.fPainter = fPainter;
  }
}
