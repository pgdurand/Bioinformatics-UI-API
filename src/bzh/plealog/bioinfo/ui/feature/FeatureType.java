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
package bzh.plealog.bioinfo.ui.feature;

import bzh.plealog.bioinfo.ui.util.CheckBoxListItem;

public class FeatureType extends CheckBoxListItem implements Comparable<FeatureType>{
   private int    count;
   private String repr;

   public FeatureType(String name, int count) {
     super(name);
     this.count = count;
   }
   public String getName() {
     return getLabel();
   }
   public int getCount() {
     return count;
   }
   public void setCount(int count) {
     this.count = count;
     repr = null;
   }
   public String toString(){
     if (repr!=null)
       return repr;
     StringBuffer buf = new StringBuffer(getLabel());
     buf.append(" [");
     buf.append(count);
     buf.append("]");
     repr = buf.toString();
     return repr;
   }
   public int compareTo(FeatureType ft){
     return this.getName().compareTo(ft.getName());
   }
 }