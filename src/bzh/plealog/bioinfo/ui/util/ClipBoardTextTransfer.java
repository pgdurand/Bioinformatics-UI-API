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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import com.plealog.genericapp.api.log.EZLogger;

/**
 * Implements functions to copy to and to paste from clipboard.
 * 
 * @author Patrick G. Durand
 */
public class ClipBoardTextTransfer implements ClipboardOwner {

  /**
   * Implementation of ClipboardOwner interface.
   */
  public void lostOwnership(Clipboard arg0, Transferable arg1) {
  }

  /**
   * Place a String on the system clipboard, and make this class the
   * owner of the Clipboard&apos;s contents.
   */
  public void setClipboardContents( String aString ){
    StringSelection stringSelection = new StringSelection( aString );
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents( stringSelection, this );
  }

  /**
   * Get the String residing on the system clipboard.
   *
   * @return any text found on the system clipboard; if none found, return an
   * empty String.
   */
  public String getClipboardContents() {
    String       result = "";
    Clipboard    clipboard;
    Transferable contents;
    boolean      hasTransferableText;

    try {
      clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      //odd: the Object param of getContents is not currently used
      contents = clipboard.getContents(null);
      hasTransferableText =
          (contents != null) &&
          contents.isDataFlavorSupported(DataFlavor.stringFlavor);
      if ( hasTransferableText ) {
        try {
          result = (String)contents.getTransferData(DataFlavor.stringFlavor);
        }
        catch (Exception ex) {
          EZLogger.warn("Clipboard transfer: "+ex);
        }
      }
    } catch (OutOfMemoryError e2) {
      EZLogger.warn("Clipboard transfer: Out of memory"+e2);
      System.gc();
    }

    return result;
  }
}
