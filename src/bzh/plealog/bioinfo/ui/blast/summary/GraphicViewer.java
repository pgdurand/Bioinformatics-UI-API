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
package bzh.plealog.bioinfo.ui.blast.summary;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import bzh.plealog.bioinfo.api.core.config.CoreSystemConfigurator;
import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.searchresult.SRIteration;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.searchresult.SRRequestInfo;
import bzh.plealog.bioinfo.api.data.sequence.BankSequenceDescriptor;
import bzh.plealog.bioinfo.api.data.sequence.BankSequenceInfo;
import bzh.plealog.bioinfo.api.data.sequence.DAlphabet;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSequenceInfo;
import bzh.plealog.bioinfo.io.searchresult.txt.TxtExportSROutput;
import bzh.plealog.bioinfo.ui.blast.core.BlastIteration;
import bzh.plealog.bioinfo.ui.blast.event.BlastIterationListEvent;
import bzh.plealog.bioinfo.ui.blast.event.BlastIterationListListener;
import bzh.plealog.bioinfo.ui.feature.FeatureViewerFactory;
import bzh.plealog.bioinfo.ui.sequence.extended.CombinedAnnotatedSequenceViewer;
import bzh.plealog.bioinfo.util.CoreUtil;
import bzh.plealog.bioinfo.util.DAlphabetUtils;

/**
 * This class is the graphical overview of a Blast result.
 * 
 * @author Patrick G. Durand
 */
public class GraphicViewer extends JPanel implements BlastIterationListListener {
  private static final long               serialVersionUID     = -8348294545825892278L;
  private CombinedAnnotatedSequenceViewer _cartoViewer;

  public static final String              BHIT_FEATURE_TYPE    = "BLAST hit";
  public static final String              SCORE_BITS_QUALIFIER = getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.SCORE_BITS]);

  /**
   * Constructor.
   */
  public GraphicViewer() {
    _cartoViewer = new CombinedAnnotatedSequenceViewer(null, false, false,
        false, false, false, true, FeatureViewerFactory.TYPE.COMBO);
    this.setLayout(new BorderLayout());
    this.add(_cartoViewer, BorderLayout.CENTER);
  }

  private static String getQualifierName(String qual) {
    return CoreUtil.replaceAll(qual, "_", "");
  }

  /**
   * Analyze a SRHit and BHsp to retrieve useful data and place it as a new
   * Feature within an FeatureTable.
   * 
   * @param queryId
   *          the query ID
   * @param queryNum
   *          the query order number
   * @param hit
   *          the hit to analyze
   * @param hsp
   *          the hsp to analyze
   * @param ft
   *          the FeatureTable where a new Feature will be added. That new
   *          feature contains a Feature/Qualifier based representation of a
   *          SRHit/SRHsp.
   * */
  private static void prepareFeature(String queryId, int queryNum, SRHit hit,
      SRHsp hsp, FeatureTable ft) {
    Feature feat;
    int fromHsp, toHsp;

    if (hit == null || hsp == null)
      return;

    fromHsp = Math.min(hsp.getQuery().getFrom(), hsp.getQuery().getTo());
    toHsp = Math.max(hsp.getQuery().getFrom(), hsp.getQuery().getTo());

    feat = CoreSystemConfigurator.getFeatureTableFactory().getFInstance();

    feat.setKey(BHIT_FEATURE_TYPE);

    // feat.setKey(hit.getHitAccession()+"/"+hsp.getHspNum());

    feat.setFrom(fromHsp);
    feat.setTo(toHsp);
    if (hsp.getQuery().getFrame() >= 0)
      feat.setStrand(Feature.PLUS_STRAND);
    else
      feat.setStrand(Feature.MINUS_STRAND);

    if (queryId != null) {
      feat.addQualifier("Query definition", queryId);
    }
    feat.addQualifier("Hit accession", hit.getHitAccession());
    feat.addQualifier("Hit identifier", hit.getHitId());
    feat.addQualifier("Hit definition", hit.getHitDef());
    feat.addQualifier("Hit length", String.valueOf(hit.getHitLen()));
    feat.addQualifier("Query #", String.valueOf(queryNum));
    feat.addQualifier("Hit #", String.valueOf(hit.getHitNum()));
    feat.addQualifier("HSP #", String.valueOf(hsp.getHspNum()));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.Q_FROM]),
        TxtExportSROutput.getFormattedData(hit, hsp, TxtExportSROutput.Q_FROM));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.Q_TO]),
        TxtExportSROutput.getFormattedData(hit, hsp, TxtExportSROutput.Q_TO));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.Q_FRAME]),
        TxtExportSROutput.getFormattedData(hit, hsp, TxtExportSROutput.Q_FRAME));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.Q_COVERAGE]),
        TxtExportSROutput.getFormattedData(hit, hsp,
            TxtExportSROutput.Q_COVERAGE));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.H_FROM]),
        TxtExportSROutput.getFormattedData(hit, hsp, TxtExportSROutput.H_FROM));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.H_TO]),
        TxtExportSROutput.getFormattedData(hit, hsp, TxtExportSROutput.H_TO));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.H_FRAME]),
        TxtExportSROutput.getFormattedData(hit, hsp, TxtExportSROutput.H_FRAME));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.H_COVERAGE]),
        TxtExportSROutput.getFormattedData(hit, hsp,
            TxtExportSROutput.H_COVERAGE));
    feat.addQualifier(SCORE_BITS_QUALIFIER, TxtExportSROutput.getFormattedData(
        hit, hsp, TxtExportSROutput.SCORE_BITS));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.SCORE]),
        TxtExportSROutput.getFormattedData(hit, hsp, TxtExportSROutput.SCORE));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.EVALUE]),
        TxtExportSROutput.getFormattedData(hit, hsp, TxtExportSROutput.EVALUE));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.ALI_LEN]),
        TxtExportSROutput.getFormattedData(hit, hsp, TxtExportSROutput.ALI_LEN));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.IDENTITY]),
        TxtExportSROutput
            .getFormattedData(hit, hsp, TxtExportSROutput.IDENTITY));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.POSITIVE]),
        TxtExportSROutput
            .getFormattedData(hit, hsp, TxtExportSROutput.POSITIVE));
    feat.addQualifier(
        getQualifierName(TxtExportSROutput.DATA_COL_HEADERS[TxtExportSROutput.GAPS]),
        TxtExportSROutput.getFormattedData(hit, hsp, TxtExportSROutput.GAPS));
    ft.addFeature(feat);
  }

  /**
   * Analyze a SROutput to prepare a FeatureTable representation.
   * 
   * @param queryId
   *          the query ID
   * @param iterNum
   *          the iteration order number. One based value.
   * @param bo
   *          the BOutput to analyze. It is supposed to be a KLAST output in
   *          this code.
   * @param ft
   *          the FeatureTable where a new Feature will be added. That new
   *          feature contains a Feature/Qualifier based representation of a
   *          BOutput.
   * */
  private static void scanBO(String queryId, int iterNum, SROutput bo,
      FeatureTable ft) {
    SRIteration bi;
    SRHit hit;
    SRHsp hsp;
    int j, k, size, size2, size3;

    size = bo.countIteration();
    // no result... does nothing
    if (size == 0)
      return;
    // KLAST contains only a single iteration
    bi = bo.getIteration(iterNum - 1);
    size2 = bi.countHit();
    for (j = 0; j < size2; j++) {
      hit = bi.getHit(j);
      size3 = hit.countHsp();
      for (k = 0; k < size3; k++) {
        hsp = hit.getHsp(k);
        prepareFeature(queryId, iterNum, hit, hsp, ft);
      }
    }
  }

  /**
   * Prepare a fake representation of a query as a DSequence object.
   */
  private static DSequence prepareDSequence(String seqId, String description,
      int size, boolean isProt) {
    DSequenceInfo seqInfo;
    DSequence sequence;
    DAlphabet alphabet;

    // create sequence object
    if (isProt)
      alphabet = DAlphabetUtils.getIUPAC_Protein_Alphabet();
    else
      alphabet = DAlphabetUtils.getIUPAC_DNA_Alphabet();

    sequence = CoreSystemConfigurator.getSequenceFactory().getSequence(size,
        alphabet);
    sequence.createRulerModel(1, 1);

    // add sequence info
    seqInfo = new DSequenceInfo();
    seqInfo.setId(seqId);
    seqInfo.setName(description);
    sequence.setSequenceInfo(seqInfo);

    return sequence;
  }

  /**
   * Prepare the viewer data given a BOutput object.
   */
  public static BankSequenceDescriptor prepareViewerData(SROutput output,
      int iterNum) {
    FeatureTable ft;
    String queryId, description;
    BankSequenceDescriptor sd;
    DSequence sequence;
    boolean isProt;
    int qSize, type;

    // get query size, query ID and description
    qSize = Integer.valueOf(output.getRequestInfo()
        .getValue(SRRequestInfo.QUERY_LENGTH_DESCRIPTOR_KEY).toString());
    queryId = output.getRequestInfo()
        .getValue(SRRequestInfo.QUERY_ID_DESCRIPTOR_KEY).toString();
    description = output.getRequestInfo()
        .getValue(SRRequestInfo.QUERY_DEF_DESCRIPTOR_KEY).toString();

    // get the result type
    type = output.getBlastType();
    if (type == SROutput.BLASTP || type == SROutput.BLASTX) {
      isProt = true;
    } else {
      isProt = false;
    }

    // prepare the FeatureTable with KLAST results
    ft = CoreSystemConfigurator.getFeatureTableFactory().getFTInstance();
    scanBO(queryId, iterNum, output, ft);

    // now, prepare a "fake sequence"
    sequence = prepareDSequence(queryId, description, qSize, isProt);
    BankSequenceInfo si = CoreSystemConfigurator.getBankSequenceInfoFactory()
        .getInstance();
    si.setSequenceSize(qSize);
    si.setId(sequence.getSequenceInfo().getId());
    si.setDescription(sequence.getSequenceInfo().getName());

    // assemble sequence, feature table and sequence information within a single
    // data structure
    sd = new BankSequenceDescriptor(ft, si, sequence);
    return sd;
  }

  /**
   * Prepare the viewer data given a BOutput object. View is prepared using
   * first SRIteration by default.
   **/
  public static BankSequenceDescriptor prepareViewerData(SROutput output) {
    return prepareViewerData(output, 1);
  }

  @Override
  public void iterationChanged(BlastIterationListEvent e) {
    BlastIteration iter = (BlastIteration) e.getBlastIteration();

    if (iter == null || iter.getIteration().countHit() == 0) {
      _cartoViewer.cleanViewer();
    } else {
      _cartoViewer.setData(prepareViewerData(iter.getEntry().getResult(), iter
          .getIteration().getIterationIterNum()));
    }
  }

  // @TODO add Hit selection handler to highlight hit on cartoviewer
}
