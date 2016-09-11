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
package bzh.plealog.bioinfo.ui.util;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JPanel;


/**
 * A basic implementation of a JPanel with a gradient colored background.
 *  
 * @author Patrick G. Durand
 */

public class GradientPanel extends JPanel {
  private static final long serialVersionUID = 3645214319959566285L;
  private boolean _selected;
  private int     _orientation;
  private Color   _color1;
  private Color   _color2;

  public static final int GRAD_ORIENTATION_LtoR   = 0;
  public static final int GRAD_ORIENTATION_TLtoBR = 1;

  public GradientPanel(Color color1, Color color2) {
    super();
    _color1 = color1;
    _color2 = color2;
  }
  public void setGradientOrientation(int orientation){
    _orientation = orientation;
  }
  public void setSelected(boolean sel){
    _selected = sel;
  }
  public boolean isSelected(){
    return _selected;
  }
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (!isOpaque() || isSelected()) {
      return;
    }
    int width  = getWidth();
    int height = getHeight();

    Graphics2D g2 = (Graphics2D) g;
    Paint storedPaint = g2.getPaint();
    GradientPaint gp;
    switch(_orientation){
      case GRAD_ORIENTATION_TLtoBR:
        gp = new GradientPaint(0, 0, _color1, width, height, _color2);
        break;
      default:
        gp = new GradientPaint(0, 0, _color1, width, 0, _color2);
    }
    g2.setPaint(gp);

    g2.fillRect(0, 0, width, height);
    g2.setPaint(storedPaint);
  }
}
