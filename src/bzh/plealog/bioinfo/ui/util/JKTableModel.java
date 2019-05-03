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

import java.awt.Color;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;

import com.plealog.genericapp.api.EZEnvironment;

import bzh.plealog.bioinfo.ui.blast.resulttable.sort.SerialEntityBag;

/**
 * Define the data model flavor to use with JKTable.
 * 
 * @author Patrick G. Durand
 * @since 2003
 */
public abstract class JKTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 5166893089130202535L;
  private TableHeaderColumnItem[] displayedHeaders;
  private int sortedColumn = 0;
  private boolean sortAscending = false;
  protected SerialEntityBag sortedItems = null;

  public JKTableModel() {
    String columnsToDisplay = EZEnvironment.getApplicationProperty(getDisplayHeaderPropertyName());
    if (StringUtils.isBlank(columnsToDisplay)) {
      displayedHeaders = getReferenceColumnHeaders();
      // set all visible for TableColumnManager
      for (TableHeaderColumnItem itemRef : displayedHeaders) {
        itemRef.setVisible(true);
      }
    } else {
      List<Integer> indexes = TableColumnManager.getDefColumns(columnsToDisplay);
      displayedHeaders = new TableHeaderColumnItem[indexes.size()];
      int i = 0;
      for (Integer indexColumn : indexes) {
        for (TableHeaderColumnItem itemRef : getReferenceColumnHeaders()) {
          if (itemRef.getIID() == indexColumn) {
            displayedHeaders[i] = itemRef;
            displayedHeaders[i].setVisible(true);
            i++;
            break;
          }
        }

      }
    }

    String prop = EZEnvironment.getApplicationProperty(this.getSortColumnPropertyName());
    if (prop != null) {
      setSortColumn(Integer.valueOf(prop));
    }
    prop = EZEnvironment.getApplicationProperty(this.getSortAscendingPropertyName());
    if (prop != null) {
      setSortColumnAscending("true".equals(prop));
    }
  }

  public abstract TableHeaderColumnItem[] getReferenceColumnHeaders();

  public abstract String getDisplayHeaderPropertyName();

  public abstract String getColumnSizePropertyName();

  public abstract String getSortColumnPropertyName();

  public abstract String getSortAscendingPropertyName();

  public void updateColumnHeaders(TableHeaderColumnItem[] newHeaders) {
    this.displayedHeaders = newHeaders;
    EZEnvironment.setApplicationProperty(getDisplayHeaderPropertyName(), TableColumnManager.getDelColumns(newHeaders));
    this.fireTableStructureChanged();
  }

  public int getColumnId(int column) {
    return displayedHeaders[column].getIID();
  }

  @Override
  public int getColumnCount() {
    return this.displayedHeaders.length;
  }

  @Override
  public String getColumnName(int columnIndex) {
    return this.displayedHeaders[columnIndex].getSID();
  }

  public int getSortColumn() {
    return this.sortedColumn;
  }

  public void setSortColumn(int sortColumn) {
    this.sortedColumn = sortColumn;
    EZEnvironment.setApplicationProperty(getSortColumnPropertyName(), String.valueOf(sortColumn));
  }

  public boolean isSortColumnAscending() {
    return this.sortAscending;
  }

  public void setSortColumnAscending(boolean sortAscending) {
    this.sortAscending = sortAscending;
    EZEnvironment.setApplicationProperty(this.getSortAscendingPropertyName(), (this.sortAscending ? "true" : "false"));
  }

  public TableHeaderColumnItem getDisplayedHeader(int columnIndex) {
    return this.displayedHeaders[columnIndex];
  }

  /**
   * To manage headers background color.
   * 
   * @param columnName name of a column
   * @return a color
   */
  public Color getHeaderColumn(String columnName) {
    return null;
  }

  public abstract JKTableModelSorter<?> getModelSorter();

  public void sortData(boolean force) {

    JKTableModelSorter<?> sorter = this.getModelSorter();
    if (sorter != null) {
      EZEnvironment.setWaitCursor();
      ProgressTinyDialog mon = null;
      if ((!sorter.stillSorted(getSortColumn())) && (sorter.getSize() > 100000)) {// display Progress UI only with large
                                                                                  // amount of data
        mon = new ProgressTinyDialog("Sorting table", 0, true, true, false);
      }

      SortDataThread task = new SortDataThread(mon, force, sorter);
      task.start();
      if (mon != null) {
        mon.setVisible(true);
      }

      EZEnvironment.setDefaultCursor();
    }
  }

  protected void sortData(ProgressTinyDialog monitor, boolean force, JKTableModelSorter<?> sorter) {
    SerialEntityBag data = sorter.sort(monitor, getSortColumn(), force);

    if (data == null)
      return;
    this.sortedItems = data;

    this.fireTableDataChanged();
  }

  private class SortDataThread extends Thread {
    ProgressTinyDialog monitor;
    boolean force;
    JKTableModelSorter<?> sorter;

    public SortDataThread(ProgressTinyDialog mon, boolean force, JKTableModelSorter<?> sorter) {
      monitor = mon;
      this.force = force;
      this.sorter = sorter;
    }

    public void run() {
      sortData(monitor, force, sorter);
    }
  }

}
