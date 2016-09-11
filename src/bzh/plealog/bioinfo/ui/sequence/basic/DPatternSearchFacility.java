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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.EventListenerList;

import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.ui.sequence.event.DPatternEvent;
import bzh.plealog.bioinfo.ui.sequence.event.DPatternListener;
import bzh.plealog.bioinfo.ui.util.SearchField;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;

public class DPatternSearchFacility {
  private JButton             _search;
  private DSequence           _sequence;
  private boolean             _inSearch;
  private int                 _lastSearchPosition = -1;
  private String              _helper = "Enter a pattern to search for";
  private SearchField         _sf;

  //listener for sequence selection.
  private EventListenerList _listenerList = new EventListenerList();

  public DPatternSearchFacility(DSequence sequence){
    setSequence(sequence);
  }
  public void resetSearch(){
    _lastSearchPosition = -1;
    _inSearch = false;
  }
  public void setSequence(DSequence sequence){
    _sequence = sequence;
    resetSearch();
  }

  /**
   * This class handles the save button actions.
   */
  private class SearchButtonActionListener extends AbstractAction{
    private static final long serialVersionUID = 1769638723310098361L;
    private Matcher matcher;

    public void actionPerformed(ActionEvent e){
      Pattern seed;
      String  str, userPattern;
      int     start, end;

      userPattern = _sf.getText();
      if (_sequence==null || userPattern.length()<1){
        return;
      }
      str = userPattern;
      if (_inSearch==false){
        try{
          seed = Pattern.compile(str, Pattern.CASE_INSENSITIVE);
          matcher = seed.matcher(_sequence.toString());
        }
        catch(Exception ex){
          JOptionPane.showMessageDialog(
              EZEnvironment.getParentFrame(),
              "Invalid pattern: "+str,
              EZApplicationBranding.getAppName(),
              JOptionPane.WARNING_MESSAGE);
          return;
        }
        _lastSearchPosition = -1;
        _inSearch = true;
      }

      if (!matcher.find(_lastSearchPosition+1)){
        _inSearch = false;
        fireDPatternEvent(new DPatternEvent(DPatternSearchFacility.this, _sequence, userPattern, -1, -1));
        return;
      }
      //returned values are zero-based, but end is the char located after the last pattern match
      _lastSearchPosition = start = matcher.start();
      end = matcher.end();
      fireDPatternEvent(new DPatternEvent(DPatternSearchFacility.this, _sequence, userPattern, start, end-1));
    }
  }
  public SearchField getSearchForm(){

    _sf = new SearchField();
    _sf.setHelperText(_helper);
    _search = _sf.addUserAction(EZEnvironment.getImageIcon("run.png"), new SearchButtonActionListener());
    _sf.addPropertyChangeListener(SearchField.PROPERTY_TEXT,
        new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        String l = evt.getNewValue().toString();
        _search.setEnabled((l!=null && l.length()>0));
        if (_lastSearchPosition!=-1){
          resetSearch();
        }
      }
    });
    _search.setEnabled(false);
    _sf.addKeyListener(new MyKeyListener());
    Dimension dim = _sf.getPreferredSize();
    dim.width = 260;
    _sf.setPreferredSize(dim);
    _sf.setMaximumSize(dim);
    return _sf;
  }
  private class MyKeyListener extends KeyAdapter {
    public void keyReleased(KeyEvent e){
      super.keyReleased(e);
      if (e.getKeyCode()==KeyEvent.VK_ENTER){
        _search.doClick();
      }
    }
  }
  /**
   * Adds a DPatternListener on this viewer.
   */
  public void addDPatternListener(DPatternListener l) {
    _listenerList.add(DPatternListener.class, l);
  }

  /**
   * Removes a DPatternListener from this viewer.
   */
  public void removeDPatternListener(DPatternListener l) {
    _listenerList.remove(DPatternListener.class, l);
  }
  /**
   * Fire a pattern matching event.
   */
  protected void fireDPatternEvent(DPatternEvent event) {
    Object[] listeners = _listenerList.getListenerList();
    for (int i = listeners.length-2; i>=0; i-=2) {
      if (listeners[i]==DPatternListener.class) {
        ((DPatternListener)listeners[i+1]).patternMatched(event);
      }
    }
  }
}
