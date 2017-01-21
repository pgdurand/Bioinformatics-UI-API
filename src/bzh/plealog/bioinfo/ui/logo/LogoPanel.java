/* Copyright (C) 2003-2017 Patrick G. Durand
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
package bzh.plealog.bioinfo.ui.logo;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class LogoPanel extends JPanel {
  private static final long serialVersionUID = -1971120756526434031L;
  private LogoCell logoCell_;
  private boolean  horizontal_;
  private boolean  outlineLetter_;
  private boolean  fillAllPanel_ = true;
  private boolean  useAntiAlias_ = true;
  private Type     type_;

  public static enum Type {
    LogoBar, LogoLetter
  };

  public LogoPanel() {
    super();
    setFont(Font.decode("Serif-10"));
    setBackground(Color.WHITE);
    setOpaque(true);
    type_ = Type.LogoBar;
  }

  public void setLogoCell(LogoCell lc) {
    logoCell_ = lc;
  }

  public void setType(LogoPanel.Type type) {
    type_ = type;
  }

  /**
   * In the current implementation, when using horizontal layout, only logo bar
   * type is available.
   */
  public void setHorizontal(boolean horz) {
    horizontal_ = horz;
  }

  public void setFillAllPanel(boolean fillAll) {
    fillAllPanel_ = fillAll;
  }

  public void setOutlineLetter(boolean val) {
    outlineLetter_ = val;
  }

  public void setUseAntiAlias(boolean val) {
    useAntiAlias_ = val;
  }

  public boolean isHorizontal() {
    return horizontal_;
  }

  public boolean isFillingAllPanel() {
    return fillAllPanel_;
  }

  public boolean isOutliningLetter() {
    return outlineLetter_;
  }

  public boolean isUsingAntiAlias() {
    return useAntiAlias_;
  }

  public LogoCell getColgoCell() {
    return logoCell_;
  }

  public void paintComponentVertically(Graphics g) {
    FontMetrics fm;
    Font fnt;
    int i, nLetters, height, width, decal, cHeight, xDecal;
    double mxBits, lHeight, totBits, baseY, v;
    Graphics2D g2;
    LogoLetter letter;
    GlyphVector gv;
    Shape outline;
    Rectangle2D oBounds;
    AffineTransform at;
    Color c;
    Font oldFnt;

    g2 = (Graphics2D) g;
    fnt = this.getFont();
    fm = this.getFontMetrics(fnt);
    cHeight = fm.getAscent();
    height = this.getBounds().height;
    width = (fillAllPanel_ ? this.getBounds().width : fm.getMaxAdvance());
    nLetters = logoCell_.size();
    mxBits = logoCell_.getMaxValue();

    totBits = 0;
    for (i = 0; i < nLetters; i++) {
      letter = logoCell_.getLogoLetter(i);
      totBits += letter.getValue();
    }
    decal = (int) ((double) height / mxBits * (mxBits - totBits));
    oldFnt = g2.getFont();
    g2.setFont(fnt);
    if (type_ == Type.LogoLetter) {
      height -= 2;
      baseY = height;
      width--;
      for (i = nLetters - 1; i >= 0; i--) {
        letter = logoCell_.getLogoLetter(i);
        gv = fnt.createGlyphVector(g2.getFontRenderContext(),
            letter.getSymbol());
        outline = gv.getOutline();
        oBounds = outline.getBounds2D();
        v = ((double) height / mxBits * letter.getValue());
        at = new AffineTransform();
        at.setToTranslation(0, baseY - v);
        at.scale(width / oBounds.getWidth(), v / oBounds.getHeight());
        at.translate(-oBounds.getMinX(), -oBounds.getMinY());
        outline = at.createTransformedShape(outline);
        c = letter.getBarColor();
        if (c.equals(this.getBackground())) {
          c = this.getForeground();
        }
        g2.setColor(c);
        g2.fill(outline);
        if (outlineLetter_) {
          g2.setColor(Color.black);
          g2.draw(outline);
        }
        baseY -= v;
      }
    } else {
      for (i = 0; i < nLetters; i++) {
        letter = logoCell_.getLogoLetter(i);
        lHeight = ((double) height / mxBits * letter.getValue());
        g2.setColor(letter.getBarColor());
        g.fillRect(0, decal, width - 1, (int) lHeight);
        g2.setColor(Color.BLACK);
        g.drawRect(0, decal, width - 1, (int) lHeight);
        decal += (int) lHeight;
        if ((int) lHeight >= cHeight) {
          g2.setColor(letter.getSymbFgColor());
          if (fillAllPanel_)
            xDecal = (width - fm.stringWidth(letter.getSymbol())) / 2;
          else
            xDecal = fm.stringWidth(letter.getSymbol()) / 4;
          g2.drawString(letter.getSymbol(), xDecal, decal - 1);
        }
        decal++;
      }
    }
    // restore DC
    g2.setFont(oldFnt);
    g2.setColor(Color.BLACK);
  }

  public void paintComponentHorizontally(Graphics g) {
    FontMetrics fm;
    Font fnt;
    int i, adv, nLetters, width, height, cWidth, cHeight, xDecal, lHeight, y;
    double mxBits;
    Graphics2D g2;
    LogoLetter letter;

    g2 = (Graphics2D) g;
    width = this.getSize().width;
    fnt = this.getFont();
    fm = this.getFontMetrics(fnt);
    cHeight = fm.getAscent();
    adv = fm.getMaxAdvance();
    height = this.getSize().height;
    nLetters = logoCell_.size();
    mxBits = logoCell_.getMaxValue();
    cWidth = (fillAllPanel_ ? width / nLetters : adv);

    g2.setColor(Color.RED);
    xDecal = 0;
    for (i = 0; i < nLetters; i++) {
      letter = logoCell_.getLogoLetter(i);
      lHeight = (int) ((double) height / mxBits * letter.getValue());
      y = height - cHeight - lHeight;
      g2.setColor(letter.getBarColor());
      g2.fillRect(xDecal, y, cWidth, lHeight);
      g2.setColor(Color.BLACK);
      g2.drawRect(xDecal, y, cWidth, lHeight);
      g2.setColor(letter.getSymbBkColor());
      g2.fillRect(xDecal, height - cHeight + 1, cWidth + 1, cHeight - 1);
      if (cWidth >= adv) {
        g2.setColor(letter.getSymbFgColor());
        g2.drawString(letter.getSymbol(), xDecal + (cWidth / 4), height - 1);
      }
      xDecal += cWidth;
    }
    g2.setColor(Color.BLACK);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (logoCell_ == null)
      return;
    if (useAntiAlias_) {
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
    }
    if (horizontal_)
      paintComponentHorizontally(g);
    else
      paintComponentVertically(g);
  }
}