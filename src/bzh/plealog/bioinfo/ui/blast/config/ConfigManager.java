/* Copyright (C) 2003-2016 Patrick G. Durand
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
package bzh.plealog.bioinfo.ui.blast.config;

import java.util.Hashtable;

import bzh.plealog.bioinfo.ui.blast.hittable.HitTableFactoryImplem;
import bzh.plealog.bioinfo.ui.blast.saviewer.SeqAlignViewerFactoryImplem;

/**
 * This class is the configuration manager of BLASTViewer. It stores a list of
 * available configuration using a Map where the key is the name of a
 * configuration and the corresponding value is an AbstractConfig object.
 * 
 * @author Patrick G. Durand
 */
public class ConfigManager {
  private static Hashtable<String, AbstractPropertiesConfig> _config = new Hashtable<>();
  private static HitTableFactory _htFactory = new HitTableFactoryImplem();
  private static SeqAlignViewerFactory _saFactory = new SeqAlignViewerFactoryImplem();
  private static boolean enableSerialApplicationProperty = false;
  
  /**
   * Add a new configuration.
   * 
   * @param config
   *          the configuration object
   */
  public static void addConfig(AbstractPropertiesConfig config) {
    if (config == null)
      return;
    _config.put(config.getName(), config);
  }

  /**
   * 
   * Get a configuration by bame.
   * 
   * @param name
   *          the name of the configuration object to retrieve
   * 
   * @return the requested object or null.
   */
  public static AbstractPropertiesConfig getConfig(String name) {
    return ((AbstractPropertiesConfig) _config.get(name));
  }
  
  public static HitTableFactory getHitTableFactory(){
    return _htFactory;
  }
  
  public static void setHitTableFactory(HitTableFactory htFactory){
    _htFactory = htFactory;
  }
  public static SeqAlignViewerFactory getSeqAlignViewerFactory(){
    return _saFactory;
  }
  
  public static void setSeqAlignViewerFactory(SeqAlignViewerFactory saFactory){
    _saFactory = saFactory;
  }

  public static boolean isEnableSerialApplicationProperty() {
    return enableSerialApplicationProperty;
  }

  public static void setEnableSerialApplicationProperty(
      boolean enableSerialApplicationProperty) {
    ConfigManager.enableSerialApplicationProperty = enableSerialApplicationProperty;
  }
  
  
}
