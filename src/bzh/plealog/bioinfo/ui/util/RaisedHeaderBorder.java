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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;


/**
 * This is a custom border for the raised header pseudo 3D effect.
 * Adapted from Karsten Lentzsch Plastic Look and Feel.
 *
 * @author Patrick G. Durand
 */
public class RaisedHeaderBorder extends AbstractBorder {

  private static final long serialVersionUID = 7607498793424562762L;
  private static final Insets INSETS = new Insets(1, 1, 1, 0);

  public Insets getBorderInsets(Component c){
    return INSETS;
  }

  public void paintBorder(Component c, Graphics g,
      int x, int y, int w, int h) {

    g.translate(x, y);
    g.setColor(UIManager.getColor("controlLtHighlight"));
    g.fillRect(0, 0, w, 1);
    g.fillRect(0, 1, 1, h-1);
    g.setColor(UIManager.getColor("controlShadow"));
    g.fillRect(0, h-1, w, 1);
    g.translate(-x, -y);
  }
}

