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
package bzh.plealog.bioinfo.ui.carto.config;

import java.util.Hashtable;

import bzh.plealog.bioinfo.ui.carto.data.FGraphics;
import bzh.plealog.bioinfo.ui.carto.painter.FeatureArrowPainter;
import bzh.plealog.bioinfo.ui.carto.painter.FeaturePainter;

/**
 * This is a central repository of graphical objects used by the viewer system.
 * 
 * @author Patrick G. Durand
 */
public class FeaturePainterSystem {
  private static Hashtable<String, FeaturePainter> painters = new Hashtable<String, FeaturePainter>();
  private static Hashtable<String, FGraphics>      graphics = new Hashtable<String, FGraphics>();
  private static FeaturePainter                    defaultPainter = new FeatureArrowPainter();
  private static FGraphics                         defaultFGraphics = new FGraphics();

  /**
   * Adds a painter for a particular type of feature.
   */
  public static void addPainter(String featureType, FeaturePainter painter){
    painters.put(featureType, painter);
  }

  /**
   * Adds a graphics for a particular type of feature.
   */
  public static void addGraphics(String featureType, FGraphics gr){
    if (!graphics.containsKey(featureType))
      graphics.put(featureType, gr);
  }
  /**
   * Returns a painter given a feature type.
   */
  public static FeaturePainter getPainter(String featureType){
    if (featureType==null)
      return defaultPainter;
    String fType = featureType.toLowerCase();
    if (painters.containsKey(fType))
      return painters.get(fType);
    else
      return defaultPainter;
  }

  /**
   * Returns a graphics given a feature type.
   */
  public static FGraphics getGraphics(String featureType){
    if (featureType==null)
      return defaultFGraphics;
    String fType = featureType.toLowerCase();
    if (graphics.containsKey(fType))
      return graphics.get(fType);
    else
      return defaultFGraphics;
  }
  /**
   * Returns an instance of a FeatureArrowPainter.
   */
  public static FeaturePainter getDefaultPainter() {
    return defaultPainter;
  }
  /**
   * Sets a new default painter.
   */
  public static void setDefaultPainter(FeaturePainter defaultPainter) {
    FeaturePainterSystem.defaultPainter = defaultPainter;
  }

  /**
   * Returns a default graphics object.
   */
  public static FGraphics getDefaultFGraphics() {
    return defaultFGraphics;
  }

  /**
   * Returns a new default graphics object.
   */
  public static void setDefaultFGraphics(FGraphics defaultFGraphics) {
    FeaturePainterSystem.defaultFGraphics = defaultFGraphics;
  }

}
