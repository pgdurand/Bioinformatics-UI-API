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
package bzh.plealog.bioinfo.ui.feature;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import com.plealog.genericapp.api.EZEnvironment;

import bzh.plealog.bioinfo.ui.resources.SVMessages;

/**
 * This is a simple text viewer with a Copy action facility.
 * 
 * @author Patrick G. Durand
 */
public class SimpleTextViewer extends JPanel {
  private static final long serialVersionUID = 8024992740674319511L;
  private JTextPane               _viewer;
  private HashMap<Object, Action> _componentActions;

  public SimpleTextViewer(){
    this(null);
  }
  public SimpleTextViewer(String contentType){
    JPanel    mainBtnPanel, mainPanel;
    JToolBar  toolBar;
    Action    act;
    JButton   btn;
    ImageIcon icon;

    //creates the Viewer panel
    _viewer = new JTextPane()/*  {
	         public boolean getScrollableTracksViewportWidth()  {
	             return false;   // force display of horizontal scroll bar
	          }
	          public EditorKit createDefaultEditorKit()  {
	             return new StyledEditorKit(); 
	          }
	       }*/; 
    _viewer.setFont(new Font("courier", Font.PLAIN, 12));
    _viewer.setEditable(false);
    if (contentType!=null)
      _viewer.setContentType(contentType);
    //adds custom key accelerators
    addBindings(_viewer);
    //retrieve default Actions map from th editor
    createActionTable(_viewer);

    //creates the custom toolbar
    toolBar = new JToolBar();
    toolBar.setFloatable(false);

    //copy btn
    act = getActionByName(DefaultEditorKit.copyAction);
    if (act!=null){
      icon = EZEnvironment.getImageIcon("copy.png");
      if (icon!=null){
        act = new LocalCopyActionListener("", icon, act);
      }
      else{
        act = new LocalCopyActionListener(
            SVMessages.getString("SimpleTextViewer.copy.btn"), null, act);
      }
      btn=toolBar.add(act);
      btn.setToolTipText(SVMessages.getString("SimpleTextViewer.copy.tip"));
    }
    mainBtnPanel = new JPanel(new BorderLayout());
    mainBtnPanel.add(toolBar, BorderLayout.WEST);
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(mainBtnPanel, BorderLayout.SOUTH);
    mainPanel.add(new JScrollPane(_viewer), BorderLayout.CENTER);

    this.setLayout(new BorderLayout());
    this.add(mainPanel, BorderLayout.CENTER);
    this.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    this.setPreferredSize(new Dimension(350, 250));
  }
  /**
   * Sets the text to be viewed.
   */
  public void setText(String txt){
    _viewer.setText(txt!=null ? txt : "");
    _viewer.setCaretPosition(0);
  }

  /**
   * Retrieves an action given its name. Call this method after a call to
   * createActionTable.
   */
  private Action getActionByName(String name) {
    return _componentActions.get(name);
  }

  /**
   * Adds particular key accelerators to the viewer component.
   */
  private void addBindings(JTextPane editor) {
    InputMap inputMap = editor.getInputMap();

    //Ctrl-c to copy current selection
    KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK);
    inputMap.put(key, DefaultEditorKit.copyAction);

    //Ctrl-v to paste clipboard content
    key = KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK);
    inputMap.put(key, DefaultEditorKit.pasteAction);

    //Ctrl-x to cut current selection
    key = KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK);
    inputMap.put(key, DefaultEditorKit.cutAction);
  }
  /**
   * Retrieves the action associated to a particular text component.
   */
  private void createActionTable(JTextComponent textComponent) {
    _componentActions = new HashMap<Object, Action>();
    Action[] actionsArray = textComponent.getActions();
    for (int i = 0; i < actionsArray.length; i++) {
      Action a = actionsArray[i];
      _componentActions.put(a.getValue(Action.NAME), a);
    }
  }
  private class LocalCopyActionListener extends AbstractAction{
    private static final long serialVersionUID = 2942418933543359763L;
    private Action cpyAction;

    public LocalCopyActionListener(String name, Icon icon, Action act) {
      super(name, icon);
      cpyAction = act;
    }
    public void actionPerformed(ActionEvent event){
      if (_viewer.getSelectedText()==null){
        EZEnvironment.displayInfoMessage(
            EZEnvironment.getParentFrame(),
            SVMessages.getString("SimpleTextViewer.copy.msg1"));
      }
      else{
        cpyAction.actionPerformed(event);
      }
    }
  }
}
