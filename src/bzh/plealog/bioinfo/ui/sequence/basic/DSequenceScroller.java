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

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JScrollBar;

import bzh.plealog.bioinfo.api.data.sequence.DSequenceModel;

/**
 * This class implements a scroller for a sequence viewer.
 * 
 * @author Patrick G. Durand
 */
public class DSequenceScroller extends JScrollBar {
  private static final long serialVersionUID = 4363144261617511235L;
  private HashSet<DSequenceViewer> _slaves;
  private DRulerViewer             _ruler;

  /**
   * Creates a DSequenceScroller for a given DSequenceViewer.
   */
  public DSequenceScroller(DSequenceViewer master){
    super(java.awt.Adjustable.HORIZONTAL, 
        0, 
        1, 
        0, 
        Math.max(((DSequenceModel) master.getSequenceList().getModel()).getSequence().size(),1));

    _slaves = new HashSet<DSequenceViewer>();
    this.addAdjustmentListener(new DScrollerAdjustmentListener(master));
    master.addComponentListener(new DScrollComponentAdapter(this));
  }

  /**
   * Notify all listeners that have registered interest for
   * notification on this event type.  
   */
  private void fireScrollChange(int value) {
    //DSequenceModel model;
    DSequenceViewer           viewer;
    Iterator<DSequenceViewer> iter;

    iter = _slaves.iterator();   
    while (iter.hasNext()) {
      viewer = iter.next();
      //viewer.setScrollPosition(value);
      viewer.repaint();
    }
    if (_ruler!=null){
      //_ruler.setScrollPosition(value);
      _ruler.repaint();
    }
  }

  /**
   * Adjusts this DSequenceScroller using the properties of a new DSequenceViewer.
   */
  public void adjust(DSequenceViewer newMaster){
    this.setMaximum(((DSequenceModel) newMaster.getSequenceList().getModel()).getSequence().size());
    adjustExtent(newMaster.getSequenceList(), this);
  }

  /**
   * Adds a slave list to this scroller. A slave is a sequence viewer that
   * will be automatically scrolled when the sequence viewer wrapped in this
   * scroller is scrolled.
   */
  public void addSlave(DSequenceViewer slave){
    if (slave==null)
      return;
    _slaves.add(slave);
  }

  /**
   * Sets a ruler to this scroller.
   */
  public void setRuler(DRulerViewer ruler){
    _ruler = ruler;
  }

  /**
   * Removes a slave list from this scroller.
   */
  public void removeSlave(DSequenceListViewer slave){
    if (slave==null)
      return;
    _slaves.remove(slave);
  }    

  private class DScrollerAdjustmentListener implements AdjustmentListener{
    //private DSequenceViewer _viewer;
    private int             _oldValue;

    public DScrollerAdjustmentListener(DSequenceViewer viewer){
      //_viewer = viewer;
    }

    public void adjustmentValueChanged(AdjustmentEvent e){
      int value;

      value = e.getValue();
      if (value==_oldValue)
        return;
      //_viewer.setScrollPosition(value);
      fireScrollChange(value);
      _oldValue = value;
    }
  }

  private void adjustExtent(DSequenceListViewer list, DSequenceScroller scroller){
    int   listWidth, cellWidth, numCells;

    listWidth = list.getBounds().width;
    cellWidth = list.getFixedCellWidth();
    numCells = listWidth / cellWidth;
    scroller.setVisibleAmount(numCells);
  }

  private class DScrollComponentAdapter extends ComponentAdapter{
    private DSequenceScroller _scroller;

    public DScrollComponentAdapter(DSequenceScroller scroller){
      _scroller = scroller;
    }
    public void componentResized(ComponentEvent e){
      DSequenceListViewer list;

      list = ((DSequenceViewer) e.getSource()).getSequenceList();
      adjustExtent(list, _scroller);
    }
  }
}
