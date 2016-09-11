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
package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import bzh.plealog.bioinfo.api.data.sequence.DSequenceModel;

/**
 * This is a sequence viewer. This class inherits from a JList so that it is
 * possible to display a sequence either horizontaly or verticaly.
 * 
 * @author Patrick G. Durand
 */
public class DSequenceListViewerTest extends JTable {
  private static final long serialVersionUID = 8097751696526057734L;
  private DSymbolRendererTest renderer;
  private int cellWidth = 15;
  /**
   * Default constructor.
   * Creates a viewer with a HORIZONTAL_WRAP layout orientation.
   */
  public DSequenceListViewerTest() {
    super(new DSequenceModelTest(null));

    renderer = new DSymbolRendererTest();
    renderer.setVerticalAlignment(JLabel.CENTER);
    renderer.setHorizontalAlignment(JLabel.CENTER);
    JTableHeader th=this.getTableHeader();
    th.setReorderingAllowed(false);
    th.setResizingAllowed(false);
    th.setPreferredSize(new Dimension(0, 0)); 
    this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    this.setColumnSelectionAllowed(true);
    this.setRowSelectionAllowed(true);
    this.setCellSelectionEnabled(true);
    this.setOpaque(true);
    this.setBackground(Color.white);
    this.setGridColor(Color.white);
    adjustCellSize();
  }

  public TableCellRenderer getCellRenderer(int row, int column){
    return renderer;
  }
  /**
   * Resizes the cells using current font specs.
   */
  private void adjustCellSize(){
    FontMetrics fm = this.getFontMetrics(this.getFont());
    cellWidth = fm.getHeight();
  }

  public void setFont(Font font){
    super.setFont(font);
    adjustCellSize();
  }
  public int getFixedCellWidth(){
    return cellWidth;
  }
  public int getFixedCellHeight(){
    return cellWidth;
  }
  public void setSelectionInterval(int from, int to){
    this.changeSelection(0, from, false, false);
    this.changeSelection(0, to, false, true);
  }
  public int[] getSelectedIndices(){
    return this.getSelectedColumns();
  }
  /**
   * Overrides JList.setModel() to only accept a DSequenceModel as the data model.
   * 
   * @throws RuntimeException if parameter model is not an instace of DSequenceModel
   */
  public void setModel(TableModel model){
    if (!(model instanceof DSequenceModel))
      throw new RuntimeException("Invalid ListModel class: expected DSequenceModel.");

    super.setModel(model);
    TableColumnModel tm =this.getColumnModel();
    int size = tm.getColumnCount();
    for(int i=0;i<size;i++){
      tm.getColumn(i).setPreferredWidth(cellWidth);
      tm.getColumn(i).setMaxWidth(cellWidth);
      tm.getColumn(i).setMinWidth(cellWidth);
    }
  }
  public int columnAtPoint(Point point) {
    int val;
    //the try/catch has been added because of an obscure behaviour
    //of the JTable's BasicTableUI: it uses a DefaulTableModel something
    //that KB never uses!!!!
    try{
      val = super.columnAtPoint(point);
    }
    catch(ArrayIndexOutOfBoundsException ex){
      val = -1;
    }
    return val;
  }
  public void paintComponent(Graphics g){
    //the try/catch has been added because of an obscure behaviour
    //of the JTable's BasicTableUI: it uses a DefaulTableModel something
    //that we never use!!!! The exception comes from a call to the getColumn
    //method.
    try{super.paintComponent(g);
    }catch(ArrayIndexOutOfBoundsException ex){
    }
  }
}
