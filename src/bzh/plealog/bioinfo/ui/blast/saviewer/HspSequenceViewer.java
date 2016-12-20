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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.StringReader;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.plealog.genericapp.ui.common.ContextMenuManager;

import bzh.plealog.bioinfo.api.core.config.CoreSystemConfigurator;
import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.sequence.DSequenceModel;
import bzh.plealog.bioinfo.ui.sequence.basic.DRulerViewer;
import bzh.plealog.bioinfo.ui.sequence.basic.DSequenceListViewer;
import bzh.plealog.bioinfo.ui.sequence.basic.DSequenceViewer;
import bzh.plealog.bioinfo.ui.sequence.basic.DViewerScroller;
import bzh.plealog.bioinfo.util.DAlphabetUtils;

/**
 * This class is the SeqAlign Viewer.
 * 
 * @author Patrick G. Durand
 */
public class HspSequenceViewer extends JPanel {
  private static final long serialVersionUID = 6162992680953130576L;
  private DSequenceViewer _hViewer;
  private DSequenceViewer _midLineViewer;
  private DSequenceViewer _qViewer;
  private DViewerScroller _scroller;
  private DSequenceModel _hSM;
  private DSequenceModel _mSM;
  private DSequenceModel _qSM;
  private Font _fnt = new Font("Arial", Font.PLAIN, 12);
  private FontMetrics _fm;

  public HspSequenceViewer() {
    DSequenceListViewer master, slave;
    DRulerViewer drv;
    JPanel viewer;
    Dimension dim;
    int cellH, scrollWidth;

    viewer = new JPanel();
    viewer.setLayout(new BoxLayout(viewer, BoxLayout.Y_AXIS));

    _fm = this.getFontMetrics(_fnt);

    // prepare the viewer for the query sequence
    master = createViewer("");
    drv = new DRulerViewer(((DSequenceModel) master.getModel()).getSequence().createRulerModel(1, 1), 15,
        SwingConstants.HORIZONTAL, SwingConstants.TOP);
    _qViewer = new DSequenceViewer(master, drv, false);
    drv.setBoxSize(_fm.getHeight());
    _hSM = (DSequenceModel) master.getModel();
    _qViewer.setAlignmentX(0);
    viewer.add(_qViewer);

    // prepare the viewer for the middle sequence
    slave = createComparer("");
    _midLineViewer = new DSequenceViewer(slave, null, false);
    _midLineViewer.setSelectionEnabled(false);
    _mSM = (DSequenceModel) slave.getModel();
    _midLineViewer.setAlignmentX(0);
    viewer.add(_midLineViewer);

    // prepare the viewer for the hit sequence
    slave = createViewer("");
    drv = new DRulerViewer(((DSequenceModel) slave.getModel()).getSequence().createRulerModel(1, 1), 15,
        SwingConstants.HORIZONTAL, SwingConstants.BOTTOM);
    _hViewer = new DSequenceViewer(slave, drv, true);
    drv.setBoxSize(_fm.getHeight());
    _qSM = (DSequenceModel) slave.getModel();
    _hViewer.setAlignmentX(0);
    viewer.add(_hViewer);

    // assemble the viewer
    viewer.add(Box.createVerticalGlue());
    viewer.setOpaque(true);
    viewer.setBackground(Color.white);

    // prepare the scroller
    _scroller = new DViewerScroller(viewer);
    _scroller.setCellWidth(_hViewer.getSequenceList().getFixedCellWidth());
    _scroller.setCellHeight(_hViewer.getSequenceList().getFixedCellHeight());
    cellH = _hViewer.getSequenceList().getFixedCellWidth();
    scrollWidth = UIManager.getDefaults().getInt("ScrollBar.width");
    dim = new Dimension(120, 7 * cellH + scrollWidth);
    _scroller.getHorizontalScrollBar().setBlockIncrement(50 * cellH);
    _scroller.getHorizontalScrollBar().setUnitIncrement(cellH);
    _scroller.setPreferredSize(dim);
    _scroller.setMinimumSize(dim);
    _scroller.setOpaque(true);
    _scroller.setBackground(Color.white);

    this.setLayout(new BorderLayout());
    this.add(_scroller, BorderLayout.CENTER);
  }

  protected void setHspSummaryDraw(HspSummaryDraw hsd) {
    if (hsd != null)
      _scroller.getHorizontalScrollBar().addAdjustmentListener(new HspViewerAdjustmentListener(hsd, _scroller));
  }

  protected DSequenceViewer getHitSequenceViewer() {
    return _hViewer;
  }

  protected DSequenceViewer getQuerySequenceViewer() {
    return _qViewer;
  }

  public void setHitContextMenu(ContextMenuManager contextMenu) {
    _hViewer.setContextMenu(contextMenu);
  }

  public void setQueryContextMenu(ContextMenuManager contextMenu) {
    _qViewer.setContextMenu(contextMenu);
  }

  private DSequenceListViewer createComparer(String seq) {
    DSequenceListViewer example = new DSequenceListViewer();
    example.setFont(_fnt);

    DSequenceModel model = new DSequenceModel(CoreSystemConfigurator.getSequenceFactory()
        .getSequence(new StringReader(seq), DAlphabetUtils.getComparer_Alphabet()));
    example.setModel(model);
    return (example);
  }

  private DSequenceListViewer createViewer(String seq) {
    DSequenceListViewer example = new DSequenceListViewer();
    example.setFont(_fnt);

    DSequenceModel model = new DSequenceModel(CoreSystemConfigurator.getSequenceFactory()
        .getSequence(new StringReader(seq), DAlphabetUtils.getIUPAC_Protein_Alphabet()));
    example.setModel(model);
    return (example);
  }

  public void cleanViewer() {
    _hViewer.setModel(_hSM, 0, 1);
    _midLineViewer.setModel(_mSM, 0, 1);
    _qViewer.setModel(_qSM, 0, 1);
  }

  public boolean displayHsp(SRHit hit, int hspNum) {
    return displayHsp(hit.getHsp(hspNum));
  }

  private int getIncrement(int from, int to, int length) {
    int inc;

    if ((Math.abs(to - from) + 1) / length != 1)
      inc = 3;// translated blast
    else
      inc = 1;// normal blast
    if (from > to)
      inc = (-inc);

    return inc;
  }

  private boolean displayHsp(SRHsp hsp) {
    DSequenceModel model;
    int increment;

    if (hsp.getQuery().getSequence(hsp) == null || hsp.getHit().getSequence(hsp) == null)
      return false;
    model = new DSequenceModel(hsp.getQuery().getSequence(hsp));
    increment = getIncrement(hsp.getQuery().getFrom(), hsp.getQuery().getTo(),
        hsp.getScores().getAlignLen() - hsp.getQuery().getGaps());
    _qViewer.setModel(model, hsp.getQuery().getFrom(), increment);
    if (hsp.getMidline() != null && hsp.getMidline().getSequence(hsp) != null) {
      model = new DSequenceModel(hsp.getMidline().getSequence(hsp));
      _midLineViewer.setModel(model, 0, 1);
      _midLineViewer.setVisible(true);
    } else {
      _midLineViewer.setVisible(false);
    }
    model = new DSequenceModel(hsp.getHit().getSequence(hsp));
    increment = getIncrement(hsp.getHit().getFrom(), hsp.getHit().getTo(),
        hsp.getScores().getAlignLen() - hsp.getHit().getGaps());
    _hViewer.getSequenceList().clearSelection();
    _hViewer.setModel(model, hsp.getHit().getFrom(), increment);
    // _scroller.getHorizontalScrollBar().setValue(0);
    return true;
  }

  protected DViewerScroller getScroller() {
    return _scroller;
  }

  private class HspViewerAdjustmentListener implements AdjustmentListener {
    private HspSummaryDraw _hspSummaryView;
    private DViewerScroller _parent;

    public HspViewerAdjustmentListener(HspSummaryDraw hsd, DViewerScroller parent) {
      _hspSummaryView = hsd;
      _parent = parent;
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
      JScrollBar scroller;
      int cellH = _parent.getCellWidth();
      scroller = (JScrollBar) e.getSource();
      _hspSummaryView.setDisplayRegion(scroller.getValue() / cellH, scroller.getVisibleAmount() / cellH);
      _hspSummaryView.repaint();
    }
  }

}
