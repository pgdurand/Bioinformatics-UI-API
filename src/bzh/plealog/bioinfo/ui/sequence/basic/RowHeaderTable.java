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

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import bzh.plealog.bioinfo.api.data.sequence.DRulerModel;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;

/**
 * This class is for internal use only. It is used to display the numbering of
 * the sequence on the left of the tabular sequence viewer.
 *
 * @author Patrick G. Durand
 */
public class RowHeaderTable extends JTable{
  private static final long serialVersionUID = 2601726956907329212L;

  protected static final Color QUERY_CELL_BK_COLOR = new Color(184,207,229);

  private int               _nbBlockPerLine = DSequenceTableViewer.DEFAULT_BLOCK_PER_LINE;
  private int               _blockSize = DSequenceTableViewer.DEFAULT_BLOCK_SIZE;

  public RowHeaderTable(DSequence seq) {
    super();
    this.setModel(new RowHeaderTableModel(new TableDSequenceModel(seq)));
  }
  public void updateModel(DSequence seq){
    this.setModel(new RowHeaderTableModel(new TableDSequenceModel(seq)));
  }
  public TableCellRenderer getCellRenderer(int row, int column) {
    TableCellRenderer tcr;

    tcr = super.getCellRenderer(row, column);
    if (tcr instanceof JLabel){
      JLabel lbl;

      lbl = (JLabel) tcr;
      lbl.setHorizontalAlignment(SwingConstants.CENTER);
      lbl.setForeground(Color.BLACK);
      lbl.setBackground(QUERY_CELL_BK_COLOR);
    }
    return tcr;
  }

  private class RowHeaderTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -8607546044496561293L;
    private TableDSequenceModel    _tSeqModel;
    private int[]                  _rowHeaderVals;

    @SuppressWarnings("unused")
    public RowHeaderTableModel(){
    }

    public RowHeaderTableModel(TableDSequenceModel tSeqModel){
      updateModel(tSeqModel);
    }

    public void updateModel(TableDSequenceModel tSeqModel){
      DRulerModel rModel = null;

      int       i, nRows;
      _tSeqModel = tSeqModel;

      if (_tSeqModel == null)
        return;
      nRows = _tSeqModel.getRowCount();
      _rowHeaderVals = new int[nRows];
      if (_tSeqModel.getSequence()!=null)
        rModel = _tSeqModel.getSequence().getRulerModel();
      for(i=0;i<nRows;i++){
        if (rModel!=null)
          _rowHeaderVals[i] = rModel.getSeqPos(i * (_nbBlockPerLine*_blockSize));
        else
          _rowHeaderVals[i] = i * (_nbBlockPerLine*_blockSize) + 1;
      }
    }
    public String getColumnName(int column){
      return "Pos";
    }

    public int getColumnCount() { 
      return 1;
    }

    public int getRowCount() {
      if (_tSeqModel==null)
        return 0;
      return _tSeqModel.getRowCount();
    }

    public Object getValueAt(int row, int col) {
      if (_tSeqModel == null)
        return "";

      return _rowHeaderVals[row];
    }
  }
}
