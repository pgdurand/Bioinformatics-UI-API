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

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

/**
 * Used by DSequenceListViewer to disallow list selection.
 * 
 * @author Patrick G. Durand
 */
public class MyListSelectionModel implements ListSelectionModel {

  /**
   * @see javax.swing.ListSelectionModel#getAnchorSelectionIndex()
   */
  public int getAnchorSelectionIndex() {
    return 0;
  }

  /**
   * @see javax.swing.ListSelectionModel#getLeadSelectionIndex()
   */
  public int getLeadSelectionIndex() {
    return 0;
  }

  /**
   * @see javax.swing.ListSelectionModel#getMaxSelectionIndex()
   */
  public int getMaxSelectionIndex() {
    return -1;
  }

  /* (non-Javadoc)
   * @see javax.swing.ListSelectionModel#getMinSelectionIndex()
   */
  public int getMinSelectionIndex() {
    return -1;
  }

  /**
   * @see javax.swing.ListSelectionModel#getSelectionMode()
   */
  public int getSelectionMode() {
    return 0;
  }

  /**
   * @see javax.swing.ListSelectionModel#clearSelection()
   */
  public void clearSelection() {
  }

  /**
   * @see javax.swing.ListSelectionModel#getValueIsAdjusting()
   */
  public boolean getValueIsAdjusting() {
    return false;
  }

  /**
   * @see javax.swing.ListSelectionModel#isSelectionEmpty()
   */
  public boolean isSelectionEmpty() {
    return true;
  }

  /**
   * @see javax.swing.ListSelectionModel#setAnchorSelectionIndex(int)
   */
  public void setAnchorSelectionIndex(int index) {
  }

  /**
   * @see javax.swing.ListSelectionModel#setLeadSelectionIndex(int)
   */
  public void setLeadSelectionIndex(int index) {
  }

  /**
   * @see javax.swing.ListSelectionModel#setSelectionMode(int)
   */
  public void setSelectionMode(int selectionMode) {
  }

  /**
   * @see javax.swing.ListSelectionModel#isSelectedIndex(int)
   */
  public boolean isSelectedIndex(int index) {
    return false;
  }

  /**
   * @see javax.swing.ListSelectionModel#addSelectionInterval(int, int)
   */
  public void addSelectionInterval(int index0, int index1) {
  }

  /**
   * @see javax.swing.ListSelectionModel#removeIndexInterval(int, int)
   */
  public void removeIndexInterval(int index0, int index1) {
  }

  /**
   * @see javax.swing.ListSelectionModel#removeSelectionInterval(int, int)
   */
  public void removeSelectionInterval(int index0, int index1) {
  }

  /**
   * @see javax.swing.ListSelectionModel#setSelectionInterval(int, int)
   */
  public void setSelectionInterval(int index0, int index1) {
  }

  /**
   * @see javax.swing.ListSelectionModel#insertIndexInterval(int, int, boolean)
   */
  public void insertIndexInterval(int index, int length, boolean before) {
  }

  /**
   * @see javax.swing.ListSelectionModel#setValueIsAdjusting(boolean)
   */
  public void setValueIsAdjusting(boolean valueIsAdjusting) {
  }

  /**
   * @see javax.swing.ListSelectionModel#addListSelectionListener(javax.swing.event.ListSelectionListener)
   */
  public void addListSelectionListener(ListSelectionListener x) {
  }

  /**
   * @see javax.swing.ListSelectionModel#removeListSelectionListener(javax.swing.event.ListSelectionListener)
   */
  public void removeListSelectionListener(ListSelectionListener x) {
  }

}
