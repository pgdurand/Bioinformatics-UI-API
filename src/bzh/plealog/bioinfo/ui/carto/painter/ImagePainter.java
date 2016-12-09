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
package bzh.plealog.bioinfo.ui.carto.painter;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.ImageIcon;

import com.plealog.genericapp.api.log.EZLogger;

import bzh.plealog.bioinfo.ui.carto.data.FGraphics;

/**
 * This painter draws an image. Please note that this painter does not
 * resize provided image. So, provide image of appropriate size for your
 * view.
 * 
 * @author Patrick G. Durand
 */
public class ImagePainter extends FeaturePainterBase {
  private ImageIcon _image;
  private int       _halfW;
  
  /**
   * Constructor.
   * 
   * @param image an image
   */
  public ImagePainter(ImageIcon image){
    _image = image;
    _halfW = _image.getIconWidth()/2;
  }
  /**
   * Constructor.
   * 
   * @param resourcePath an absolute path to an image resource. When using
   * package based path, use slash as package sub names separator, e.g.
   * /foo/bar/image.png.
   */
  public ImagePainter(String resourcePath){
    URL url = this.getClass().getResource(resourcePath);
    if (url==null){
      EZLogger.warn("Resource not found: "+resourcePath);
      return;
    }
    _image = new ImageIcon(url);
    _halfW = _image.getIconWidth()/2;
  }

  public void paintFeature(Graphics2D g, Rectangle box, FGraphics fg, int strand) {
    if (_image==null)
      return;
    _image.paintIcon(null, g, box.x-_halfW, box.y);
  }

  public String getName(){
    return "image";
  }

}
