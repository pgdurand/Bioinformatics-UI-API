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
package bzh.plealog.bioinfo.ui.blast.hittable;

import java.util.Comparator;

import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.sequence.BankSequenceInfo;
import bzh.plealog.bioinfo.ui.blast.config.ConfigManager;
import bzh.plealog.bioinfo.ui.blast.config.color.ColorPolicyConfig;
import bzh.plealog.bioinfo.ui.blast.core.BlastHitHSP;

/**
 * This is a BlastHitHsp comparator.
 * 
 * @author Patrick G. Durand
 */
public class BlastHitHspSortComparator implements Comparator<BlastHitHSP> {
  private int     colID;
  private boolean ascent;

  /**
   * Set the ordering method to use to sort objects.
   */
  public void setAscent(boolean ascent) {
    this.ascent = ascent;
  }

  /**
   * Set the column ID for which we have to do a sort.
   */
  public void setColID(int colID) {
    this.colID = colID;
  }

  public int compare(BlastHitHSP o1, BlastHitHSP o2) {
    BankSequenceInfo si1, si2;
    ColorPolicyConfig nc;
    SRHit hit1, hit2;
    SRHsp hsp1, hsp2;
    int val, v1, v2;
    double d1, d2;

    hit1 = o1.getHit();
    hsp1 = hit1.getHsp(0);
    hit2 = o2.getHit();
    hsp2 = hit2.getHsp(0);

    nc = (ColorPolicyConfig) ConfigManager.getConfig(ColorPolicyConfig.NAME);

    switch (colID) {
      case BlastHitTable.HIT_NUM:
        val = hit1.getHitNum() - hit2.getHitNum();
        break;
      case BlastHitTable.ACCESS:
        val = hit1.getHitAccession().compareTo(hit2.getHitAccession());
        break;
      case BlastHitTable.DEFINITION:
        val = hit1.getHitDef().compareTo(hit2.getHitDef());
        break;
      case BlastHitTable.HSP_SUMMARY:
        val = 0;
        break;
      case BlastHitTable.LENGTH:
        val = hit1.getHitLen() - hit2.getHitLen();
        break;
      case BlastHitTable.QUALITY:
        if (nc != null) {
          v1 = nc.getQualityValue(hsp1);
          v2 = nc.getQualityValue(hsp2);
          val = (v1 < v2 ? -1 : (v1 == v2 ? 0 : 1));
        } else {
          val = 0;
        }
        break;
      case BlastHitTable.SCORE_BITS:
        val = (hsp1.getScores().getBitScore() < hsp2.getScores().getBitScore() ? -1
            : (hsp1.getScores().getBitScore() == hsp2.getScores().getBitScore() ? 0
                : 1));
        break;
      case BlastHitTable.EVALUE:
        val = (hsp1.getScores().getEvalue() < hsp2.getScores().getEvalue() ? -1
            : (hsp1.getScores().getEvalue() == hsp2.getScores().getEvalue() ? 0
                : 1));
        break;
      case BlastHitTable.SCORE:
        val = (hsp1.getScores().getScore() < hsp2.getScores().getScore() ? -1
            : (hsp1.getScores().getScore() == hsp2.getScores().getScore() ? 0
                : 1));
        break;
      case BlastHitTable.NBHSPS:
        val = hit1.countHsp() - hit2.countHsp();
        break;
      case BlastHitTable.Q_FROM:
        val = hsp1.getQuery().getFrom() - hsp2.getQuery().getFrom();
        break;
      case BlastHitTable.Q_TO:
        val = hsp1.getQuery().getTo() - hsp2.getQuery().getTo();
        break;
      case BlastHitTable.Q_GAPS:
        val = hsp1.getQuery().getGaps() - hsp2.getQuery().getGaps();
        break;
      case BlastHitTable.H_FROM:
        val = hsp1.getHit().getFrom() - hsp2.getHit().getFrom();
        break;
      case BlastHitTable.H_TO:
        val = hsp1.getHit().getTo() - hsp2.getHit().getTo();
        break;
      case BlastHitTable.H_GAP:
        val = hsp1.getHit().getGaps() - hsp2.getHit().getGaps();
        break;
      case BlastHitTable.Q_FRAME:
        val = hsp1.getQuery().getFrame() - hsp2.getQuery().getFrame();
        break;
      case BlastHitTable.H_FRAME:
        val = hsp1.getHit().getFrame() - hsp2.getHit().getFrame();
        break;
      case BlastHitTable.IDENTITY:
        val = (hsp1.getScores().getIdentityP() < hsp2.getScores()
            .getIdentityP() ? -1 : (hsp1.getScores().getIdentityP() == hsp2
            .getScores().getIdentityP() ? 0 : 1));

        break;
      case BlastHitTable.POSITIVE:
        val = (hsp1.getScores().getPositiveP() < hsp2.getScores()
            .getPositiveP() ? -1 : (hsp1.getScores().getPositiveP() == hsp2
            .getScores().getPositiveP() ? 0 : 1));
        break;
      case BlastHitTable.GAPS:
        val = (hsp1.getScores().getGapsP() < hsp2.getScores().getGapsP() ? -1
            : (hsp1.getScores().getGapsP() == hsp2.getScores().getGapsP() ? 0
                : 1));
        break;
      case BlastHitTable.T_GAPS:
        val = hsp1.getScores().getGaps() - hsp2.getScores().getGaps();
        break;
      case BlastHitTable.MISMATCHES:
        val = hsp1.getScores().getMismatches()
            - hsp2.getScores().getMismatches();
        break;
      case BlastHitTable.ALI_LEN:
        val = hsp1.getScores().getAlignLen() - hsp2.getScores().getAlignLen();
        break;
      case BlastHitTable.Q_COVERAGE:
        // d1 = AnalysisUtils.computeCoverage((BlastHitHsp) o1);
        // d2 = AnalysisUtils.computeCoverage((BlastHitHsp) o2);
        d1 = ((BlastHitHSP) o1).getHit().getQueryGlobalCoverage();
        d2 = ((BlastHitHSP) o2).getHit().getQueryGlobalCoverage();
        val = (d1 < d2 ? -1 : (d1 == d2 ? 0 : 1));
        break;
      case BlastHitTable.H_COVERAGE:
        // d1 = AnalysisUtils.computeCoverage((BlastHitHsp) o1);
        // d2 = AnalysisUtils.computeCoverage((BlastHitHsp) o2);
        d1 = ((BlastHitHSP) o1).getHit().getHitGlobalCoverage();
        d2 = ((BlastHitHSP) o2).getHit().getHitGlobalCoverage();
        val = (d1 < d2 ? -1 : (d1 == d2 ? 0 : 1));
        break;
      case BlastHitTable.ORGANISM:
        si1 = hit1.getSequenceInfo();
        si2 = hit2.getSequenceInfo();
        if (si1 != null && si1.getOrganism() != null && si2 != null
            && si2.getOrganism() != null)
          val = si1.getOrganism().compareTo(si2.getOrganism());
        else
          val = 0;
        break;
      case BlastHitTable.DIVISION:
        si1 = hit1.getSequenceInfo();
        si2 = hit2.getSequenceInfo();
        if (si1 != null && si1.getDivision() != null && si2 != null
            && si2.getDivision() != null)
          val = si1.getDivision().compareTo(si2.getDivision());
        else
          val = 0;
        break;
      case BlastHitTable.TAXONOMY:
        si1 = hit1.getSequenceInfo();
        si2 = hit2.getSequenceInfo();
        if (si1 != null && si1.getTaxonomy() != null && si2 != null
            && si2.getTaxonomy() != null)
          val = si1.getTaxonomy().compareTo(si2.getTaxonomy());
        else
          val = 0;
        break;
      case BlastHitTable.C_DATE:
        si1 = hit1.getSequenceInfo();
        si2 = hit2.getSequenceInfo();
        if (si1 != null && si1.getCreationDate() != 0 && si2 != null
            && si2.getCreationDate() != 0)
          val = si1.getCreationDate() - si2.getCreationDate();
        else
          val = 0;
        break;
      case BlastHitTable.U_DATE:
        si1 = hit1.getSequenceInfo();
        si2 = hit2.getSequenceInfo();
        if (si1 != null && si1.getUpdateDate() != 0 && si2 != null
            && si2.getUpdateDate() != 0)
          val = si1.getUpdateDate() - si2.getUpdateDate();
        else
          val = 0;
        break;
      default:
        val = 0;
    }
    return (ascent ? val : -val);
  }
}