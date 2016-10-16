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
package bzh.plealog.bioinfo.ui.sequence.basic;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.StringReader;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.MouseInputAdapter;

import bzh.plealog.bioinfo.api.data.sequence.DAlphabet;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSequenceModel;
import bzh.plealog.bioinfo.api.data.sequence.DSymbol;
import bzh.plealog.bioinfo.api.data.sequence.DViewerSystem;

import com.plealog.genericapp.ui.common.ClipBoardTextTransfer;
import com.plealog.genericapp.ui.common.ContextMenuManager;

/**
 * This is a sequence viewer. This class inherits from a JList so that it is
 * possible to display a sequence either horizontally or vertically.
 * 
 * @author Patrick G. Durand
 */
public class DSequenceListViewer extends JList<DSymbol> {
  private static final long serialVersionUID = 8199084364383690222L;
  private DSymbolRenderer renderer;
  private ContextMenuManager _contextMenu;
  private int cellWidth = 15;
  @SuppressWarnings("unused")
  private int factor    = 1;

  /**
   * Default constructor.
   * Creates a viewer with a HORIZONTAL_WRAP layout orientation.
   */
  public DSequenceListViewer() {
    super(new DSequenceModel(null));

    renderer = new DSymbolRenderer();
    renderer.setVerticalAlignment(JLabel.CENTER);
    renderer.setHorizontalAlignment(JLabel.CENTER);
    this.setCellRenderer(renderer);
    this.setLayoutOrientation( JList.HORIZONTAL_WRAP );
    this.setVisibleRowCount(1);
    this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    this.setOpaque(true);
    this.setBackground(Color.white);
    DragSelectionListener mil = new DragSelectionListener();
    this.addMouseMotionListener(mil);
    this.addMouseListener(mil);
    initActionMap();
    adjustCellSize(1);
  }
  private void initActionMap(){
    ActionMap am = this.getActionMap();
    am.put(TransferHandler.getCopyAction().getValue(Action.NAME), new CopyAction());
  }
  public ListCellRenderer<DSymbol> getCellRenderer(int row){
    return renderer;
  }
  /**
   * Resizes the cells using current font specs.
   */
  private void adjustCellSize(int factor){
    FontMetrics fm = this.getFontMetrics(this.getFont());
    this.factor = factor;
    cellWidth = fm.getHeight();
    super.setFixedCellHeight(cellWidth);
    super.setFixedCellWidth(factor*cellWidth);
  }

  public void setFont(Font font){
    super.setFont(font);
    adjustCellSize(1);
  }
  public void setFont(Font font, int factor){
    super.setFont(font);
    adjustCellSize(factor);
  }
  public int getFixedCellWidth(){
    return cellWidth;
  }
  public int getFixedCellHeight(){
    return cellWidth;
  }

  public int[] getSelectedIndices(){
    return super.getSelectedIndices();
  }
  /**
   * Overrides JList.setModel() to only accept a DSequenceModel as the data model.
   * 
   * @throws RuntimeException if parameter model is not an instance of DSequenceModel
   */
  public void setModel(ListModel<DSymbol> model){
    if (!(model instanceof DSequenceModel))
      throw new RuntimeException("Invalid ListModel class: expected DSequenceModel.");

    super.setModel(model);
    super.setFixedCellHeight(30/*cellWidth*/);
    super.setFixedCellWidth(30/*factor*cellWidth*/);
  }
  public DSequence getSequence(){
    return ((DSequenceModel)this.getModel()).getSequence();
  }
  public void setContextMenu(ContextMenuManager contextMenu){
    _contextMenu = contextMenu;
  }

  public DSequence getSelectedSequence(){
    StringBuffer buf;
    DSequence    seq, curSeq;
    DAlphabet    alphabet;
    DSymbol      symbol, gap;
    String       chain;
    int[]        indices;
    int          i;

    indices = this.getSelectedIndices();
    if (indices.length==0)
      return null;
    curSeq = ((DSequenceModel)this.getModel()).getSequence();
    alphabet = curSeq.getAlphabet();
    buf = new StringBuffer();
    gap = alphabet.getSymbol(DSymbol.GAP_SYMBOL_CODE);
    for(i=0;i<indices.length;i++){
      symbol = curSeq.getSymbol(indices[i]);
      if (!symbol.equals(gap)){
        buf.append(symbol.getChar());
      }
    }
    chain = buf.toString();
    if (chain.length()==0)
      return null;
    seq = DViewerSystem.getSequenceFactory().getSequence(new StringReader(chain), alphabet);

    return seq;
  }

  //http://forum.java.sun.com/thread.jspa?threadID=390304&messageID=1685964
  private class DragSelectionListener extends MouseInputAdapter {
    Point lastPoint = null;
    public void mousePressed(MouseEvent e) {
      lastPoint = e.getPoint();     // Need to hold onto starting mousepress location...
    }
    public void mouseReleased(MouseEvent e) {
      lastPoint = null;
      if (SwingUtilities.isRightMouseButton(e) && _contextMenu!=null){
        _contextMenu.showContextMenu(e.getX(), e.getY());
      }
    }
    public void mouseDragged (MouseEvent e) {
      JList<?> list = (JList<?>) e.getSource();
      if (lastPoint != null && !e.isConsumed () && SwingUtilities.isLeftMouseButton(e)
          && ! e.isShiftDown ()) {
        int row = list.locationToIndex(e.getPoint());
        if (row != -1) {
          int leadIndex = list.locationToIndex(lastPoint);
          if (row != leadIndex) { // ignore drag within row
            Rectangle cellBounds = list.getCellBounds(row, row);
            if (cellBounds != null) {
              list.scrollRectToVisible(cellBounds);
              // Cannot use getAnchorSelectionIndex cause keeps getting reset to current row..
              //  jfc suggested code had:  int anchorIndex = list.getAnchorSelectionIndex ();

              int anchorIndex = leadIndex;
              if (e.isControlDown ()) {
                if (list.isSelectedIndex (anchorIndex)) { // add selection
                  list.removeSelectionInterval (anchorIndex, leadIndex);
                  list.addSelectionInterval (anchorIndex, row);
                }
                else { // remove selection
                  list.addSelectionInterval (anchorIndex, leadIndex);
                  list.removeSelectionInterval (anchorIndex, row);
                }
              }
              else { // replace selection
                list.setSelectionInterval (leadIndex, row);
              }
            }
          }
        }
      }
    }
  }
  private class CopyAction extends AbstractAction {
    private static final long serialVersionUID = -7758792383742723675L;

    public void actionPerformed(ActionEvent a){
      DSequence seq;

      seq = getSelectedSequence();
      if (seq==null)
        return;
      ClipBoardTextTransfer cbtt = new ClipBoardTextTransfer();
      cbtt.setClipboardContents(seq.toString());
    }
  }
}
