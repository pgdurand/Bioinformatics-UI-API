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
package bzh.plealog.bioinfo.ui.blast.saviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTabbedPane;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSelectionEvent;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSelectionListener;
import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.sequence.BankSequenceInfo;
import bzh.plealog.bioinfo.api.data.sequence.DRulerModel;
import bzh.plealog.bioinfo.api.data.sequence.DSequenceModel;
import bzh.plealog.bioinfo.ui.blast.core.BlastHitHSP;
import bzh.plealog.bioinfo.ui.feature.FeatureStatusViewer;
import bzh.plealog.bioinfo.ui.feature.FeatureViewer;
import bzh.plealog.bioinfo.ui.feature.FeatureWebLinker;
import bzh.plealog.bioinfo.ui.resources.SVMessages;
import bzh.plealog.bioinfo.ui.seqinfo.SequenceInfoViewer;
import bzh.plealog.bioinfo.ui.sequence.basic.DSequenceListViewer;
import bzh.plealog.bioinfo.ui.sequence.basic.DViewerScroller;

/**
 * This class is the Feature Viewer implementation designed to work within the
 * SeqAlign Viewer.
 * 
 * @author Patrick G. Durand
 */
public class HspFeatureViewer extends JPanel {
  private static final long serialVersionUID = 2557267636077884983L;
  private FeatureViewer _featViewer;
  private SequenceInfoViewer _seqInfoViewer;
  private FeatureStatusViewer _featStatViewer;
  private HspSequenceViewer _hspViewer;
  private HspSummaryDraw _hspSummaryView;
  private BlastHitHSP _curHit;
  private SRHsp _curHsp;

  public HspFeatureViewer() {
    JPanel featP, siP;
    JTabbedPane jtp2 = new JTabbedPane();
    jtp2.setFocusable(false);

    _featViewer = new FeatureViewer(new FeatureWebLinker());
    _featViewer.setPreferredSize(new Dimension(50, 50));
    _featViewer.addFeatureSelectionListener(new SubjectFeatureSelectionListener());

    _seqInfoViewer = new SequenceInfoViewer();
    _featStatViewer = new FeatureStatusViewer();

    featP = new JPanel(new BorderLayout());
    featP.add(_featViewer, BorderLayout.CENTER);

    siP = new JPanel(new BorderLayout());
    siP.add(_seqInfoViewer, BorderLayout.CENTER);

    jtp2.addTab(SVMessages.getString("BlastSeqAlignViewer.14"), featP);
    jtp2.addTab(SVMessages.getString("BlastSeqAlignViewer.15"), siP);
    jtp2.addTab(SVMessages.getString("BlastSeqAlignViewer.16"), _featStatViewer);

    this.setLayout(new BorderLayout());
    this.add(jtp2, BorderLayout.CENTER);
  }

  protected void setHspSequenceViewer(HspSequenceViewer hspViewer) {
    _hspViewer = hspViewer;
  }

  protected void setHspSummaryDraw(HspSummaryDraw hspSummaryView) {
    _hspSummaryView = hspSummaryView;
  }

  protected FeatureViewer getFeatureViewer() {
    return _featViewer;
  }

  protected void cleanViewer() {
    _curHit = null;
    _curHsp = null;
    _featViewer.clear();
    // _featTabbedPane.setTitleAt(0,
    // SVMessages.getString("BlastSeqAlignViewer.14"));
    _seqInfoViewer.clear();
    _featStatViewer.clear();
  }

  protected void displayHsp(BlastHitHSP bhh, int hspNum) {
    FeatureTable fTable;
    BankSequenceInfo si;

    _curHit = bhh;
    _curHsp = ((SRHit) bhh.getHit()).getHsp(hspNum);

    fTable = _curHsp.getFeatures();
    _featViewer.setData(fTable);
    _featStatViewer.setData(fTable);
    si = _curHit.getHit().getSequenceInfo();
    if (si != null) {
      _seqInfoViewer.setData(si);
    } else {
      _seqInfoViewer.clear();
    }
  }

  /**
   * This class is used to handle events resulting from selections of features
   * made on the Feature Viewer.
   */
  private class SubjectFeatureSelectionListener implements FeatureSelectionListener {
    public void featureSelected(FeatureSelectionEvent event) {
      Feature feature;
      DViewerScroller scroller;
      JScrollBar sBar;
      DSequenceListViewer dlv;
      DRulerModel drm;
      DSequenceModel dsm;
      int val, max, sFrom, sTo;

      if (_hspViewer == null || _hspSummaryView == null)
        return;
      feature = event.getFeature();
      dlv = (DSequenceListViewer) _hspViewer.getHitSequenceViewer().getSequenceList();
      dsm = (DSequenceModel) dlv.getModel();
      drm = dsm.getSequence().getRulerModel();
      if (feature != null) {
        sFrom = drm.getRulerPos(feature.getFrom());
        sTo = drm.getRulerPos(feature.getTo());
        dlv.setSelectionInterval(Math.min(sFrom, sTo), Math.max(sFrom, sTo));
        val = feature.getFrom();
        scroller = _hspViewer.getScroller();
        sBar = scroller.getHorizontalScrollBar();
        val = Math.max(0, drm.getRulerPos(val) - 1) * dlv.getFixedCellWidth();
        sFrom = sBar.getValue();
        sTo = sFrom + sBar.getVisibleAmount() / 2 - 3;
        sFrom -= 3;
        if (val <= sFrom || val >= sTo) {
          max = sBar.getMaximum();// -sBar.getVisibleAmount();
          if (val > max) {
            val = max;
          }
          sBar.setValue(val);
        }
      } else {
        dlv.clearSelection();
      }

      _hspSummaryView.setHitFeature(feature);
      _hspSummaryView.updateUI();
    }

    public void featureTypesSelected(String[] types) {
    }

  }

}
