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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

/**
 * This class is responsible for saving Table data in an OutputStream using the
 * standard CSV format. See http://www.wotsit.org/ for details on CSV.
 * 
 * @author Patrick G. Durand
 * @since 2006
 */
public class CSVExport {

  private char _separator = ',';
  private boolean _escapeString = true; // used for data only, not column
                                        // headers

  public CSVExport() {
  }

  /**
   * Constructor.
   * 
   * @param separator
   *          the character used to separate data fields. Default is a comma.
   */
  public CSVExport(char separator) {
    _separator = separator;
  }

  /**
   * Figures out if parameter obj is a basic Java type.
   * 
   * @return true if obj is either a Number, a String, a Character or a Boolean.
   */
  protected boolean isBasicType(Object obj) {
    if (obj instanceof Number || obj instanceof String || obj instanceof Character || obj instanceof Boolean)
      return true;
    else
      return false;
  }

  /**
   * Figures out if parameter obj is a String.
   * 
   * @return true if obj is a String.
   */
  protected boolean isString(Object obj) {
    if (obj instanceof String)
      return true;
    else
      return false;
  }

  public void setSeparator(char separator) {
    _separator = separator;
  }

  public void escapeString(boolean escapeString) {
    _escapeString = escapeString;
  }

  private void writeString(Writer writer, String data) throws IOException {
    if (_escapeString) {
      writer.write("\"");
    }
    writer.write(data);
    if (_escapeString) {
      writer.write("\"");
    }
  }

  /**
   * Export data.
   * 
   * @param os
   *          the OutputStream where to save data
   * @param data
   *          the data to save
   * @param selection
   *          the rows that only have to be saved. If null, all the rows in data
   *          are saved.
   * 
   * @throws Exception
   *           this exception is thrown if something wrong occured during save
   *           process.
   */
  public void export(OutputStream os, TableModel data, ListSelectionModel selection) throws Exception {
    export(os, data, selection, null);
  }

  public void export(OutputStream os, TableModel data, ListSelectionModel selection, ProgressTinyDialog ptf)
      throws Exception {
    PrintWriter writer;
    Object obj;
    int i, j, row, col;

    if (os == null || data == null)
      return;
    writer = new PrintWriter(os);
    row = data.getRowCount();
    col = data.getColumnCount();

    // header first
    for (i = 0; i < col; i++) {
      writer.write("\"" + data.getColumnName(i) + "\"");
      if ((i + 1) < col)
        writer.write(_separator);
    }
    writer.println();
    // table content
    for (j = 0; j < row; j++) {
      if (selection != null) {
        if (selection.isSelectedIndex(j) == false) {
          continue;
        }
      }
      for (i = 0; i < col; i++) {
        obj = data.getValueAt(j, i);
        if (obj == null) {
          writer.write("null");
        } else if (isBasicType(obj)) {
          if (isString(obj)) {
            writeString(writer, obj.toString().replaceAll("\"", "'"));
          } else {
            writer.write(obj.toString());
          }
        } else {
          writeString(writer, "n/a");
        }
        if ((i + 1) < col)
          writer.write(_separator);
      }
      if (ptf != null) {
        ptf.addToProgress(1);
        if (ptf.stopProcessing()) {
          break;
        }
      }
      writer.println();
    }
    writer.flush();
  }

}
