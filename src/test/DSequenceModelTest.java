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
package test;

import javax.swing.table.AbstractTableModel;

import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSymbol;
import bzh.plealog.bioinfo.api.data.sequence.DViewerSystem;

/**
 * This class implements a sequence model. It is intended to wraps a DSequence
 * so that it can be viewed in a DSequenceListViewer.
 * 
 * @author Patrick G. Durand
 */
public class DSequenceModelTest extends AbstractTableModel {
  private static final long serialVersionUID = 6519721557306681212L;
  private DSequence  _sequence;
  private DSymbol    _letter = DViewerSystem.getSymbolFactory().createDSymbol(0,' ');

  /**
   * Constructor from a DSequence.
   */
  public DSequenceModelTest(DSequence seq){
    _sequence = seq;
  }

  /**
   * Returns the DSeqeunce wraps in this model.
   */
  public DSequence getSequence(){
    return _sequence;
  }

  /**
   * Returns the maximum size of this model.
   */
  public int getSize() { 
    if (_sequence==null)
      return 0;
    return _sequence.size();
  }

  public int getColumnCount(){
    return getSize();
  }
  public int getRowCount(){
    return 1;
  }
  /**
   * Returns the element located at a particular position. 
   * 
   * @return actually it is a DSymbol.
   * @see javax.swing.ListModel#getElementAt(int)
   */
  public Object getValueAt(int row, int index) {
    int idx;
    if (_sequence==null)
      return "";
    idx = index;
    if (idx>=0 && idx<_sequence.size()){
      return(_sequence.getSymbol(idx));
    }
    else{
      return (_letter);
    }
  }

}
