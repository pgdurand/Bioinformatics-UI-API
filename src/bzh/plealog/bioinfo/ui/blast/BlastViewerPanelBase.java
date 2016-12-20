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
package bzh.plealog.bioinfo.ui.blast;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.searchresult.SRRequestInfo;
import bzh.plealog.bioinfo.ui.blast.config.ConfigManager;
import bzh.plealog.bioinfo.ui.blast.core.BlastEntry;
import bzh.plealog.bioinfo.ui.blast.core.BlastHitHSP;
import bzh.plealog.bioinfo.ui.blast.event.BlastHitListSupport;
import bzh.plealog.bioinfo.ui.blast.hittable.BlastHitTable;
import bzh.plealog.bioinfo.ui.blast.nav.BlastNavigator;
import bzh.plealog.bioinfo.ui.blast.saviewer.SeqAlignViewer;
import bzh.plealog.bioinfo.ui.resources.SVMessages;
import bzh.plealog.bioinfo.ui.util.JHeadPanel;

import com.plealog.genericapp.api.EZEnvironment;

/**
 * This is the BlastViewer Main Module.
 * 
 * It wraps within a single component the various
 * elements required to displayed Blast data: a BlastNavigator, a Blast Hit Table,
 * the pairwise sequence alignment viewer, etc.
 * 
 * @author Patrick G. Durand
 */
public class BlastViewerPanelBase extends JPanel {

  private static final long serialVersionUID = -2405089127382200483L;

  protected BlastHitTable _hitListPane;
  protected SeqAlignViewer _seqAlignViewer;
  protected BlastNavigator _summaryPane;
  protected JPanel _rightPane;
  protected BlastHitListSupport _updateSupport;

  protected static final String HITPANEL_HEADER = SVMessages.getString("BlastViewerPanel.0");
  protected static final String HITPANEL_LIST = SVMessages.getString("BlastViewerPanel.1");
  protected static final String HITPANEL_GRAPHIC = SVMessages.getString("BlastViewerPanel.2");

  /**
   * Default constructor.
   */
  public BlastViewerPanelBase() {
    super();
    createGUI();
  }

  /**
   * Set the data to display in this viewer.
   */
  public void setContent(BlastEntry entry) {
    _summaryPane.setContent(entry);
  }

  /**
   * Set the data to display in this viewer.
   */
  public void setContent(SROutput so, String soPath) {
    _summaryPane.setContent(prepareEntry(so, soPath));
  }

  /**
   * Set the data to display in this viewer.
   */
  public void setContent(SROutput so) {
    _summaryPane.setContent(prepareEntry(so, null));
  }

  /**
   * Return the Hit currently selected in this BlastViewerPanel. Actually, the
   * method returns the Hit that is currently displayed by the SeqAlignViewer
   * panel.
   */
  public BlastHitHSP getSelectedHit() {
    return _seqAlignViewer.getCurrentHit();
  }

  /**
   * Return the HSP currently selected in this BlastViewerPanel. Actually, the
   * method returns the HSP that is currently displayed by the SeqAlignViewer
   * panel.
   */
  public SRHsp getSelectedHsp() {
    return _seqAlignViewer.getCurrentHsp();
  }

  private BlastEntry prepareEntry(SROutput bo, String soPath) {
    String val;
    int pos;

    // analyze SROutput object (i.e. a Blast result) to get:
    // program name, query name and databank name
    SRRequestInfo bri = bo.getRequestInfo();
    Object obj = bri.getValue(SRRequestInfo.PRGM_VERSION_DESCRIPTOR_KEY);
    if (obj != null) {
      val = obj.toString();
      if ((pos = val.indexOf('[')) > 0) {
        val = val.substring(0, pos - 1);
      } else {
        val = obj.toString();
      }
    } else {
      val = null;
    }
    String program = val != null ? val : "?";
    obj = bri.getValue(SRRequestInfo.DATABASE_DESCRIPTOR_KEY);
    String dbname = obj != null ? obj.toString() : "?";
    obj = bri.getValue(SRRequestInfo.QUERY_DEF_DESCRIPTOR_KEY);
    String queryName = obj != null ? obj.toString() : "?";

    return new BlastEntry(program, queryName, soPath, bo, null, dbname, false);
  }

  private void createGUI() {
    JHeadPanel headPanel;
    ImageIcon icon;

    _updateSupport = new BlastHitListSupport();
    _summaryPane = new BlastNavigator();
    _hitListPane = ConfigManager.getHitTableFactory().createViewer();
    _seqAlignViewer = ConfigManager.getSeqAlignViewerFactory().createViewer();
    icon = EZEnvironment.getImageIcon("hitTable.png");
    if (icon != null) {
      headPanel = new JHeadPanel(icon, HITPANEL_HEADER, _hitListPane);
    } else {
      headPanel = new JHeadPanel(null, HITPANEL_HEADER, _hitListPane);
    }
    headPanel.setToolPanel(_summaryPane);
    _rightPane = new JPanel(new BorderLayout());
    _rightPane.add(headPanel, BorderLayout.CENTER);
    _rightPane.add(_seqAlignViewer, BorderLayout.SOUTH);

    this.setLayout(new BorderLayout());
    this.add(_rightPane, BorderLayout.CENTER);

    // listeners to the selection of a new BIteration
    _summaryPane.addIterationListener(_hitListPane);
    // listeners to the change of data model
    _hitListPane.addHitDataListener(_seqAlignViewer);
    // listeners to selection within hit tables
    _hitListPane.registerHitListSupport(_updateSupport);
    _seqAlignViewer.registerHitListSupport(_updateSupport);
    _updateSupport.addBlastHitListListener(_hitListPane);
    _updateSupport.addBlastHitListListener(_seqAlignViewer);
    this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
  }

}
