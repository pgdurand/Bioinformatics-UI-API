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
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import bzh.plealog.bioinfo.api.data.searchjob.BFileSummary;
import bzh.plealog.bioinfo.api.data.searchjob.QueryBase;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.ui.blast.core.QueryBaseUI;
import bzh.plealog.bioinfo.ui.blast.resulttable.sort.Entity;
import bzh.plealog.bioinfo.ui.blast.resulttable.sort.SummaryTableModelSorter;
import bzh.plealog.bioinfo.ui.blast.resulttable.sort.SerialEntityBag;
import bzh.plealog.bioinfo.ui.resources.SVMessages;
import bzh.plealog.bioinfo.ui.util.JKTableModel;
import bzh.plealog.bioinfo.ui.util.JKTableModelSorter;
import bzh.plealog.bioinfo.ui.util.ProgressTinyDialog;
import bzh.plealog.bioinfo.ui.util.TableHeaderColumnItem;

/**
 * This is the table model for the JTable displaying the list of Blast hits for
 * a particular query.
 * 
 * @author Patrick G. Durand
 */
public class SummaryTableModel extends JKTableModel {
  private static final long serialVersionUID = 1650459792644776263L;

  private TableHeaderColumnItem[] _colItemsReference;
  private boolean[] _hasHits; // use to optimize display with big job
  private int _queryStatus = -1;
  private QueryBaseUI _query;
  private SerialEntityBag _sortedSummaries;
  private int _rows = -1;
  private HashMap<Integer, Integer> _indexConvertor;
  private ArrayList<Integer> _viewTypeDataBinner;
  private VIEW_TYPE _viewType = VIEW_TYPE.ALL;

  public static final Color COLOR_QUERY = new Color(95, 159, 253);
  public static final Color COLOR_HIT = new Color(224, 120, 92);

  public static final int SUMMARY_DATA_COL = -1;
  public static final int QUERY_DATA_COL = -2;
  public static final int SEQUENCE_DATA_COL = -3;
  public static final int STATUS_DATA_COL = -4;
  public static final int FEAT_DATA_COL = -5;
  public static final int FILTER_DATA_COL = -6;
  public static final int RESULT_DATA_COL = -7;

  private static final String DEF_COL_PROP_KEY = "resTable.columns";
  private static final String SORT_COL_PROP_KEY = "resTable.columns.sort.id";
  private static final String SORT_ASCEND_PROP_KEY = "resTable.columns.sort.asc";
  public static final String DEF_COL_SIZE_PROP_KEY = "resTable.columns.size";

  // DO NOT MODIFY THESE VALUES: ALWAYS INCREMENT !
  public static final int RES_FILE_NUM_HEADER = 0;
  public static final int RES_SEQ_NAME_HEADER = 1;
  public static final int RES_STATUS_HEADER = 2;
  public static final int RES_SUMMARY_BEST_HIT_ACC = 3;
  public static final int RES_SUMMARY_BEST_HIT_DEF = 4;
  public static final int RES_SUMMARY_BEST_HIT_LEN = 5;
  public static final int RES_SUMMARY_BEST_HIT_EVAL = 6;
  public static final int RES_IDENTITY = 7;
  public static final int RES_SIMILARITY = 8;
  public static final int RES_COVERAGE = 9;
  public static final int RES_FILE_NAME_HEADER = 10;
  public static final int RES_SUMMARY_BEST_HIT_SCORE = 11;
  public static final int RES_SUMMARY_BEST_HIT_SCOREBITS = 12;
  public static final int RES_COVERAGE_H = 13;
  public static final int RES_TAXONOMY = 14;
  public static final int RES_ORGANISM = 15;
  public static final int RES_QUERY_LENGTH = 16;
  public static final int RES_QUERY_FROM = 17;
  public static final int RES_QUERY_TO = 18;
  public static final int RES_QUERY_FRAME = 19;
  public static final int RES_QUERY_GAPS = 20;
  public static final int RES_BESTHIT_FROM = 21;
  public static final int RES_BESTHIT_TO = 22;
  public static final int RES_BESTHIT_FRAME = 23;
  public static final int RES_BESTHIT_GAPS = 24;
  public static final int RES_ALIGN_LENGTH = 25;
  public static final int RES_NB_HITS = 26;
  public static final int RES_NB_HSPS = 27;
  public static final int RES_T_GAPS = 28;
  public static final int RES_P_GAPS = 29;
  public static final int RES_MISMATCHES = 30;
  public static final int RES_LCA = 31;
  public static final int RES_RANK_LCA = 32;
  public static final int RES_ORIGIN_JOB = 33;

  public static enum VIEW_TYPE {
    ALL, // all queries
    HITS_ONLY, // all queries having hits
    NO_HITS_ONLY // all queries that do not match
  };

  private static final int[] QUERY_ORDERED_HEADER_IDS = new int[] { RES_FILE_NUM_HEADER, RES_SEQ_NAME_HEADER,
      RES_QUERY_LENGTH, RES_STATUS_HEADER, RES_QUERY_FROM, RES_QUERY_TO, RES_QUERY_FRAME, RES_QUERY_GAPS, RES_COVERAGE,
      RES_LCA, RES_RANK_LCA };

  private static final int[] HIT_ORDERED_HEADER_IDS = new int[] { RES_NB_HITS, RES_SUMMARY_BEST_HIT_ACC,
      RES_SUMMARY_BEST_HIT_DEF, RES_SUMMARY_BEST_HIT_LEN, RES_NB_HSPS, RES_TAXONOMY, RES_ORGANISM, RES_BESTHIT_FROM,
      RES_BESTHIT_TO, RES_BESTHIT_FRAME, RES_BESTHIT_GAPS, RES_COVERAGE_H, RES_SUMMARY_BEST_HIT_EVAL,
      RES_SUMMARY_BEST_HIT_SCORE, RES_SUMMARY_BEST_HIT_SCOREBITS, RES_IDENTITY, RES_SIMILARITY, RES_P_GAPS,
      RES_ALIGN_LENGTH, RES_T_GAPS, RES_MISMATCHES, RES_FILE_NAME_HEADER, RES_ORIGIN_JOB };

  private static String[] QUERY_HEADERS = new String[QUERY_ORDERED_HEADER_IDS.length];

  // WARNING: arrays ORDERED_HEADER_IDS, RES_HEADERS_INT and RES_HEADERS must have
  // the same size

  // this array is used to prepare the order used to display the various columns
  public static final int[] ORDERED_HEADER_IDS = ArrayUtils.addAll(QUERY_ORDERED_HEADER_IDS, HIT_ORDERED_HEADER_IDS);

  protected static final int[] RES_HEADERS_INT = { /* 0 */RES_FILE_NUM_HEADER, /* 1 */RES_SEQ_NAME_HEADER,
      /* 2 */RES_STATUS_HEADER, /* 3 */RES_SUMMARY_BEST_HIT_ACC, /* 4 */RES_SUMMARY_BEST_HIT_DEF,
      /* 5 */RES_SUMMARY_BEST_HIT_LEN, /* 6 */RES_SUMMARY_BEST_HIT_EVAL, /* 7 */RES_IDENTITY, /* 8 */RES_SIMILARITY,
      /* 9 */RES_COVERAGE, /* 10 */RES_FILE_NAME_HEADER, /* 11 */RES_SUMMARY_BEST_HIT_SCORE,
      /* 12 */RES_SUMMARY_BEST_HIT_SCOREBITS, /* 13 */RES_COVERAGE_H, /* 14 */RES_TAXONOMY, /* 15 */RES_ORGANISM,
      /* 16 */RES_QUERY_LENGTH, /* 17 */RES_QUERY_FROM, /* 18 */RES_QUERY_TO, /* 19 */RES_QUERY_FRAME,
      /* 20 */RES_QUERY_GAPS, /* 21 */RES_BESTHIT_FROM, /* 22 */RES_BESTHIT_TO, /* 23 */RES_BESTHIT_FRAME,
      /* 24 */RES_BESTHIT_GAPS, /* 25 */RES_ALIGN_LENGTH, /* 26 */RES_NB_HITS, /* 27 */RES_NB_HSPS, /* 28 */RES_T_GAPS,
      /* 29 */RES_P_GAPS, /* 30 */RES_MISMATCHES, /* 31 */RES_LCA, /* 32 */RES_RANK_LCA, /* 33 */RES_ORIGIN_JOB };

  public static final String[] RES_HEADERS = { 
      SVMessages.getString("ResultTableModel.tableHeader.1"),
      SVMessages.getString("ResultTableModel.tableHeader.2"),
      SVMessages.getString("ResultTableModel.tableHeader.7"),
      SVMessages.getString("ResultTableModel.tableHeader.3"),
      SVMessages.getString("ResultTableModel.tableHeader.4"),
      SVMessages.getString("ResultTableModel.tableHeader.5"),
      SVMessages.getString("ResultTableModel.tableHeader.6"),
      SVMessages.getString("ResultTableModel.tableHeader.9"),
      SVMessages.getString("ResultTableModel.tableHeader.10"),
      SVMessages.getString("ResultTableModel.tableHeader.11"),
      SVMessages.getString("ResultTableModel.tableHeader.8"),
      SVMessages.getString("ResultTableModel.tableHeader.12"),
      SVMessages.getString("ResultTableModel.tableHeader.13"),
      SVMessages.getString("ResultTableModel.tableHeader.14"),
      SVMessages.getString("ResultTableModel.tableHeader.15"),
      SVMessages.getString("ResultTableModel.tableHeader.16"),
      SVMessages.getString("ResultTableModel.tableHeader.17"),
      SVMessages.getString("ResultTableModel.tableHeader.18"),
      SVMessages.getString("ResultTableModel.tableHeader.19"),
      SVMessages.getString("ResultTableModel.tableHeader.20"),
      SVMessages.getString("ResultTableModel.tableHeader.21"),
      SVMessages.getString("ResultTableModel.tableHeader.22"),
      SVMessages.getString("ResultTableModel.tableHeader.23"),
      SVMessages.getString("ResultTableModel.tableHeader.24"),
      SVMessages.getString("ResultTableModel.tableHeader.25"),
      SVMessages.getString("ResultTableModel.tableHeader.26"),
      SVMessages.getString("ResultTableModel.tableHeader.27"),
      SVMessages.getString("ResultTableModel.tableHeader.28"),
      SVMessages.getString("ResultTableModel.tableHeader.29"),
      SVMessages.getString("ResultTableModel.tableHeader.30"),
      SVMessages.getString("ResultTableModel.tableHeader.31"),
      SVMessages.getString("ResultTableModel.tableHeader.32"),
      SVMessages.getString("ResultTableModel.tableHeader.33"),
      SVMessages.getString("ResultTableModel.tableHeader.34") };

  // set query header for background table headers
  static {
    for (int i = 0; i < QUERY_ORDERED_HEADER_IDS.length; i++) {
      QUERY_HEADERS[i] = RES_HEADERS[QUERY_ORDERED_HEADER_IDS[i]];
    }
  }

  private static final Logger _logger = Logger.getLogger("SummaryTableModel");

  /**
   * Constructor.
   */
  public SummaryTableModel() {
    super();
  }

  @Override
  public Color getHeaderColumn(String columnName) {
    if (ArrayUtils.contains(QUERY_HEADERS, columnName)) {
      return SummaryTableModel.COLOR_QUERY;
    } else {
      return SummaryTableModel.COLOR_HIT;
    }
  }

  @Override
  public String getDisplayHeaderPropertyName() {
    return DEF_COL_PROP_KEY;
  }

  @Override
  public String getColumnSizePropertyName() {
    return DEF_COL_SIZE_PROP_KEY;
  }

  @Override
  public String getSortColumnPropertyName() {
    return SORT_COL_PROP_KEY;
  }

  @Override
  public String getSortAscendingPropertyName() {
    return SORT_ASCEND_PROP_KEY;
  }

  @Override
  public TableHeaderColumnItem[] getReferenceColumnHeaders() {
    if (_colItemsReference == null) {
      _colItemsReference = new TableHeaderColumnItem[RES_HEADERS.length];
      for (int i = 0; i < ORDERED_HEADER_IDS.length; i++) {
        _colItemsReference[i] = new TableHeaderColumnItem(RES_HEADERS[ORDERED_HEADER_IDS[i]],
            RES_HEADERS_INT[ORDERED_HEADER_IDS[i]], (i < 2), (i < 2));

      }
    }
    return this._colItemsReference;
  }

  /**
   * Set the data model.
   * 
   * @param a QueryBase object
   */
  public void setQuery(QueryBaseUI query) {
    if (_query != null) {
      // clean current query if any
      _query.setListener(null);
      _query.resetInternalData();
    }
    if ((query != null) && (query.allSequencesIndexed())) {
      _query = query;
    } else {
      _query = null;
    }
    _hasHits = null;
    _queryStatus = -1;
    _rows = -1;

    _indexConvertor = null;
    _sortedSummaries = null;

    prepareViewerTypeData();

    // to avoid sort computation with potentially huge jobs, avoid that the first
    // time
    // the query is displayed within the component. We use the natural ordering by
    // query
    // which does not need any computation
    setSortColumn(RES_FILE_NUM_HEADER);
    setSortColumnAscending(true);
    // inform UI to refresh view
    this.fireTableDataChanged();

  }

  /**
   * This method is used to convert a BFileSummary index to a row table. This method relies on the internal sorted
   * data model of BFileSummaries.
   * 
   * @param idx BFileSummary index
   * 
   * @return table row index
   */
  public int convertSummaryIdxToTableRow(int idx) {
    Integer value;
    int i, size;

    if (_viewTypeDataBinner == null) {
      return idx;
    }
    if (_indexConvertor == null) {// first call
      if (_viewTypeDataBinner != null) {
        _indexConvertor = new HashMap<Integer, Integer>();
        size = _viewTypeDataBinner.size();
        for (i = 0; i < size; i++) {
          _indexConvertor.put(_viewTypeDataBinner.get(i), i);
        }
      }
    }
    value = _indexConvertor.get(idx);
    return value == null ? -1 : value;
  }

  /**
   * Reverse of convertSummaryIdxToTableRow method.
   * 
   * @param idx table row index
   * 
   * @return BFileSummary index
   */
  public int convertTableRowToSummaryIdx(int idx) {
    if (_viewTypeDataBinner == null || _viewTypeDataBinner.size() == 0)
      return idx;
    else
      return _viewTypeDataBinner.get(idx);
  }

  @Override
  public void sortData(boolean force) {
    super.sortData(force);
    _indexConvertor = null;
  }

  @Override
  protected void sortData(ProgressTinyDialog monitor, boolean force, JKTableModelSorter<?> sorter) {
    // sort colums
    SerialEntityBag data = sorter.sort(monitor, getSortColumn(), force);

    if (data == null)
      return;
    _sortedSummaries = data;

    prepareViewerTypeData();

    // inform UI to refresh view
    this.fireTableDataChanged();
  }

  /**
   * Reset internal data model. Used when displaying an empty table.
   */
  public void clear() {
    if (_query != null) {// clean current query if any
      _query.resetInternalData();
    }
    _hasHits = null;
    _query = null;
    _queryStatus = -1;
    _rows = -1;
    if (_indexConvertor != null) {
      _indexConvertor.clear();
      _indexConvertor = null;
    }
    _sortedSummaries = null;
    if (_viewTypeDataBinner != null) {
      _viewTypeDataBinner.clear();
      _viewTypeDataBinner = null;
    }
    this.fireTableDataChanged();
  }

  public int getRowCount() {
    int rows;

    if (_rows != -1)
      return _rows;
    if (_viewTypeDataBinner != null) {
      rows = _rows = _viewTypeDataBinner.size();
    } else if (_query != null) {
      rows = _rows = _query.sequences();
    } else {
      rows = _rows = 0;
    }
    return rows;
  }

  /**
   * Return the data model.
   * 
   * @return a QueryBase object
   */
  public QueryBase getQuery() {
    return _query;
  }

  /**
   * Return the data associated to each table cell.
   * 
   * @param rowID row index
   * @param colID column index
   * @param summary BFileSummary
   * @param status status
   * @param query the QueryBase object
   * 
   * @return a cell data value
   */
  public static Object getValueItem(int rowID, int colID, BFileSummary summary, String status, QueryBaseUI query) {
    Object val = null;
    switch (colID) {
    case RES_FILE_NUM_HEADER:
      val = new Integer(rowID + 1);
      break;
    case RES_SEQ_NAME_HEADER:
      if (summary != null) {
        val = summary.getQueryId();
      }
      break;
    case RES_SUMMARY_BEST_HIT_ACC:
      if (summary != null) {
        val = summary.getBestHitAccession();
      }
      break;
    case RES_SUMMARY_BEST_HIT_DEF:
      if (summary != null) {
        val = summary.getBestHitDescription();
      }
      break;
    case RES_SUMMARY_BEST_HIT_LEN:
      if (summary != null) {
        val = summary.getBestHitLength();
      }
      break;
    case RES_SUMMARY_BEST_HIT_EVAL:
      if (summary != null) {
        val = summary.getBestHitEValue();
      }
      break;
    case RES_SUMMARY_BEST_HIT_SCORE:
      if (summary != null) {
        val = summary.getBestHitScore();
      }
      break;
    case RES_SUMMARY_BEST_HIT_SCOREBITS:
      if (summary != null) {
        val = summary.getBestHitScoreBits();
      }
      break;
    case RES_STATUS_HEADER:
      val = status;
      break;
    case RES_FILE_NAME_HEADER:
      if (summary != null) {
        val = summary.getQueryRID();// starting with KB 3.2, get RID
      }
      if (val == null || val.equals("-")) {
        if (query.getRID() != null) {
          val = query.getRID(); // KServer 4.1
        } else {
          val = "-";
        }
      }
      break;
    case RES_IDENTITY:
      if (summary != null) {
        val = summary.getBestHitIdentify();
      }
      break;
    case RES_SIMILARITY:
      if (summary != null) {
        val = summary.getBestHitSimilarity();
      }
      break;
    case RES_COVERAGE:
      if (summary != null) {
        val = summary.getBestHitCoverage();
      }
      break;
    case RES_COVERAGE_H:
      if (summary != null) {
        val = summary.getBestHitCoverageH();
      }
      break;
    case RES_TAXONOMY:
      if (summary != null) {
        val = summary.getTaxonomy();
      }
      break;
    case RES_ORGANISM:
      if (summary != null) {
        val = summary.getOrganism();
      }
      break;
    case RES_QUERY_LENGTH:
      if (summary != null) {
        val = summary.getQueryLength();
      }
      break;
    case RES_QUERY_FROM:
      if (summary != null) {
        val = summary.getQueryFrom();
      }
      break;
    case RES_QUERY_TO:
      if (summary != null) {
        val = summary.getQueryTo();
      }
      break;
    case RES_QUERY_FRAME:
      if (summary != null) {
        val = summary.getQueryFrame();
      }
      break;
    case RES_QUERY_GAPS:
      if (summary != null) {
        val = summary.getQueryGaps();
      }
      break;
    case RES_BESTHIT_FROM:
      if (summary != null) {
        val = summary.getBestHitFrom();
      }
      break;
    case RES_BESTHIT_TO:
      if (summary != null) {
        val = summary.getBestHitTo();
      }
      break;
    case RES_BESTHIT_FRAME:
      if (summary != null) {
        val = summary.getBestHitFrame();
      }
      break;
    case RES_BESTHIT_GAPS:
      if (summary != null) {
        val = summary.getBestHitGaps();
      }
      break;
    case RES_ALIGN_LENGTH:
      if (summary != null) {
        val = summary.getAlignLength();
      }
      break;
    case RES_NB_HITS:
      if (summary != null) {
        val = summary.getNHits();
      }
      break;
    case RES_NB_HSPS:
      if (summary != null) {
        val = summary.getNbHsps();
      }
      break;
    case RES_T_GAPS:
      if (summary != null) {
        val = summary.getTotalGaps();
      }
      break;
    case RES_P_GAPS:
      if (summary != null) {
        val = summary.getPercentGaps();
      }
      break;
    case RES_MISMATCHES:
      if (summary != null) {
        val = summary.getMistmatches();
      }
      break;
    case RES_LCA:
      if (summary != null) {
        val = summary.getLCA();
      }
      break;
    case RES_RANK_LCA:
      if (summary != null) {
        val = summary.getRankLCA();
      }
      break;
    case RES_ORIGIN_JOB:
      if (summary != null) {
        val = summary.getOriginJobName();
      }
      break;
    }

    if (val == null) {
      val = "-";
    }
    return (val);
  }

  /**
   * Return the data associated to each table cell.
   * 
   * @param row row index
   * @param col column index
   * 
   * @return a cell data value
   */
  private Object getValueAtEx(int row, int col) {
    Object val = null;
    BFileSummary summary;
    String status;
    Entity entity;
    int colID, rowID;

    if (_query == null)
      return null;

    if (row == -1)
      return this.getDisplayedHeader(col);
    if (col == QUERY_DATA_COL) {
      return _query;
    }
    if (_viewTypeDataBinner != null) {
      rowID = _viewTypeDataBinner.get(row);
    } else {
      if (_sortedSummaries != null) {
        entity = _sortedSummaries.getEntity(row, this.isSortColumnAscending());
        rowID = entity.getId();
      } else {
        if (this.isSortColumnAscending())
          rowID = row;
        else
          rowID = getRowCount() - 1 - row;
      }
    }
    if (col == SEQUENCE_DATA_COL) {
      return _query.getSequence(rowID);
    }

    summary = _query.getSummary(rowID);
    status = _query.getStatus(rowID);
    // check to return special data
    if (col == SUMMARY_DATA_COL) {// summary itself
      if (summary != null)
        return summary;
      else
        return "";
    } else if (col == STATUS_DATA_COL) {// query exec status
      return status;
    } else if (col == FEAT_DATA_COL) {// has features, some, none
      if (summary != null) {
        return summary.getFeatContainer();
      } else {
        return SROutput.FEATURES_CONTAINER.none;
      }
    } else if (col == FILTER_DATA_COL) {// is filtered or not
      if (summary != null) {
        return summary.isFiltered() ? Boolean.TRUE : Boolean.FALSE;
      } else {
        return Boolean.FALSE;
      }
    } else if (col == RESULT_DATA_COL) {
      return _query.getResult(rowID);
    }
    colID = this.getColumnId(col);
    // return column specific data
    val = getValueItem(rowID, colID, summary, status, _query);
    // optimization : when resubmitting a job, BFileSummary is not updated before
    // starting job
    // (task may take a long time with huge amount of queries). So, unless query is
    // "ok", we only
    // return data for query (name, length, status).
    if (!status.startsWith(QueryBase.STATUS_OK)) {
      if (colID == RES_FILE_NUM_HEADER || colID == RES_SEQ_NAME_HEADER || colID == RES_QUERY_LENGTH
          || colID == RES_STATUS_HEADER || colID == RES_FILE_NAME_HEADER) {
        return val;
      } else {
        return "-";
      }
    }
    return (val);
  }

  @Override
  public Object getValueAt(int row, int col) {
    try {
      return getValueAtEx(row, col);
    } catch (Exception ex) {
      _logger.warn("unable to get value: [" + row + "," + col + "]: " + ex);
    }
    return "-";
  }

  /**
   * Set the view type of this component.
   * 
   * @param vType viewer type
   */
  public void setViewType(SummaryTableModel.VIEW_TYPE vType) {
    _viewType = vType;

    prepareViewerTypeData();
    _indexConvertor = null;
    this.fireTableDataChanged();
  }

  public boolean[] queryHasHits() {
    boolean[] hasHits = null;

    // see
    // http://nadeausoftware.com/articles/2008/02/java_tip_how_read_files_quickly
    if (_hasHits != null) {
      // check required since status of query and hasHits array may change during
      // query execution
      if (_queryStatus == _query.getStatus()) {
        return _hasHits;
      }
    }
    _queryStatus = _query.getStatus();
    try {
      hasHits = new boolean[_query.sequences()];
      int size = _query.sequences();
      for (int i = 0; i < size; i++) {
        hasHits[i] = _query.hasHits(i);
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex.toString());
    }
    _hasHits = hasHits;
    return _hasHits;
  }

  /**
   * Utility aims at creating the data structure used to prepare the view type.
   * Indeed the ResultTableViewer can display all queries, all queries having hits
   * or all queries that do not match.
   */
  private void prepareViewerTypeData() {
    boolean[] hasHits;
    boolean hashit, hasnohit, allhit;
    int i, size, idx;

    if (_viewTypeDataBinner != null) {
      _viewTypeDataBinner.clear();
      _viewTypeDataBinner = null;
      _rows = -1;
    }
    if (_query == null) {
      return;
    }
    try {

      hasHits = queryHasHits();
    } catch (Exception e) {
      _logger.warn("problem to know if query has hits : " + e.getMessage());
      return;
    }

    _viewTypeDataBinner = new ArrayList<Integer>();

    hashit = _viewType.equals(VIEW_TYPE.HITS_ONLY);
    hasnohit = _viewType.equals(VIEW_TYPE.NO_HITS_ONLY);
    allhit = _viewType.equals(VIEW_TYPE.ALL);
    if (_sortedSummaries != null) {// sorted data: use it to prepare view
      size = _sortedSummaries.size();
      for (i = 0; i < size; i++) {
        idx = _sortedSummaries.getEntity(i, this.isSortColumnAscending()).getId();
        if ((hasHits[idx] && hashit) || (!hasHits[idx] && hasnohit) || allhit) {
          _viewTypeDataBinner.add(idx);
        }
      }
    } else {// nothing is sorted
      for (i = 0; i < hasHits.length; i++) {
        if ((hasHits[i] && hashit) || (!hasHits[i] && hasnohit) || allhit) {
          _viewTypeDataBinner.add(i);
        }
      }
    }
  }

  @Override
  public JKTableModelSorter<?> getModelSorter() {
    return new SummaryTableModelSorter(this._query);
  }

  public static Object getValueItem(int rowID, int colID, BFileSummary summary, String status, QueryBase query) {
    Object val = null;
    switch (colID) {
    case RES_FILE_NUM_HEADER:
      val = new Integer(rowID + 1);
      break;
    case RES_SEQ_NAME_HEADER:
      if (summary != null) {
        val = summary.getQueryId();
      }
      break;
    case RES_SUMMARY_BEST_HIT_ACC:
      if (summary != null) {
        val = summary.getBestHitAccession();
      }
      break;
    case RES_SUMMARY_BEST_HIT_DEF:
      if (summary != null) {
        val = summary.getBestHitDescription();
      }
      break;
    case RES_SUMMARY_BEST_HIT_LEN:
      if (summary != null) {
        val = summary.getBestHitLength();
      }
      break;
    case RES_SUMMARY_BEST_HIT_EVAL:
      if (summary != null) {
        val = summary.getBestHitEValue();
      }
      break;
    case RES_SUMMARY_BEST_HIT_SCORE:
      if (summary != null) {
        val = summary.getBestHitScore();
      }
      break;
    case RES_SUMMARY_BEST_HIT_SCOREBITS:
      if (summary != null) {
        val = summary.getBestHitScoreBits();
      }
      break;
    case RES_STATUS_HEADER:
      val = status;
      break;
    case RES_FILE_NAME_HEADER:
      if (summary != null) {
        val = summary.getQueryRID();//starting with KB 3.2, get RID
      }
      if (val == null || val.equals("-")) {
        if (query.getRID() != null) {
          val = query.getRID(); // KServer 4.1
        } else {
          val = "-";
        }
      }
      break;
    case RES_IDENTITY:
      if (summary != null) {
        val = summary.getBestHitIdentify();
      }
      break;
    case RES_SIMILARITY:
      if (summary != null) {
        val = summary.getBestHitSimilarity();
      }
      break;
    case RES_COVERAGE:
      if (summary != null) {
        val = summary.getBestHitCoverage();
      }
      break;
    case RES_COVERAGE_H:
      if (summary != null) {
        val = summary.getBestHitCoverageH();
      }
      break;
    case RES_TAXONOMY:
      if (summary != null) {
        val = summary.getTaxonomy();
      }
      break;
    case RES_ORGANISM:
      if (summary != null) {
        val = summary.getOrganism();
      }
      break;
    case RES_QUERY_LENGTH:
      if (summary != null) {
        val = summary.getQueryLength();
      }
      break;
    case RES_QUERY_FROM:
      if (summary != null) {
        val = summary.getQueryFrom();
      }
      break;
    case RES_QUERY_TO:
      if (summary != null) {
        val = summary.getQueryTo();
      }
      break;
    case RES_QUERY_FRAME:
      if (summary != null) {
        val = summary.getQueryFrame();
      }
      break;
    case RES_QUERY_GAPS:
      if (summary != null) {
        val = summary.getQueryGaps();
      }
      break;
    case RES_BESTHIT_FROM:
      if (summary != null) {
        val = summary.getBestHitFrom();
      }
      break;
    case RES_BESTHIT_TO:
      if (summary != null) {
        val = summary.getBestHitTo();
      }
      break;
    case RES_BESTHIT_FRAME:
      if (summary != null) {
        val = summary.getBestHitFrame();
      }
      break;
    case RES_BESTHIT_GAPS:
      if (summary != null) {
        val = summary.getBestHitGaps();
      }
      break;
    case RES_ALIGN_LENGTH:
      if (summary != null) {
        val = summary.getAlignLength();
      }
      break;
    case RES_NB_HITS:
      if (summary != null) {
        val = summary.getNHits();
      }
      break;
    case RES_NB_HSPS:
      if (summary != null) {
        val = summary.getNbHsps();
      }
      break;
    case RES_T_GAPS:
      if (summary != null) {
        val = summary.getTotalGaps();
      }
      break;
    case RES_P_GAPS:
      if (summary != null) {
        val = summary.getPercentGaps();
      }
      break;
    case RES_MISMATCHES:
      if (summary != null) {
        val = summary.getMistmatches();
      }
      break;
    case RES_LCA:
      if (summary != null) {
        val = summary.getLCA();
      }
      break;
    case RES_RANK_LCA:
      if (summary != null) {
        val = summary.getRankLCA();
      }
      break;
    case RES_ORIGIN_JOB:
      if (summary != null) {
        val = summary.getOriginJobName();
      }
      break;
    }

    if (val == null) {
      val = "-";
    }
    return (val);
  }
}
