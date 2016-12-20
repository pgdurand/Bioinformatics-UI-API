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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.ui.blast.config.ConfigManager;
import bzh.plealog.bioinfo.ui.blast.config.color.DefaultHitColorPolicy;
import bzh.plealog.bioinfo.ui.blast.config.color.ColorPolicyConfig;
import bzh.plealog.bioinfo.ui.blast.core.AnalysisUtils;
import bzh.plealog.bioinfo.ui.resources.SVMessages;

/**
 * This class implements the summary viewer. It aims at displaying a map of all
 * query sub-sequences (HSP) aligned on the hit sequence.
 * 
 * @author Patrick G. Durand
 */
public class HspSummaryDraw extends JPanel {
  private static final long serialVersionUID = 3447190155908298118L;
  private SRHit _data;
  private String _queryStr;
  private String _hitsStr;
  private String _hitAcc;
  private Feature _hitFeature;
  private int _hspNum = -1;
  private int _querySize = -1;
  private boolean _dispSeqName;
  private boolean _detailedView;
  private int _xPoints[] = new int[5];
  private int _yPoints[] = new int[5];
  private int _regionStart = -1;
  private int _regionExtent = -1;
  private boolean _selected;

  private static final Font VIEWER_FNT = new Font("Arial", Font.PLAIN, 9);
  private static final Color CLR_DETAIL = new Color(Color.YELLOW.getRed(), Color.YELLOW.getGreen(),
      Color.YELLOW.getBlue(), 192);

  /**
   * Default constructor.
   */
  public HspSummaryDraw() {
    this.setBackground(Color.WHITE);
    this.setOpaque(true);
    this.setFont(VIEWER_FNT);
    _queryStr = SVMessages.getString("BlastSeqAlignViewer.7");
    _hitsStr = SVMessages.getString("BlastSeqAlignViewer.10");
  }

  public void setSelected(boolean b) {
    _selected = b;
  }

  public boolean isSelected() {
    return _selected;
  }

  /**
   * Set the Hit to display.
   */
  public void setHit(SRHit hit) {
    _data = hit;
    if (_data == null)
      return;
    _hitAcc = _data.getHitAccession();
    if (_hitAcc.length() > 10)
      _hitAcc = _hitAcc.substring(0, 10);
  }

  /**
   * Set the size of the query.
   */
  public void setQuerySize(int qsize) {
    _querySize = qsize;
  }

  /**
   * Set the HSP ID that have to be selected on the drawing. Pass the value -1
   * to select nothing.
   * 
   * @param hspNum
   *          the HSP ID (zero-based value)
   */
  public void selectHsp(int hspNum) {
    _hspNum = hspNum;
  }

  /**
   * Figure out if sequence names have to be displayed or not.
   */
  public void displaySeqName(boolean dsn) {
    _dispSeqName = dsn;
  }

  /**
   * Figure out if viewer has to display query hits details.
   */
  public void displayDetails(boolean det) {
    _detailedView = det;
  }

  /**
   * Set a feature that has to be highlighted on the hit sequence map.
   */
  public void setHitFeature(Feature feat) {
    _hitFeature = feat;
  }

  /**
   * Identifie the region that is currently displayed in details in another HSP
   * viewer. Parameters from and extent are given as zero-based values in the
   * coordinate system of the HSP (sequence alignment ccordinate system).
   */
  public void setDisplayRegion(int from, int extent) {
    _regionStart = from;
    _regionExtent = extent;
  }

  /**
   * Reset the viewer.
   */
  public void cleanViewer() {
    _data = null;
    _hitFeature = null;
    _hspNum = _querySize = _regionStart = _regionExtent = -1;
  }

  private Color getColor(SRHsp hsp) {
    Color clr = Color.BLACK;
    ColorPolicyConfig nc;

    nc = (ColorPolicyConfig) ConfigManager.getConfig(ColorPolicyConfig.NAME);
    if (nc == null) {
      clr = DefaultHitColorPolicy.getColor((int) hsp.getScores().getBitScore());
    } else {
      clr = nc.getHitColor(hsp, nc.isUsingColorTransparency());
    }
    return clr;
  }

  private void simplePaint(Graphics g) {
    FontMetrics fm;
    SRHsp hsp;
    int yBase, xDecal, paneWidth, from, to, i, size, cy;
    double factorHit;
    Color clr;

    fm = g.getFontMetrics();
    yBase = this.getHeight() / 2;
    cy = Math.min(fm.getHeight() / 2, yBase / 2);
    if (_dispSeqName) {
      xDecal = Math.max(fm.stringWidth(_queryStr), fm.stringWidth(_hitAcc));
      xDecal = Math.max(xDecal, fm.stringWidth(_hitsStr)) + 2;
      g.drawString(_hitsStr, 1, yBase);
      g.drawString(_hitAcc, 1, yBase + 2 + cy);
    } else {
      xDecal = 2;
    }
    paneWidth = this.getWidth() - xDecal - 2;
    factorHit = (double) paneWidth / (double) _data.getHitLen();
    if (isSelected())
      g.setColor(this.getForeground());
    else
      g.setColor(Color.black);
    g.fillRect(xDecal, yBase + 2, paneWidth, cy);

    // draw all HSP: summary
    size = _data.countHsp();
    for (i = 0; i < size; i++) {
      hsp = _data.getHsp(i);
      from = Math.min(hsp.getHit().getFrom() - 1, hsp.getHit().getTo() - 1);
      to = Math.max(hsp.getHit().getFrom() - 1, hsp.getHit().getTo() - 1);
      if (isSelected())
        clr = this.getForeground();
      else
        clr = getColor(hsp);
      g.setColor(clr.darker());
      g.fillRect(xDecal + (int) ((double) from * factorHit), yBase - cy,
          Math.max(2, (int) ((double) (to - from + 1) * factorHit)), cy + 1);

    }
    g.setColor(Color.BLACK);
  }

  private void paintHitFeature(Graphics g, int xDecal, int yBase, int height, double factor) {
    int fromFeat, toFeat, x1, x2;
    boolean normal;

    // the content of this method is adapated from Graphic Hit List
    // please, refer to this class for more information

    fromFeat = _hitFeature.getFrom() - 1;
    toFeat = _hitFeature.getTo() - 1;
    if (fromFeat < 0 || toFeat < 0)
      return;
    normal = (_hitFeature.getStrand() == Feature.PLUS_STRAND ? true : false);
    x1 = xDecal + (int) ((double) fromFeat * factor);
    x2 = x1 + Math.max(5, (int) (((double) (toFeat - fromFeat + 1)) * factor));
    AnalysisUtils.computePolygone(_xPoints, _yPoints, x1, yBase, x2, height, normal);
    g.setColor(Color.DARK_GRAY);
    g.fillPolygon(_xPoints, _yPoints, 5);
  }

  private void detailPaint(Graphics g) {
    FontMetrics fm;
    SRHsp hsp;
    DSequence seq;
    int yBase, xDecal, paneWidth, from, to, i, size, cy, x1, x2, y1, y2, inc, extent;
    double factorQuery, factorHit;
    Color clr;

    fm = g.getFontMetrics();
    yBase = this.getHeight() / 2;
    cy = Math.min(fm.getHeight() / 2, yBase / 2);
    if (_dispSeqName) {
      xDecal = Math.max(fm.stringWidth(_queryStr), fm.stringWidth(_hitAcc));
      xDecal = Math.max(xDecal, fm.stringWidth(_hitsStr)) + 2;
      g.drawString(_queryStr, 1, 4 + cy);
      g.drawString(_hitsStr, 1, yBase);
      g.drawString(_hitAcc, 1, yBase + 7 + 2 * cy);
    } else {
      xDecal = 2;
    }
    paneWidth = this.getWidth() - xDecal - 2;
    factorHit = (double) paneWidth / (double) _data.getHitLen();
    factorQuery = (double) paneWidth / (double) _querySize;

    // reference rect for Query
    g.fillRect(xDecal, 4, paneWidth, cy);
    // reference rect for Hit
    g.fillRect(xDecal, yBase + 7 + cy, paneWidth, cy);

    // draw all HSP: summary
    size = _data.countHsp();
    for (i = 0; i < size; i++) {
      hsp = _data.getHsp(i);
      from = Math.min(hsp.getHit().getFrom() - 1, hsp.getHit().getTo() - 1);
      to = Math.max(hsp.getHit().getFrom() - 1, hsp.getHit().getTo() - 1);
      clr = getColor(hsp).darker();
      g.setColor(clr);
      g.fillRect(xDecal + (int) ((double) from * factorHit), yBase - cy - 2,
          Math.max(1, (int) ((double) (to - from + 1) * factorHit)), cy);

    }
    if (size > 1) {
      g.setColor(Color.RED);
      hsp = _data.getHsp(_hspNum);
      from = Math.min(hsp.getHit().getFrom() - 1, hsp.getHit().getTo() - 1);
      to = Math.max(hsp.getHit().getFrom() - 1, hsp.getHit().getTo() - 1);
      x1 = xDecal + (int) ((double) from * factorHit);
      x2 = x1 + Math.max(2, (int) ((double) (to - from) * factorHit));
      g.drawRect(x1 - 1, yBase - cy - 4, Math.max(2, (int) ((double) (to - from + 1) * factorHit)), cy + 3);
    }
    // highlight on Query and Hit reference rectangles the region that is
    // displayed in the SeqAlignViewer
    if (_regionStart >= 0 && _regionExtent >= 0) {
      // process query
      hsp = _data.getHsp(_hspNum);
      g.setColor(CLR_DETAIL);
      from = Math.min(hsp.getQuery().getFrom(), hsp.getQuery().getTo()) - 1;
      to = Math.max(hsp.getQuery().getFrom(), hsp.getQuery().getTo()) - 1;
      extent = to - from + 1;
      if (_regionExtent < extent)
        extent = _regionExtent;
      seq = hsp.getQuery().getSequence(hsp);
      if (seq != null) {
        inc = Math.abs(seq.getRulerModel().getIncrement());
        x1 = xDecal + (int) ((double) (from + (_regionStart - AnalysisUtils.getGapContent(seq, 0, _regionStart)) * inc)
            * factorQuery);
        x2 = Math.max(2,
            (int) ((double) (extent - AnalysisUtils.getGapContent(seq, _regionStart, _regionStart + extent))
                * factorQuery) * inc);
        g.fillRect(x1, 4, x2, cy);
      }
      // process hit
      from = Math.min(hsp.getHit().getFrom(), hsp.getHit().getTo()) - 1;
      to = Math.max(hsp.getHit().getFrom(), hsp.getHit().getTo()) - 1;
      extent = to - from + 1;
      if (_regionExtent < extent)
        extent = _regionExtent;
      seq = hsp.getHit().getSequence(hsp);
      if (seq != null) {
        inc = Math.abs(seq.getRulerModel().getIncrement());
        x1 = xDecal + (int) ((double) (from + (_regionStart - AnalysisUtils.getGapContent(seq, 0, _regionStart)) * inc)
            * factorHit);
        x2 = Math.max(2,
            (int) ((double) (extent - AnalysisUtils.getGapContent(seq, _regionStart, _regionStart + extent))
                * factorHit) * inc);
        g.fillRect(x1, yBase + 7 + cy, x2, cy);
      }
    }

    // highlight current HSP in red on the Hit seq
    g.setColor(Color.RED);
    hsp = _data.getHsp(_hspNum);
    from = Math.min(hsp.getHit().getFrom() - 1, hsp.getHit().getTo() - 1);
    to = Math.max(hsp.getHit().getFrom() - 1, hsp.getHit().getTo() - 1);
    x1 = xDecal + (int) ((double) from * factorHit);
    x2 = x1 + Math.max(2, (int) ((double) (to - from + 1) * factorHit));
    y1 = yBase + 4;
    y2 = y1 + cy;
    g.drawLine(x1, y2, x1, y1);
    g.drawLine(x1, y1, x2, y1);
    g.drawLine(x2, y1, x2, y2);

    // highlight current HSP in red on the Query seq
    g.setColor(Color.RED);
    hsp = _data.getHsp(_hspNum);
    from = Math.min(hsp.getQuery().getFrom() - 1, hsp.getQuery().getTo() - 1);
    to = Math.max(hsp.getQuery().getFrom() - 1, hsp.getQuery().getTo() - 1);
    x1 = xDecal + (int) ((double) from * factorQuery);
    x2 = x1 + Math.max(2, (int) ((double) (to - from + 1) * factorQuery));
    y1 = 4 + 2 * cy + 3;
    y2 = y1 - cy;
    g.drawLine(x1, y2, x1, y1);
    g.drawLine(x1, y1, x2, y1);
    g.drawLine(x2, y1, x2, y2);
    if (_hitFeature != null) {
      paintHitFeature(g, xDecal, yBase + 8 + 2 * cy, // see above the value when
                                                     // drawing ref rect for Hit
          cy / 2, factorHit);
    }
    g.setColor(Color.BLACK);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (_data == null) {
      return;
    }

    ColorPolicyConfig nc = (ColorPolicyConfig) ConfigManager.getConfig(ColorPolicyConfig.NAME);
    if (nc != null && nc.isUsingAntialias()) {
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    if (_detailedView)
      detailPaint(g);
    else
      simplePaint(g);
  }
}