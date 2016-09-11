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
import java.util.Iterator;

import bzh.plealog.bioinfo.ui.carto.painter.FeatureArrowNeedlePainter;
import bzh.plealog.bioinfo.ui.carto.painter.FeatureArrowPainter;
import bzh.plealog.bioinfo.ui.carto.painter.FeatureBoxPainter;
import bzh.plealog.bioinfo.ui.carto.painter.FeatureLinePainter;
import bzh.plealog.bioinfo.ui.carto.painter.FeatureNeedlePainter;
import bzh.plealog.bioinfo.ui.carto.painter.FeatureOvalPainter;
import bzh.plealog.bioinfo.ui.carto.painter.FeaturePainter;
import bzh.plealog.bioinfo.ui.carto.painter.FeatureTrianglePainter;

/**
 * Utility class containing the type of painters handled by the Cartographic Viewer.
 * 
 * @author Patrick G. Durand
 */
public class FeaturePaintersConfig {
  private static Hashtable<String, FeaturePainter> painters;

  @SuppressWarnings("rawtypes")
  private static Class[] painterClasses = {
    FeatureArrowNeedlePainter.class,
    FeatureArrowPainter.class,
    FeatureBoxPainter.class,
    FeatureLinePainter.class,
    FeatureNeedlePainter.class,
    FeatureOvalPainter.class,
    FeatureTrianglePainter.class
  };

  static{
    painters = new Hashtable<String, FeaturePainter>();
    FeaturePainter fp;
    for(int i=0;i<painterClasses.length;i++){
      try {
        fp = (FeaturePainter) painterClasses[i].newInstance();
        painters.put(fp.getName(), fp);
      } catch (Exception e) {
        System.out.println("Unable to initialize FeaturePaintersConfig: "+e);
      }
    }
  }

  public static FeaturePainter getPainter(String name){
    return painters.get(name);
  }
  public static int size(){
    return painters.size();
  }
  public static Iterator<String> getPainterNames(){
    return painters.keySet().iterator();
  }


}
