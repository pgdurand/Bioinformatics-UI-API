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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;

/**
 * This class is used to organize features on category lanes.
 * 
 * @author Patrick G. Durand
 */
public class FeatureOrganizer {
  private List<FeatureCategoryMatcher> matchers;
  private boolean                      forceOneLanePerCategory;

  /**
   * Creates a feature organizer given a list of feature category matchers.
   * 
   * @param matchers the list of feature category matchers.
   */
  public FeatureOrganizer(List<FeatureCategoryMatcher> matchers){
    this.matchers = matchers;
  }

  /**
   * Creates a feature organizer given a list of feature category matchers.
   * 
   * @param matchers the list of feature category matchers.
   * @param forceOneLanePerCategory if true then all features will be added to a unique lane.
   * Otherwise, as soon as a lane contains spanning features, they will be added to separate
   * lanes.
   */
  public FeatureOrganizer(List<FeatureCategoryMatcher> matchers,
      boolean forceOneLanePerCategory) {
    super();
    this.matchers = matchers;
    this.forceOneLanePerCategory = forceOneLanePerCategory;
  }

  private void processFeature(Feature feat, ArrayList<FeatureForACategory> results){
    int i, size;

    size = matchers.size();
    for(i=0;i<size;i++){
      if (matchers.get(i).match(feat)){
        results.get(i).add(feat);
        return;
      }
    }
  }
  /**
   * Call this method to organize the features on various category lanes.
   * 
   * @param fTable the features to organize.
   * @param xFactor the sequence to pixel conversion factor.
   */
  public List<FeatureForACategory> organize(FeatureTable fTable, double xFactor){
    ArrayList<FeatureForACategory> results;
    FeatureForACategory            cat;
    Enumeration<Feature>           myEnum;
    String                         name;

    //step 1: order features by positions
    fTable.sort(FeatureTable.POS_SORTER);

    //step 2: organize features
    results = new ArrayList<FeatureForACategory>();
    for(int i=0;i<matchers.size();i++){
      name = matchers.get(i).getName();
      cat = new FeatureForACategory(name, xFactor, forceOneLanePerCategory);
      results.add(cat);
    }
    myEnum = fTable.enumFeatures();
    while(myEnum.hasMoreElements()){
      processFeature((Feature) myEnum.nextElement(), results);
    }
    return results;
  }
}
