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
import java.text.MessageFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.plealog.genericapp.api.EZEnvironment;

import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.ui.blast.core.AnalysisUtils;
import bzh.plealog.bioinfo.ui.blast.core.BlastHitHSP;
import bzh.plealog.bioinfo.ui.blast.core.BlastHitHspImplem;
import bzh.plealog.bioinfo.ui.blast.event.BlastHitListEvent;
import bzh.plealog.bioinfo.ui.blast.event.BlastHitListListener;
import bzh.plealog.bioinfo.ui.blast.event.BlastHitListSupport;
import bzh.plealog.bioinfo.ui.feature.FeatureViewer;
import bzh.plealog.bioinfo.ui.resources.SVMessages;
import bzh.plealog.bioinfo.ui.sequence.basic.DViewerScroller;
import bzh.plealog.bioinfo.ui.util.JHeadPanel;

/**
 * This class is the Blast sequence alignment viewer implementation for the
 * Blast Viewer.
 * 
 * @author Patrick G. Durand
 */
public class SeqAlignViewer extends JPanel implements BlastHitListListener, TableModelListener {
  private static final long serialVersionUID = 4217671736041386508L;
  protected JHeadPanel _headPanel;
  protected HspSequenceViewer _hspViewer;
  protected HspValuesPanel _hspValues;
  protected HspSummaryDraw _hspSummaryView;
  protected BlastHitListSupport _updateSupport;
  protected BlastHitHSP _curHit;
  protected SRHsp _curHsp;
  protected Object[] _headerTitleElement;
  protected HspFeatureViewer _featureViewer;

  protected static final String PANEL_HEADER = SVMessages.getString("BlastSeqAlignViewer.0");
  private static final MessageFormat HEADER_FORMATTER = new MessageFormat(
      SVMessages.getString("BlastSeqAlignViewer.8"));

  /**
   * Default constructor.
   */
  protected SeqAlignViewer() {
    ImageIcon icon;
    Box box;
    JPanel pnl, pnl2;
    JToolBar tBar;
    _headerTitleElement = new Object[7];
    _hspValues = new HspValuesPanel();
    _hspViewer = new HspSequenceViewer();
    _hspSummaryView = _hspValues.getHspSummaryDraw();
    _hspViewer.setHspSummaryDraw(_hspSummaryView);

    tBar = getToolbar();
    setContextMenu();

    //sequence viewer + hsp selector + tbar
    pnl = new JPanel(new BorderLayout());
    pnl.add(_hspValues.getHSPSelector(), BorderLayout.WEST);
    if (tBar != null)
      pnl.add(tBar, BorderLayout.EAST);
    box = Box.createVerticalBox();
    box.add(_hspViewer);
    box.add(pnl);
    box.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 1));

    pnl2 = new JPanel(new BorderLayout());
    getFeatureViewerCompo();
    pnl2.add(_hspValues, BorderLayout.WEST);
    pnl2.add(box, BorderLayout.CENTER);
    // for now, feature viewer is hidden since Features are not available in BlastViewer
    //pnl2.add(fViewer, BorderLayout.CENTER);
    
    icon = EZEnvironment.getImageIcon("alignment.png");
    if (icon != null) {
      _headPanel = new JHeadPanel(icon, PANEL_HEADER, pnl2, true, false);
    } else {
      _headPanel = new JHeadPanel(null, PANEL_HEADER, pnl2, true, false);
    }

    this.setLayout(new BorderLayout());
    this.add(_headPanel, BorderLayout.CENTER);
  }
  public Dimension getMinimumSize() {
    return new Dimension(50, 50);
  }

  public void registerHitListSupport(BlastHitListSupport us) {
    _updateSupport = us;
    _hspValues.registerBlastHitListSupport(us);
  }

  /**
   * Return the FeatureViewer associated to this component.
   */
  public FeatureViewer getFeatureViewer() {
    if (_featureViewer != null)
      return _featureViewer.getFeatureViewer();
    else
      return null;
  }

  public JComponent getFeatureViewerCompo() {
    _featureViewer = new HspFeatureViewer();
    _featureViewer.setHspSequenceViewer(_hspViewer);
    _featureViewer.setHspSummaryDraw(_hspSummaryView);
    return _featureViewer;
  }

  /**
   * Implementation of BlastHitListListener interface. This implementation is
   * intended to listen to notifications from BlastHitList: as soon as a hit is
   * selected, this method will ensures to display the sequence alignment.
   */
  public void hitChanged(BlastHitListEvent e) {
    List<BlastHitHspImplem> hits;
    BlastHitHSP hit;
    boolean displaySeqAlign;

    hits = e.getHitHsps();
    if (hits == null || hits.isEmpty() || hits.size() > 1) {
      resetViewer();
      return;
    }

    hit = hits.get(0);
    if (hit.equals(_curHit))
      return;
    displaySeqAlign = _hspViewer.displayHsp(hit.getHit(), hit.getHspNum() - 1);
    _hspViewer.setVisible(displaySeqAlign);
    _hspViewer.updateUI();
    _hspSummaryView.setHit(hit.getHit());
    _hspSummaryView.setQuerySize(hit.getQuerySize());
    _hspSummaryView.selectHsp(hit.getHspNum() - 1);
    _hspSummaryView.updateUI();
    _hspValues.displayHsp(hit, hit.getHspNum() - 1, hit.getBlastType());
    _headPanel.setTitle(getHeaderTitle(hit));
    _curHit = hit;
    setHsp(hit, hit.getHspNum() - 1);
    // if no sequences disables commands
    activateActions(displaySeqAlign);
  }

  protected String getHeaderTitle(BlastHitHSP hit) {

    _headerTitleElement[0] = PANEL_HEADER;
    _headerTitleElement[1] = SVMessages.getString("BlastSeqAlignViewer.7");
    _headerTitleElement[2] = String.valueOf(hit.getQuerySize());
    _headerTitleElement[3] = SROutput.SEQ_TYPES[hit.getQuerySeqType() - 1];
    _headerTitleElement[4] = hit.getHit().getHitAccession();
    _headerTitleElement[5] = AnalysisUtils.POS_FORMATTER.format(hit.getHit().getHitLen());
    _headerTitleElement[6] = SROutput.SEQ_TYPES[hit.getHitSeqType() - 1];

    String header = HEADER_FORMATTER.format(_headerTitleElement);
    SRHsp hsp;

    hsp = hit.getHit().getHsp(0);
    if (hsp.getQuery().getSequence(hsp) == null || hsp.getHit().getSequence(hsp) == null) {
      header += (" [" + SVMessages.getString("BlastSeqAlignViewer.91") + "]");
    }
    return header;
  }

  protected void setHsp(BlastHitHSP bhh, int hspNum) {
    _featureViewer.displayHsp(bhh, hspNum);
    //_featureViewer.setVisible(bhh.getHit().getHsp(hspNum).getFeatures()!=null);
    _headPanel.setTitle(getHeaderTitle(bhh));
  }

  protected void resetViewer() {
    _hspValues.cleanViewer();
    _hspViewer.cleanViewer();
    _hspViewer.updateUI();
    _hspSummaryView.cleanViewer();
    _hspSummaryView.updateUI();
    _headPanel.setTitle(PANEL_HEADER);
    _curHit = null;
    activateActions(false);
    _featureViewer.cleanViewer();
  }

  protected void activateActions(boolean activate) {
  }

  /**
   * Implementation of TableModelListener interface. This implementation is
   * intended to listen to the internal data model used in BlastHitList: when
   * this model is reset, this method will reset the content of the sequence
   * alignment viewer.
   */
  public void tableChanged(TableModelEvent e) {
    resetViewer();
  }

  /**
   * Return the current Hit displayed in the SeqAlignViewer.
   */
  public BlastHitHSP getCurrentHit() {
    return _curHit;
  }

  /**
   * Return the current HSP displayed in the SeqAlignViewer.
   */
  public SRHsp getCurrentHsp() {
    return _curHsp;
  }

  /**
   * Create a toolbar with additional functions.
   */
  protected JToolBar getToolbar() {
    return null;
  }

  /**
   * Return the component responsible for scrolling HSP sequence alignment
   * viewer.
   */
  public DViewerScroller getScroller() {
    return _hspViewer.getScroller();
  }

  protected void setContextMenu() {
  }

}
