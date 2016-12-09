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

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSystem;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureTableFactory;

/**
 * This class stores all the features matching a particular category.
 * 
 * @author Patrick G. Durand
 */
public class FeatureForACategory {
  private ArrayList<OrganizedFeatureTable> organizedFeatures;
  private double                           factor;
  private String                           catName;
  private boolean                          forceOneLanePerCategory;

  private FeatureForACategory(){
    super();
    organizedFeatures = new ArrayList<OrganizedFeatureTable>();
  }
  /**
   * Constructor.
   * @param factor the sequence to pixel conversion factor.
   */
  public FeatureForACategory(double factor){
    this();
    this.factor = factor;
  }

  /**
   * Constructor.
   * @param catName the category name.
   * @param factor the sequence to pixel conversion factor.
   */
  public FeatureForACategory(String catName, double factor){
    this();
    this.factor = factor;
    this.catName = catName;
  }
  /**
   * Constructor.
   * @param catName the category name.
   * @param factor the sequence to pixel conversion factor.
   * @param forceOneLanePerCategory if true then all features will be added to a unique lane.
   * Otherwise, as soon as a lane contains spanning features, they will be added to separate
   * lanes.
   */
  public FeatureForACategory(String catName, double factor, 
      boolean forceOneLanePerCategory) {
    this();
    this.factor = factor;
    this.catName = catName;
    this.forceOneLanePerCategory = forceOneLanePerCategory;
  }
  /**
   * Adds a feature.
   */
  protected void add(Feature feat){
    for(OrganizedFeatureTable oft : organizedFeatures){
      if (oft.addFeature(feat))
        return;
    }
    OrganizedFeatureTable oft = new OrganizedFeatureTable(factor);
    oft.addFeature(feat);
    organizedFeatures.add(oft);
  }
  /**
   * Returns the number of lanes contained in this object.
   */
  public int getLanes(){
    return organizedFeatures.size();
  }
  /**
   * Returns the features from a particular lane.
   */
  public FeatureTable getFeaturesForLane(int idx){
    return organizedFeatures.get(idx).getFTable();
  }
  /**
   * Return the category name.
   */
  public String getCategoryName(){
    return catName;
  }
  /**
   * Utility class used during features handling.
   */
  private class OrganizedFeatureTable {
    private FeatureTable fTable;
    private double       xFactor;
    private int          positionCursor = -1;

    private OrganizedFeatureTable(){
      super();
      FeatureTableFactory ftFactory = FeatureSystem.getFeatureTableFactory();
      fTable = ftFactory.getFTInstance();
    }

    public OrganizedFeatureTable(double factor) {
      this();
      xFactor = factor;
    }
    public FeatureTable getFTable() {
      return fTable;
    }
    public boolean canAddFeature(Feature feat){
      if (forceOneLanePerCategory)
        return true;
      int pixPos = (int)(xFactor * (double) feat.getFrom());
      return (pixPos>positionCursor);
    }
    public boolean addFeature(Feature feat){
      boolean bRet = canAddFeature(feat);
      if (bRet){
        fTable.addFeature(feat);
        positionCursor = (int)(xFactor * (double) feat.getTo());
      }
      return bRet;
    }
  }

}
