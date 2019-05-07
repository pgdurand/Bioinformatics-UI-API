package bzh.plealog.bioinfo.ui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.plealog.genericapp.api.EZEnvironment;

/**
 * This is a utility class aims at providing a search component. It can be used to hightlight
 * rows containing a keyword. By default the component scans the entire table, but the method
 * setColumnsToScan() can be used to set the list of columns to scan.
 * 
 * @author Patrick Durand
 */
@SuppressWarnings("serial")
public class TableSearcherComponent extends JPanel implements TableSearcherComponentAPI {

	private JButton             _searchNext;
	private JButton             _searchPrev;
    private int                 _lastMatchRow = -1;
    private String              _helper = "Enter a pattern to search for";
    private SearchField         _sf;
    private JTable              _table;
    private int[]               _colsIdx;
    private int[]               _indices;
    private Pattern             _pattern;
    private ArrayList<TableSearcherComponentAction> _userActions;
    private boolean             _searching;
    
	private static final Color LOCATE_NO_MATCH_BG_COLOR = new Color(255,102,102);
	private static final Color LOCATE_NO_MATCH_FG_COLOR = Color.white;
	private static final Color LOCATE_MATCH_FG_COLOR = Color.black;
	private static final Color LOCATE_MATCH_BG_COLOR = Color.white;

	@SuppressWarnings("unused")
	private TableSearcherComponent(){}
    
	/**
	 * Constructor.
	 * 
	 * @param table the table used to search for keyword.
	 */
    public TableSearcherComponent (JTable table){
    	_table = table;
    	_userActions = new ArrayList<TableSearcherComponentAction>();
    	buildGUI();
    }
    /**
     * Reset the component.
     */
	public void resetSearch(){
		_lastMatchRow = -1;
		_pattern = null;
		_indices = null;
		_sf.setTextForeground(LOCATE_MATCH_FG_COLOR);
    	_sf.setTextBackground(LOCATE_MATCH_BG_COLOR);
	}
	/**
	 * Set a list of columns that have to used to scan for the keyword. By default, this component
	 * will scan all columns of the table.
	 * 
	 * @param colsIdx an array of column indices
	 * */
	public void setColumnsToScan(int[] colsIdx){
		_colsIdx = colsIdx;
	}
	public JButton addUserAction(ImageIcon icon, TableSearcherComponentAction act){
		JButton btn = _sf.addUserAction(icon, act);
		act.setEnabled(false);
		act.setTableSearcherComponentActionGateway(this);
		_userActions.add(act);
		return btn;
	}
	private void buildGUI(){
		
		_sf = new SearchField();
		_sf.setHelperText(_helper);
		_searchPrev = _sf.addUserAction(EZEnvironment.getImageIcon("run_back.png"), new SearchPrevActionListener());
		_searchNext = _sf.addUserAction(EZEnvironment.getImageIcon("run.png"), new SearchNextActionListener());
		_sf.addPropertyChangeListener(SearchField.PROPERTY_TEXT,
				new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				String l = evt.getNewValue().toString();
				boolean enable = (l!=null && l.length()>0);
				_searchPrev.setEnabled(enable);
				_searchNext.setEnabled(enable);
				if (_lastMatchRow!=-1){
					resetSearch();
				}
				for(TableSearcherComponentAction act : _userActions){
					act.setSearchText(l);
					act.setEnabled(enable);
				}
			}
		});
		_searchPrev.setEnabled(false);
		_searchNext.setEnabled(false);
		_sf.addKeyListener(new MyKeyListener());
		Dimension dim = _sf.getPreferredSize();
		dim.width = 260;
		_sf.setPreferredSize(dim);
		_sf.setMaximumSize(dim);
		this.setLayout(new BorderLayout());
		this.add(_sf, BorderLayout.CENTER);
	}
	private boolean locatePattern(int row, int colCount){
		String field = "";
		Object val;
		for(int col = 0 ; col < colCount ; col++){
			if (_colsIdx != null){
				val = _table.getValueAt(row, _colsIdx[col]);
			}
			else{
				val = _table.getValueAt(row, col);
			}
			
			if (val == null)
				continue;
			
			field = val.toString();
			if(matchStrings(field)) {  
	        	_sf.setTextForeground(LOCATE_MATCH_FG_COLOR);
	        	_sf.setTextBackground(LOCATE_MATCH_BG_COLOR);
	        	_lastMatchRow = row;
	        	scrollToMachRow(row);
	        	return true;
	        } 
		}
		return false;
	}
	private void locatePattern(int startRow, boolean next){
		int rowCount, colCount;
		
		if(_indices!=null){
			if(next)
				_lastMatchRow++;
			else
				_lastMatchRow--;
			if (_lastMatchRow<0){
				_lastMatchRow = 0;
				_sf.setTextForeground(LOCATE_NO_MATCH_FG_COLOR);
				_sf.setTextBackground(LOCATE_NO_MATCH_BG_COLOR);
				return;
			}
			if (_lastMatchRow>=_indices.length){
				_lastMatchRow = _indices.length-1;
				_sf.setTextForeground(LOCATE_NO_MATCH_FG_COLOR);
				_sf.setTextBackground(LOCATE_NO_MATCH_BG_COLOR);
				return;
			}
			_sf.setTextForeground(LOCATE_MATCH_FG_COLOR);
        	_sf.setTextBackground(LOCATE_MATCH_BG_COLOR);
        	scrollToMachRow(_indices[_lastMatchRow]);
		}
		else{
			if (_colsIdx != null){
				colCount = _colsIdx.length;
			}
			else{
				colCount = _table.getColumnCount();
			}
			if (next){
				rowCount = _table.getRowCount();
				for(int row = startRow; row < rowCount; row++) {  
					if (locatePattern(row, colCount)){
						return;
					}
				}
			}
			else{
				for(int row = startRow; row > -1; row--) {  
					if (locatePattern(row, colCount)){
						return;
					}
				}
			}
			_sf.setTextForeground(LOCATE_NO_MATCH_FG_COLOR);
			_sf.setTextBackground(LOCATE_NO_MATCH_BG_COLOR);
		}
	}

	private class SearchPrevActionListener extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (_searching)
				return;
			new MainSelectThread(false).start();
		}
	}

	private class SearchNextActionListener extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (_searching)
				return;
			new MainSelectThread(true).start();
		}
	}

	private void scrollToMachRow(int row) {
		if (_table.getSelectionModel().isSelectedIndex(row)==false){
			_table.setRowSelectionInterval(row, row);
		}
		_table.scrollRectToVisible(new Rectangle(_table.getCellRect(row, 0, true)));
		_table.repaint();
	}

	private boolean matchStrings (String field) {
		Matcher matcher = null;
		
		if (_pattern == null){
			String txt = _sf.getText();
			_pattern = Pattern.compile(txt.toLowerCase());
		}
	    matcher =  _pattern.matcher(field.toLowerCase());
		return matcher.find();
	}

	private class MyKeyListener extends KeyAdapter {
    	public void keyReleased(KeyEvent e){
    		super.keyReleased(e);
    		if (e.getKeyCode()==KeyEvent.VK_ENTER){
    			_searchNext.doClick();
    		}
    	}
    }
	private class MainSelectThread extends Thread {
    	private boolean next;
		
    	public MainSelectThread(boolean next){
    		this.next = next;
    	}
    	
    	public void run(){
    		_searching = true;
    		EZEnvironment.setWaitCursor();
    		if(next){
    			locatePattern(_lastMatchRow + 1, true);
    		}
    		else{
    			locatePattern(_lastMatchRow - 1, false);
    		}
    		EZEnvironment.setDefaultCursor();
    		_searching = false;
    	}
    }

	@Override
	public void setPrecomputedIndex(int[] indices) {
		_indices = indices;
		_lastMatchRow = 0;
	}
}
