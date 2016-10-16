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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import bzh.plealog.bioinfo.api.data.sequence.DLocation;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSymbol;
import bzh.plealog.bioinfo.api.data.sequence.DViewerSystem;
import bzh.plealog.bioinfo.ui.sequence.event.DDSelectionRange;
import bzh.plealog.bioinfo.ui.sequence.event.DDSequenceViewerConn;
import bzh.plealog.bioinfo.ui.sequence.event.DSelectionListenerSupport;
import bzh.plealog.bioinfo.ui.sequence.event.DSequenceSelectionEvent;
import bzh.plealog.bioinfo.ui.sequence.event.DSequenceSelectionListener;

import com.plealog.genericapp.ui.common.ContextMenuManager;

/**
 * This is a basic sequence viewer implemented on top of a JTable.
 * 
 * @author Patrick G. Durand
 */
public class DSequenceTableViewer extends JTable implements DSequenceSelectionListener, DDSequenceViewerConn {
  private static final long serialVersionUID = -3332072451012875677L;
  private Font                      _fnt = new Font("Arial", Font.PLAIN, 10);
  //private ColorPolicyConfig         _cpc;
  private TDMouseListener           _mListener;
  private int                       _cellW = 15;
  private int                       _nbBlockPerLine = DEFAULT_BLOCK_PER_LINE;
  private int                       _blockSize = DEFAULT_BLOCK_SIZE;
  private JTextArea                 _positionF;
  private ContextMenuManager        _mnuManager;
  private DSelectionListenerSupport _lSupport;

  //listener for sequence selection.
  //private EventListenerList _listenerList = new EventListenerList();
  private Color clr3 = UIManager.getDefaults().getColor("Table.selectionBackground");
  private Color clr4 = UIManager.getDefaults().getColor("Table.selectionForeground");

  public static final int DEFAULT_BLOCK_SIZE = 10;
  public static final int DEFAULT_BLOCK_PER_LINE = 5;

  /**
   * Default constructor.
   */
  public DSequenceTableViewer(){
    this(new TableDSequenceModel());
  }
  /**
   * Create a viewer with a table sequence model.
   */
  public DSequenceTableViewer(TableDSequenceModel model) {
    super(model);
    initialize();
    //_cpc = (ColorPolicyConfig) ConfigManager.getConfig(ColorPolicyConfig.NAME);
  }
  /**
   * Register the component used by this viewer to broadcast selection events.
   */
  public void registerSelectionListenerSupport(DSelectionListenerSupport lSupport){
    _lSupport = lSupport;
    _lSupport.addDSequenceSelectionListener(this);
  }
  /**
   * Sets the block size of this viewer. Default is 10, i.e. blocks of ten letters are
   * displayed.
   */
  public void setBlockSize(int blockSize){
    _blockSize = blockSize;
    ((TableDSequenceModel)this.getModel()).setBlockSize(blockSize);
    this.repaint();
  }
  /**
   * Sets the number of blocks per row of this viewer. Default is 5, i.e. each row display
   * 5 blocks of blockSize letters.
   */
  public void setBlockPerLine(int bpl){
    _nbBlockPerLine = bpl;
    ((TableDSequenceModel)this.getModel()).setBlockPerLine(bpl);
    this.repaint();
  }

  /**
   * Overload the JTable method to only accept a TableDSequenceModel table model.
   * Throws a RuntimeException otherwise.
   */
  public void setModel(TableModel tm){
    if ((tm instanceof TableDSequenceModel) == false)
      throw new RuntimeException("Table Model is invalid, expecting a TableDSequenceModel");
    super.setModel(tm);
  }
  /**
   * Sets the field displaying the mouse position mapped in sequence coordinate. Use this
   * field to display that information wherever you want.
   */
  public void setPositionField(JTextArea positionF){
    _positionF = positionF;
  }
  /**
   * Sets up the UI of this viewer.
   */
  private void initialize(){
    this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    this.setColumnSelectionAllowed(false);
    this.setRowSelectionAllowed(false);
    this.setCellSelectionEnabled(false);
    this.getTableHeader().setReorderingAllowed(false);
    this.setShowGrid(false);
    this.setIntercellSpacing(new Dimension(0,0));
    this.setFont(_fnt);
    intCellWidth();
    this.setTableHeader(null);
    _mListener = new TDMouseListener();
    this.addMouseListener(_mListener);
    this.addMouseMotionListener(_mListener);
  }
  /**
   * Sets the sequence to be displayed by this viewer. Pass in null to reset the viewer content.
   */
  public void setSequence(DSequence sequence){
    TableDSequenceModel model;

    model = (TableDSequenceModel) this.getModel();
    model.setSequence(sequence);
    _mListener.setSequence(sequence!=null?sequence.size():0);
    if (_positionF!=null)
      _positionF.setText("");
    initColumnSize();
    this.updateUI();
  }
  /**
   * Returns the sequence currently displayed by this viewer.
   */
  public DSequence getSequence(){
    return ((TableDSequenceModel) this.getModel()).getSequence();
  }
  /**
   * Returns the selected region of the sequence currently displayed by this viewer.
   * Returns null if nothing is selected.
   */
  public DSequence getSelectedSequence(){
    List<DLocation> lst;
    DLocation       loc;
    StringBuffer    buf;
    DSequence       seq, curSeq;
    int             i, size;

    lst = getSelectedRanges();
    if (lst==null)
      return null;
    curSeq = this.getSequence();
    if (curSeq==null)
      return null;
    size = lst.size();
    buf = new StringBuffer();
    for(i=0;i<size;i++){
      loc = lst.get(i);
      buf.append(curSeq.getSubSequence(loc.getFrom(), loc.getTo()+1, false));
    }
    seq = DViewerSystem.getSequenceFactory().getSequence(new StringReader(buf.toString()), curSeq.getAlphabet());
    seq.createRulerModel(1, 1);
    seq.setSequenceInfo(curSeq.getSequenceInfo());
    return seq;
  }
  /**
   * Sets the selected region of the sequence displayed in the viewer.
   * Values have to be zero-based and absolute, so use the DSequence DRulerModel to get
   * an absolute position from a sequence coordinate. Set from and to to -1 to reset
   * selection.
   */
  public void setSelectedSequenceRange(int from, int to){
    DSequence curSeq = this.getSequence();
    if (curSeq==null)
      return;
    if (from==-1 && to==-1){
      _mListener.resetSelection();
      this.repaint();
      return;
    }
    from = Math.max(0, from);
    to = Math.min(curSeq.size()-1, to);
    this.repaint();
    _mListener.setSelectedSequence(this, from, to);
  }
  /**
   * Returns the global selected region of the sequence displayed in the viewer.
   * The method returns null if nothing is selected, otherwise the array contains
   * the selected region. Index zero contains from and index one contains to. Values
   * are zero-based. Please note that the values are absolute: use the DRulerModel
   * from the DSequence to switch to the sequence coordinate system.
   */
  public int[] getSelectedSequenceRange(){
    int from, to;

    from = _mListener.getStartSelection();
    to = _mListener.getStopSelection();
    if (from==-1 || to==-1)
      return null;
    else
      return new int[]{from, to};
  }
  /**
   * Returns a list of selected segments over the sequence.  Value are
   * zero-based and absolute: use the DRulerModel from the DSequence to 
   * switch to the sequence coordinate system. Returns null if nothing is selected.
   */
  public List<DLocation> getSelectedRanges(){
    return _mListener.getSelectedRanges();
  }
  public void setSelectionRanges(List<DLocation> locs){
    DLocation loc;
    int       i, size;

    size = locs.size();
    for(i=0;i<size;i++){
      loc = locs.get(i);
      if (i==0){
        _mListener.setSelectedSequence(this, loc.getFrom(), loc.getTo());
      }
      else{
        _mListener.addSelectedSequence(this, loc.getFrom(), loc.getTo());
      }
    }
    this.repaint();
  }
  /**
   * Sets a contextual popup menu to this viewer.
   */
  public void setContextMenu(ContextMenuManager contextMnu){
    if (contextMnu!=null){
      contextMnu.setParent(this);
    }
    _mnuManager = contextMnu;
  }
  public TableCellRenderer getCellRenderer(int row, int column) {
    TableCellRenderer tcr;
    boolean           useRV = false;
    DSymbol           symb, spaceSymb;

    tcr = super.getCellRenderer(row, column);
    if (tcr instanceof JLabel){
      JLabel lbl;

      lbl = (JLabel) tcr;
      lbl.setHorizontalAlignment(SwingConstants.CENTER);
      DSymbol symbol = (DSymbol) getModel().getValueAt(row, column);
      /*if (_cpc!=null && _cpc.useInverseVideo()){
            	useRV=true;
            }*/
      //todo: add an inverse video mode for the sequence viewer
      useRV = false;
      if (symbol.getGraphics()!=null){
        lbl.setBackground(
            useRV ? symbol.getGraphics().getTextColor() : Color.WHITE);
        lbl.setForeground(useRV ? Color.WHITE : symbol.getGraphics().getTextColor());
      }
      else{
        lbl.setForeground(Color.BLACK);
        lbl.setBackground(Color.WHITE);
      }
      symb = (DSymbol) this.getValueAt(row, column);
      spaceSymb = DViewerSystem.getIUPAC_Protein_Alphabet().getSymbol(DSymbol.SPACE_SYMBOL_CODE);
      if (_mListener.isCellMySelected(row, column) && !symb.equals(spaceSymb)){
        lbl.setForeground(clr4);
        lbl.setBackground(clr3);
      }
    }
    return tcr;
  }
  /**
   * Utility method used to setup cell width.
   */
  private void intCellWidth(){
    FontMetrics      fm;

    fm = this.getFontMetrics(this.getFont());
    _cellW = fm.getHeight();

  }
  public int getCellWidth(){
    return _cellW;
  }
  /**
   * Initializes columns size of table to default values.
   */
  private void initColumnSize(){
    TableColumnModel tcm;
    TableColumn      tc;
    int              i, size;

    intCellWidth();
    tcm = this.getColumnModel();
    size = tcm.getColumnCount();
    for (i=0;i<size;i++){
      tc = tcm.getColumn(i);
      tc.setPreferredWidth(_cellW);
      tc.setMinWidth(_cellW);
      tc.setMaxWidth(_cellW);
    }
  }

  /*private class TableComponentAdapter extends ComponentAdapter{
        public void componentResized(ComponentEvent e){
        	initColumnSize();
        }
    }*/
  public void selectionChanged(DSequenceSelectionEvent event){
    if (event.getSource() == this){
      return;
    }
    else{
      if (event.getSelectionType()==DSequenceSelectionEvent.SIMPLE_RANGE)
        setSelectedSequenceRange(event.getSelFrom(), event.getSelTo());
      else
        setSelectionRanges(event.getLocs());
    }
  }
  /**
   * This class has been designed to overload the standard JTable selection system that only allows 
   * rectangular selection of cells. This selection system behaves as a standard sequence viewers
   * to extend selection of cells (i.e. letters) through table rows.
   */
  private class TDMouseListener implements MouseListener, MouseMotionListener {
    private DDSelectionRange previousRange;//use to manage mouse selection
    private DDSelectionRange currentRange;//use to manage mouse selection
    private BitSet         selectionStates;//keep the selection state
    private int            lastPos, firstPos, seqSize;

    public TDMouseListener(){
      previousRange = new DDSelectionRange();
      currentRange = new DDSelectionRange();
    }
    private void setSequence(int size){
      seqSize = size;
      if (size==0)
        selectionStates = null;
      else
        selectionStates = new BitSet(size);
    }
    /**
     * Figures out if a cell is selected.
     */
    private boolean isCellMySelected(int row, int col){
      return selectionStates.get(row * (_nbBlockPerLine*_blockSize) + col - (col/(_blockSize+1)));
    }
    /**
     * Gets the global starting position of the selected region. Value is
     * are zero-based and absolute: use the DRulerModel
     * from the DSequence to switch to the sequence coordinate system. If selection
     * is empty, then -1 is returned.
     */
    private int getStartSelection(){
      return selectionStates.nextSetBit(0);
    }
    private int getNextSegment(int start){
      int stop = -1;
      for(int i=start; i>=0; i=selectionStates.nextSetBit(i+1)) { 
        stop=i;
      }
      return stop;
    }
    /**
     * Gets the global ending position of the selected region. Value is
     * zero-based and absolute: use the DRulerModel
     * from the DSequence to switch to the sequence coordinate system.
     */
    private int getStopSelection(){
      int i, stop=-1;

      i = selectionStates.nextSetBit(0);
      if (i==-1)
        return i;
      while(i>=0){
        stop = getNextSegment(i);
        i = selectionStates.nextSetBit(stop+1);
      }
      return stop;
    }
    /**
     * Returns a list of selected segments over the sequence.  Value are
     * zero-based and absolute: use the DRulerModel from the DSequence to 
     * switch to the sequence coordinate system. Returns null if nothing is selected.
     */
    private List<DLocation> getSelectedRanges(){
      ArrayList<DLocation> lst;
      int                  i, stop=-1;

      i = selectionStates.nextSetBit(0);
      if (i==-1)
        return null;
      lst = new ArrayList<DLocation>();
      while(i>=0){
        stop = selectionStates.nextClearBit(i+1)-1;
        lst.add(new DLocation(i, stop));
        i = selectionStates.nextSetBit(stop+1);
      }
      return lst;
    }
    /**
     * Clears the current selected region if any.
     */
    private void resetSelection(){
      selectionStates.clear();
      previousRange.reset();
      currentRange.reset();
    }
    /**
     * Sets a selection on the viewer. Values are zero-based and absolute: use 
     * the DRulerModel from the DSequence to switch to the sequence coordinate 
     * system.
     */
    private void setSelectedSequence(JTable viewer, int from, int to){
      Rectangle rect;
      int       row, col;

      previousRange.reset();
      currentRange.reset();
      selectionStates.clear();
      if (from>=selectionStates.size()-1)
        return;
      if (to>=selectionStates.size()-1)
        return;
      if (from!=-1 && to!=-1){
        if (from==to)
          selectionStates.set(from);
        else
          selectionStates.set(Math.min(from, to), Math.max(from, to)+1, true);
        row = from / (_nbBlockPerLine*_blockSize);
        col = from - row * (_nbBlockPerLine*_blockSize);
        col += (col/_blockSize);
        rect = viewer.getCellRect(row, col, true);
        viewer.scrollRectToVisible(rect);
      }
    }
    private void addSelectedSequence(JTable viewer, int from, int to){
      if (from>=selectionStates.size()-1)
        return;
      if (to>=selectionStates.size()-1)
        return;
      if (from==to)
        selectionStates.set(from);
      else
        selectionStates.set(Math.min(from, to), Math.max(from, to)+1, true);
    }
    public void mousePressed(MouseEvent e){
      DSequenceTableViewer td;
      Point                pt;
      int                  max;
      if (seqSize==0)
        return;
      if (SwingUtilities.isRightMouseButton(e)){
        return;
      }
      td = (DSequenceTableViewer) e.getSource();
      pt = e.getPoint();
      //extended selection with shift keyboard key
      if ((e.getModifiers() & MouseEvent.SHIFT_MASK)!=0){
        currentRange.startRow = previousRange.startRow;
        currentRange.lastRow = td.rowAtPoint(pt);
        currentRange.startCol = previousRange.startCol;
        currentRange.lastCol = td.columnAtPoint(pt);
      }
      else{
        currentRange.startRow = currentRange.lastRow = td.rowAtPoint(pt);
        currentRange.startCol = currentRange.lastCol = td.columnAtPoint(pt);
      }
      firstPos = currentRange.lastRow * (_nbBlockPerLine*_blockSize) + currentRange.lastCol - (currentRange.lastCol/(_blockSize+1));
      max = seqSize - 1;
      if (firstPos > max)
        firstPos = max;
      td.repaint();
    }

    public void mouseReleased(MouseEvent e){
      DSequenceTableViewer td;

      if (seqSize==0)
        return;
      if (SwingUtilities.isRightMouseButton(e)){
        if (_mnuManager!=null)
          _mnuManager.showContextMenu(e.getX(), e.getY());
        return;
      }
      td = (DSequenceTableViewer) e.getSource();
      if (currentRange.equals(previousRange)){
        selectionStates.clear();
        currentRange.reset();
        previousRange.reset();
      }
      else{
        previousRange.set(currentRange);
        updateSelectionStates();
      }
      firstPos = lastPos = -1;
      td.repaint();
      fireSelectionEvent();
    }
    private void updateSelectionStates(){
      //store selection in the bitset
      selectionStates.clear();
      int from = currentRange.startRow * (_nbBlockPerLine*_blockSize) + 
          currentRange.startCol - (currentRange.startCol/(_blockSize+1));
      int to = currentRange.lastRow * (_nbBlockPerLine*_blockSize) + 
          currentRange.lastCol - (currentRange.lastCol/(_blockSize+1));
      if (from<0 || to<0)
        return;
      int a, b;
      a = Math.min(from, to);
      b = Math.max(from, to);
      if (b>=seqSize)
        b = seqSize-1;
      selectionStates.set(a, b+1, true);
    }
    public void mouseDragged(MouseEvent e){
      DSequenceTableViewer td;
      Point                pt;

      if (SwingUtilities.isRightMouseButton(e)){
        return;
      }
      if(currentRange.startRow==-1)
        return;
      td = (DSequenceTableViewer) e.getSource();
      pt = e.getPoint();
      currentRange.lastRow = td.rowAtPoint(pt);
      currentRange.lastCol = td.columnAtPoint(pt);
      displayMouseLocation(e, true);
      updateSelectionStates();
      td.repaint();
    }
    private void fireSelectionEvent(){
      if (_lSupport!=null){
        _lSupport.setSelectedSequenceRange(
            DSequenceTableViewer.this, 
            DSequenceTableViewer.this.getSequence(), 
            getStartSelection(), 
            getStopSelection());
      }
    }
    private void displayMouseLocation(MouseEvent e, boolean displayRange){
      DSequenceTableViewer td;
      Point                pt;
      DSequence            seq;
      int                  row, col, pos;

      if (_positionF==null)
        return;
      td = (DSequenceTableViewer) e.getSource();
      seq = ((TableDSequenceModel)td.getModel()).getSequence();
      if (seq==null || seq.getRulerModel()==null)
        return;
      pt = e.getPoint();
      row = td.rowAtPoint(pt);
      col = td.columnAtPoint(pt);
      pos = row * (_nbBlockPerLine*_blockSize) + col - (col/(_blockSize+1));
      if (pos==lastPos)
        return;
      lastPos = pos;
      if (pos>=0 &&pos<seq.size()){
        if (displayRange){
          row = Math.min(firstPos, pos);
          col = Math.max(firstPos, pos);
          _positionF.setText(
              String.valueOf(seq.getRulerModel().getSeqPos(row))+" -\n"+
                  String.valueOf(seq.getRulerModel().getSeqPos(col)));
        }
        else{
          _positionF.setText(String.valueOf(seq.getRulerModel().getSeqPos(pos)));
        }
      }
      else{
        _positionF.setText("");
      }	

    }
    public void mouseMoved(MouseEvent e){
      displayMouseLocation(e, false);
    }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){
      if (_positionF!=null)
        _positionF.setText("");
    }
    public void mouseClicked(MouseEvent e){}

  }

}
