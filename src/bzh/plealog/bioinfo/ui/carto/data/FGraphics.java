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

import java.awt.Color;
import java.awt.Stroke;

/**
 * This object contains the graphical properties that can be associated to every feature
 * that can be painted by the viewer system.
 * 
 * @author Patrick G. Durand
 */
public class FGraphics {
  private Color        lineColor;
  private Color        backgroundColor;
  private Stroke       stroke;
  private boolean      paintLine;
  private boolean      paintBackground;

  public FGraphics(){
    lineColor = Color.black;
    backgroundColor = Color.orange.brighter();
    paintLine = paintBackground = true;
  }
  public Object clone(){
    FGraphics fg = new FGraphics();
    fg.copy(this);
    return fg;
  }
  public void copy(FGraphics src){
    this.setBackgroundColor(src.getBackgroundColor());
    this.setLineColor(src.getLineColor());
    this.setPaintBackground(src.isPaintBackground());
    this.setPaintLine(src.isPaintLine());
    this.setStroke(src.getStroke());
  }
  public Color getLineColor() {
    return lineColor;
  }
  public Color getBackgroundColor() {
    return backgroundColor;
  }
  public boolean isPaintLine() {
    return paintLine;
  }
  public boolean isPaintBackground() {
    return paintBackground;
  }
  public void setLineColor(Color lineColor) {
    this.lineColor = lineColor;
  }
  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }
  public void setPaintLine(boolean paintLine) {
    this.paintLine = paintLine;
  }
  public void setPaintBackground(boolean paintBackground) {
    this.paintBackground = paintBackground;
  }
  public Stroke getStroke() {
    return stroke;
  }
  public void setStroke(Stroke stroke) {
    this.stroke = stroke;
  }

}
