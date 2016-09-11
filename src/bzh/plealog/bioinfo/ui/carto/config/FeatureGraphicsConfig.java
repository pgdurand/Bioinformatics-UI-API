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

/**
 * Utility class containing the type of features handled by the Cartographic Viewer.
 * 
 * @author Patrick G. Durand
 */
public class FeatureGraphicsConfig {

  private Hashtable<String, FGraphicsAtom> entries;

  public FeatureGraphicsConfig(){
    entries = new Hashtable<String, FGraphicsAtom>();
  }

  public void add(String type, FGraphicsAtom fg){
    entries.put(type, fg);
  }
  public int size(){
    return entries.size();
  }
  public FGraphicsAtom getGraphics(String type){
    return entries.get(type);
  }
  public void clear(){
    entries.clear();
  }
  public void remove(String type){
    entries.remove(type);
  }
  public Iterator<String> getGraphicTypes(){
    return entries.keySet().iterator();
  }
  //For XStream serialization only. Do not use otherwise.
  public Hashtable<String, FGraphicsAtom> getFeatureGraphics() {
    return entries;
  }

  //For XStream erialization only. Do not use otherwise.
  public void setFeatureGraphics(Hashtable<String, FGraphicsAtom> featureGraphics) {
    this.entries = featureGraphics;
  }

}
