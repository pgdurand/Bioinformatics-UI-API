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
package bzh.plealog.bioinfo.ui.sequence.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import bzh.plealog.bioinfo.api.data.sequence.DRulerModel;

/**
 * This class implements a ruler viewer.
 * 
 * @author Patrick G. Durand
 */
public class DRulerViewer extends JPanel {
  private static final long serialVersionUID = 7120863738570981242L;
  private FontMetrics _fm;
  private DRulerModel _rulerModel;
  private Dimension   _dim;
  private int         _length;
  private int         _orientation;
  private int         _rulerPosition;
  private int         _boxSize;
  private int         _tickSize = 10;
  private int         _fntHeight;

  /**
   * Creates a ruler viewer either vertically or horizontally.
   * @param model a ruler model
   * @param boxSize width of a single ruler cell. See method setBoxSize().
   * @param orientation either SwingConstants.HORIZONTAL or SwingConstants.VERTICAL
   * @param rulerPosition one of SwingConstants.BOTTOM, TOP, RIGHT or LEFT. 
   */
  public DRulerViewer(DRulerModel model, int boxSize, int orientation, int rulerPosition) {
    _rulerModel = model;
    _length = model.size();
    _orientation = orientation;
    setBoxSize(boxSize);
    _rulerPosition = rulerPosition;
    setFont(new Font("sans-serif", Font.PLAIN, 9));
    setBackground(Color.white);
  }

  private void adjustSize(){
    _fm = this.getFontMetrics(this.getFont());
    _fntHeight = _fm.getHeight();
    int width = _orientation == SwingConstants.HORIZONTAL ? 
        _length*_boxSize : _fntHeight+_tickSize;
    int height = _orientation != SwingConstants.HORIZONTAL ? 
        _length*_boxSize : _fntHeight+_tickSize;
    _dim = new Dimension(width, height);
  }
  /**
   * Sets the width of a single ruler cell. Usually, you pass here the value
   * of the method getHeight() from the FontMetrics representing the Font
   * used within the DSequenceListViewer. In that way, ruler's cells are
   * correctly aligned with the letters displayed in the DSequenceListViewer. 
   * In that way too, this allows the DRulerViewer to use its own Font.
   * */
  public void setBoxSize(int s){
    _boxSize = s;
  }
  /**
   * Sets a new ruler model.
   */
  public void setRulerModel(DRulerModel ruler){
    _rulerModel = ruler;
    _length = ruler.size();
    adjustSize();
  }

  /**
   * Sets a new font.
   */
  public void setFont(Font font){
    super.setFont(font);
    adjustSize();
  }

  public Dimension getPreferredSize(){
    return (_dim);
  }

  public Dimension getMaximumSize(){
    return (_dim);
  }

  public Dimension getMinimumSize(){
    return (_dim);
  }

  public void paint(Graphics graphics) {
    super.paint(graphics);

    int    pos, pos2, yBase, max, shortTickStart, shortTickStop, 
    longTickStart, longTickStop, curSeqPos;
    float  dPos;
    String val;
    Font   oldFont;

    oldFont = graphics.getFont();
    graphics.setFont(getFont());
    if (_orientation == SwingConstants.HORIZONTAL) {
      dPos = ((float)_boxSize/2.0f);
      yBase = (_rulerPosition==SwingConstants.BOTTOM ? 
          _fntHeight+_tickSize-1:_fntHeight-1);
      max = this.getBounds().width;
      shortTickStart=(_rulerPosition==SwingConstants.BOTTOM ? 
          1:_fntHeight+_tickSize/2);
      shortTickStop=(_rulerPosition==SwingConstants.BOTTOM ? 
          _tickSize/2:_fntHeight+_tickSize);
      longTickStart=(_rulerPosition==SwingConstants.BOTTOM ? 1:_fntHeight);
      longTickStop=(_rulerPosition==SwingConstants.BOTTOM ? 
          _tickSize:_fntHeight+_tickSize);
      for (int x = 0 ; x < _length; x++) {
        curSeqPos = _rulerModel.getSeqPos(x);
        pos = Math.round(dPos);
        if (curSeqPos>=0){
          graphics.drawLine(pos,shortTickStart,pos,shortTickStop);
          if (curSeqPos % 10 == 0) {
            graphics.drawLine(pos,longTickStart,pos,longTickStop);
            val = String.valueOf(curSeqPos);
            graphics.drawString(val,pos - _fm.stringWidth(val)/2,yBase);
            val=null;
          }
        }
        dPos+=_boxSize;
        if (pos>max)
          break;
      }
    } 
    else { // orientation == VERTICAL
      dPos = ((float)_boxSize/2.0f);
      yBase = (_rulerPosition==SwingConstants.RIGHT ? 
          _fntHeight/2+_tickSize-1:_fntHeight/2-1);
      max = this.getBounds().height;
      shortTickStart=(_rulerPosition==SwingConstants.RIGHT ? 
          1:_fntHeight+_tickSize/2);
      shortTickStop=(_rulerPosition==SwingConstants.RIGHT ? 
          _tickSize/2:_fntHeight+_tickSize);
      longTickStart=(_rulerPosition==SwingConstants.RIGHT ? 1:_fntHeight);
      longTickStop=(_rulerPosition==SwingConstants.RIGHT ? 
          _tickSize:_fntHeight+_tickSize);
      for (int y = 0 ; y < _length; y ++) {
        curSeqPos = _rulerModel.getSeqPos(y);
        pos = Math.round(dPos);
        if (curSeqPos>=0){
          graphics.drawLine(shortTickStart, pos, shortTickStop, pos);
          if (curSeqPos % 10 == 0) {
            graphics.drawLine(longTickStart, pos, longTickStop, pos);
            val = String.valueOf(curSeqPos);
            pos2 = pos - (_fntHeight*(val.length()/2)) + _fntHeight;
            for (int i = 0; i < val.length(); i++) {
              graphics.drawString(val.substring(i, i+1), yBase, 
                  pos2 + (i * _fntHeight));
            }
            val=null;
          }
        }
        dPos+=_boxSize;
        if (pos>max)
          break;
      }
    }
    graphics.setFont(oldFont);
  }
}
