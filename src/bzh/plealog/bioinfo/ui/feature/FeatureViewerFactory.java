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
package bzh.plealog.bioinfo.ui.feature;

/**
 * FeatureViewer instances factory.
 * 
 * @author Patrick G. Durand
 */
public class FeatureViewerFactory {

  public enum TYPE {COMBO, LIST}
  
  private FeatureViewerFactory() { }

  /**
   * Get a new instance of a FeatureViewer.
   * 
   * @param t the FeatureViewer type to create
   * @param fwl a FeatureWebLinker instance
   * @param showQualTable figures out whether or not the Qualifier table
   * has to be displayed
   * 
   * @return a new FeatureViewer instance
   */
  public static FeatureViewer getInstance(TYPE t, FeatureWebLinker fwl, boolean showQualTable){
    switch(t){
    case LIST:
      return new FeatureViewerList(fwl, showQualTable);
    case COMBO:
      default:
      return new FeatureViewerCombo(fwl, showQualTable);
    }
  }
}
