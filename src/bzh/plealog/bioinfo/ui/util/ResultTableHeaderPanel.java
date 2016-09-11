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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


/**
 * This panel is used to add a header on top of JTableHeader. 
 * 
 * @author Patrick G. Durand
 */
public class ResultTableHeaderPanel extends JPanel{
  private static final long serialVersionUID = 3260210568705779305L;
  private JTable          _resTable;
  private Dimension       _prefSize = new Dimension(100, PNL_HEIGHT);
  private Set<String>     _leftColNames;
  private Set<String>     _rightColNames;
  private int             _leftHeaderWidth;
  private int             _rightHeaderWidth;
  private int             _nColumns;
  private int             _leftMargin = 0;
  private Font            _fnt = new Font("Arial", Font.PLAIN, 12);
  private String          _leftHeader;
  private String          _rightHeader;

  private static final int PNL_HEIGHT = 20;
  private static final Color BK_CLR = new Color(153,186,243) ;
  //UIManager.getDefaults().getColor("TableHeader.background");
  private static final Color BK_CLR2 = new Color(184,207,229);

  /**
   * Constructor.
   * 
   * @param leftHeader left header
   * @param rightHeader right header
   * @param leftColNames list of column names to gather under the left header
   * @param rightColNames list of column names to gather under the right header
   */
  public ResultTableHeaderPanel(String leftHeader, String rightHeader, Set<String> leftColNames, Set<String> rightColNames){
    setOpaque(true);
    _leftHeader = leftHeader;
    _rightHeader = rightHeader;
    _leftColNames = leftColNames;
    _rightColNames = rightColNames;
  }
  /**
   * Associates a data table to this table header.
   */
  public void registerResultTable(JTable rt){
    _resTable = rt;
    _resTable.getColumnModel().addColumnModelListener(new ResTableColumnListener());
    _resTable.addComponentListener(new ResultTableComponentAdapter());
  }

  /**
   * Get preferred size.
   * 
   * @return preferred size of the component
   * */
  public Dimension getPreferredSize(){
    return _prefSize;
  }
  /**
   * Set the size of the left margin.
   * 
   * @param margin size of the left margin
   */
  public void setLeftMargin(int margin){
    _leftMargin = margin;
  }
  /**
   * This class listens to the TableColumnModel modifications to recompute the
   * width of the two headers Query and Best Hit.
   */
  private class ResTableColumnListener implements TableColumnModelListener{
    private void resizePanels(){
      TableColumnModel tcm;
      TableColumn      tc;
      String           colHeader;
      int              i;

      tcm = _resTable.getColumnModel();
      _nColumns = tcm.getColumnCount();
      //minimum display: the two columns of the Query
      _leftHeaderWidth = _rightHeaderWidth = 0;
      for(i=0;i<_nColumns;i++){
        tc = tcm.getColumn(i);
        colHeader = tc.getHeaderValue().toString();
        if (_leftColNames.contains(colHeader)){
          _leftHeaderWidth += tc.getWidth();
        }
        if (_rightColNames.contains(colHeader)){
          _rightHeaderWidth += tc.getWidth();
        }
      }
    }
    public void columnMarginChanged(ChangeEvent e){
      resizePanels();
      ResultTableHeaderPanel.this.repaint();
    }

    public void columnSelectionChanged(ListSelectionEvent e){}
    public void columnAdded(TableColumnModelEvent e){
      resizePanels();
      ResultTableHeaderPanel.this.repaint();
    }
    public void columnMoved(TableColumnModelEvent e){}
    public void columnRemoved(TableColumnModelEvent e){
      resizePanels();
      ResultTableHeaderPanel.this.repaint();
    }
  }

  /**
   * This class listens to the ResultTable size modifications to recompute the
   * width of the two headers Query and Best Hit.
   */
  private class ResultTableComponentAdapter extends ComponentAdapter{
    public void componentResized(ComponentEvent e){
      Component parent;
      parent = (Component) e.getSource();
      _prefSize = new Dimension(parent.getBounds().width, PNL_HEIGHT);
      ResultTableHeaderPanel.this.repaint();
    }
  }

  public void paintComponent(Graphics g){
    super.paintComponent(g);

    Font        fnt;
    FontMetrics fmt;
    Color       oldClr;
    String      val;
    int         hitWidth, h;

    fnt = g.getFont();
    g.setFont(_fnt);
    fmt = g.getFontMetrics(_fnt);
    h = PNL_HEIGHT - fmt.getHeight()/2 + 2;

    oldClr = g.getColor();
    if (_leftHeaderWidth!=0){
      g.setColor(BK_CLR);
      g.fill3DRect(_leftMargin, 0, _leftHeaderWidth+1, PNL_HEIGHT, true);
      if(_leftHeader!=null){
        val = UIUtils.clipText(fmt, _leftHeader, _leftHeaderWidth);
        g.setColor(Color.BLACK);
        g.drawString(val, _leftMargin + (_leftHeaderWidth-fmt.stringWidth(val))/2, h);
      }
    }
    if(_rightHeaderWidth!=0){//We have more than two columns if we display columns related
      //to Best Hit. Otherwise we do not draw anything for the Best Hit Header
      hitWidth = _rightHeaderWidth;//this.getWidth() - _leftHeaderWidth;
      g.setColor(BK_CLR2);
      g.fill3DRect(_leftMargin+_leftHeaderWidth+1, 0, hitWidth, PNL_HEIGHT, true);
      if(_rightHeader!=null){
        val = UIUtils.clipText(fmt, _rightHeader, hitWidth);
        g.setColor(Color.BLACK);
        g.drawString(val, _leftMargin+_leftHeaderWidth+(hitWidth-fmt.stringWidth(val))/2, h);
      }
    }
    g.setColor(oldClr);
    g.setFont(fnt);
  }
}
