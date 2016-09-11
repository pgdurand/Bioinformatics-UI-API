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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import bzh.plealog.bioinfo.util.CoreUtil;

/**
 * This is a component capable of displaying a percentage value as a colored
 * bar. Filling color spans the component according to the percent value.
 * 
 * @author Patrick G. Durand
 * */
public class JPercentLabel extends JPanel {
  private static final long serialVersionUID = 3076952943927153813L;

  protected static final DecimalFormat PCT_FORMATTER = new DecimalFormat("###.#");

  protected JLabel  _lbl;
  protected double  _percent = -1.0;
  protected Color   _clr;
  protected boolean _drawBox;
  protected boolean _drawZero = true;

  /**
   * Constructor.
   */
  public JPercentLabel(){
    this(false);
  }

  /**
   * Constructor.
   * 
   * @param drawBox use true to draw the percent bar border
   */
  public JPercentLabel(boolean drawBox){
    _lbl = new JLabel();

    setDrawBox(drawBox);
    this.setLayout(new BorderLayout());
    _lbl.setOpaque(false);
    _lbl.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(_lbl, BorderLayout.CENTER);
    this.setOpaque(true);
  }
  /**
   * Figures out if the component has to draw a box as the border. Default
   * is false.
   * 
   * @param b true or false
   */
  public void setDrawBox(boolean b){
    _drawBox = b;
  }
  /**
   * Figures out if the component has to draw value set to zero percent. Default
   * is true.
   * 
   * @param b true or false
   */
  public void setDrawZeroValue(boolean b){
    _drawZero = b;
  }

  /**
   * @see JComponent#getPreferredSize()
   * */
  public Dimension getPreferredSize(){
    return _lbl.getPreferredSize();
  }
  /**
   * @see JComponent#getMinimumSize()
   * */
  public Dimension getMinimumSize(){
    return _lbl.getMinimumSize();
  }
  /**
   * @see JComponent#getMaximumSize()
   * */
  public Dimension getMaximumSize(){
    return _lbl.getMaximumSize();
  }
  /**
   * Set the value. It has to be in the range 0.0 to 100.0. Note that
   * this method assigns a color to the component background according
   * to the value passed in. This color can be overloaded by a subsequent
   * call to setColor().
   * 
   * @param percent the value
   */
  public void setValue(double percent){
    _percent = percent;
    if (_percent==-1.0 || (_percent==0.0 && !_drawZero)){
      _lbl.setText("-");
    }
    else{
      _lbl.setText(PCT_FORMATTER.format(percent)+"%");
    }
    if (_percent>=75.0)
      _clr = Color.green;
    else if  (_percent>=50.0)
      _clr = Color.yellow;
    else if  (_percent>=25.0)
      _clr = Color.orange;
    else 
      _clr = Color.red;
  }
  /**
   * Return the value.
   * 
   * @return a percent value
   */
  public double getValue(){
    return _percent;
  }
  /**
   * Set a percent value as a String. Must be formatted as ##.##%.
   */
  public void setValue(String pct){
    String val;
    int    idx;
    double d;

    if (pct==null){
      setValue(-1.0);
      return;
    }
    val = pct;
    idx = val.indexOf("%");
    if (idx!=-1){
      val = val.substring(0, idx);
    }
    idx = val.indexOf(",");
    if (idx!=-1){
      val = CoreUtil.replaceFirst(val, ",", ".");
    }
    if (pct.equals("-")){// no value available: avoid an exception
      d = -1.0;
    }
    else{
      try {
        d = Double.valueOf(val);
      } catch (NumberFormatException e) {
        d = -1.0;
      }
    }
    setValue(d);
  }
  /**
   * Set a particular color.
   * 
   * @param clr the color
   */
  public void setColor(Color clr){
    _clr = clr;
  }
  /**
   * Get a particular color.
   * @return the color
   */
  public Color getColor(){
    return _clr;
  }
  /**
   * Set the background color of the JLabel displaying the value.
   * 
   * @param bg the color
   */
  public void setBackground(Color bg){
    super.setBackground(bg);
    if (_lbl!=null)
      _lbl.setBackground(bg);
  }
  /**
   * Set the foreground color of the JLabel displaying the value.
   *
   * @param fg the color
   */
  public void setForeground(Color fg){
    super.setForeground(fg);
    if (_lbl!=null)
      _lbl.setForeground(fg);
  }
  /**
   * @see JComponent#paintComponent(Graphics)
   * */
  public void paintComponent(Graphics g){
    super.paintComponent(g);
    Dimension dim = this.getSize();
    Color     oldClr = g.getColor();
    int       x, y;

    if (_percent==-1.0 || (_percent==0.0 && !_drawZero))
      return;
    //fill
    g.setColor(_clr);
    x = (int) ((double)dim.width*_percent/100.0)-2;
    y = dim.height-2;
    g.fillRect(1, 1, x, y);
    //draw a box
    if (_drawBox){
      g.setColor(Color.GRAY);
      x = dim.width - 1;
      y++;
      g.drawLine(0, y, x, y);
      g.drawLine(x, y, x, 0);
      g.setColor(Color.BLACK);
      g.drawLine(0, y, 0, 0);
      g.drawLine(0, 0, x, 0);
    }
    //restore graphics
    g.setColor(oldClr);
  }
}
