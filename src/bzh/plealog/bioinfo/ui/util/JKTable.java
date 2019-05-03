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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.ui.common.ClipBoardTextTransfer;
import com.plealog.genericapp.ui.common.ContextMenuManager;

/**
 * Define a JTable with a Column Selection Manager.
 * 
 * @author Patrick G. Durand
 * @since 2003
 */
public class JKTable extends JTable implements ColumnManagableTable {
  private static final long serialVersionUID = 6098014675207451352L;
  private CopySelectionToClipBoard cpySelectionAction;
  private boolean isColumnWidthChanged;
  private TableComponentAdapter componentAdapter;
  protected boolean tableChanged = false;
  private ContextMenuManager contextMenu;

  public JKTable(TableModel dm) {
    super(dm);
    this.setGridColor(Color.LIGHT_GRAY);
    initActionMap();
    if (dm instanceof JKTableModel) {
      this.getColumnModel().addColumnModelListener(new TableColumnWidthListener(this));
      this.getTableHeader().addMouseListener(new TableHeaderMouseListener(this));
      this.componentAdapter = new TableComponentAdapter();
      DefaultTableCellRenderer label = new UIResourceTableCellRenderer(this);
      label.setHorizontalAlignment(JLabel.CENTER);
      this.getTableHeader().setDefaultRenderer(label);
    }

    HashMap<String, String> toolTips = this.getToolTips();
    if (toolTips != null) {
      // header tool tips
      JTableHeader header = this.getTableHeader();
      ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
      // Assign a tooltip for each of the columns
      for (int c = 0; c < this.getColumnCount(); c++) {
        TableColumn col = this.getColumnModel().getColumn(c);
        for (String headerName : toolTips.keySet()) {
          if (col.getHeaderValue().toString().equals(headerName)) {
            tips.setToolTip(col, toolTips.get(headerName));
          }
        }
      }
      header.addMouseMotionListener(tips);
    }

    this.addMouseListener(new TableMouseListener());
  }

  @Override
  public void tableChanged(TableModelEvent event) {
    super.tableChanged(event);
    tableChanged = true;
  }

  public void setContextMenu(ContextMenuManager contextMenu) {
    this.contextMenu = contextMenu;
  }

  public HashMap<String, String> getToolTips() {
    return null;
  }

  public ComponentAdapter getTableComponentAdapter() {
    return this.componentAdapter;
  }

  public void updateColumnHeaders(TableHeaderColumnItem[] colH) {
    ((JKTableModel) this.getModel()).updateColumnHeaders(colH);
  }

  public void initColumnSize(int width, int[] colWidth) {

    if (this.getModel() instanceof JKTableModel) {
      TableColumnModel columnModel;
      TableColumn column = null;
      int i, size;
      JKTableModel model;

      if (width <= 0)// may happen (found with the debugger)!!!
        return;

      columnModel = this.getColumnModel();
      size = columnModel.getColumnCount();
      model = (JKTableModel) this.getModel();
      HashMap<Integer, Integer> defWidths;

      defWidths = getColumnsDefWidthMap();
      if (defWidths == null)
        return;

      for (i = 0; i < size; i++) {
        column = columnModel.getColumn(i);
        if (defWidths.containsKey(model.getColumnId(i))) {
          column.setPreferredWidth(defWidths.get(model.getColumnId(i)));
        }
      }

    }
  }

  public HashMap<String, Icon> getHeaderIcons() {
    return null;
  }

  private boolean hasColumnWidthChanged() {
    return isColumnWidthChanged;
  }

  private void setColumnWidthChanged(boolean widthChanged) {
    isColumnWidthChanged = widthChanged;
  }

  private void initActionMap() {
    ActionMap am = this.getActionMap();
    cpySelectionAction = new CopySelectionToClipBoard();
    am.put(TransferHandler.getCopyAction().getValue(Action.NAME), cpySelectionAction);
  }

  public Action getCopySelectionAction() {
    return cpySelectionAction;
  }

  private void saveDefColHeaders() {
    TableColumnModel tModel = this.getColumnModel();
    JKTableModel model = (JKTableModel) this.getModel();
    TableColumn tc;
    StringBuffer buf;

    int i, size = tModel.getColumnCount();
    buf = new StringBuffer();
    for (i = 0; i < size; i++) {
      tc = tModel.getColumn(i);
      buf.append(model.getColumnId(i));
      buf.append(":");
      buf.append(tc.getWidth());
      if ((i + 1) < size) {
        buf.append(",");
      }
    }
    EZEnvironment.setApplicationProperty(model.getColumnSizePropertyName(), buf.toString());
  }

  private int[] getColumnsDefWidth() {
    JKTableModel model = (JKTableModel) this.getModel();
    int[] colWidth;
    StringTokenizer tokenizer;
    String defSize, token;
    int idx, i = 0;

    defSize = EZEnvironment.getApplicationProperty(model.getColumnSizePropertyName());
    if (defSize == null)
      return null;
    tokenizer = new StringTokenizer(defSize, ",");
    colWidth = new int[tokenizer.countTokens()];
    while (tokenizer.hasMoreElements()) {
      token = tokenizer.nextToken();
      idx = token.indexOf(":");
      colWidth[i] = Integer.valueOf(token.substring(idx + 1));
      i++;
    }
    return colWidth;
  }

  protected HashMap<Integer, Integer> getColumnsDefWidthMap() {
    JKTableModel model = (JKTableModel) this.getModel();
    HashMap<Integer, Integer> defWidths;
    StringTokenizer tokenizer;
    String defSize, token;
    int idx;

    defSize = EZEnvironment.getApplicationProperty(model.getColumnSizePropertyName());
    if (defSize == null)
      return null;
    defWidths = new HashMap<Integer, Integer>();
    tokenizer = new StringTokenizer(defSize, ",");
    while (tokenizer.hasMoreElements()) {
      token = tokenizer.nextToken();
      idx = token.indexOf(":");
      defWidths.put(Integer.valueOf(token.substring(0, idx)), Integer.valueOf(token.substring(idx + 1)));
    }
    return defWidths;
  }

  /**
   * This class is in charge of resizing the result table when the parent panel
   * is resized.
   */
  private class TableComponentAdapter extends ComponentAdapter {

    private boolean needInit = true;

    public void componentResized(ComponentEvent e) {
      int width;

      if (!needInit)
        return;
      JKTableModel model = (JKTableModel) getModel();
      width = JKTable.this.getBounds().width;// parent.getBounds().width;

      int[] colWidth = null;
      String defSize = EZEnvironment.getApplicationProperty(model.getColumnSizePropertyName());
      if (defSize != null) {
        colWidth = getColumnsDefWidth();
      } else {
        // to force initial column sizes ;
        // could be enhanced by using a value adapted from real desktop size
        width = 1024;
      }
      initColumnSize(width, colWidth);
      needInit = false;// called only once at startup to restore savec column
                       // sizes
    }
  }

  @SuppressWarnings("serial")
  private class CopySelectionToClipBoard extends AbstractAction {
    public void actionPerformed(ActionEvent event) {
      int[] sels = JKTable.this.getSelectedRows();
      if (sels.length == 0)
        return;
      ByteArrayOutputStream baos;
      ClipBoardTextTransfer cbtt;
      CSVExport exporter;
      try {
        exporter = new CSVExport('\t');
        baos = new ByteArrayOutputStream();
        exporter.export(baos, JKTable.this.getModel(), JKTable.this.getSelectionModel());
        cbtt = new ClipBoardTextTransfer();
        cbtt.setClipboardContents(baos.toString());
      } catch (Exception ex) {
      }
    }
  }

  private class TableColumnWidthListener implements TableColumnModelListener {
    private JKTable tableObj;

    private TableColumnWidthListener(JKTable tableObj) {
      this.tableObj = tableObj;
    }

    @Override
    public void columnMarginChanged(ChangeEvent e) {

      /*
       * columnMarginChanged is called continuously as the column width is
       * changed by dragging. Therefore, execute code below ONLY if we are not
       * already aware of the column width having changed
       */
      if (!tableObj.hasColumnWidthChanged()) {
        /*
         * the condition below will NOT be true if the column width is being
         * changed by code.
         */
        if (tableObj.getTableHeader().getResizingColumn() != null) {
          // User must have dragged column and changed width
          tableObj.setColumnWidthChanged(true);
        }
      }
    }

    @Override
    public void columnMoved(TableColumnModelEvent e) {
    }

    @Override
    public void columnAdded(TableColumnModelEvent e) {
    }

    @Override
    public void columnRemoved(TableColumnModelEvent e) {
    }

    @Override
    public void columnSelectionChanged(ListSelectionEvent e) {
    }
  }

  private class TableHeaderMouseListener extends MouseAdapter {
    private JKTable tableObj;

    private TableHeaderMouseListener(JKTable tableObj) {
      this.tableObj = tableObj;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      /* On mouse release, check if column width has changed */
      if (tableObj.hasColumnWidthChanged()) {
        // Do whatever you need to do here

        // Reset the flag on the table.
        tableObj.setColumnWidthChanged(false);
        saveDefColHeaders();
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
      JKTableModel model;
      JTableHeader header;
      int i, col, colID, nCol;
      Rectangle r;
      boolean bSort = false;

      header = tableObj.getTableHeader();
      nCol = header.getColumnModel().getColumnCount();
      for (i = 0; i < nCol; i++) {
        // this code figures out if the user has clicked outside the
        // resizing column zone.
        r = header.getHeaderRect(i);
        r.grow(-3, 0); // exact value retrieved in the source code of
                       // BasicTableHeaderUI
        if (r.contains(e.getPoint())) {
          bSort = true;
        }
      }

      if (!bSort) {
        return;
      }
      model = (JKTableModel) tableObj.getModel();
      col = tableObj.columnAtPoint(e.getPoint());
      colID = model.getColumnId(col);
      if (colID == model.getSortColumn()) {
        model.setSortColumnAscending(!model.isSortColumnAscending());
      } else {
        model.setSortColumn(colID);
        model.setSortColumnAscending(true);
      }
      header.repaint();
      tableObj.clearSelection();
      boolean bForceReload = (e.getModifiers() & ActionEvent.CTRL_MASK) != 0;
      model.sortData(bForceReload);
    }
  }

  private class UIResourceTableCellRenderer extends DefaultTableCellRenderer implements UIResource {
    private static final long serialVersionUID = -162050309161381597L;
    private int[] xPoints = new int[3];
    private int[] yPoints = new int[3];
    private int curColID;
    private JTable tableObj;

    public UIResourceTableCellRenderer(JTable jt) {
      super();
      tableObj = jt;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {
      if (table != null) {
        JTableHeader header = table.getTableHeader();
        if (header != null) {
          setForeground(header.getForeground());
          setBackground(header.getBackground());
          setFont(header.getFont());

          if ((row == -1) && (table.getModel() instanceof JKTableModel)) {
            JKTableModel tableModel = (JKTableModel) table.getModel();
            Color headerColor = tableModel.getHeaderColumn(table.getColumnName(column));
            if (headerColor != null) {
              this.setBackground(headerColor);
            }
          }
        }
      }

      setText((value == null) ? "" : value.toString());
      setBorder(UIManager.getBorder("TableHeader.cellBorder"));
      if (getHeaderIcons() != null) {
        Icon icon = getHeaderIcons().get(getColumnName(column));
        if (icon != null) {
          setIcon(icon);
        } else {
          setIcon(null);
        }
      }
      JKTableModel model = (JKTableModel) tableObj.getModel();
      curColID = model.getColumnId(column);
      return this;
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      JKTableModel model = (JKTableModel) tableObj.getModel();
      if (model.getSortColumn() != curColID)
        return;

      int topInset = this.getBorder().getBorderInsets(this).top;
      int rightInset = this.getBorder().getBorderInsets(this).right;
      int right = this.getBounds().width - 2 * rightInset;

      if (model.isSortColumnAscending()) {
        xPoints[0] = right - 9;
        xPoints[1] = xPoints[0] + 3;
        xPoints[2] = xPoints[0] + 6;
        yPoints[0] = topInset + 5;
        yPoints[1] = yPoints[0] - 5;
        yPoints[2] = yPoints[0];
      } else {
        xPoints[0] = right - 9;
        xPoints[1] = xPoints[0] + 3;
        xPoints[2] = xPoints[0] + 6;
        yPoints[0] = topInset + 2;
        yPoints[1] = yPoints[0] + 3;
        yPoints[2] = yPoints[0];
      }

      g.fillPolygon(xPoints, yPoints, 3);
    }
  }

  private class TableMouseListener extends MouseAdapter {
    public void mouseReleased(MouseEvent e) {
      if (SwingUtilities.isRightMouseButton(e) && contextMenu != null) {
        contextMenu.showContextMenu(e.getX(), e.getY());
      }
    }
  }

  private class ColumnHeaderToolTips extends MouseMotionAdapter {

    // Current column whose tooltip is being displayed.
    // This variable is used to minimize the calls to setToolTipText().
    TableColumn curCol;

    // Maps TableColumn objects to tooltips
    @SuppressWarnings("rawtypes")
    HashMap tips = new HashMap();

    // If tooltip is null, removes any tooltip text.
    @SuppressWarnings("unchecked")
    public void setToolTip(TableColumn col, String tooltip) {
      if (tooltip == null) {
        tips.remove(col);
      } else {
        tips.put(col, tooltip);
      }
    }

    public void mouseMoved(MouseEvent evt) {
      TableColumn col = null;
      JTableHeader header = (JTableHeader) evt.getSource();
      JTable table = header.getTable();
      TableColumnModel colModel = table.getColumnModel();
      int vColIndex = colModel.getColumnIndexAtX(evt.getX());

      // Return if not clicked on any column header
      if (vColIndex >= 0) {
        col = colModel.getColumn(vColIndex);
      }

      if (col != curCol) {
        header.setToolTipText((String) tips.get(col));
        curCol = col;
      }
    }
  }
}
