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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.ui.blast.core.BlastHitHSP;
import bzh.plealog.bioinfo.ui.blast.core.BlastHitHspImplem;
import bzh.plealog.bioinfo.ui.blast.event.BlastHitListEvent;
import bzh.plealog.bioinfo.ui.blast.event.BlastHitListSupport;
import bzh.plealog.bioinfo.ui.resources.SVMessages;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This class displays HSP values.
 * 
 * @author Patrick G. Durand
 */
public class HspValuesPanel extends JPanel {
  /**
   * 
   */
  private static final long serialVersionUID = 3429916031532692556L;
  private JTextField _score;
  private JTextField _scoreBits;
  private JTextField _evalue;
  private JTextField _identity;
  private JTextField _positive;
  private JTextField _gaps;
  private JTextField _qFromTo;
  private JTextField _hFromTo;
  private JTextField _hitAccession;
  private JTextField _aliLength;
  private JTextField _hspNum;
  private JTextField _qCoverage;
  private JTextField _hCoverage;
  private JTextArea _description;
  private JButton _prevHsp;
  private JButton _nextHsp;
  private Component _hspSelector;
  private HspSummaryDraw _hspSummaryView;
  private BlastHitHSP _curHit;
  private BlastHitListSupport _updateSupport;

  private static final DecimalFormat POS_FORMATTER = new DecimalFormat("#,###,###,###");
  private static final DecimalFormat SCORE_FORMATTER = new DecimalFormat("#,###");
  private static final DecimalFormat EVALUE_FORMATTER1 = new DecimalFormat("0.E0");
  private static final DecimalFormat EVALUE_FORMATTER2 = new DecimalFormat("##.##");
  private static final DecimalFormat PCT_FORMATTER = new DecimalFormat("###.#");

  /**
   * Default constructor.
   */
  public HspValuesPanel() {
    JTabbedPane jtp;
    JPanel hspCount;

    hspCount = new JPanel(new BorderLayout());
    _hspSelector = createHSPSelectorPanel();
    _hspSummaryView = new HspSummaryDraw();
    _hspSummaryView.displaySeqName(true);
    _hspSummaryView.displayDetails(true);
    _hspSummaryView.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    hspCount.add(_hspSummaryView, BorderLayout.CENTER);

    jtp = new JTabbedPane();
    jtp.setFocusable(false);
    jtp.add(SVMessages.getString("BlastSeqAlignViewer.1"), hspCount);
    jtp.add(SVMessages.getString("BlastSeqAlignViewer.9"), createDescPanel());
    jtp.add(SVMessages.getString("BlastSeqAlignViewer.2"), createStatPanel());
    jtp.add(SVMessages.getString("BlastSeqAlignViewer.3"), createAlignPanel());

    this.setLayout(new BorderLayout());
    this.add(jtp, BorderLayout.CENTER);
  }

  protected void registerBlastHitListSupport(BlastHitListSupport support) {
    _updateSupport = support;
  }

  protected HspSummaryDraw getHspSummaryDraw() {
    return _hspSummaryView;
  }

  /**
   * Resets the content of the entire viewer.
   */
  public void cleanViewer() {
    _curHit = null;
    _score.setText("");
    _scoreBits.setText("");
    _evalue.setText("");
    _identity.setText("");
    _positive.setText("");
    _gaps.setText("");
    _qCoverage.setText("");
    _hCoverage.setText("");
    _qFromTo.setText("");
    _hFromTo.setText("");
    _hitAccession.setText(SVMessages.getString("BlastSeqAlignViewer.65"));
    _aliLength.setText("");
    _hspNum.setText("");
    _prevHsp.setEnabled(false);
    _nextHsp.setEnabled(false);
    _description.setText("");
  }

  protected Component getHSPSelector() {
    return _hspSelector;
  }

  /**
   * Displays the data for a particular HSP.
   * 
   * @param bhh
   *          the hit containing the HSP to display
   * @param hspNum
   *          the HSP to display
   * @param bType
   *          one of SROutput.BLAST_XXX constants
   */
  protected void displayHsp(BlastHitHSP bhh, int hspNum, int bType) {
    SRHsp hsp;
    SRHit hit;
    double eval;
    String sVal;
    int frame, from, to;

    hit = (SRHit) bhh.getHit();
    _curHit = bhh;
    hsp = hit.getHsp(hspNum);
    _description.setText(hit.getHitId() + "\n" + hit.getHitDef());
    _description.setCaretPosition(0);
    _scoreBits.setText(SCORE_FORMATTER.format(hsp.getScores().getBitScore()) + " bits");
    _score.setText("(" + SCORE_FORMATTER.format(hsp.getScores().getScore()) + ")");
    eval = hsp.getScores().getEvalue();
    if (eval > 0 && eval < 0.1)
      _evalue.setText(EVALUE_FORMATTER1.format(eval));
    else
      _evalue.setText(EVALUE_FORMATTER2.format(eval));
    _identity.setText(PCT_FORMATTER.format(hsp.getScores().getIdentityP()) + "%");
    _positive.setText(PCT_FORMATTER.format(hsp.getScores().getPositiveP()) + "%");
    if (hsp.getScores().getGaps() != 0) {
      _gaps.setText(PCT_FORMATTER.format(hsp.getScores().getGapsP()) + "%");
    } else {
      _gaps.setText("-");
    }
    if (hsp.getQueryCoverage() != 0) {
      _qCoverage.setText(PCT_FORMATTER.format(hsp.getQueryCoverage()) + "%");
    } else {
      _qCoverage.setText("-");
    }
    if (hsp.getHitCoverage() != 0) {
      _hCoverage.setText(PCT_FORMATTER.format(hsp.getHitCoverage()) + "%");
    } else {
      _hCoverage.setText("-");
    }
    from = hsp.getQuery().getFrom();
    to = hsp.getQuery().getTo();
    sVal = SVMessages.getString("BlastSeqAlignViewer.32") + POS_FORMATTER.format(from)
        + SVMessages.getString("BlastSeqAlignViewer.33") + POS_FORMATTER.format(to);
    frame = hsp.getQuery().getFrame();
    switch (bType) {
    case SROutput.BLASTN:
      sVal = sVal + SVMessages.getString("BlastSeqAlignViewer.34") + (from > to ? "-" : "+") + "'";
      break;
    case SROutput.TBLASTX:
    case SROutput.BLASTX:
      sVal = sVal + SVMessages.getString("BlastSeqAlignViewer.38") + (frame > 0 ? "+" : "") + String.valueOf(frame)
          + "'";
      break;
    }
    _qFromTo.setText(sVal);
    _qFromTo.moveCaretPosition(0);
    from = hsp.getHit().getFrom();
    to = hsp.getHit().getTo();
    sVal = SVMessages.getString("BlastSeqAlignViewer.42") + POS_FORMATTER.format(from)
        + SVMessages.getString("BlastSeqAlignViewer.43") + POS_FORMATTER.format(to);
    frame = hsp.getHit().getFrame();
    switch (bType) {
    case SROutput.BLASTN:
      sVal = sVal + SVMessages.getString("BlastSeqAlignViewer.44") + (from > to ? "-" : "+") + "'";
      break;
    case SROutput.TBLASTX:
    case SROutput.TBLASTN:
      sVal = sVal + SVMessages.getString("BlastSeqAlignViewer.48") + (frame > 0 ? "+" : "") + String.valueOf(frame)
          + "'";
      break;
    }
    _hitAccession.setText(hit.getHitAccession() + ":");
    _hFromTo.setText(sVal);
    _hFromTo.moveCaretPosition(0);
    _aliLength.setText(POS_FORMATTER.format(hsp.getScores().getAlignLen()));
    _hspNum.setText(hsp.getHspNum() + "/" + hit.countHsp());
    if (hit.countHsp() != 1) {
      if (hsp.getHspNum() == 1)
        _prevHsp.setEnabled(false);
      else
        _prevHsp.setEnabled(true);
      if (hsp.getHspNum() == hit.countHsp())
        _nextHsp.setEnabled(false);
      else
        _nextHsp.setEnabled(true);
    } else {
      _prevHsp.setEnabled(false);
      _nextHsp.setEnabled(false);
    }
    /*
     * _fetchFeatures.setEnabled(true); _displayEntry.setEnabled(true);
     * _saveFasta.setEnabled(true); _cBoardFasta.setEnabled(true);
     * _submit.setEnabled(true);
     */
  }

  /**
   * Utility method to create a JTextField.
   */
  private JTextField createTextField() {
    JTextField tf;

    tf = new JTextField();
    tf.setEditable(false);
    tf.setBorder(null);
    tf.setOpaque(true);
    tf.setBackground(this.getBackground());
    return tf;
  }

  private JTextArea createTxtArea() {
    JTextArea jta;

    jta = new JTextArea();
    jta.setEditable(false);
    jta.setOpaque(true);
    jta.setBackground(this.getBackground());
    jta.setWrapStyleWord(true);
    jta.setLineWrap(true);
    jta.setBorder(null);
    jta.setRows(4);
    return jta;
  }

  private JPanel createDescPanel() {
    JPanel pnl = new JPanel(new BorderLayout());

    _description = createTxtArea();
    pnl.add(
        new JScrollPane(_description, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
        BorderLayout.CENTER);
    return pnl;
  }

  /**
   * Utility method to create a JButton.
   */
  private JButton createBtn(String label) {
    JButton btn;

    btn = new JButton(label);
    return btn;
  }

  private Component createStatPanel() {
    DefaultFormBuilder builder;
    FormLayout layout;

    _score = createTextField();
    _scoreBits = createTextField();
    _evalue = createTextField();
    _identity = createTextField();
    _positive = createTextField();
    _gaps = createTextField();
    _qCoverage = createTextField();
    _hCoverage = createTextField();
    layout = new FormLayout("right:max(20dlu;p), 2dlu, 30dlu, 10dlu, " + "right:max(20dlu;p), 2dlu, 30dlu", "");
    builder = new DefaultFormBuilder(layout);
    builder.setDefaultDialogBorder();
    // builder.appendSeparator("Statistics");
    builder.append(SVMessages.getString("BlastSeqAlignViewer.56"), _scoreBits);
    builder.append(SVMessages.getString("BlastSeqAlignViewer.57"), _identity);
    builder.nextLine();
    builder.append(new JLabel(""));
    builder.append(_score);
    builder.append(SVMessages.getString("BlastSeqAlignViewer.59"), _positive);
    builder.nextLine();
    builder.append(SVMessages.getString("BlastSeqAlignViewer.60"), _evalue);
    builder.append(SVMessages.getString("BlastSeqAlignViewer.61"), _gaps);
    builder.nextLine();
    builder.append("Q. Coverage:", _qCoverage);
    builder.append("H. Coverage:", _hCoverage);
    return builder.getContainer();
  }

  private Component createAlignPanel() {
    DefaultFormBuilder builder;
    FormLayout layout;

    _qFromTo = createTextField();
    _hFromTo = createTextField();
    _aliLength = createTextField();
    _hitAccession = createTextField();
    layout = new FormLayout("right:max(20dlu;p), 2dlu, 140dlu", "");
    builder = new DefaultFormBuilder(layout);
    builder.setDefaultDialogBorder();
    // builder.appendSeparator("Alignment");
    builder.append(SVMessages.getString("BlastSeqAlignViewer.64"));
    builder.append(_qFromTo);
    builder.nextLine();
    builder.append(_hitAccession);
    builder.append(_hFromTo);
    builder.nextLine();
    builder.append(SVMessages.getString("BlastSeqAlignViewer.66"));
    builder.append(_aliLength);
    return builder.getContainer();
  }

  private Component createHSPSelectorPanel() {
    DefaultFormBuilder builder;
    FormLayout layout;
    HspActionListener hal;
    JPanel pnl;

    _hspNum = createTextField();
    _hspNum.setHorizontalAlignment(JTextField.CENTER);
    _prevHsp = createBtn("<");
    _nextHsp = createBtn(">");

    hal = new HspActionListener();
    _prevHsp.addActionListener(hal);
    _nextHsp.addActionListener(hal);
    _prevHsp.setEnabled(false);
    _nextHsp.setEnabled(false);

    layout = new FormLayout("30dlu, 2dlu, 30dlu, 2dlu, 30dlu, 2dlu, 30dlu", "");
    builder = new DefaultFormBuilder(layout);
    builder.append("HSP:");
    builder.append(_prevHsp);
    builder.append(_hspNum);
    builder.append(_nextHsp);

    pnl = new JPanel(new BorderLayout());
    pnl.add(builder.getContainer(), BorderLayout.WEST);
    pnl.setBorder(BorderFactory.createEmptyBorder(3, 5, 1, 1));
    return pnl;
  }

  /**
   * This class handles actions coming from the two buttons allowing to display
   * the various HSP of a same Hit.
   */
  private class HspActionListener implements ActionListener {
    private int getCurHsp() {
      String txt;

      txt = _hspNum.getText().substring(0, _hspNum.getText().indexOf('/'));
      return Integer.valueOf(txt).intValue();
    }

    public void actionPerformed(ActionEvent event) {
      int newHsp = 0;

      if (_curHit == null)
        return;
      if (event.getSource() == _prevHsp) {
        newHsp = getCurHsp() - 1;
      } else {
        newHsp = getCurHsp() + 1;
      }
      if (newHsp != 0) {
        ArrayList<BlastHitHSP> hits = new ArrayList<BlastHitHSP>();
        hits.add(new BlastHitHspImplem((SRHit) _curHit.getHit(), _curHit.getBlastCLient(), newHsp,
            _curHit.getQuerySize(), _curHit.getBlastType()));
        _updateSupport.fireHitChange(new BlastHitListEvent(HspValuesPanel.this, hits, BlastHitListEvent.HIT_CHANGED));
      }
    }
  }
}
