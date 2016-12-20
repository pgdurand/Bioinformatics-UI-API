/* Copyright (C) 2003-2016 Patrick G. Durand
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
package bzh.plealog.bioinfo.ui.blast.io;

/**
 * This class defines the options that can be used to modify the rendering of
 * SROutput used within the TxtExportSROutput export system.
 * 
 * @author Patrick G. Durand
 */
public class TxtExportSROutputOptions {
  private boolean exportTable;
  private boolean exportAlignments;
  private boolean exportHeaderPrgmDb;
  private boolean exportHeaderQueryName;
  private int[]   colIds;

  /**
   * Constructor.
   * 
   * By default we export hit table, sequence alignments, program description
   * and query description. You can use methods is this class to tune what you want
   * to export, then use that object to configure TxtExportSROutput framework.
   */
  public TxtExportSROutputOptions() {
    setExportTable(true);
    setExportAlignments(true);
    setExportHeaderPrgmDb(true);
    setExportHeaderQueryName(true);
  }

  /**
   * Constructor.
   * 
   * @param exportTable true or false
   * @param exportAlignments true or false
   */
  public TxtExportSROutputOptions(boolean exportTable, boolean exportAlignments) {
    super();
    this.exportTable = exportTable;
    this.exportAlignments = exportAlignments;
  }

  /**
   * Do we have to export the hit table.
   */
  public boolean isExportTable() {
    return exportTable;
  }

  /**
   * Do we have to export the sequence alignments.
   */
  public boolean isExportAlignments() {
    return exportAlignments;
  }

  /**
   * Do we have to export the program description.
   */
  public boolean isExportHeaderPrgmDb() {
    return exportHeaderPrgmDb;
  }

  /**
   * Do we have to export the query description.
   */
  public boolean isExportHeaderQueryName() {
    return exportHeaderQueryName;
  }

  /**
   * What are the columns to display in the exported hit table.
   */
  public int[] getColIds() {
    return colIds;
  }

  /**
   * Figure out whether or not to export hit table.
   * */
  public void setExportTable(boolean exportTable) {
    this.exportTable = exportTable;
  }

  /**
   * Figure out whether or not to export sequence alignments.
   * */
  public void setExportAlignments(boolean exportAlignments) {
    this.exportAlignments = exportAlignments;
  }

  /**
   * Figure out whether or not to export program description.
   * */
  public void setExportHeaderPrgmDb(boolean exportHeaderPrgmDb) {
    this.exportHeaderPrgmDb = exportHeaderPrgmDb;
  }

  /**
   * Figure out whether or not to export query description.
   * */
  public void setExportHeaderQueryName(boolean exportHeaderQueryName) {
    this.exportHeaderQueryName = exportHeaderQueryName;
  }

  /**
   * Set the hit table column model to export.
   * */
  public void setColIds(int[] colIds) {
    this.colIds = colIds;
  }
}
