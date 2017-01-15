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
package bzh.plealog.bioinfo.ui.blast.hittable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.UIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import bzh.plealog.bioinfo.api.data.searchresult.SRHit;
import bzh.plealog.bioinfo.api.data.searchresult.SRHsp;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.sequence.BankSequenceInfo;
import bzh.plealog.bioinfo.api.data.sequence.DSeqUtils;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSequenceInfo;
import bzh.plealog.bioinfo.ui.blast.config.ConfigManager;
import bzh.plealog.bioinfo.ui.blast.config.color.ColorPolicyConfig;
import bzh.plealog.bioinfo.ui.blast.config.color.DefaultHitColorPolicy;
import bzh.plealog.bioinfo.ui.blast.core.AnalysisUtils;
import bzh.plealog.bioinfo.ui.blast.core.BlastHitHSP;
import bzh.plealog.bioinfo.ui.blast.core.BlastHitHspImplem;
import bzh.plealog.bioinfo.ui.blast.core.BlastIteration;
import bzh.plealog.bioinfo.ui.blast.event.BlastHitListEvent;
import bzh.plealog.bioinfo.ui.blast.event.BlastHitListListener;
import bzh.plealog.bioinfo.ui.blast.event.BlastHitListSupport;
import bzh.plealog.bioinfo.ui.blast.event.BlastIterationListEvent;
import bzh.plealog.bioinfo.ui.blast.event.BlastIterationListListener;
import bzh.plealog.bioinfo.ui.resources.SVMessages;
import bzh.plealog.bioinfo.ui.seqinfo.SequenceInfoViewer;
import bzh.plealog.bioinfo.ui.util.JKTable;
import bzh.plealog.bioinfo.ui.util.TableColumnManager;
import bzh.plealog.bioinfo.ui.util.TableHeaderColumnItem;

import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.ui.common.ContextMenuManager;

/**
 * This is the Blast Hit Viewer. It relies on an extended customized JTable.
 * 
 * @author Patrick G. Durand
 */
public class BlastHitTable extends JPanel implements BlastHitListListener,
    BlastIterationListListener {
  private static final long         serialVersionUID         = -7321805973629682885L;
  protected HitListTable            _blastList;
  protected BlastIteration          _curIteration;
  protected ContextMenuManager      _contextMnu;
  private JPanel                    _mainPanel;
  private BlastHitListSupport       _updateSupport;
  private JScrollPane               _scroll;
  private TableHeaderColumnItem[]   _colItems;
  private TableHeaderColumnItem[]   _colItemsReference;
  private int                       _largestColItem;
  protected JButton                 autoAnnotatorButton_;
  protected JLabel                  autoAnnotatorProgressLbl_;
  protected JLabel                  autoAnnotatorLbl_;
  public HSPSummaryCellRenderer     _hspSummaryRenderer;
  private HitQualityCellRenderer    _qualityCellRenderer;
  private PercentRenderer           _pctRenderer;
  private boolean                   _bLockSelection;
  private String                    _tableID                 = "";
  private int                       _colIdForSorting         = HIT_NUM;
  private boolean                   _ascentSortOrder         = true;
  private BlastHitHspSortComparator _blastHitHspSortComparator;

  private static final String       DEF_COL_PROP_KEY         = "hitList.columns";
  public static final String        DEF_COL_ITEM_HEADERS_INT = "0,1,2,6,7,8,18,19,22,24";

  // when adding a column ID here, to not modify existing ones. Always increase
  // values,
  // even if you insert a new column between existing ones.
  public static final int           HIT_NUM                  = 0;
  public static final int           ACCESS                   = 1;
  public static final int           DEFINITION               = 2;
  public static final int           HSP_SUMMARY              = 3;
  public static final int           LENGTH                   = 4;
  public static final int           SCORE_BITS               = 5;
  public static final int           EVALUE                   = 6;
  public static final int           QUALITY                  = 7;
  public static final int           NBHSPS                   = 8;
  public static final int           SCORE                    = 9;
  public static final int           Q_FROM                   = 10;
  public static final int           Q_TO                     = 11;
  public static final int           Q_GAPS                   = 12;
  public static final int           H_FROM                   = 13;
  public static final int           H_TO                     = 14;
  public static final int           H_GAP                    = 15;
  public static final int           Q_FRAME                  = 16;
  public static final int           H_FRAME                  = 17;
  public static final int           IDENTITY                 = 18;
  public static final int           POSITIVE                 = 19;
  public static final int           GAPS                     = 20;
  public static final int           ALI_LEN                  = 21;
  public static final int           Q_COVERAGE               = 22;
  public static final int           ORGANISM                 = 23;
  public static final int           H_COVERAGE               = 24;
  public static final int           TAXONOMY                 = 25;
  public static final int           C_DATE                   = 26;
  public static final int           U_DATE                   = 27;
  public static final int           DIVISION                 = 28;
  public static final int           MISMATCHES               = 29;
  public static final int           T_GAPS                   = 30;

  private static final String       EMPTY_STR                = " ";

  /**
   * Default constructor.
   */
  protected BlastHitTable() {
    this(null);
  }

  /**
   * Constructor allowing to set an id to the table. This ID is used to ensure
   * persistence of the graphical properties of the table. Is you use multiple
   * instance of a BlastHitList, you should use a different id for each of them.
   */
  protected BlastHitTable(String id) {
    JPanel panel, actPnl;
    JTableHeader tHeader;
    TableColumnManager tcm;

    if (id != null)
      _tableID = id;

    createReferenceTableColumnModel();
    createDefaultTableColumnModel();

    _blastHitHspSortComparator = new BlastHitHspSortComparator();
    _blastHitHspSortComparator.setAscent(_ascentSortOrder);
    _blastHitHspSortComparator.setColID(_colIdForSorting);
    _hspSummaryRenderer = new HSPSummaryCellRenderer();
    _qualityCellRenderer = new HitQualityCellRenderer();
    _pctRenderer = new PercentRenderer();
    _largestColItem = 2;
    _blastList = new HitListTable(new BlastHitTableModel());
    _blastList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    _blastList.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    tHeader = _blastList.getTableHeader();
    tHeader.setDefaultRenderer(createDefaultRenderer());
    tHeader.setReorderingAllowed(false);
    tHeader.addMouseListener(new ColumnSelector(_blastList));
    _blastList.setColumnSelectionAllowed(false);
    _blastList.setRowSelectionAllowed(true);
    _blastList.getSelectionModel().addListSelectionListener(
        new BlastHitTableSelectionListener());
    _blastList.addMouseListener(new TableMouseListener());

    _blastList.setGridColor(Color.LIGHT_GRAY);
    tcm = new TableColumnManager(_blastList, _colItemsReference,
        getTableSpecialActionsForMenu());
    _scroll = new JScrollPane(_blastList);
    _scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    _scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, tcm.getInvoker());
    panel = new JPanel(new BorderLayout());
    panel.add(_scroll, BorderLayout.CENTER);
    panel.addComponentListener(new TableComponentAdapter());

    this.setLayout(new BorderLayout());
    this.add(panel, BorderLayout.CENTER);

    JToolBar optionsTBar = getToolbar();
    if (optionsTBar != null) {
      actPnl = new JPanel(new BorderLayout());
      actPnl.add(optionsTBar, BorderLayout.EAST);
      this.add(actPnl, BorderLayout.SOUTH);
    }
    _mainPanel = panel;
    activateActions(false);
    activateSelBasedActions(false, true);
  }

  /**
   * Prepare specific actions to be displayed in the Table menu. Such a menu is
   * located at the top right corner of the Table and also serves to switch
   * column display to on or off.
   */
  protected Action[] getTableSpecialActionsForMenu() {
    return null;
  }

  /**
   * Creates a toolbar with additional functions.
   */
  protected JToolBar getToolbar() {
    return null;
  }

  /**
   * Create the reference column table model.
   */
  private void createReferenceTableColumnModel() {
    String defColIDs = null;
    List<Integer> idSet;

    if (ConfigManager.isEnableSerialApplicationProperty()) {
      defColIDs = EZEnvironment.getApplicationProperty(_tableID
          + DEF_COL_PROP_KEY);
    }
    if (defColIDs == null)
      defColIDs = DEF_COL_ITEM_HEADERS_INT;
    idSet = TableColumnManager.getDefColumns(defColIDs);

    _colItemsReference = new TableHeaderColumnItem[31];
    _colItemsReference[0] = new TableHeaderColumnItem("#", HIT_NUM, true, true);
    _colItemsReference[1] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.3"), ACCESS, true,
        idSet.contains(ACCESS));
    _colItemsReference[2] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.4"), DEFINITION, true,
        idSet.contains(DEFINITION));
    _colItemsReference[3] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.28"), C_DATE, false,
        idSet.contains(C_DATE));
    _colItemsReference[4] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.29"), U_DATE, false,
        idSet.contains(U_DATE));
    _colItemsReference[5] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.30"), DIVISION, false,
        idSet.contains(DIVISION));
    _colItemsReference[6] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.27"), TAXONOMY, false,
        idSet.contains(TAXONOMY));
    _colItemsReference[7] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.25"), ORGANISM, false,
        idSet.contains(ORGANISM));
    _colItemsReference[8] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.23"), HSP_SUMMARY, false,
        idSet.contains(HSP_SUMMARY));
    _colItemsReference[9] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.7"), LENGTH, false,
        idSet.contains(LENGTH));
    _colItemsReference[10] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.5"), SCORE_BITS, false,
        idSet.contains(SCORE_BITS));
    _colItemsReference[11] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.6"), EVALUE, false,
        idSet.contains(EVALUE));
    _colItemsReference[12] = new TableHeaderColumnItem("Quality", QUALITY,
        false, idSet.contains(QUALITY));
    _colItemsReference[13] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.8"), NBHSPS, false,
        idSet.contains(NBHSPS));
    _colItemsReference[14] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.9"), SCORE, false,
        idSet.contains(SCORE));
    _colItemsReference[15] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.10"), Q_FROM, false,
        idSet.contains(Q_FROM));
    _colItemsReference[16] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.11"), Q_TO, false,
        idSet.contains(Q_TO));
    _colItemsReference[17] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.12"), Q_GAPS, false,
        idSet.contains(Q_GAPS));
    _colItemsReference[18] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.13"), H_FROM, false,
        idSet.contains(H_FROM));
    _colItemsReference[19] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.14"), H_TO, false,
        idSet.contains(H_TO));
    _colItemsReference[20] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.15"), H_GAP, false,
        idSet.contains(H_GAP));
    _colItemsReference[21] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.16"), Q_FRAME, false,
        idSet.contains(Q_FRAME));
    _colItemsReference[22] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.17"), H_FRAME, false,
        idSet.contains(H_FRAME));
    _colItemsReference[23] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.18"), IDENTITY, false,
        idSet.contains(IDENTITY));
    _colItemsReference[24] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.19"), POSITIVE, false,
        idSet.contains(POSITIVE));
    _colItemsReference[25] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.20"), GAPS, false,
        idSet.contains(GAPS));
    _colItemsReference[26] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.21"), ALI_LEN, false,
        idSet.contains(ALI_LEN));
    _colItemsReference[27] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.24"), Q_COVERAGE, false,
        idSet.contains(Q_COVERAGE));
    _colItemsReference[28] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.26"), H_COVERAGE, false,
        idSet.contains(H_COVERAGE));
    _colItemsReference[29] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.32"), T_GAPS, false,
        idSet.contains(T_GAPS));
    _colItemsReference[30] = new TableHeaderColumnItem(
        SVMessages.getString("BlastHitList.31"), MISMATCHES, false,
        idSet.contains(MISMATCHES));

  }

  /**
   * Creates a default table column model. This is the one used when the viewer
   * is created. Usually it does not contain all the columns defined in the
   * reference table column model.
   */
  private void createDefaultTableColumnModel() {
    int i, n;

    n = 0;
    for (i = 0; i < _colItemsReference.length; i++) {
      if (_colItemsReference[i].isVisible())
        n++;
    }
    _colItems = new TableHeaderColumnItem[n];
    n = 0;
    for (i = 0; i < _colItemsReference.length; i++) {
      if (_colItemsReference[i].isVisible()) {
        _colItems[n] = _colItemsReference[i];
        n++;
      }
    }
  }

  /**
   * Convert a hit ordering number to a table row.
   */
  private int getRowIndex(int hitNum) {
    int idx, i, size;

    size = _blastList.getModel().getRowCount();
    for (i = 0; i < size; i++) {
      idx = (Integer) _blastList.getValueAt(i, 0);
      if (idx == hitNum)
        return i;
    }
    return -1;
  }

  /**
   * Implementation of BlastIterationListListener interface. This implementation
   * is intended to listen to notifications from BlastNavigator.
   */
  public void iterationChanged(BlastIterationListEvent e) {
    BlastIteration iter = (BlastIteration) e.getBlastIteration();

    if (iter == null || iter.getIteration().countHit() == 0) {
      resetDataModel();
      _curIteration = null;
    } else {
      if (_curIteration != iter) {
        setDataModel(AnalysisUtils.prepareDataModel(iter));
        _curIteration = iter;
      }
    }
  }

  public SROutput getResult(){
    return _curIteration.getEntry().getResult();
  }
  
  public int getSelectedIteration(){
    return _curIteration.getIterNum();
  }
  
  public int[] getSelectedHits(){
    return _blastList.getSelectedHitIds();
  }

  /**
   * Handle event when a hit is selected in the table.
   */
  public void hitChanged(BlastHitListEvent e) {
    List<BlastHitHspImplem> hits;
    BlastHitHSP hit;
    boolean bFirst = true;
    int rowIdx, firstRow = 0;

    if (_blastList.getModel().getRowCount() == 0) {
      return;
    }
    if (e.getSource() == this)
      return;
    _bLockSelection = true;
    hits = e.getHitHsps();
    if (hits == null || hits.isEmpty()) {
      _blastList.getSelectionModel().clearSelection();
      activateSelBasedActions(false, true);
      blastHitHspChanged(null, 0);
    } else {
      _blastList.getSelectionModel().setValueIsAdjusting(true);
      _blastList.getSelectionModel().clearSelection();
      for (BlastHitHSP h : hits) {
        rowIdx = getRowIndex(h.getHit().getHitNum());
        if (rowIdx < _blastList.getModel().getRowCount()) {
          if (bFirst) {
            firstRow = rowIdx;
            bFirst = false;
          }
          _blastList.getSelectionModel().addSelectionInterval(rowIdx, rowIdx);
        }
      }
      _blastList.scrollRectToVisible(_blastList.getCellRect(firstRow, 0, true));
      _blastList.getSelectionModel().setValueIsAdjusting(false);
      hit = hits.get(0);
      activateSelBasedActions(true, true);
      blastHitHspChanged(hit, hit.getHit().getHitNum() - 1);
    }
    _bLockSelection = false;
  }

  /**
   * Clear the content of this viewer.
   */
  public void resetDataModel() {
    BlastHitTableModel tModel = (BlastHitTableModel) _blastList.getModel();

    tModel.resetModel();
    // this line will notifies SeqAlign Viewer to reset its view
    // (remember that SeqAlignViewer listens to this selection model
    // to figure out which BHit's seqAlign to display)
    _blastList.getSelectionModel().clearSelection();
    // clear data model
    activateActions(false);
    _scroll.getVerticalScrollBar().setValue(0);
  }

  /**
   * Set a new data model.
   */
  public void setDataModel(BlastHitHSP[] bhh) {
    int[] colWidth = _blastList.getCurrentColumnSize();
    _blastList.setModel(new BlastHitTableModel(bhh));
    _blastList.initColumnSize(_mainPanel.getWidth(), colWidth);
    _blastList.getSelectionModel().setSelectionInterval(0, 0);
    activateActions(true);
    _scroll.getVerticalScrollBar().setValue(0);
  }

  /**
   * Register a BlastHitListSupport.
   * 
   * */
  public void registerHitListSupport(BlastHitListSupport us) {
    _updateSupport = us;
  }

  /**
   * Adds a table model listener to this hit table. Such a listener can be used
   * to follow modifications made on the data displayed in this hit table.
   */
  public void addHitDataListener(TableModelListener listener) {
    _blastList.getModel().addTableModelListener(listener);
  }

  /**
   * Removes a table model listener to this hit table.
   */
  public void removeHitDataListener(TableModelListener listener) {
    _blastList.getModel().removeTableModelListener(listener);
  }

  /**
   * This method is called when a hit is selected in the Table.
   */
  public void blastHitHspChanged(BlastHitHSP bhh, int hspNum) {

  }

  /**
   * This method is automatically called to enable actions to activate
   * themselves or not.
   */
  protected void activateActions(boolean activate) {
  }

  /**
   * This method is automatically called to enable actions to activate
   * themselves or not in response to selection on the table.
   */
  protected void activateSelBasedActions(boolean activate, boolean isSingleSel) {
  }

  /**
   * Return the column header model currently displayed by this table.
   */
  protected TableHeaderColumnItem[] getColumnHeaders() {
    return _colItems;
  }

  /**
   * This class displays the hit list as a table.
   */
  protected class HitListTable extends JKTable {
    /**
     * 
     */
    private static final long serialVersionUID = -8847098953371908198L;

    private HitListTable(TableModel dm) {
      super(dm);
    }

    /**
     * Given a bit score, returned the appropriate color from the COnfigManager.
     */
    private Color getColor(SRHsp hsp) {
      Color clr = Color.BLACK;
      ColorPolicyConfig nc;

      nc = (ColorPolicyConfig) ConfigManager.getConfig(ColorPolicyConfig.NAME);
      if (nc == null) {
        clr = DefaultHitColorPolicy.getColor((int) hsp.getScores()
            .getBitScore());
      } else {
        clr = nc.getHitColor(hsp, false);
      }
      return clr;
    }

    /**
     * This table cell renderer.
     */
    public TableCellRenderer getCellRenderer(int row, int column) {
      TableCellRenderer tcr;
      int id;

      id = _colItems[column].getIID();
      if (id == HSP_SUMMARY) {
        tcr = _hspSummaryRenderer;
      } else if (id == QUALITY) {
        tcr = _qualityCellRenderer;
      } else if (id == IDENTITY || id == POSITIVE || id == GAPS
          || id == Q_COVERAGE || id == H_COVERAGE) {
        tcr = _pctRenderer;
      } else {
        tcr = super.getCellRenderer(row, column);
      }
      if (tcr instanceof JLabel) {
        JLabel lbl;
        lbl = (JLabel) tcr;
        Object obj;

        // adjust alignment
        switch (id) {
          case HIT_NUM:
          case ACCESS:
          case DEFINITION:
          case ORGANISM:
          case TAXONOMY:
            lbl.setHorizontalAlignment(SwingConstants.LEFT);
            break;
          default:
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
        }

        lbl.setOpaque(true);
        // adjust colors
        obj = getModel().getValueAt(row, -1);
        if (obj != null && obj instanceof BlastHitHspImplem) {
          BlastHitHspImplem bhh = (BlastHitHspImplem) obj;
          SRHsp hsp = (bhh.getHit()).getHsp(bhh.getHspNum() - 1);
          lbl.setForeground(getColor(hsp));
          if (column == 1 && AnalysisUtils.hasFeatures(bhh.getHit())) {
            if (AnalysisUtils.hasWarnFeatures(bhh.getHit()))
              lbl.setIcon(EZEnvironment.getImageIcon("featWarn.png"));
            else
              lbl.setIcon(EZEnvironment.getImageIcon("feature.png"));
          } else {
            // adjust icon for hits having features attached to them
            lbl.setIcon(null);
          }
        } else {
          // adjust icon for hits having features attached to them
          lbl.setIcon(null);
        }
        if (row % 2 == 0) {
          lbl.setBackground(ColorPolicyConfig.BK_COLOR);
        } else {
          lbl.setBackground(Color.WHITE);
        }
      }
      return tcr;
    }

    public void updateColumnHeaders(TableHeaderColumnItem[] colH) {
      ((BlastHitTableModel) this.getModel()).setColHeaders(colH);
      if (ConfigManager.isEnableSerialApplicationProperty()) {
        EZEnvironment.setApplicationProperty(_tableID + DEF_COL_PROP_KEY,
            TableColumnManager.getDelColumns(colH));
      }
    }

    protected int[] getCurrentColumnSize() {
      TableColumnModel tcm;
      int[] cols;
      int i, size;

      tcm = getColumnModel();
      size = tcm.getColumnCount();
      if (size == 0)
        return null;
      cols = new int[size];
      for (i = 0; i < size; i++) {
        cols[i] = tcm.getColumn(i).getPreferredWidth();
      }

      return cols;
    }

    public void initColumnSize(int width, int[] colWidth) {
      FontMetrics fm;
      TableColumnModel tcm;
      TableColumn tc, lastTc = null;
      String header;
      int i, size, tot, val;

      if (width <= 0)// may happen (found with the debugger)!!!
        return;
      tcm = getColumnModel();
      size = tcm.getColumnCount();
      if (colWidth != null && colWidth.length == size) {
        for (i = 0; i < size; i++) {
          tcm.getColumn(i).setPreferredWidth(colWidth[i]);
        }
      } else {
        fm = getFontMetrics(getFont());
        tot = 0;
        for (i = 0; i < size; i++) {
          tc = tcm.getColumn(i);
          header = tc.getHeaderValue().toString();
          // by default, 'Description' is the largest column
          if (!header.equals(_colItems[_largestColItem].getSID())) {
            val = fm.stringWidth(header) + 20;
            tc.setPreferredWidth(val);
            /*
             * Following can be use to lock the width of a column Could be
             * interesting to add to the TableHeaderColumItem a field specifying
             * which column has a locked size. tc.setMinWidth(val);
             * tc.setMaxWidth(val);
             */
            tot += val;
          } else {
            lastTc = tc;
          }
        }
        if (lastTc != null) {
          lastTc.setPreferredWidth(width - tot - 2);
        }
      }
    }

    public int[] getSelectedHitIds() {
      BlastHitHSP bhh;
      int[] ids, sels;
      int i, size;

      size = this.getSelectedRowCount();
      if (size == 0)
        return null;
      sels = this.getSelectedRows();
      ids = new int[size];
      for (i = 0; i < size; i++) {
        bhh = (BlastHitHSP) this.getValueAt(sels[i], -1);
        ids[i] = bhh.getHit().getHitNum();
      }
      // table column may be sorted in various. We need to have Hit IDs by
      // ascending
      // order.
      Arrays.sort(ids);
      return ids;
    }

    public DSequence[] getPartialSequences() {
      ListSelectionModel sModel;
      DSequence[] seqs;
      DSequence seq;
      DSequenceInfo dsi;
      TableModel model;
      BlastHitHSP bhh;
      SRHsp hsp;
      SRHit hit;
      int[] selRows;
      int i, size;

      model = this.getModel();
      sModel = this.getSelectionModel();
      if (sModel.isSelectionEmpty())
        return null;
      selRows = this.getSelectedRows();
      size = selRows.length;
      seqs = new DSequence[size];
      for (i = 0; i < size; i++) {
        bhh = (BlastHitHSP) model.getValueAt(selRows[i], -1);
        hit = bhh.getHit();
        hsp = hit.getHsp(bhh.getHspNum() - 1);
        seq = hsp.getHit().getSequence(hsp);
        if (seq != null) {
          dsi = seq.getSequenceInfo();
          if (dsi == null) {
            dsi = new DSequenceInfo();
            dsi.setName(DSeqUtils.formatSequenceHeader(hit.getHitAccession(),
                hit.getHitDef(), hsp.getHit().getFrom(), hsp.getHit().getTo()));
            seq.setSequenceInfo(dsi);
          }
        }
        seqs[i] = seq;
      }
      return seqs;
    }

    public boolean isSelectionEmpty() {
      return this.getSelectionModel().isSelectionEmpty();
    }

    public String getHeader() {
      return "";
    }

    public ImageIcon getImageIcon(int row, int col) {
      ImageIcon icon = null;
      ColorPolicyConfig nc;
      SRHit hit;
      int id;

      id = _colItems[col].getIID();
      if (id != QUALITY) {
        return null;
      }
      hit = (SRHit) this.getValueAt(row, col);
      nc = (ColorPolicyConfig) ConfigManager.getConfig(ColorPolicyConfig.NAME);
      if (nc != null) {
        SRHsp hsp = hit.getHsp(0);
        icon = nc.getQualityIcon(hsp);
        if (icon != null)
          return icon;
      }
      return null;
    }
  }

  /**
   * The hit table model.
   */
  private class BlastHitTableModel extends AbstractTableModel {
    private static final long serialVersionUID = -8111948924562038269L;
    private BlastHitHSP[]     _hits;

    public BlastHitTableModel() {
    }

    public BlastHitTableModel(BlastHitHSP[] bhh) {
      setColHeaders(null);
      _hits = bhh;
      sortData();
    }

    public void resetModel() {
      _hits = null;
      fireTableChanged(new TableModelEvent(this, 0, 0, 0,
          TableModelEvent.DELETE));
    }

    public void setColHeaders(TableHeaderColumnItem[] colH) {
      if (colH != null)
        _colItems = colH;
    }

    public String getColumnName(int column) {
      return _colItems[column].getSID();
    }

    public int getColumnCount() {
      return _colItems.length;
    }

    public int getRowCount() {
      if (_hits == null)
        return 0;
      return _hits.length;
    }

    private Object getValue(BlastHitHSP bhh, int col) {
      BankSequenceInfo si;
      Object val = EMPTY_STR;
      double eval;
      String str;
      int id, idx;
      SRHit hit = bhh.getHit();
      SRHsp hsp = hit.getHsp(0);

      id = _colItems[col].getIID();
      switch (id) {
        case HIT_NUM:
          val = new Integer(hit.getHitNum());
          break;
        case ACCESS:
          val = hit.getHitAccession();
          break;
        case DEFINITION:
          val = hit.getHitDef();
          // code added because of KDMS : when classification IDs are available,
          // there
          // are added in the Fasta header file between tags [[ and ]]. Here, we
          // cut off
          // this data for display purpose. This code is not optimal... todo:
          // optimize it!
          if (val != null) {
            str = val.toString();
            idx = str.indexOf("[[");
            if (idx != -1)
              val = str.subSequence(0, idx);
          }

          break;
        case HSP_SUMMARY:
          val = hit;
          break;
        case LENGTH:
          val = new Integer(hit.getHitLen());
          break;
        case SCORE_BITS:
          val = AnalysisUtils.SCORE_FORMATTER.format(hsp.getScores()
              .getBitScore());
          break;
        case EVALUE:
          // _logger.info(hsp.getScores().getEvalue());
        case SCORE:
          eval = (id == EVALUE ? hsp.getScores().getEvalue() : hsp.getScores()
              .getScore());
          if (eval > 0 && eval < 0.1)
            val = AnalysisUtils.EVALUE_FORMATTER1.format(eval);
          else
            val = AnalysisUtils.EVALUE_FORMATTER2.format(eval);
          break;
        case NBHSPS:
          val = new Integer(hit.countHsp());
          break;
        case Q_FROM:
          val = new Integer(hsp.getQuery().getFrom());
          break;
        case Q_TO:
          val = new Integer(hsp.getQuery().getTo());
          break;
        case Q_GAPS:
          val = new Integer(hsp.getQuery().getGaps());
          break;
        case H_FROM:
          val = new Integer(hsp.getHit().getFrom());
          break;
        case H_TO:
          val = new Integer(hsp.getHit().getTo());
          break;
        case H_GAP:
          val = new Integer(hsp.getHit().getGaps());
          break;
        case Q_FRAME:
          val = new Integer(hsp.getQuery().getFrame());
          break;
        case H_FRAME:
          val = new Integer(hsp.getHit().getFrame());
          break;
        case IDENTITY:
          val = AnalysisUtils.PCT_FORMATTER.format(hsp.getScores()
              .getIdentityP()) + "%";
          break;
        case POSITIVE:
          val = AnalysisUtils.PCT_FORMATTER.format(hsp.getScores()
              .getPositiveP()) + "%";
          break;
        case GAPS:
          val = AnalysisUtils.PCT_FORMATTER.format(hsp.getScores().getGapsP())
              + "%";
          break;
        case T_GAPS:
          val = new Integer(hsp.getScores().getGaps());
          break;
        case MISMATCHES:
          val = new Integer(hsp.getScores().getMismatches());
          break;
        case ALI_LEN:
          val = new Integer(hsp.getScores().getAlignLen());
          break;
        case QUALITY:
          val = hit;
          break;
        case Q_COVERAGE:
          val = AnalysisUtils.PCT_FORMATTER
              .format(hit.getQueryGlobalCoverage()) + "%";
          break;
        case H_COVERAGE:
          val = AnalysisUtils.PCT_FORMATTER.format(hit.getHitGlobalCoverage())
              + "%";
          break;
        case ORGANISM:
          si = hit.getSequenceInfo();
          if (si != null && si.getOrganism() != null)
            val = si.getOrganism();
          else
            val = "?";
          break;
        case DIVISION:
          si = hit.getSequenceInfo();
          if (si != null && si.getDivision() != null)
            val = si.getDivision();
          else
            val = "?";
          break;
        case TAXONOMY:
          si = hit.getSequenceInfo();
          if (si != null && si.getTaxonomy() != null)
            val = si.getTaxonomy();
          else
            val = "?";
          break;
        case C_DATE:
          si = hit.getSequenceInfo();
          if (si != null && si.getCreationDate() != 0)
            val = SequenceInfoViewer.prepareDate(String.valueOf(si
                .getCreationDate()));
          else
            val = "?";
          break;
        case U_DATE:
          si = hit.getSequenceInfo();
          if (si != null && si.getUpdateDate() != 0)
            val = SequenceInfoViewer.prepareDate(String.valueOf(si
                .getUpdateDate()));
          else
            val = "?";
          break;
      }
      return val;
    }

    public Object getValueAt(int row, int col) {
      Object obj = null;

      if (_hits == null)
        return (EMPTY_STR);

      if (row == -1)
        return _colItems[col];

      if (col < 0)// this is used by the SeqAlignViewer Component to get full
                  // Hit data
        obj = _hits[row];
      else
        obj = getValue(_hits[row], col);

      return obj == null ? EMPTY_STR : obj;
    }

    public void sortData() {
      if (_hits == null || _hits.length == 0)
        return;
      BlastHitHspImplem[] hitsCopy = new BlastHitHspImplem[_hits.length];
      System.arraycopy(_hits, 0, hitsCopy, 0, _hits.length);
      Arrays.sort(hitsCopy, _blastHitHspSortComparator);
      _hits = hitsCopy;
    }
  }

  private class TableComponentAdapter extends ComponentAdapter {
    private int oldWidth;

    public void componentResized(ComponentEvent e) {
      Component parent;

      int width;
      parent = (Component) e.getSource();
      width = parent.getBounds().width;
      if (width == oldWidth)
        return;
      _blastList.initColumnSize(width, null);
      oldWidth = width;
    }
  }

  private class BlastHitTableSelectionListener implements ListSelectionListener {
    // Listen to the JTable embedded within BlastHitList component
    public void valueChanged(ListSelectionEvent e) {
      ListSelectionModel lsm;
      ArrayList<BlastHitHspImplem> hits = null;
      BlastHitHSP hit = null;
      Object obj;
      int[] sels;
      boolean isSingleSel;
      if (e.getValueIsAdjusting())
        return;
      if (_bLockSelection) {
        return;
      }
      lsm = (ListSelectionModel) e.getSource();
      isSingleSel = ((!lsm.isSelectionEmpty() && (lsm.getMinSelectionIndex() == lsm
          .getMaxSelectionIndex())));
      if (!lsm.isSelectionEmpty()) {
        hits = new ArrayList<BlastHitHspImplem>();
        sels = _blastList.getSelectedRows();
        for (int i : sels) {
          obj = _blastList.getModel().getValueAt(i, -1);
          if (obj != null && obj instanceof BlastHitHspImplem)
            hits.add((BlastHitHspImplem) obj);
        }
      } else {
        hits = null;
      }
      if (isSingleSel) {
        obj = _blastList.getModel().getValueAt(lsm.getMinSelectionIndex(), -1);
        if (obj != null && obj instanceof BlastHitHspImplem)
          hit = (BlastHitHSP) obj;
      }
      if (_updateSupport != null)
        _updateSupport.fireHitChange(new BlastHitListEvent(BlastHitTable.this,
            hits, BlastHitListEvent.HIT_CHANGED));
      activateSelBasedActions(!lsm.isSelectionEmpty(), isSingleSel);
      blastHitHspChanged(hit, 1);
    }

  }

  private class ColumnSelector extends MouseAdapter {
    JTable table;

    public ColumnSelector(JTable table) {
      this.table = table;
    }

    public void mousePressed(MouseEvent e) {
      JTableHeader header;
      int i, col, colID, nCol;
      Rectangle r;
      boolean bSort = false;

      header = table.getTableHeader();
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
      col = table.columnAtPoint(e.getPoint());
      colID = _colItems[col].getIID();
      if (colID == _colIdForSorting) {
        _ascentSortOrder = !_ascentSortOrder;
      } else {
        _colIdForSorting = colID;
        _ascentSortOrder = true;
      }
      header.repaint();
      _blastList.clearSelection();
      _blastHitHspSortComparator.setAscent(_ascentSortOrder);
      _blastHitHspSortComparator.setColID(_colIdForSorting);
      ((BlastHitTableModel) table.getModel()).sortData();
      _blastList.repaint();
    }
  }

  protected TableCellRenderer createDefaultRenderer() {
    DefaultTableCellRenderer label = new UIResourceTableCellRenderer();
    label.setHorizontalAlignment(JLabel.CENTER);
    return label;
  }

  private class UIResourceTableCellRenderer extends DefaultTableCellRenderer
      implements UIResource {
    /**
     * 
     */
    private static final long serialVersionUID = 5981037911491336663L;
    private int[]             xPoints          = new int[3];
    private int[]             yPoints          = new int[3];
    private int               curColID;

    public UIResourceTableCellRenderer() {
      super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      if (table != null) {
        JTableHeader header = table.getTableHeader();
        if (header != null) {
          setForeground(header.getForeground());
          setBackground(header.getBackground());
          setFont(header.getFont());
        }
      }

      setText((value == null) ? "" : value.toString());
      if (EZEnvironment.getOSType() == EZEnvironment.MAC_OS) {
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setBackground(UIManager.getColor("Panel.background"));
      } else {
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
      }
      curColID = _colItems[column].getIID();
      return this;
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (_colIdForSorting != curColID)
        return;

      int topInset = this.getBorder().getBorderInsets(this).top;
      int rightInset = this.getBorder().getBorderInsets(this).right;
      int right = this.getBounds().width - 2 * rightInset;

      if (_ascentSortOrder) {
        xPoints[0] = right - 9;
        xPoints[1] = xPoints[0] + 3;
        xPoints[2] = xPoints[0] + 6;
        yPoints[0] = topInset + 5;
        yPoints[1] = yPoints[0] - 4;
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
      if (SwingUtilities.isRightMouseButton(e) && _contextMnu != null) {
        _contextMnu.showContextMenu(e.getX(), e.getY());
      }
    }
  }
}
