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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * This class contains utility methods used to serialize feature graphics.
 * 
 * @author Patrick G. Durand
 */
public class FGraphicsSerializer {
  private static XStream streamer;

  static {
    streamer = new XStream(new DomDriver("ISO-8859-1"));
    streamer.alias("FeatureGraphics", FeatureGraphicsConfig.class);
    streamer.alias("Feature", FGraphicsAtom.class);
  }
  /**
   * Save data.
   */
  public static void save(FeatureGraphicsConfig fTypes, File f) throws FGraphicsSerializerException{
    FileOutputStream fos = null;

    try {
      fos= new FileOutputStream(f);
      streamer.toXML(fTypes, fos);
      fos.flush();
    } catch (Exception e) {
      throw new FGraphicsSerializerException("Unable to save FeatureGraphics in: "+
          f.getAbsolutePath()+": "+e);
    }
    finally{
      try{if (fos!=null) fos.close();}catch(Exception ex){}
    }
  }
  /**
   * Load data.
   */
  public static FeatureGraphicsConfig load(File f) throws FGraphicsSerializerException{
    FileInputStream fis = null;
    FeatureGraphicsConfig    fTypes = null;
    try {
      fis= new FileInputStream(f);
      fTypes = (FeatureGraphicsConfig) streamer.fromXML(fis);
    } catch (Exception e) {
      throw new FGraphicsSerializerException("Unable to load FeatureGraphics from: "+
          f.getAbsolutePath()+": "+e);
    }
    finally{
      try{if (fis!=null) fis.close();}catch(Exception ex){}
    }
    return fTypes;
  }
  /**
   * Load data.
   */
  public static FeatureGraphicsConfig load(InputStream is) throws FGraphicsSerializerException{
    FeatureGraphicsConfig    fTypes = null;
    try {
      fTypes = (FeatureGraphicsConfig) streamer.fromXML(is);
    } catch (Exception e) {
      throw new FGraphicsSerializerException("Unable to load FeatureGraphics: "+e);
    }
    return fTypes;
  }

}
