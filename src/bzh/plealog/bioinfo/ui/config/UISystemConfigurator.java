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
package bzh.plealog.bioinfo.ui.config;

import com.plealog.genericapp.api.EZEnvironment;

import bzh.plealog.bioinfo.ui.carto.config.CartoMainConfig;

/**
 * This is the UI system. Always call initializeSystem method before using the API.
 * 
 * @author Patrick G. Durand
 */
public class UISystemConfigurator {
  private static boolean  _bInited = false;

  static{
    initializeSystem();
  }

  /**
   * Initialize core system.
   */
  public static final void initializeSystem(){
    if (_bInited)
      return;
    CartoMainConfig.initialize(null);
    EZEnvironment.addResourceLocator(bzh.plealog.bioinfo.ui.resources.SVMessages.class);
    _bInited = true;
  }
}
