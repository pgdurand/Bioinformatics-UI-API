/* Copyright (C) 2006-2019 Patrick G. Durand
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
 */package bzh.plealog.bioinfo.ui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import com.plealog.genericapp.api.EZEnvironment;

import bzh.plealog.bioinfo.ui.resources.SVMessages;

/**
 * A simple component displaying memory settings.
 * 
 * @author Patrick G. Durand
 */
public class MemoryMeter extends JPanel {

  private static final long serialVersionUID = -8799718265534591438L;
  private JLabel _memory;
  private JMemPanel _memP;
  private JButton _gcCaller;

  public MemoryMeter() {
    Timer timer;
    JPanel pnl;

    // the JLabel is just used to compute the size of the memoryMeter panel
    _memory = new JLabel();
    setMemoryText();
    _memP = new JMemPanel();
    _gcCaller = new JButton(EZEnvironment.getImageIcon("recycle_s.png"));
    _gcCaller.setBorder(null);
    _gcCaller.addActionListener(new GCCaller2());
    this.setBorder(new CompoundBorder(new EmptyBorder(0, 2, 0, 0), new SoftBevelBorder(1)));
    this.addMouseListener(new GCCaller());
    this.setLayout(new BorderLayout());
    pnl = new JPanel(new BorderLayout());
    pnl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    pnl.add(_gcCaller, BorderLayout.CENTER);
    this.add(pnl, BorderLayout.WEST);
    this.add(_memP, BorderLayout.CENTER);
    this.setToolTipText(SVMessages.getString("MemoryMeter.tip"));
    timer = new Timer(5000, new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        String oldVal;
        oldVal = _memory.getText();
        setMemoryText();
        if (_memory.getText().length() > oldVal.length())
          MemoryMeter.this.updateUI();
        else
          MemoryMeter.this.repaint();
      }
    });
    timer.start();
  }

  private void setMemoryText() {
    _memory.setText(getUsedMem() + "/" + getMaxMem());
  }

  protected Dimension getBestSize(Dimension dim1, Dimension dim2) {
    Insets insets;

    insets = this.getInsets();
    dim1.height += (insets.bottom + insets.top);
    dim1.width += (3 * (insets.left + insets.right) + dim2.width);
    return dim1;
  }

  public Dimension getPreferredSize() {
    return getBestSize(_memory.getPreferredSize(), _gcCaller.getPreferredSize());
  }

  public Dimension getMaximumSize() {
    return getBestSize(_memory.getMaximumSize(), _gcCaller.getMaximumSize());
  }

  public Dimension getMinimumSize() {
    return getBestSize(_memory.getMinimumSize(), _gcCaller.getMinimumSize());
  }

  private String getFormattedMem(long value) {
    String mem, unit;
    long size, decal;

    mem = String.valueOf(value);

    size = mem.length();
    if (size >= 12) {
      decal = 1024 * 1024 * 1024;
      unit = "Go";
    } else if (size >= 6) {
      decal = 1024 * 1024;
      unit = "Mo";
    } else if (size >= 5) {
      decal = 1024;
      unit = "Ko";
    } else {
      decal = 1;
      unit = "o";
    }

    decal = value / decal;
    return (new String(decal + unit));
  }

  public String getUsedMem() {
    long totMem, usedMem;

    totMem = Runtime.getRuntime().totalMemory();
    usedMem = totMem - Runtime.getRuntime().freeMemory();
    return getFormattedMem(usedMem);
  }

  public String getMaxMem() {
    long totMem;

    totMem = Runtime.getRuntime().totalMemory();
    return getFormattedMem(totMem);
  }

  public String getFreeMem() {
    long totMem;

    totMem = Runtime.getRuntime().freeMemory();
    return getFormattedMem(totMem);
  }

  private class JMemPanel extends JPanel {
    private static final long serialVersionUID = 4951735617110802096L;

    public JMemPanel() {
      super();
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      FontMetrics fm;
      Insets insets;
      Color clr;
      String str;
      long totMem, usedMem;
      int max;

      totMem = Runtime.getRuntime().totalMemory();
      usedMem = totMem - Runtime.getRuntime().freeMemory();
      insets = this.getInsets();
      max = this.getWidth() - insets.left - insets.right;
      clr = g.getColor();
      g.setColor(Color.orange);

      g.fill3DRect(insets.left, insets.top,
          Math.max(3, (int) (((long) max * usedMem) / totMem)), this.getHeight() - (insets.top + insets.bottom), true);
      g.setColor(EZEnvironment.getSystemTextColor());
      str = _memory.getText();
      fm = _memory.getFontMetrics(_memory.getFont());
      g.setFont(_memory.getFont());
      g.drawString(str, insets.left + (max - fm.stringWidth(str)) / 2, this.getHeight() - (fm.getMaxAscent() / 4));
      g.setColor(clr);
    }
  }

  private void cleanMem() {
    String oldVal;

    System.gc();

    oldVal = _memory.getText();
    setMemoryText();
    if (_memory.getText().length() > oldVal.length())
      MemoryMeter.this.updateUI();
    else
      MemoryMeter.this.repaint();
  }

  private class GCCaller2 implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      cleanMem();
    }
  }

  private class GCCaller extends MouseAdapter {
    public void mousePressed(MouseEvent event) {
      cleanMem();
    }
  }
}
