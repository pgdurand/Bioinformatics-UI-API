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
import java.util.HashSet;
import java.util.List;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.ui.carto.config.FeaturePainterSystem;
import bzh.plealog.bioinfo.ui.carto.core.CartoViewerPanel;
import bzh.plealog.bioinfo.ui.carto.core.FeatureGraphics;
import bzh.plealog.bioinfo.ui.carto.drawer.BasicFeatureDrawingLane;

/**
 * Utility class aims at preparing FeatureTable data for the viewer.
 * 
 * @author Patrick G. Durand
 */
public class BasicFeatureOrganizer {
  private static String _referenceFeatureName;

  public static void setReferenceFeatureName(String referenceFeatureName){
    _referenceFeatureName = referenceFeatureName;
  }

  private static void organizeFeaturesUsingReferenceFeature(CartoViewerPanel viewer, FeatureTable ft, DSequence seq, 
      String[] featureOrdering, boolean forceOneLanePerCategory, int viewerWidth){
    ArrayList<FeatureGraphics>        gFeatures;
    Enumeration<Feature>              features;
    Feature                           feature;
    BasicFeatureDrawingLane           bfdl;
    String                            featKey;

    features = ft.enumFeatures();
    while(features.hasMoreElements()){
      feature = (Feature) features.nextElement();
      gFeatures = new ArrayList<FeatureGraphics>();
      gFeatures.add(new FeatureGraphics(feature, FeaturePainterSystem.getGraphics(feature.getKey()), FeaturePainterSystem.getPainter(feature.getKey())));
      bfdl = new BasicFeatureDrawingLane(seq, gFeatures);
      featKey = feature.getKey();
      bfdl.setLeftLabel(featKey);
      bfdl.setTopMargin(1);
      bfdl.setBottomMargin(1);
      viewer.addDrawingLane(bfdl);
    }

  }
  private static List<FeatureGraphics> getFeatureGraphics(FeatureTable ft){
    ArrayList<FeatureGraphics> data;
    Enumeration<Feature>       myEnum;
    Feature                    feature;
    data = new ArrayList<FeatureGraphics>();
    myEnum = ft.enumFeatures();
    while(myEnum.hasMoreElements()){
      feature = (Feature) myEnum.nextElement();
      data.add(new FeatureGraphics(feature, FeaturePainterSystem.getGraphics(feature.getKey()), FeaturePainterSystem.getPainter(feature.getKey())));
    }
    return data;
  }

  private static void handleFeatures(CartoViewerPanel viewer, FeatureForACategory fac, DSequence seq){
    BasicFeatureDrawingLane           bfdl;
    String                            featKey;
    int                               i, nLanes;

    nLanes = fac.getLanes();
    for(i=0;i<nLanes;i++){
      bfdl = new BasicFeatureDrawingLane(seq, getFeatureGraphics(fac.getFeaturesForLane(i)));
      featKey = fac.getCategoryName();
      bfdl.setLeftLabel(featKey);
      bfdl.setTopMargin(1);
      bfdl.setBottomMargin(1);
      viewer.addDrawingLane(bfdl);
    }
  }
  public static void organizeFeatures(CartoViewerPanel viewer, FeatureTable ft, DSequence seq, 
      boolean forceOneLanePerCategory, int viewerWidth){
    BasicFeatureOrganizer.organizeFeatures(viewer, ft, seq, null, forceOneLanePerCategory, viewerWidth);
  }
  /**
   * Prepare the features for viewing purpose.
   * 
   * @param viewer the panel used to display the features.
   * @param ft the features to display.
   * @param seq the DNA sequence on which the features will be mapped.
   * @param featureOrdering an array of feature types representing the order to use to display
   * feature lanes. Can be null.
   * @param forceOneLanePerCategory if true then all features of a same type are put within a
   * single lane.
   * @param viewerWidth the width of the viewing area. Unit is pixels.
   */
  public static void organizeFeatures(CartoViewerPanel viewer, FeatureTable ft, DSequence seq, 
      String[] featureOrdering, boolean forceOneLanePerCategory, int viewerWidth){
    FeatureOrganizer                  organizer;
    ArrayList<FeatureCategoryMatcher> matchers;
    List<FeatureForACategory>         results;
    String                            featKey;
    HashSet<String>                   featTypes;
    Enumeration<Feature>              enu;

    if (_referenceFeatureName!=null){
      organizeFeaturesUsingReferenceFeature(viewer, ft, seq, featureOrdering, forceOneLanePerCategory, viewerWidth);
      return;
    }
    matchers = new ArrayList<FeatureCategoryMatcher>();
    featTypes = new HashSet<String>();
    enu = ft.enumFeatures();
    while(enu.hasMoreElements()){
      featKey = ((Feature)enu.nextElement()).getKey();
      if (featTypes.contains(featKey)==false){
        featTypes.add(featKey);
        matchers.add(new FeatureCategoryMatcher(featKey));
      }
    }
    organizer = new FeatureOrganizer(matchers, forceOneLanePerCategory);
    results = organizer.organize(ft, (double)viewerWidth/(double) seq.size());

    if (featureOrdering != null){
      featTypes.clear();
      for(String type : featureOrdering){
        for(FeatureForACategory fac : results){
          if (type.equalsIgnoreCase(fac.getCategoryName())){
            featTypes.add(type);
            handleFeatures(viewer, fac, seq);
          }
        }
      }

      for(FeatureForACategory fac : results){
        if (featTypes.contains(fac.getCategoryName())){
          continue;
        }
        handleFeatures(viewer, fac, seq);
      }
    }
    else{
      for(FeatureForACategory fac : results){
        handleFeatures(viewer, fac, seq);
      }
    }

  }

}
