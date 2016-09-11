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
package bzh.plealog.bioinfo.ui.sequence.event;

/**
 * Utility class used to maintain selection range during mouse actions.
 * 
 * @author Patrick G. Durand
 */
public class DDSelectionRange {
  public int startRow;
  public int lastRow;
  public int lastCol;
  public int startCol;
  public DDSelectionRange(){
    super();
    this.set(-1, -1, -1, -1);
  }
  public DDSelectionRange(int startRow, int startCol, int lastRow, int lastCol) {
    super();
    this.set(startRow, startCol, lastRow, lastCol);
  }
  public void set(DDSelectionRange src){
    this.set(src.startRow, src.startCol, src.lastRow, src.lastCol);
  }
  public void set(int startRow, int startCol, int lastRow, int lastCol){
    this.startRow = startRow;
    this.startCol = startCol;
    this.lastRow = lastRow;
    this.lastCol = lastCol;
  }
  public void reset(){
    startRow = startCol = lastRow = lastCol = -1;
  }
  public String toString(){
    return "["+startRow+","+startCol+";"+lastRow+","+lastCol+"]";
  }
  public boolean equals(Object obj){
    if (! (obj instanceof DDSelectionRange))
      return false;
    DDSelectionRange range = (DDSelectionRange) obj;
    return (this.startRow==range.startRow &&
        this.startCol==range.startCol &&
        this.lastRow==range.lastRow &&
        this.lastCol==range.lastCol
        );
  }
}
