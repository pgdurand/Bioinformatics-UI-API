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
package bzh.plealog.bioinfo.ui.blast.nav;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.ui.common.ResizableComboboxPopupMenuListener;

import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRIteration;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.searchresult.SRParameters;
import bzh.plealog.bioinfo.api.data.searchresult.SRRequestInfo;
import bzh.plealog.bioinfo.ui.blast.core.BlastEntry;
import bzh.plealog.bioinfo.ui.blast.core.BlastIteration;
import bzh.plealog.bioinfo.ui.blast.event.BlastIterationListEvent;
import bzh.plealog.bioinfo.ui.blast.event.BlastIterationListListener;
import bzh.plealog.bioinfo.ui.blast.event.BlastIterationListSupport;
import bzh.plealog.bioinfo.ui.resources.SVMessages;

/**
 * This class enables to navigate through Blast result content. It relies on 
 * a Iteration selector that in turns displays hits in an appropriate table.
 * In addition, it can show the summary information of a Blast result. This summary
 * information concerns the blast program name, the database, the query, etc.
 * 
 * @author Patrick G. Durand
 */
public class BlastNavigator extends JPanel {
  private static final long serialVersionUID = -8990734742504547760L;
  private JTextField _hits;
  private JTextField _hsps;
  private JComboBox<BlastIteration> _iterations;
  private JButton _btnInfo;
  private BlastIterationListSupport _iterSelectionSupport;
  private BlastEntry _entry;
  private int _iterNum;
  private int _nHits;
  private int _nHsps;

  /**
   * Default constructor.
   */
  public BlastNavigator() {
    _iterSelectionSupport = new BlastIterationListSupport();
    buildLightPanel();
    clearContent();
  }

  private void buildLightPanel() {
    JPanel pnl;
    pnl = new JPanel(new BorderLayout());
    pnl.setOpaque(false);
    pnl.add(createPanelStatisticsLight(), BorderLayout.NORTH);
    this.setLayout(new BorderLayout());
    this.add(pnl, BorderLayout.WEST);
    this.setOpaque(false);
  }

  /**
   * Utility method to create a JTextField.
   */
  private JTextField createTextField() {
    JTextField tf;

    tf = new JTextField();
    tf.setEditable(false);
    tf.setBorder(null);
    tf.setOpaque(false);
    return tf;
  }

  /**
   * Utility method to create a JComboBox.
   */
  private void createCombo() {
    _iterations = new JComboBox<>();
    _iterations.addActionListener(new IterationComboListener());
    _iterations.addPopupMenuListener(new ResizableComboboxPopupMenuListener());
  }

  /**
   * Create the panel displaying the Blast statistics information.
   */
  private Component createPanelStatisticsLight() {
    JPanel pnl = new JPanel();
    JLabel lbl;

    createCombo();
    _hits = createTextField();
    _hsps = createTextField();

    pnl.setOpaque(false);
    FontMetrics fm = _hits.getFontMetrics(_hits.getFont());
    Dimension dim = new Dimension(fm.stringWidth("XXXXX"), fm.getHeight());
    _hits.setPreferredSize(dim);
    _hsps.setPreferredSize(dim);
    dim = _iterations.getPreferredSize();
    dim.width = 5 * dim.width + (dim.width / 3);
    _iterations.setPreferredSize(dim);
    lbl = new JLabel(SVMessages.getString("BlastSummary.18"));
    lbl.setOpaque(false);
    pnl.add(lbl);
    pnl.add(_iterations);
    lbl = new JLabel("...");
    lbl.setForeground(lbl.getBackground());
    lbl.setOpaque(false);
    pnl.add(lbl);
    lbl = new JLabel(SVMessages.getString("BlastSummary.19"));
    lbl.setOpaque(false);
    pnl.add(lbl);
    pnl.add(_hits);
    lbl = new JLabel(SVMessages.getString("BlastSummary.20"));
    lbl.setOpaque(false);
    pnl.add(lbl);
    pnl.add(_hsps);
    _btnInfo = new JButton(EZEnvironment.getImageIcon("about_result.png"));
    _btnInfo.setBorder(null);
    pnl.add(_btnInfo);
    _btnInfo.addActionListener(new ButtonInfoActionListener());
    return pnl;
  }

  /**
   * Reset the content of this BlastSummary.
   */
  public void clearContent() {
    _iterations.removeAllItems();
    _hits.setText("");
    _hsps.setText("");
    _iterations.setEnabled(false);
    _btnInfo.setEnabled(false);
    /*
     * _entry = null; _iterNum = _nHits = _nHsps = 0;
     */
  }

  /**
   * Count the total number of Hits and HSPs contained in the Blast file
   * displayed in this BlastSummary.
   * 
   * @return an array of two integers. First integer gives the total number of
   *         Hits, second gives the total number of HSPs.
   */
  private int[] countHitHsp(SROutput bo, int iterNum) {
    SRIteration bIter;
    SRHit hit;
    int[] vals = new int[2];
    int i;

    vals[0] = 0;// Hit count
    vals[1] = 0;// Hsps count
    if (iterNum >= 0 && iterNum < bo.countIteration()) {
      bIter = bo.getIteration(iterNum);
      vals[0] = bIter.countHit();
      for (i = 0; i < vals[0]; i++) {
        hit = bIter.getHit(i);
        vals[1] += hit.countHsp();
      }
    }

    return vals;
  }

  /**
   * Set the content of this BlastSummary.
   */
  public void setContent(BlastEntry entry) {
    _entry = entry;
    if (entry == null) {
      clearContent();
      return;
    }
    SROutput bo;
    int iVal, size;
    int[] count;

    _iterNum = 0;
    bo = entry.getResult();
    _iterations.removeAllItems();
    size = bo.countIteration();
    for (iVal = 0; iVal < size; iVal++) {
      _iterations.addItem(new BlastIteration(entry, iVal));
    }
    count = countHitHsp(bo, 0);
    _nHits = iVal = count[0];
    _hits.setText(iVal >= 0 ? String.valueOf(iVal) : "?");
    _nHsps = iVal = count[1];
    _hsps.setText(iVal >= 0 ? String.valueOf(iVal) : "?");
    _iterations.setEnabled(size != 1);
    _btnInfo.setEnabled(true);
  }

  private class IterationComboListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      BlastIteration iter;
      SROutput bo;
      int[] count;
      int iVal;

      // iter = (BlastIteration) _iterations.getSelectedItem();
      _iterNum = _iterations.getSelectedIndex();
      if (_iterNum != -1) {
        iter = _iterations.getItemAt(_iterNum);
        bo = iter.getEntry().getResult();
        count = countHitHsp(bo, iter.getIterNum());
        _nHits = iVal = count[0];
        _hits.setText(iVal >= 0 ? String.valueOf(iVal) : "?");
        _nHsps = iVal = count[1];
        _hsps.setText(iVal >= 0 ? String.valueOf(iVal) : "?");
      } else {
        iter = null;
        clearContent();
      }
      _iterSelectionSupport.fireHitChange(
          new BlastIterationListEvent(BlastNavigator.this, iter, BlastIterationListEvent.ITERATION_CHANGED));
    }
  }

  public void addIterationListener(BlastIterationListListener listener) {
    _iterSelectionSupport.addBlastIterationListListener(listener);
  }

  public void removeIterationListener(BlastIterationListListener listener) {
    _iterSelectionSupport.removeBlastIterationListListener(listener);
  }

  private class ButtonInfoActionListener implements ActionListener {
    public void actionPerformed(ActionEvent event) {
      if (_entry == null)
        return;
      JOptionPane.showOptionDialog(EZEnvironment.getParentFrame(), new BlastInfoPanel(_entry, _iterNum, _nHits, _nHsps),
          SVMessages.getString("BlastSummary.21"), JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
          null, null);
    }
  }

  private class BlastInfoPanel extends JPanel {
    private static final long serialVersionUID = 7238322137385088381L;
    private JTextField program_;
    private JTextField db_;
    private JTextField query_;
    private JTextField queryLen_;
    private JTextField matrix_;
    private JTextField gapOpen_;
    private JTextField gapExtend_;
    private JTextField expect_;
    private JTextField hits_;
    private JTextField hsps_;
    private JTextField iNum_;

    public BlastInfoPanel(BlastEntry entry, int iterNum, int nHits, int nHsps) {
      JTabbedPane jtp;

      jtp = new JTabbedPane();
      jtp.setFocusable(false);
      jtp.add(SVMessages.getString("BlastSummary.3"), createPanelProgram());
      jtp.add(SVMessages.getString("BlastSummary.10"), createPanelParameters());
      jtp.add(SVMessages.getString("BlastSummary.17"), createPanelStatistics());

      this.setLayout(new BorderLayout());
      this.add(jtp, BorderLayout.CENTER);
      setContent(entry, iterNum, nHits, nHsps);
    }

    /**
     * Utility method to create a JLabel.
     */
    private JLabel createLabel(String txt) {
      JLabel lbl;

      lbl = new JLabel(txt);
      /*
       * lbl.setBackground(Color.LIGHT_GRAY); lbl.setOpaque(true);
       */
      return lbl;
    }

    /**
     * Creates the panel displaying the Blast program information.
     */
    private Component createPanelProgram() {
      DefaultFormBuilder builder;
      FormLayout layout;

      program_ = createTextField();
      db_ = createTextField();
      query_ = createTextField();
      queryLen_ = createTextField();
      layout = new FormLayout("right:max(50dlu;p), 4dlu, 150dlu", "");
      builder = new DefaultFormBuilder(layout);
      builder.append(createLabel(SVMessages.getString("BlastSummary.4")), program_);
      builder.nextLine();
      builder.append(createLabel(SVMessages.getString("BlastSummary.5")), db_);
      builder.nextLine();
      builder.append(createLabel(SVMessages.getString("BlastSummary.6")), query_);
      builder.nextLine();
      builder.append(createLabel(SVMessages.getString("BlastSummary.7")), queryLen_);
      return builder.getContainer();
    }

    /**
     * Creates the panel displaying the Blast parameters information.
     */
    private Component createPanelParameters() {
      DefaultFormBuilder builder;
      FormLayout layout;

      matrix_ = createTextField();
      expect_ = createTextField();
      gapOpen_ = createTextField();
      gapExtend_ = createTextField();
      layout = new FormLayout("right:max(50dlu;p), 4dlu, 50dlu", "");
      builder = new DefaultFormBuilder(layout);
      builder.append(SVMessages.getString("BlastSummary.11"), matrix_);
      builder.nextLine();
      builder.append(SVMessages.getString("BlastSummary.12"), expect_);
      builder.nextLine();
      builder.append(SVMessages.getString("BlastSummary.13"), gapOpen_);
      builder.nextLine();
      builder.append(SVMessages.getString("BlastSummary.14"), gapExtend_);
      return builder.getContainer();
    }

    /**
     * Creates the panel displaying the Blast statistics information.
     */
    private Component createPanelStatistics() {
      DefaultFormBuilder builder;
      FormLayout layout;

      hits_ = createTextField();
      hsps_ = createTextField();
      iNum_ = createTextField();
      layout = new FormLayout("right:max(50dlu;p), 4dlu, 50dlu", "");
      builder = new DefaultFormBuilder(layout);
      builder.append(SVMessages.getString("BlastSummary.18"), iNum_);
      builder.nextLine();
      builder.append(SVMessages.getString("BlastSummary.19"), hits_);
      builder.nextLine();
      builder.append(SVMessages.getString("BlastSummary.20"), hsps_);
      return builder.getContainer();
    }

    private void setQueryContent(SRRequestInfo bri, SRIteration bi) {
      String val;
      int qLength;
      Object obj;

      val = bi.getIterationQueryDesc();
      if (val == null) {
        obj = bri.getValue(SRRequestInfo.QUERY_DEF_DESCRIPTOR_KEY);
        query_.setText(obj != null ? obj.toString() : "?");
      } else {
        query_.setText(val);
      }
      query_.moveCaretPosition(0);
      qLength = bi.getIterationQueryLength();
      if (qLength == 0) {
        obj = bri.getValue(SRRequestInfo.QUERY_LENGTH_DESCRIPTOR_KEY);
        if (obj != null) {
          queryLen_.setText(obj.toString());
        } else {
          queryLen_.setText("?");
        }
      } else {
        queryLen_.setText(String.valueOf(qLength));
      }

    }

    /**
     * Sets the content of this BlastSummary.
     */
    private void setContent(BlastEntry entry, int iterNum, int nHits, int nHsps) {
      if (entry == null) {
        clearContent();
        return;
      }
      SROutput bo;
      SRRequestInfo bri;
      String val;
      int pos;
      Object obj;

      bo = entry.getResult();
      bri = bo.getRequestInfo();
      // _fName.setText(entry.getAbsolutePath());
      obj = bri.getValue(SRRequestInfo.PRGM_VERSION_DESCRIPTOR_KEY);
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
      program_.setText(val != null ? val : "?");
      obj = bri.getValue(SRRequestInfo.DATABASE_DESCRIPTOR_KEY);
      db_.setText(obj != null ? obj.toString() : "?");
      db_.moveCaretPosition(0);
      obj = bri.getValue(SRRequestInfo.QUERY_DEF_DESCRIPTOR_KEY);
      query_.setText(obj != null ? obj.toString() : "?");
      query_.moveCaretPosition(0);
      obj = bri.getValue(SRRequestInfo.QUERY_LENGTH_DESCRIPTOR_KEY);
      if (obj != null) {
        queryLen_.setText(obj.toString());
      } else {
        queryLen_.setText("?");
      }
      obj = bo.getBlastOutputParam().getValue(SRParameters.MATRIX_DESCRIPTOR_KEY);
      matrix_.setText(obj != null ? obj.toString() : "?");
      obj = bo.getBlastOutputParam().getValue(SRParameters.GAPOPEN_DESCRIPTOR_KEY);
      gapOpen_.setText(obj != null ? obj.toString() : "?");
      obj = bo.getBlastOutputParam().getValue(SRParameters.GAPEXTEND_DESCRIPTOR_KEY);
      gapExtend_.setText(obj != null ? obj.toString() : "?");
      obj = bo.getBlastOutputParam().getValue(SRParameters.EXPECT_DESCRIPTOR_KEY);
      expect_.setText(obj != null ? obj.toString() : "?");
      hits_.setText(nHits >= 0 ? String.valueOf(nHits) : "?");
      hsps_.setText(nHits >= 0 ? String.valueOf(nHsps) : "?");
      if (entry.getResult().getBlastType() == SROutput.PSIBLAST) {
        iNum_.setText("Iteration " + String.valueOf(iterNum + 1));
      } else {
        iNum_.setText("Sequence " + String.valueOf(iterNum + 1));
      }
      setQueryContent(bri, bo.getIteration(iterNum));
    }

  }
}
