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
package bzh.plealog.bioinfo.ui.blast.core;

import java.text.DecimalFormat;

import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.searchjob.BFileSummary;
import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.searchresult.SRIteration;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.sequence.DAlphabet;
import bzh.plealog.bioinfo.api.data.sequence.DRulerModel;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSymbol;

/**
 * Set of utility methods for the BLASTViewer tool.
 * 
 * @author Patrick G. Durand
 */
public class AnalysisUtils {
  //Refactoring part of AnalysisUtils to BFileSummary while keeping API compatibility
  public static final DecimalFormat EVALUE_FORMATTER1      = BFileSummary.EVALUE_FORMATTER1;
  public static final DecimalFormat EVALUE_FORMATTER2      = BFileSummary.EVALUE_FORMATTER2;
  public static final DecimalFormat PCT_FORMATTER          = BFileSummary.PCT_FORMATTER;
  public static final DecimalFormat SCORE_FORMATTER        = BFileSummary.SCORE_FORMATTER;
  
  public static final DecimalFormat KSERVER_COST_FORMATTER = new DecimalFormat(
                                                               "0.### \u20ac");
  public static final DecimalFormat POS_FORMATTER          = new DecimalFormat(
                                                               "#,###,###,###");

  /**
   * Given a location in the hit sequence coordinate system, return the
   * corresponding location in the query coordinate system. Mapping is possible
   * since hit and query sequences are aligned. May return -1 if mapping fails.
   * 
   * @param hsp
   *          an HSP
   * @param hitLocation
   *          a location
   */
  public static int getCoordMappedToQuery(SRHsp hsp, int hitLocation) {
    DRulerModel rmodel;
    int idx;

    if (hsp.getHit().getSequence(hsp) == null
        || hsp.getQuery().getSequence(hsp) == null)
      return -1;
    idx = hsp.getHit().getSequence(hsp).getRulerModel()
        .getRulerPos(hitLocation);
    rmodel = hsp.getQuery().getSequence(hsp).getRulerModel();
    if (idx < 0 || idx >= rmodel.size())
      return -1;
    return rmodel.getSeqPos(idx);
  }

  /**
   * Compute an arrow that can be used to draw features. It is very important to
   * note that arrays must contain 5 cells.
   */
  public static void computePolygone(int[] xPoints, int[] yPoints, int x1,
      int y1, int x2, int yDecal, boolean plusStrand) {
    if (plusStrand) {
      xPoints[0] = x1;
      yPoints[0] = y1;
      xPoints[1] = x2 - 5;
      yPoints[1] = y1;
      xPoints[2] = x2;
      yPoints[2] = y1 + yDecal;
      xPoints[3] = xPoints[1];
      yPoints[3] = yPoints[2] + yDecal;
      xPoints[4] = xPoints[0];
      yPoints[4] = yPoints[3];
    } else {
      xPoints[0] = x1;
      yPoints[0] = y1 + yDecal;
      xPoints[1] = x1 + 5;
      yPoints[1] = y1;
      xPoints[2] = x2;
      yPoints[2] = y1;
      xPoints[3] = x2;
      yPoints[3] = yPoints[0] + yDecal;
      xPoints[4] = xPoints[1];
      yPoints[4] = yPoints[3];

    }
  }

  /**
   * Return strand type given sequence range and BLAST type.
   */
  public static boolean isNormalStrand(int from, int to, int frame, int bType) {
    boolean normal = true;

    switch (bType) {
      case SROutput.BLASTN:
        if (from > to)
          normal = false;
        break;
      case SROutput.TBLASTX:
      case SROutput.BLASTX:
        if (frame < 0)
          normal = false;
        break;
    }
    return normal;
  }

  /**
   * Return the number of gap symbols contained in a particular region of a
   * sequence.
   */
  public static int getGapContent(DSequence seq, int from, int to) {
    DAlphabet alphabet;
    int i, gapContent = 0;
    DSymbol gap;

    alphabet = seq.getAlphabet();
    gap = alphabet.getSymbol(DSymbol.GAP_SYMBOL_CODE);
    for (i = from; i <= to; i++) {
      if (seq.getSymbol(i).equals(gap)) {
        gapContent++;
      }
    }
    return gapContent;
  }

  /**
   * Figure out whether a SRHsp contains some features.
   */
  public static boolean hasFeatures(SRHsp hsp) {
    return hsp.getFeatures() != null ? true : false;
  }

  /**
   * Figure out whether a SRHit contains some features. This method looks at the
   * SRHsp contained in the SRHit and, if at least one of them contains some
   * features, the method returns true.
   */
  public static boolean hasFeatures(SRHit hit) {
    boolean bRet = false;
    int i, size;
    size = hit.countHsp();
    for (i = 0; i < size; i++) {
      if (hasFeatures(hit.getHsp(i))) {
        bRet = true;
        break;
      }
    }
    return bRet;
  }

  /**
   * Check if a SRHsp contains a FeatureTable with an error status. Returns
   * false if no FeatureTable is found.
   */
  public static boolean hasWarnFeatures(SRHsp hsp) {
    FeatureTable ft;

    ft = hsp.getFeatures();
    if (ft != null && ft.getStatus() == FeatureTable.ERROR_STATUS) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Check if a SRHit contains a least one SRHsp having a FeatureTable with an
   * error status. Returns false if no FeatureTable is found.
   */
  public static boolean hasWarnFeatures(SRHit hit) {
    FeatureTable ft;
    boolean bRet = false;
    int i, size;

    size = hit.countHsp();

    for (i = 0; i < size; i++) {
      ft = hit.getHsp(i).getFeatures();
      if (ft != null && ft.getStatus() == FeatureTable.ERROR_STATUS) {
        bRet = true;
        break;
      }
    }
    return bRet;

  }

  /**
   * Compute the coverage of a particular Hit. Returned value is in the range 0
   * to 100 percent.
   */
  public static double computeCoverage(BlastHitHSP bhh) {
    SRHit hit = (SRHit) bhh.getHit();
    SRHsp hsp = hit.getHsp(0);

    return ((double) Math.abs(hsp.getQuery().getTo() - hsp.getQuery().getFrom()
        + 1)
        / (double) bhh.getQuerySize() * 100.d);
  }
  
  /**
   * Create the table data model. Since the content of this viewer relies on the
   * BlastNavigator Viewer, this data table model is created from a BlastIteration.
   */
  public static BlastHitHSP[] prepareDataModel(BlastIteration iter) {
    int i, size;
    SRIteration bh;
    BlastHitHspImplem[] bhh;

    bh = iter.getIteration();
    size = bh.countHit();
    if (size == 0)
      return null;
    bhh = new BlastHitHspImplem[size];
    for (i = 0; i < size; i++) {
      bhh[i] = new BlastHitHspImplem(bh.getHit(i), iter.getEntry()
          .getBlastClientName(), 1, iter.getQuerySize(), iter.getBlastType());
    }
    return bhh;
  }

}
