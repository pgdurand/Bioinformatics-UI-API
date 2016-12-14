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

import javax.swing.table.AbstractTableModel;

import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSymbol;
import bzh.plealog.bioinfo.util.DAlphabetUtils;

/**
 * This class wraps a DSequence within a TableModel so that it can be displayed
 * within a JTable component.
 * 
 * @author Patrick G. Durand
 */
public class TableDSequenceModel extends AbstractTableModel {
  private static final long serialVersionUID = -6628093497998350021L;
  private DSequence _sequence;
  private int       _nRows;
  private int       _nbBlockPerLine = DSequenceTableViewer.DEFAULT_BLOCK_PER_LINE;
  private int       _blockSize = DSequenceTableViewer.DEFAULT_BLOCK_SIZE;
  private DSymbol   _spaceSymbol;

  public TableDSequenceModel(){
    super();
  }

  public TableDSequenceModel(int blockSize, int bpl){
    super();
    setBlockSize(blockSize);
    setBlockPerLine(bpl);
  }
  public TableDSequenceModel(DSequence sequence){
    super();
    setSequence(sequence);
  }
  public TableDSequenceModel(DSequence sequence, int blockSize, int bpl){
    super();
    setBlockSize(blockSize);
    setBlockPerLine(bpl);
    setSequence(sequence);
  }
  public void setSequence(DSequence sequence){
    _sequence = sequence;
    _spaceSymbol = DAlphabetUtils.getIUPAC_Protein_Alphabet().getSymbol(DSymbol.SPACE_SYMBOL_CODE);
    computeRowCols();
  }
  public DSequence getSequence(){
    return _sequence;
  }
  public void resetModel(){
    _sequence = null;
    _nRows = 0;
  }
  public String getColumnName(int column){
    //value for header is handled by the CellRenderer
    return "";
  }
  public int getColumnCount() {
    return (_nbBlockPerLine*_blockSize) + _nbBlockPerLine - 1;
  }
  public int getRowCount() {
    return _sequence!=null ? _nRows : 0 ;
  }
  private void computeRowCols(){
    int nbSeqCells = _nbBlockPerLine*_blockSize;
    if (_sequence==null){
      _nRows = 0;
      return;
    }
    _nRows = _sequence.size() / nbSeqCells;
    if ((_sequence.size()%nbSeqCells)!=0){
      _nRows++;
    }
  }
  protected void setBlockSize(int blockSize){
    _blockSize = blockSize;
    computeRowCols();
  }
  protected void setBlockPerLine(int bpl){
    _nbBlockPerLine = bpl;
    computeRowCols();
  }
  public Object getValueAt(int row, int col){
    DSymbol sym = _spaceSymbol;
    int     idx;

    if (_sequence!=null){
      col++;
      if ((col%(_blockSize+1))!=0){
        col--;
        idx = row * (_nbBlockPerLine*_blockSize) + col - (col/(_blockSize+1));
        if (idx<_sequence.size())
          sym = _sequence.getSymbol(idx);
      }
    }
    return sym;
  }
}
