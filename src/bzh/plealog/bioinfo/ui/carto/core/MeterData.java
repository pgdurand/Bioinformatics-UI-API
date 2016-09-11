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
package bzh.plealog.bioinfo.ui.carto.core;

/**
 * The data structure to be used with a FeatureMeterBoxPainter.
 * 
 * @author Patrick G. Durand
 */
public class MeterData {
  private double maximum;
  private double value;

  public MeterData(){}

  public MeterData(double maximum, double value) {
    super();
    this.maximum = maximum;
    this.value = value;
  }

  public double getMaximum() {
    return maximum;
  }
  public void setMaximum(double maximum) {
    this.maximum = maximum;
  }
  public double getValue() {
    return value;
  }
  public void setValue(double value) {
    this.value = value;
  }


}
