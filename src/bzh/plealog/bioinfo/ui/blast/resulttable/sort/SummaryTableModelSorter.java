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
package bzh.plealog.bioinfo.ui.blast.resulttable.sort;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import bzh.plealog.bioinfo.api.data.searchjob.BFileSummary;
import bzh.plealog.bioinfo.ui.blast.core.QueryBaseUI;
import bzh.plealog.bioinfo.ui.blast.resulttable.SummaryTableModel;
import bzh.plealog.bioinfo.ui.util.JKTableModelSorter;
import bzh.plealog.bioinfo.ui.util.ProgressTinyDialog;
import bzh.plealog.bioinfo.util.CoreUtil;

/**
 * This is the main class of the ResultTableModelSorter framework. It aims at
 * enabling the support of column data sorting for the ResultTableModel.
 */
public class SummaryTableModelSorter extends JKTableModelSorter<BFileSummary> {
  private QueryBaseUI _query;

  public static final String SORT_FILE_PREFIX = "sort_";

  /**
   * Default Constructor. The sort system is associated to a BlastQuery.
   */
  public SummaryTableModelSorter(QueryBaseUI bq) {
    super(bq.getQueryPath(), SORT_FILE_PREFIX);
    _query = bq;
  }

  /*@Override
  protected boolean canSave() {
    // because of the new retrieve mode from KServer which allows display of results
    // even the job is not finished : do not save the sort while some queries are
    // not still executed
    return (_query.countStatuses((byte) QueryBase.OK) == _query.sequences());
  }*/

  /**
   * 
   */
  protected List<Entity> getEntities(ProgressTinyDialog monitor, int sortColumn) {
    List<Entity> entities;
    BFileSummary summary;
    ENTITY_TYPE eType;
    int i, size, delta = 100;

    // if not, start ordering BFileSummary as needed
    entities = new ArrayList<Entity>();
    size = _query.sequences();
    eType = getEntityType(sortColumn);
    // get data
    if (monitor != null) {
      monitor.setMessage("Preparing data...");
      monitor.setMaxSteps(size);
    }

    if (sortColumn == SummaryTableModel.RES_FILE_NUM_HEADER) {
      for (i = 0; i < size; i++) {
        entities.add(new IntegerEntity(i, i));
        if (monitor != null) {
          if ((i % delta) == 0)
            monitor.addToProgress(delta);
          if (monitor.stopProcessing()) {
            entities.clear();
            monitor.dispose();
            return null;
          }
        }
      }
    } else if (sortColumn == SummaryTableModel.RES_STATUS_HEADER) {
      for (i = 0; i < size; i++) {
        entities.add(new StringEntity(i, _query.getStatus(i)));
        if (monitor != null) {
          if ((i % delta) == 0)
            monitor.addToProgress(delta);
          if (monitor.stopProcessing()) {
            entities.clear();
            monitor.dispose();
            return null;
          }
        }
      }
    } else {
      Enumeration<BFileSummary> summaries = _query.getSummaries();
      int index = 0;
      while (summaries.hasMoreElements()) {
        try {
          summary = summaries.nextElement();
        } catch (Exception e) {
          // exception not bad here, so hide it
          summary = _query.getSummary(index);
        }
        // if null => get a light summary constructed with few data from sequence
        if (summary == null) {
          summary = _query.getSummary(index);
        }
        entities.add(getEntity(summary, eType, index, sortColumn));

        if (monitor != null) {
          if ((index % delta) == 0)
            monitor.addToProgress(delta);
          if (monitor.stopProcessing()) {
            entities.clear();
            monitor.dispose();
            return null;
          }
        }

        index++;
      }
    }

    return entities;
  }

  /**
   * Utility method. Prepare a % formatted string value into a string ready to be
   * converted to a Number.
   */
  private String removePct(String value) {
    String str, str2;
    if (value == null)
      return null;
    // As a reminder, BFileSummary was created to store all data as String for
    // display purpose
    // % values contains an ending %, so remove it
    int idx = value.indexOf("%");
    if (idx != -1) {
      str = value.substring(0, idx);
    } else {
      str = value;
    }
    // % formatting (see BFileSummary class) uses decimal formatter and current
    // Locale
    // when dealing with French-based Locale, decimal separator is a comma: so
    // replace it
    // this solution was chosen instead of using parse() methods from BFileSummary
    // class
    // formatter. Indeed, that class uses several formatters, and here it is quite
    // difficult
    // to figure out which one to use.
    str2 = CoreUtil.replaceFirst(str, ",", ".");// if "," not found, then method returns null
    if (str2 != null)
      return str2;
    else
      return str;
  }

  /**
   * Get a specific value from a BFileSummary data block.
   * 
   * @param summary    the BFileSummary from where to retrieve the data value
   * 
   * @param sortColumn id of the column that targets the value to retrieve. This
   *                   parameter must be one of the RES_XXX constants defined in
   *                   the ResultTableModel class.
   */
  @Override
  protected Object getValue(BFileSummary summary, int sortColumn) {
    Object val = "";
    switch (sortColumn) {
    case SummaryTableModel.RES_SEQ_NAME_HEADER:
      if (summary != null) {
        val = summary.getQueryId();
      }
      break;
    case SummaryTableModel.RES_SUMMARY_BEST_HIT_ACC:
      if (summary != null) {
        val = summary.getBestHitAccession();
      }
      break;
    case SummaryTableModel.RES_SUMMARY_BEST_HIT_DEF:
      if (summary != null) {
        val = summary.getBestHitDescription();
      }
      break;
    case SummaryTableModel.RES_SUMMARY_BEST_HIT_LEN:
      if (summary != null) {
        val = summary.getBestHitLength();
      }
      break;
    case SummaryTableModel.RES_SUMMARY_BEST_HIT_EVAL:
      if (summary != null) {
        val = summary.getBestHitEValue();
      }
      break;
    case SummaryTableModel.RES_SUMMARY_BEST_HIT_SCORE:
      if (summary != null) {
        val = summary.getBestHitScore();
      }
      break;
    case SummaryTableModel.RES_SUMMARY_BEST_HIT_SCOREBITS:
      if (summary != null) {
        val = summary.getBestHitScoreBits();
      }
      break;
    case SummaryTableModel.RES_FILE_NAME_HEADER:
      if (summary != null) {
        val = summary.getQueryRID();// starting with KB 3.2, get RID
      }
      if (val == null)
        val = "";// starting with KB 3.1, no more available with Lucene storage
      break;
    case SummaryTableModel.RES_IDENTITY:
      if (summary != null) {
        val = removePct(summary.getBestHitIdentify());
      }
      break;
    case SummaryTableModel.RES_SIMILARITY:
      if (summary != null) {
        val = removePct(summary.getBestHitSimilarity());
      }
      break;
    case SummaryTableModel.RES_COVERAGE:
      if (summary != null) {
        val = removePct(summary.getBestHitCoverage());
      }
      break;
    case SummaryTableModel.RES_COVERAGE_H:
      if (summary != null) {
        val = removePct(summary.getBestHitCoverageH());
      }
      break;
    case SummaryTableModel.RES_TAXONOMY:
      if (summary != null) {
        val = summary.getTaxonomy();
      }
      break;
    case SummaryTableModel.RES_ORGANISM:
      if (summary != null) {
        val = summary.getOrganism();
      }
      break;
    case SummaryTableModel.RES_QUERY_LENGTH:
      if (summary != null) {
        val = summary.getQueryLength();
      }
      break;
    case SummaryTableModel.RES_QUERY_FROM:
      if (summary != null) {
        val = summary.getQueryFrom();
      }
      break;
    case SummaryTableModel.RES_QUERY_TO:
      if (summary != null) {
        val = summary.getQueryTo();
      }
      break;
    case SummaryTableModel.RES_QUERY_FRAME:
      if (summary != null) {
        val = summary.getQueryFrame();
      }
      break;
    case SummaryTableModel.RES_QUERY_GAPS:
      if (summary != null) {
        val = summary.getQueryGaps();
      }
      break;
    case SummaryTableModel.RES_BESTHIT_FROM:
      if (summary != null) {
        val = summary.getBestHitFrom();
      }
      break;
    case SummaryTableModel.RES_BESTHIT_TO:
      if (summary != null) {
        val = summary.getBestHitTo();
      }
      break;
    case SummaryTableModel.RES_BESTHIT_FRAME:
      if (summary != null) {
        val = summary.getBestHitFrame();
      }
      break;
    case SummaryTableModel.RES_BESTHIT_GAPS:
      if (summary != null) {
        val = summary.getBestHitGaps();
      }
      break;
    case SummaryTableModel.RES_ALIGN_LENGTH:
      if (summary != null) {
        val = summary.getAlignLength();
      }
      break;
    case SummaryTableModel.RES_NB_HITS:
      if (summary != null) {
        val = summary.getNHits();
      }
      break;
    case SummaryTableModel.RES_NB_HSPS:
      if (summary != null) {
        val = summary.getNbHsps();
      }
      break;
    case SummaryTableModel.RES_T_GAPS:
      if (summary != null) {
        val = summary.getTotalGaps();
      }
      break;
    case SummaryTableModel.RES_P_GAPS:
      if (summary != null) {
        val = removePct(summary.getPercentGaps());
      }
      break;
    case SummaryTableModel.RES_MISMATCHES:
      if (summary != null) {
        val = summary.getMistmatches();
      }
      break;
    case SummaryTableModel.RES_LCA:
      if (summary != null) {
        val = summary.getLCA();
      }
      break;
    case SummaryTableModel.RES_RANK_LCA:
      if (summary != null) {
        val = summary.getRankLCA();
      }
      break;
    case SummaryTableModel.RES_ORIGIN_JOB:
      if (summary != null) {
        val = summary.getOriginJobName();
      }
      break;
    }

    if (val == null)
      val = "?";

    return (val);
  }

  /**
   * Returns the entity type of the column that is going to be used to sort data.
   * 
   * @param sortColumn id of the column that targets the value to retrieve. This
   *                   parameter must be one of the RES_XXX constants defined in
   *                   the ResultTableModel class.
   * 
   */
  @Override
  protected ENTITY_TYPE getEntityType(int sortColumn) {
    // default is supposed to be a string value
    // so, do not modify this default value, since it was used to simply the code
    // below
    ENTITY_TYPE eType = ENTITY_TYPE.tString;
    switch (sortColumn) {
    case SummaryTableModel.RES_SUMMARY_BEST_HIT_EVAL:
    case SummaryTableModel.RES_SUMMARY_BEST_HIT_SCOREBITS:
    case SummaryTableModel.RES_IDENTITY:
    case SummaryTableModel.RES_SIMILARITY:
    case SummaryTableModel.RES_COVERAGE:
    case SummaryTableModel.RES_COVERAGE_H:
    case SummaryTableModel.RES_P_GAPS:
      eType = ENTITY_TYPE.tDouble;
      break;
    case SummaryTableModel.RES_FILE_NUM_HEADER:
    case SummaryTableModel.RES_SUMMARY_BEST_HIT_LEN:
    case SummaryTableModel.RES_SUMMARY_BEST_HIT_SCORE:
    case SummaryTableModel.RES_QUERY_LENGTH:
    case SummaryTableModel.RES_QUERY_FROM:
    case SummaryTableModel.RES_QUERY_TO:
    case SummaryTableModel.RES_QUERY_FRAME:
    case SummaryTableModel.RES_QUERY_GAPS:
    case SummaryTableModel.RES_BESTHIT_FROM:
    case SummaryTableModel.RES_BESTHIT_TO:
    case SummaryTableModel.RES_BESTHIT_FRAME:
    case SummaryTableModel.RES_BESTHIT_GAPS:
    case SummaryTableModel.RES_ALIGN_LENGTH:
    case SummaryTableModel.RES_NB_HITS:
    case SummaryTableModel.RES_T_GAPS:
    case SummaryTableModel.RES_MISMATCHES:

      eType = ENTITY_TYPE.tInteger;
      break;
    }
    return eType;
  }

  @Override
  protected int getSize() {
    return _query.sequences();
  }

}
