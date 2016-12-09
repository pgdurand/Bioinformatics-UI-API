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
package bzh.plealog.bioinfo.ui.carto.drawer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.ui.carto.event.SViewerSelectionListener;

/**
 * This interface defines the methods describing a drawing lane.
 * 
 * @author Patrick G. Durand
 */
public interface DrawingLane extends SViewerSelectionListener {
  /**
   * Returns the maximum size of the lane. Unit is pixel.
   */
  public Dimension getMaximumSize();
  /**
   * Returns the minimum size of the lane. Unit is pixel.
   */
  public Dimension getMinimumSize();
  /**
   * Returns the preferred size of the lane. Unit is pixel.
   */
  public Dimension getPreferredSize();
  /**
   * Sets the preferred size of the lane. Unit is pixel.
   */
  public void setPreferredSize(Dimension dim);
  /**
   * Sets the width of the left margin of this lane. Unit is pixel.
   */
  public void setLeftMargin(int leftMargin);
  /**
   * Returns the width of the left margin of this lane.
   */
  public int getLeftMargin();
  /**
   * Returns the left label of this lane. This label is used to display the name
   * of the lane on the left side of the viewer. 
   */
  public String getLeftLabel();
  /**
   * Sets the width of the right margin of this lane. Unit is pixel.
   */
  public void setRightMargin(int rightMargin);
  /**
   * Returns the width of the right margin of this lane.
   */
  public int getRightMargin();
  /**
   * Returns the right label of this lane. This label is used to display the name
   * of the lane on the right side of the viewer. 
   */
  public String getRightLabel();
  /**
   * Sets the height of the top margin of this lane. Unit is pixel.
   */
  public void setTopMargin(int top);
  /**
   * Returns the height of the top margin of this lane.
   */
  public int getTopMargin();
  /**
   * Sets the height of the bottom margin of this lane. Unit is pixel.
   */
  public void setBottomMargin(int bottom);
  /**
   * Returns the height of the bottom margin of this lane.
   */
  public int getBottomMargin();
  /**
   * Returns the font used by the lane to display strings.
   */
  public Font getFont();
  /**
   * Sets the font to use by the lane to display strings.
   */
  public void setFont(Font f);
  /**
   * Compute the scale factor. Basically this value should be computed by dividing
   * the sequence size by the drawing panel width.
   */
  public double computeScaleFactor();
  /**
   * Returns the sequence used as a reference for the drawing procedure.
   */
  public DSequence getSequence();
  /**
   * Find a drawing object given a mouse coordinate. This method should return null
   * if no object is located at that position.
   */
  public Object getObjectFromSection(int xFrom, int xTo);
  /**
   * Find a drawing object given a x coordinate. This method should return null
   * if no object is located at that position.
   */
  public Object getClickedObject(int x);
  /**
   * Return an absolute ruler position given a x coordinate. Return value is in the
   * range from 0 to DSequence.size()-1.
   */
  public int getRulerPositionAt(int x);
  /**
   * Sets the size of a reference label. Unit is number of letters.
   */
  public void setReferenceLabelSize(int size);
  /**
   * Requires the lane to paint its content.
   * @param g the Graphics object
   * @param drawingArea the region that has to be used to draw
   */
  public void paintLane(Graphics2D g, Rectangle drawingArea);
  
  /**
   * Set whether or not the drawing lane has to display a grid.
   */
  public void setDrawGrid(boolean b);
}
