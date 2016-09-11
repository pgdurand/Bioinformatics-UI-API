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

import java.util.HashSet;
import java.util.StringTokenizer;

import bzh.plealog.bioinfo.api.data.feature.Feature;

/**
 * This class is used to check whether or not a feature is of a particular type.
 * 
 * @author Patrick G. Durand
 */
public class FeatureCategoryMatcher {
  private HashSet<String> featKeys;
  private STRAND_TYPE     strandType;
  private boolean         matchIfInCategory;
  private String          name;

  public static enum STRAND_TYPE {ALL};

  public FeatureCategoryMatcher(){
    featKeys = new HashSet<String>();
    strandType = STRAND_TYPE.ALL;
    matchIfInCategory = true;
  }
  /**
   * Constructor.
   * 
   * @param keys a comma separated list of feature types.
   */
  public FeatureCategoryMatcher(String keys){
    this();
    handleTypes(keys);
  }
  /**
   * Constructor.
   * 
   * @param keys a comma separated list of feature types.
   * @param matchIfInCategory if true then a feature will be member of a category if its type
   * matches one of the keys. If false, then a feature will be rejected if its type matches 
   * one of the keys.
   */
  public FeatureCategoryMatcher(String keys, boolean matchIfInCategory){
    this();
    handleTypes(keys);
    this.matchIfInCategory = matchIfInCategory;
  }
  /**
   * Constructor.
   * 
   * @param keys a comma separated list of feature types.
   * @param st strand type. A feature will match a category if its strand matches STRAND_TYPE.
   */
  public FeatureCategoryMatcher(String keys, STRAND_TYPE st){
    this();
    handleTypes(keys);
    strandType = st;
  }
  /**
   * Constructor.
   * 
   * @param keys a comma separated list of feature types.
   * @param st strand type. A feature will match a category if its strand matches STRAND_TYPE.
   * @param matchIfInCategory if true then a feature will be member of a category if its type
   * matches one of the keys. If false, then a feature will be rejected if its type matches 
   * one of the keys
   */
  public FeatureCategoryMatcher(String keys, STRAND_TYPE st, boolean matchIfInCategory){
    this(keys, st);
    this.matchIfInCategory = matchIfInCategory;
  }
  /**
   * Constructor.
   * 
   * @param st strand type. A feature will match a category if its strand matches STRAND_TYPE.
   * @param matchIfInCategory if true then a feature will be member of a category if its type
   * matches one of the keys. If false, then a feature will be rejected if its type matches 
   * one of the keys
   */
  public FeatureCategoryMatcher(STRAND_TYPE st, boolean matchIfInCategory){
    this();
    strandType = st;
    this.matchIfInCategory = matchIfInCategory;
  }
  /**
   * Utility method used to split keys.
   */
  private void handleTypes(String keys){
    StringTokenizer tokenizer;
    if(keys==null)
      return;
    name = keys;
    tokenizer = new StringTokenizer(keys,",");
    while(tokenizer.hasMoreTokens()){
      featKeys.add(tokenizer.nextToken());
    }
  }
  /**
   * Figures out if a feature matches this category.
   * 
   * @param feat the feature to check.
   * @return true if the feature feat matches this category.
   */
  public boolean match(Feature feat){
    boolean answer;

    answer = featKeys.contains(feat.getKey());
    if (strandType!=STRAND_TYPE.ALL){//TODO
    }

    if (!matchIfInCategory)
      answer = !answer;
    return answer;
  }
  /**
   * Return the name of this category matcher.
   */
  public String getName(){
    return name;
  }
}
