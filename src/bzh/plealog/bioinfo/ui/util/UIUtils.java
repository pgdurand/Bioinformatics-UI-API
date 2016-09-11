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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.plealog.genericapp.api.EZEnvironment;

/**
 * This class contains some utility methods.
 * 
 * @author Patrick G. Durand
 */
public class UIUtils {
  private static final String CLIPPING = "...";

  /**
   * Clip a text.
   * 
   * @param c the component used to clip the text. Width and FontMetrics from 
   * that component will be used for that purpose.
   * @param val the text to clip.
   * @param borderWidth width of the component border if any.
   * 
   * @return a clipped text terminating with three dots if the text does not
   * fit within the component. Otherwise, the text remains unchanged.
   */
  public static String clipText(JComponent c, String val, int borderWidth){
    FontMetrics fm;
    String      str;
    Insets      insets;
    int         i, size, width, totWidth;

    fm = c.getFontMetrics(c.getFont());
    insets = c.getInsets();
    width = c.getWidth() - (insets.left + insets.right) - 2*borderWidth;
    totWidth = fm.stringWidth(CLIPPING) + 2*borderWidth;
    size=val.length();
    for(i=0;i<size;i++){
      totWidth += fm.charWidth(val.charAt(i));
      if (totWidth > width) {
        break;
      }
    }
    if (i<size){
      str = val.substring(0, i)+CLIPPING;
    }
    else{
      str=val;
    }
    return str;
  }

  /**
   * Clip a text.
   * 
   * @param fm the FontMetrics used to compute the clipped text.
   * @param val the text to clip.
   * @param width width of the component where the string will be displayed.
   * 
   * @return a clipped text terminating with three dots if the text does not
   * fit within the component. Otherwise, the text remains unchanged.
   */
  public static String clipText(FontMetrics fm, String val, int width){
    String      str;
    int         i, size, totWidth;

    totWidth = fm.stringWidth(CLIPPING);
    size=val.length();
    for(i=0;i<size;i++){
      totWidth += fm.charWidth(val.charAt(i));
      if (totWidth > width) {
        break;
      }
    }
    if (i<size){
      str = val.substring(0, i)+CLIPPING;
    }
    else{
      str=val;
    }
    return str;
  }
  /**
   * Clip a text.
   * 
   * @param c the component used to clip the text. Width from 
   * that component will be used for that purpose.
   * @param fnt the Font to use to evaluate whether or not the text fits within
   * the component. If null, Font is retrieved from the component.
   * @param val the text to clip.
   * @param xFrom starting x position within the component
   * @param xTo ending position within the component
   * 
   * @return a clipped text terminating with three dots if the text does not
   * fit within xFrom and xTo. Otherwise, the text remains unchanged.
   */
  public static String clipText(JComponent c, Font fnt, String val, int xFrom, int xTo){
    FontMetrics fm;
    String      str;
    int         i, size, totWidth;

    fm = c.getFontMetrics(fnt!=null ? fnt : c.getFont());
    totWidth = xFrom;
    size=val.length();
    for(i=0;i<size;i++){
      totWidth += fm.charWidth(val.charAt(i));
      if (totWidth > xTo) {
        break;
      }
    }
    if (i<size){
      str = val.substring(0, i)+CLIPPING;
    }
    else{
      str=val;
    }
    return str;
  }
  /**
   * Centers the component on the screen. 
   */
  public static void centerOnScreen(Component compo){
    Dimension screenSize = compo.getToolkit().getScreenSize();
    Dimension dlgSize = compo.getSize();

    compo.setLocation(screenSize.width/2 - dlgSize.width/2,
        screenSize.height/2 - dlgSize.height/2);
  }
  public static void setCpyAccelerator(Action act){
    KeyStroke stroke=null;
    if (EZEnvironment.getOSType()==EZEnvironment.MAC_OS){
      stroke = KeyStroke.getKeyStroke("meta C");
    }
    else{
      stroke = KeyStroke.getKeyStroke("ctrl C");
    }
    if (stroke!=null)
      act.putValue(Action.ACCELERATOR_KEY, stroke);
  }
  public static void setCtrlAccelerator(Action act, String letter){
    KeyStroke stroke=null;
    if (EZEnvironment.getOSType()==EZEnvironment.MAC_OS){
      stroke = KeyStroke.getKeyStroke("meta "+letter);
    }
    else{
      stroke = KeyStroke.getKeyStroke("ctrl "+letter);
    }
    if (stroke!=null)
      act.putValue(Action.ACCELERATOR_KEY, stroke);
  }
  public static RadioChooserEntry selectLoader(Component parent, String dlgHeader, List<RadioChooserEntry> entries){
    RadioChooserDialog dialog;

    dialog = new RadioChooserDialog(EZEnvironment.getParentFrame(), dlgHeader, entries);
    dialog.showDlg();
    return dialog.getSelectedEntry();
  }
}
