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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import com.plealog.genericapp.api.EZEnvironment;

/**
 * This class handles selection behaviors of entries in an Table.
 * 
 * @author Patrick G. Durand
 * @since 2003
 */
public class BasicSelectTableAction extends AbstractAction {
  private static final long serialVersionUID = -985182696603351294L;
  private JTable _table;
  private SelectType _selectType;
  private boolean _running = false;

  public static enum SelectType {
    ALL, INVERT, CLEAR
  };

  /**
   * Action constructor.
   * 
   * @param name
   *          the name of the action.
   */
  public BasicSelectTableAction(String name, SelectType selType) {
    super(name);
    _selectType = selType;
  }

  /**
   * Action constructor.
   * 
   * @param name
   *          the name of the action.
   * @param icon
   *          the icon of the action.
   */
  public BasicSelectTableAction(String name, Icon icon, SelectType selType) {
    super(name, icon);
    _selectType = selType;
  }

  public void setTable(JTable rt) {
    _table = rt;
  }

  private void selectAll() {
    _table.getSelectionModel().setSelectionInterval(0, _table.getModel().getRowCount() - 1);
  }

  private void clearSelection() {
    _table.clearSelection();
  }

  private void invertSelection() {
    DefaultListSelectionModel dModel;
    ListSelectionModel sModel;
    int i, size;

    sModel = _table.getSelectionModel();
    if (sModel.isSelectionEmpty()) {
      selectAll();
      return;
    }
    size = _table.getModel().getRowCount();
    dModel = new DefaultListSelectionModel();
    for (i = 0; i < size; i++) {
      if (!sModel.isSelectedIndex(i)) {
        dModel.addSelectionInterval(i, i);
      }
    }
    sModel.setValueIsAdjusting(true);
    sModel.clearSelection();
    for (i = 0; i < size; i++) {
      if (dModel.isSelectedIndex(i)) {
        sModel.addSelectionInterval(i, i);
      }
    }
    sModel.setValueIsAdjusting(false);
  }

  public void actionPerformed(ActionEvent event) {
    if (_table == null || _running == true)
      return;
    new SelectThread().start();
  }

  protected class SelectThread extends Thread {
    public SelectThread() {
    }

    public void run() {
      _running = true;
      EZEnvironment.setWaitCursor();
      switch (_selectType) {
      case ALL:
        selectAll();
        break;
      case INVERT:
        invertSelection();
        break;
      case CLEAR:
        clearSelection();
        break;
      }
      EZEnvironment.setDefaultCursor();
      _running = false;
    }
  }
}
