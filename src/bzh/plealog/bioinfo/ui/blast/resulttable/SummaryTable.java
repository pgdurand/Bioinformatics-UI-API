/* Copyright (C) 2003-2019 Patrick G. Durand
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
package bzh.plealog.bioinfo.ui.blast.resulttable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.HashMap;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.plealog.genericapp.api.EZEnvironment;

import bzh.plealog.bioinfo.api.data.searchjob.QueryBase;
import bzh.plealog.bioinfo.api.data.searchjob.SJTermSummary;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.ui.resources.SVMessages;
import bzh.plealog.bioinfo.ui.util.JKTable;
import bzh.plealog.bioinfo.ui.util.JPercentLabel;

/**
 * Summary Table framework.
 * 
 * It aims at displaying individual fasta query vs best hit within a unique
 * customized JTable.
 * 
 * @author Patrick G. Durand
 */
public class SummaryTable extends JKTable {
  private static final long serialVersionUID = -6439157624769576500L;

  private PercentRenderer _pctRenderer;

  private ImageIcon _doneImg1 = EZEnvironment.getImageIcon("gear_ok_16_s1.png");
  private ImageIcon _doneImg2 = EZEnvironment.getImageIcon("gear_ok_16_s2.png");
  private ImageIcon _doneImg3 = EZEnvironment.getImageIcon("gear_ok_16_s3.png");
  private ImageIcon _doneImg4 = EZEnvironment.getImageIcon("gear_ok_16_s4.png");
  private ImageIcon _doneImg5 = EZEnvironment.getImageIcon("gear_ok_16_s5.png");
  private ImageIcon _doneImg6 = EZEnvironment.getImageIcon("gear_ok_16_s6.png");
  private ImageIcon _errorImg = EZEnvironment.getImageIcon("gear_warning_16.png");
  private ImageIcon _waitingImg = EZEnvironment.getImageIcon("gear_pause_16.png");
  private ImageIcon _runningImg = EZEnvironment.getImageIcon("gear_run_16.png");
  private ImageIcon _stoppedImg = EZEnvironment.getImageIcon("gear_stop_16.png");
  private HashMap<String, Icon> headerIcons;

  public static final Color BK_TWO_ROWS_CLR = new Color(228, 236, 236);

  /**
   * Constructor.
   * 
   * @param dm the data model to display.
   */
  public SummaryTable(SummaryTableModel dm) {
    super(dm);
    _pctRenderer = new PercentRenderer();
  }

  /**
   * This method converts the JTable based selected value to the corresponding
   * BFileSummary by taking into account the sorting structure of the table.
   * 
   * @param selRow selected row index
   * 
   * @return BFileSummary index
   */
  public int convertSelectedRowToSelectedSummary(int selRow) {
    if (selRow == -1)
      return selRow;

    SummaryTableModel model = (SummaryTableModel) this.getModel();
    return model.convertTableRowToSummaryIdx(selRow);
  }

  /**
   * This method converts the JTable based selected values to the corresponding
   * BFileSummaries by taking into account the sorting structure of the table.
   * 
   * @param selRows selected row indices
   * 
   * @return BFileSummary indices
   */
  public int[] convertSelectedRowsToSelectedSummaries(int[] selRows) {
    SummaryTableModel model = (SummaryTableModel) this.getModel();
    int[] selIdx;
    int i, size;

    size = selRows.length;
    // nothing selected ; see JTable API documentation
    if (size == 0)
      return selRows;

    selIdx = new int[size];
    for (i = 0; i < size; i++) {
      selIdx[i] = model.convertTableRowToSummaryIdx(selRows[i]);
    }
    return selIdx;
  }

  /**
   * This method is used to convert a BFileSummary index to a row table. This
   * method relies on the internal sorted data model of BFileSummaries.
   * 
   * @param idx BFileSummary index
   * 
   * @return table row index
   */
  public int convertSummaryIdxToTableRow(int idx) {
    SummaryTableModel model = (SummaryTableModel) this.getModel();
    return model.convertSummaryIdxToTableRow(idx);
  }

  /**
   * Set the view type of this component.
   * 
   * @param vType viewer type
   */
  public void setViewType(SummaryTableModel.VIEW_TYPE vType) {
    SummaryTableModel model = (SummaryTableModel) this.getModel();
    model.setViewType(vType);
  }

  /**
   * Returns the types of classification to view.
   * See SRFileSummary.getClassificationForView()
   */
  public List<String> getClassificationsToView(){
    SummaryTableModel model = (SummaryTableModel) this.getModel();
    return model.getClassificationsToView();
  }
  
  /**
   * Returns the types of classification to view.
   * See SRFileSummary.getClassificationForView()
   */
  public void setClassificationsToView(List<String> cToV){
    SummaryTableModel model = (SummaryTableModel) this.getModel();
    model.setClassificationsToView(cToV);
    updateRowHeights();
  }

  
  @Override
  public TableCellRenderer getCellRenderer(int row, int column) {
    TableCellRenderer tcr;
    SummaryTableModel tModel;
    int colID;

    tModel = (SummaryTableModel) this.getModel();
    colID = tModel.getColumnId(column);

    if (colID == SummaryTableModel.RES_IDENTITY || colID == SummaryTableModel.RES_SIMILARITY
        || colID == SummaryTableModel.RES_COVERAGE || colID == SummaryTableModel.RES_COVERAGE_H
        || colID == SummaryTableModel.RES_P_GAPS) {
      tcr = _pctRenderer;
    } else {
      tcr = super.getCellRenderer(row, column);
    }

    if (tcr instanceof JPercentLabel) {
      ((JPercentLabel)tcr)._lbl.setVerticalAlignment(SwingConstants.TOP);
    }
    else if (tcr instanceof JLabel) {
      JLabel lbl;

      lbl = (JLabel) tcr;
      lbl.setVerticalAlignment(SwingConstants.TOP);
      if (colID == SummaryTableModel.RES_FILE_NUM_HEADER || colID == SummaryTableModel.RES_SUMMARY_BEST_HIT_ACC
          || colID == SummaryTableModel.RES_SUMMARY_BEST_HIT_EVAL
          || colID == SummaryTableModel.RES_SUMMARY_BEST_HIT_SCORE
          || colID == SummaryTableModel.RES_SUMMARY_BEST_HIT_SCOREBITS || colID == SummaryTableModel.RES_IDENTITY
          || colID == SummaryTableModel.RES_SIMILARITY || colID == SummaryTableModel.RES_COVERAGE
          || colID == SummaryTableModel.RES_COVERAGE_H || colID == SummaryTableModel.RES_SUMMARY_BEST_HIT_LEN
          || colID == SummaryTableModel.RES_QUERY_LENGTH || colID == SummaryTableModel.RES_QUERY_FROM
          || colID == SummaryTableModel.RES_QUERY_TO || colID == SummaryTableModel.RES_QUERY_FRAME
          || colID == SummaryTableModel.RES_QUERY_GAPS || colID == SummaryTableModel.RES_BESTHIT_FROM
          || colID == SummaryTableModel.RES_BESTHIT_TO || colID == SummaryTableModel.RES_BESTHIT_FRAME
          || colID == SummaryTableModel.RES_BESTHIT_GAPS || colID == SummaryTableModel.RES_ALIGN_LENGTH
          || colID == SummaryTableModel.RES_NB_HITS || colID == SummaryTableModel.RES_NB_HSPS
          || colID == SummaryTableModel.RES_T_GAPS || colID == SummaryTableModel.RES_P_GAPS
          || colID == SummaryTableModel.RES_MISMATCHES) {
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
      } else {
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
      }
      if (colID == SummaryTableModel.RES_STATUS_HEADER) {
        Object value;

        value = tModel.getValueAt(row, SummaryTableModel.STATUS_DATA_COL);
        if (value != null) {
          String status = value.toString();
          if (status.startsWith(QueryBase.STATUS_OK)) {
            Boolean isFiltered = (Boolean) tModel.getValueAt(row, SummaryTableModel.FILTER_DATA_COL);
            SROutput.FEATURES_CONTAINER fCont = (SROutput.FEATURES_CONTAINER) tModel.getValueAt(row,
                SummaryTableModel.FEAT_DATA_COL);
            ImageIcon img = _doneImg1;
            if (isFiltered) {
              switch (fCont) {
              case none:
                img = _doneImg2;
                break;
              case allHits:
                img = _doneImg3;
                break;
              case someWithErrors:
                img = _doneImg4;
                break;
              }
            } else {
              switch (fCont) {
              case none:
                img = _doneImg1;
                break;
              case allHits:
                img = _doneImg6;
                break;
              case someWithErrors:
                img = _doneImg5;
                break;
              }
            }
            lbl.setIcon(img);
          } else if (status.startsWith(QueryBase.STATUS_STOP)) {
            lbl.setIcon(_stoppedImg);
          } else if (status.startsWith(QueryBase.STATUS_RUNNING)) {
            lbl.setIcon(_runningImg);
          } else if (status.startsWith(QueryBase.STATUS_WAITING)) {
            lbl.setIcon(_waitingImg);
          } else {
            lbl.setIcon(_errorImg);
          }
        } else {
          lbl.setIcon(null);
        }
      } else {
        lbl.setIcon(null);
      }

      if (row % 2 == 0) {
        lbl.setBackground(BK_TWO_ROWS_CLR);
      } else {
        lbl.setBackground(Color.WHITE);
      }
    }
    return tcr;
  }

  public void updateRowHeights() {
    //System.out.println("--> updateRowHeights()");

    SummaryTableModel model = (SummaryTableModel) this.getModel();
    int colID;
    boolean showClassification = false;
    //find out whether Classification column is displayed
    for (int i=0 ; i< this.getColumnCount() ; i++) {
      colID = model.getColumnId(i);
      if (colID == SummaryTableModel.RES_HITCLASSIFICATION 
          || colID == SummaryTableModel.RES_QUERYCLASSIFICATION) {
        showClassification = true;
        break;
      }
    }
    //get current FontMetrics for Table Font
    Font fnt = UIManager.getLookAndFeelDefaults().getFont("Table.font");//this.getFont()->null !
    FontMetrics fm = this.getFontMetrics(fnt);
    int height = fm.getHeight();
    int i, size, classifs;
    size = this.getRowCount();
    //setup row height
    for(i=0 ; i<size ; i++) {
      if (showClassification) {
        List<SJTermSummary> lst_h = model.getQuery().getSummary(i).getHitClassificationForView(model.getClassificationsToView()); 
        List<SJTermSummary> lst_q = model.getQuery().getSummary(i).getQueryClassificationForView(model.getClassificationsToView()); 
        classifs = Math.max(lst_h!=null ? lst_h.size() : 1, lst_q!=null ? lst_q.size() : 1);
        classifs = Math.max(1, classifs);
      }
      else {
        classifs = 1;
      }
      //System.out.println("Entry: "+i+" - rows: "+classifs);
      this.setRowHeight(i, (classifs * height) + fm.getDescent());
    }

  }
  @Override
  public void tableChanged(TableModelEvent event) {
    super.tableChanged(event);
    updateRowHeights();
  }

  /**
   * Render a cell table using Percent Component.
   */
  private class PercentRenderer extends JPercentLabel implements TableCellRenderer {
    private static final long serialVersionUID = 7570892723306000099L;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

      setForeground(UIManager.getColor("TextField.foreground"));
      if (isSelected) {
        setBackground(table.getSelectionBackground());
      } else {
        if (row % 2 == 0) {
          setBackground(BK_TWO_ROWS_CLR);
        } else {
          setBackground(table.getBackground());
        }
      }
      this.setValue(value != null ? value.toString() : null);
      return this;
    }
  }

  @Override
  public void initColumnSize(int width, int[] colWidth) {
    FontMetrics fm;
    TableColumnModel tcm;
    TableColumn tc, lastTc = null;
    String header, widerColumnName = null;
    int i, size, tot, val, fact;

    if (width <= 0)// may happen (found with the debugger)!!!
      return;
    fm = this.getFontMetrics(SummaryTable.this.getFont());
    tcm = this.getColumnModel();
    size = tcm.getColumnCount();
    if (tableChanged) {
      tableChanged = false;
      HashMap<Integer, Integer> defWidths;

      defWidths = getColumnsDefWidthMap();
      if (defWidths == null)
        return;
      TableColumnModel tModel = this.getColumnModel();
      SummaryTableModel model = (SummaryTableModel) this.getModel();

      size = tModel.getColumnCount();
      for (i = 0; i < size; i++) {
        tc = tModel.getColumn(i);
        if (defWidths.containsKey(model.getColumnId(i))) {
          tc.setPreferredWidth(defWidths.get(model.getColumnId(i)));
        }
      }
      return;
    }
    if (colWidth != null && colWidth.length == size) {
      for (i = 0; i < size; i++) {
        tcm.getColumn(i).setPreferredWidth(colWidth[i]);
      }
    } else {
      tot = 0;
      for (i = 0; i < size; i++) {
        tc = tcm.getColumn(i);
        header = tc.getHeaderValue().toString();
        // by default, 'HitDesc' (RES_HEADERS[4]) is the largest column
        if (header.equals(SummaryTableModel.RES_HEADERS[4])) {
          widerColumnName = SummaryTableModel.RES_HEADERS[4];
          break;
        }
      }
      // if column HitDesc not found, then the QueryName will the wider column
      if (widerColumnName == null) {
        widerColumnName = SummaryTableModel.RES_HEADERS[1];
      }
      for (i = 0; i < size; i++) {
        tc = tcm.getColumn(i);
        header = tc.getHeaderValue().toString();
        if (!header.equals(widerColumnName)) {
          if (header.equals(SummaryTableModel.RES_HEADERS[1]))// name
            fact = 3;
          else
            fact = 1;
          val = fact * fm.stringWidth(header) + 20;
          tc.setPreferredWidth(val);
          tot += val;
        } else {
          lastTc = tc;
        }
      }
      if (lastTc != null) {
        lastTc.setPreferredWidth(width - tot - 2);
      }
    }
  }

  @Override
  public HashMap<String, Icon> getHeaderIcons() {
    if (this.headerIcons == null) {
      this.headerIcons = new HashMap<String, Icon>();
      this.headerIcons.put(SVMessages.getString("ResultTableModel.tableHeader.32"),
          EZEnvironment.getImageIcon("gear.png"));
      this.headerIcons.put(SVMessages.getString("ResultTableModel.tableHeader.33"),
          EZEnvironment.getImageIcon("gear.png"));
    }
    return this.headerIcons;
  }

  @Override
  public HashMap<String, String> getToolTips() {
    HashMap<String, String> result = new HashMap<String, String>();

    result.put(SVMessages.getString("ResultTableModel.tableHeader.32"),
        SVMessages.getString("ResultTableModel.tableHeader.tooltip.32"));
    result.put(SVMessages.getString("ResultTableModel.tableHeader.33"),
        SVMessages.getString("ResultTableModel.tableHeader.tooltip.33"));

    return result;
  }

  /**
   * Return a string representation of table header.
   * 
   * @return job name associated with the QueryBase.
   */
  public String getHeader() {
    SummaryTableModel model = (SummaryTableModel) this.getModel();
    return model.getQuery().getJobName();
  }
}