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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.event.TableModelEvent;

import com.plealog.genericapp.api.EZEnvironment;

import bzh.plealog.bioinfo.util.CoreUtil;

/**
 * Define a way to handle column selection in a JTable.
 * 
 * @author Patrick G. Durand
 * @since 2003
 */
public class TableColumnManager {
  private ColumnManagableTable _refTable;
  private TableHeaderColumnItem[] _refColumns;
  private JPopupMenu _tOptionsMenu;
  private JButton _invoker;
  private TableOptionAllMenuItem _showAllItems;
  private TableOptionAllMenuItem _hideAllItems;
  private boolean _lockState = false;

  private static final Font DEF_FNT = new Font("sans-serif", Font.PLAIN, 12);

  public TableColumnManager(ColumnManagableTable refTable, TableHeaderColumnItem[] colH) {
    this(refTable, colH, null);
  }

  public TableColumnManager(ColumnManagableTable refTable, TableHeaderColumnItem[] colH, Action[] specialActions) {
    _refTable = refTable;
    _refColumns = colH;
    _showAllItems = new TableOptionAllMenuItem("Show all", true);
    _showAllItems.setFont(DEF_FNT);
    _hideAllItems = new TableOptionAllMenuItem("Hide all", false);
    _hideAllItems.setFont(DEF_FNT);
    createTableOptionPoupMenu(specialActions);
    createInvoker();
  }

  private void createInvoker() {
    // configure button for Table Options
    ImageIcon icon = EZEnvironment.getImageIcon("tableOptions.png");
    JButton tOptions;

    if (icon != null) {
      tOptions = new JButton(icon);
    } else {
      tOptions = new JButton(EZEnvironment.getMessage("Table.options.lbl"));
    }
    tOptions.setToolTipText(EZEnvironment.getMessage("Table.options.tip"));
    tOptions.addActionListener(new TableOptionActionListener(tOptions));
    tOptions.setBorder(null);
    _invoker = tOptions;
  }

  public JButton getInvoker() {
    return _invoker;
  }

  /**
   * Creates a pop-up menu that displays the list of optional fields available
   * to modify the elements displayed by the hit list.
   */
  private void createTableOptionPoupMenu(Action[] specialActions) {
    TableOptionCheckBoxItem item;
    TableOptionPopupItemListener listener;
    Action act;
    JMenuItem jItem;
    int i;

    _tOptionsMenu = new JPopupMenu();
    listener = new TableOptionPopupItemListener();
    if (specialActions != null) {
      for (i = 0; i < specialActions.length; i++) {
        act = specialActions[i];
        jItem = new JMenuItem(act);
        jItem.setFont(DEF_FNT);
        _tOptionsMenu.add(jItem);
      }
      _tOptionsMenu.addSeparator();
    }

    _tOptionsMenu.add(_showAllItems);
    _tOptionsMenu.add(_hideAllItems);
    _tOptionsMenu.addSeparator();
    for (i = 0; i < _refColumns.length; i++) {
      if (_refColumns[i].isRequired())
        continue;
      item = new TableOptionCheckBoxItem(_refColumns[i]);
      item.setFont(DEF_FNT);
      if (_refColumns[i].isVisible()) {
        item.setState(true);
      }
      item.addItemListener(listener);
      _tOptionsMenu.add(item);
    }
  }

  private int countItem() {
    MenuElement[] items;
    TableOptionCheckBoxItem cBox;
    int i, selectedItems = 0;

    items = _tOptionsMenu.getSubElements();
    if (items == null || items.length == 0) {
      return 0;
    }
    for (i = 0; i < items.length; i++) {
      if (items[i] instanceof TableOptionCheckBoxItem) {
        cBox = (TableOptionCheckBoxItem) items[i];
        if (cBox.getState()) {
          selectedItems++;
        }
      }
    }
    return selectedItems;
  }

  private int countRequiredItems() {
    int i, requiredItems = 0;
    for (i = 0; i < _refColumns.length; i++) {
      if (_refColumns[i].isRequired())
        requiredItems++;
    }
    return requiredItems;
  }

  private void fillArray(TableHeaderColumnItem[] hItems) {
    MenuElement[] items;
    TableOptionCheckBoxItem cBox;
    int j, i;

    j = 0;
    for (i = 0; i < _refColumns.length; i++) {
      if (_refColumns[i].isRequired()) {
        hItems[j] = _refColumns[i];
        j++;
      }
    }
    items = _tOptionsMenu.getSubElements();
    if (items == null || items.length == 0) {
      return;
    }
    for (i = 0; i < items.length; i++) {
      if (items[i] instanceof TableOptionCheckBoxItem) {
        cBox = (TableOptionCheckBoxItem) items[i];
        if (cBox.getState()) {
          hItems[j] = cBox.getItem();
          j++;
        }
      }
    }
  }

  private void changeState(boolean state) {
    MenuElement[] items;
    TableOptionCheckBoxItem cBox;
    int i;

    items = _tOptionsMenu.getSubElements();
    if (items == null || items.length == 0) {
      return;
    }
    for (i = 0; i < items.length; i++) {
      if (items[i] instanceof TableOptionCheckBoxItem) {
        cBox = (TableOptionCheckBoxItem) items[i];
        if (cBox._item.isRequired() == false)
          cBox.setState(state);
      }
    }
  }

  private void updateSelection() {
    int nItems, selRow;
    TableHeaderColumnItem[] hItems;

    nItems = countRequiredItems() + countItem();
    if (nItems == 0)
      return;
    hItems = new TableHeaderColumnItem[nItems];
    fillArray(hItems);
    selRow = _refTable.getSelectedRow();
    _refTable.updateColumnHeaders(hItems);
    _refTable.tableChanged(new TableModelEvent(_refTable.getModel(), TableModelEvent.HEADER_ROW));
    _refTable.initColumnSize(_refTable.getBounds().width, null);
    if (selRow >= 0) {
      _refTable.getSelectionModel().setSelectionInterval(selRow, selRow);
    }
  }

  private void updateColumnName() {
    TableOptionCheckBoxItem cBox = null;
    TableHeaderColumnItem header = null;
    for (MenuElement items : _tOptionsMenu.getSubElements()) {
      for (TableHeaderColumnItem thci : _refColumns) {
        if (items instanceof TableOptionCheckBoxItem) {
          cBox = (TableOptionCheckBoxItem) items;
          header = cBox.getItem();
        }
        if (header != null && header.getIID() == thci.getIID()) {
          header.setSID(thci.getSID());
          cBox.setText(thci.getSID());
        }
      }
    }
  }

  private class TableOptionPopupItemListener implements ItemListener {

    public void itemStateChanged(ItemEvent e) {

      if (_lockState)
        return;
      updateSelection();
    }
  }

  @SuppressWarnings("serial")
  private class TableOptionAllMenuItem extends JMenuItem implements ActionListener {
    private boolean bShowAll;

    public TableOptionAllMenuItem(String title, boolean st) {
      super(title);
      bShowAll = st;
      this.addActionListener(this);
    }

    public void actionPerformed(ActionEvent event) {
      _lockState = true;
      changeState(bShowAll);
      _lockState = false;
      updateSelection();
    }
  }

  @SuppressWarnings("serial")
  private class TableOptionCheckBoxItem extends JCheckBoxMenuItem {
    private TableHeaderColumnItem _item;

    public TableOptionCheckBoxItem(TableHeaderColumnItem item) {
      super(item.getSID(), false);
      _item = item;
    }

    public TableHeaderColumnItem getItem() {
      return _item;
    }
  }

  private class TableOptionActionListener implements ActionListener {
    private JComponent _invoker;

    public TableOptionActionListener(JComponent invoker) {
      _invoker = invoker;
    }

    public void actionPerformed(ActionEvent event) {
      updateColumnName();
      _tOptionsMenu.show(_invoker, 0, _invoker.getBounds().height);
    }
  }

  public static List<Integer> getDefColumns(String defColIDs) {
    List<Integer> idSet;
    String[] ids;
    int i;

    idSet = new ArrayList<Integer>();
    ids = CoreUtil.tokenize(defColIDs);
    for (i = 0; i < ids.length; i++) {
      if (!idSet.contains(Integer.valueOf(ids[i]))) {
        idSet.add(Integer.valueOf(ids[i]));
      }
    }
    return idSet;
  }

  public static String getDelColumns(TableHeaderColumnItem[] colH) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < colH.length; i++) {
      buf.append(colH[i].getIID());
      if ((i + 1) < colH.length) {
        buf.append(",");
      }
    }
    return buf.toString();
  }
}
