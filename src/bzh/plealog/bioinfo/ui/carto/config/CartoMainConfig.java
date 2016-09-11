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

import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bzh.plealog.bioinfo.ui.carto.painter.FeaturePainter;
import bzh.plealog.bioinfo.ui.resources.SVMessages;

/**
 * This class is used to configure the Cartographic viewer system.
 * 
 * @author Patrick G. Durand
 */
public class CartoMainConfig {
  private static Log LOGGER = LogFactory.getLog("CartoMainConfig");

  private static boolean initialized = false;

  /**
   * Call this method to configure the viewer. 
   * 
   * @param resource an input stream to a FeatureGraphicsConfig XML formatted resource. If null
   * the system will try to locate and load the resource 'cartoDefaults.conf' located in the
   * resources package of the viewer.
   */
  public static synchronized void initialize(InputStream resource){
    InputStream is = null;

    if (initialized)
      return;
    if (resource==null){
      is = SVMessages.class.getResourceAsStream("cartoDefaults.conf");
    }
    if (is==null){
      LOGGER.warn("no resource bundle to load.");
      return;
    }

    FeatureGraphicsConfig fgc;
    try {
      fgc = FGraphicsSerializer.load(is);
    } catch (FGraphicsSerializerException e) {
      LOGGER.warn(e);
      return;
    }
    FGraphicsAtom         atom;
    FeaturePainter        fp;
    Iterator<String>      types;
    String                type;

    types = fgc.getGraphicTypes();
    while(types.hasNext()){
      type = types.next();
      atom = fgc.getGraphics(type);
      FeaturePainterSystem.addGraphics(type.toLowerCase(), atom.getGraphics());
      fp = FeaturePaintersConfig.getPainter(atom.getPainter());
      if (fp==null){
        LOGGER.warn("Painter "+atom.getPainter()+" unknown for feature: "+type);
      }
      else{
        FeaturePainterSystem.addPainter(type.toLowerCase(), fp);
      }
    }
    initialized = true;
  }
}
