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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;

/**
 * Implement a JList where cells are check boxes.
 * 
 * @author Patrick G. Durand
 * */
public class CheckBoxList extends JList<CheckBoxListItem> {
  private static final long serialVersionUID = 8861286652322994482L;
  protected EventListenerList       _listenerList;
  
  /**
   * Constructor.
   */
  public CheckBoxList() {
    super();
    initUI();
  }

  /**
   * Constructor.
   * 
   * @param dataModel data model
   */
  public CheckBoxList(ListModel<CheckBoxListItem> dataModel) {
    super(dataModel);
    initUI();
  }

  /**
   * Constructor.
   * 
   * @param listData data model
   */
  public CheckBoxList(Vector<? extends CheckBoxListItem> listData) {
    super(listData);
    initUI();
  }

  /**
   * Constructor.
   * 
   * @param data data model
   */
  public CheckBoxList(CheckBoxListItem[] data) {
    super(data);
    initUI();
  }
  
  /**
   * Initialize UI.
   */
  private void initUI(){
    _listenerList = new EventListenerList();
    this.setCellRenderer(new CheckboxListItemRenderer());
    this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unchecked")
      public void mouseClicked(MouseEvent event) {
        JList<CheckBoxListItem> list = (JList<CheckBoxListItem>) event.getSource();
        int index = list.locationToIndex(event.getPoint());
        CheckBoxListItem item = (CheckBoxListItem) list.getModel().getElementAt(index);
        item.setSelected(!item.isSelected());
        list.repaint(list.getCellBounds(index, index));
        fireItemSelectedEvent(item);
      }
    });
  }
  /**
   * Register an Item Selection Listener.
   * 
   * @param listener a listener
   */
  public void addCheckBoxListSelectionListener(CheckBoxListSelectionListener listener){
    if (listener!=null)
      _listenerList.add(CheckBoxListSelectionListener.class, listener);
  }

  /**
   * Remove an Item Selection Listener.
   * 
   * @param listener a listener
   */
  public void removeCheckBoxListSelectionListener(CheckBoxListSelectionListener listener){
    if (listener!=null)
      _listenerList.remove(CheckBoxListSelectionListener.class, listener);
  }
  
  /**
   * Notify all CheckBoxListSelectionListeners registered to this CheckBoxList that
   * an item has been clicked.
   * 
   * @param item the clicked item
   */
  protected void fireItemSelectedEvent(CheckBoxListItem item){
    Object[] observers = _listenerList.getListenerList();
    int i;
    for (i = observers.length - 2; i >= 0; i -= 2) {
      if (observers[i] == CheckBoxListSelectionListener.class) {
        ((CheckBoxListSelectionListener)observers[i+1]).itemSelected(item);
      }
    }
  }
}
