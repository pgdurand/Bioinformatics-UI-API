/* Copyright (C) 2006-2016 Patrick G. Durand
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
package bzh.plealog.bioinfo.ui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;

/**
 * This is simple Dialog displaying up to two progress bars.
 *
 * @author Patrick G. Durand
 * @since 2003
 */
public class ProgressTinyDialog extends JDialog implements ExportMonitor {

  private static final long serialVersionUID = -6284362150177939612L;

  // if this dialog is visible, it will be visible at least MIN_MILLIS_DISPLAY
  // mmiliseconds
  private final static int MIN_MILLIS_DISPLAY = 1000;

  protected JProgressBar _progressBar;
  protected JLabel _msg;
  protected JProgressBar _progressBar2;
  protected JLabel _msg2;
  private boolean _stopProcessing;

  // To avoid EDT freezes on MAC OS, the dialog must not be visible if the
  // monitored process is very short
  private long startVisibleTime = 0;

  // to avoid a call of setVisible(true) after dispose()
  private boolean isDisposed = false;

  public ProgressTinyDialog(String header, int max, boolean showProgressBar, boolean showStopAction,
      boolean showProgressBar2) {
    super((Frame) EZEnvironment.getParentFrame(), true);
    buildUI(header, max, showProgressBar, showStopAction, showProgressBar2);
  }

  private void buildUI(String header, int max, boolean showProgressBar, boolean showStopAction,
      boolean showProgressBar2) {
    Dimension screen, frame;
    JPanel panel;

    panel = new JPanel(new BorderLayout());
    panel.add(createProgressPanel(header, max, showProgressBar, showStopAction, showProgressBar2), BorderLayout.CENTER);
    panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.setTitle(EZApplicationBranding.getAppName());
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(panel, BorderLayout.CENTER);
    this.pack();

    screen = getToolkit().getScreenSize();
    frame = this.getSize();
    setLocation(screen.width / 2 - frame.width / 2, screen.height / 2 - frame.height / 2);
    super.toFront();
  }

  private Component createProgressPanel(String header, int max, boolean showProgressBar, boolean showStopAction,
      boolean showProgressBar2) {
    DefaultFormBuilder builder;
    FormLayout layout;
    JPanel pnl;
    JButton btn;

    layout = new FormLayout("150dlu");
    builder = new DefaultFormBuilder(layout);
    builder.setDefaultDialogBorder();
    builder.appendSeparator(header);
    if (showProgressBar) {
      _progressBar = new JProgressBar(0, max);
      if (!showStopAction) {
        builder.append(_progressBar);
      } else {
        pnl = new JPanel(new BorderLayout());
        pnl.add(_progressBar, BorderLayout.CENTER);
        btn = new JButton(EZEnvironment.getImageIcon("stopScheduler.png"));
        btn.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            _stopProcessing = true;
          }
        });
        btn.setBorder(null);
        pnl.add(btn, BorderLayout.EAST);
        builder.append(pnl);
      }
    }
    _msg = new JLabel(" ");
    _msg.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
    if (_progressBar != null)
      builder.nextLine();
    builder.append(_msg);
    if (showProgressBar2) {
      _progressBar2 = new JProgressBar(0, max);
      builder.nextLine();
      builder.append(_progressBar2);
      _msg2 = new JLabel("");
      _msg2.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
      builder.nextLine();
      builder.append(_msg2);
      builder.nextLine();
      builder.append(new JLabel(" "));
    }
    return builder.getContainer();
  }

  /**
   * If this method returns true then the caller should stop processing.
   */
  @Override
  public boolean stopProcessing() {
    return _stopProcessing;
  }

  @Override
  public void addToProgress(int delta) {
    if (_progressBar != null) {
      int val = _progressBar.getValue() + delta;
      if (val <= _progressBar.getMaximum()) {
        _progressBar.setValue(val);
      }
    }
  }

  @Override
  public void setMaxSteps(int max) {
    if (_progressBar == null)
      return;
    _progressBar.setValue(0);
    _progressBar.setMaximum(max);
  }

  /**
   * Sets a message. This one appears just below the progressbar.
   */
  @Override
  public void setMessage(String msg) {
    _msg.setText(msg);
  }

  @Override
  public String getMessage() {
    return _msg.getText();
  }

  @Override
  public void addToProgress2(int delta) {
    if (_progressBar2 != null) {
      int val = _progressBar2.getValue() + delta;
      if (val <= _progressBar2.getMaximum()) {
        _progressBar2.setValue(val);
      }
    }
  }

  @Override
  public void setMaxSteps2(int max) {
    if (_progressBar2 == null)
      return;
    _progressBar2.setValue(0);
    _progressBar2.setMaximum(max);
  }

  @Override
  public void setMessage2(String msg) {
    if (_msg2 != null)
      _msg2.setText(msg);
  }

  public String getCurrentMessage2() {
    if (_msg2 != null)
      return _msg2.getText();
    else
      return "";
  }

  @Override
  public String getMessage2() {
    return this.getCurrentMessage2();
  }

  @Override
  public void setVisible(boolean visible) {
    if (!isVisible() && visible && !this.isDisposed) {
      startVisibleTime = System.currentTimeMillis();
      super.setVisible(true);
    } else if (isVisible() && !visible) {
      // wait for minimum 1s to be invisible to avoid EDT freeze
      long dif = System.currentTimeMillis() - startVisibleTime;
      if (dif < MIN_MILLIS_DISPLAY) {
        try {
          Thread.sleep(MIN_MILLIS_DISPLAY - dif);
        } catch (InterruptedException e1) {
        }
      }
      super.setVisible(false);
    }
  }

  @Override
  public void dispose() {
    if (!this.isDisposed) {
      this.isDisposed = true;
      setVisible(false);
      super.dispose();
    }
  }

  public JProgressBar getProgressBar() {
    return _progressBar;
  }

  public JProgressBar getProgressBar2() {
    return _progressBar2;
  }

}
