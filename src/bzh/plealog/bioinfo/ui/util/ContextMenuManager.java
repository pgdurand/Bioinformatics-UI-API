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

import java.awt.Font;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * This class defines a simple system to handle contextual popup menus.
 * 
 * @author Patrick G. Durand
 */
public class ContextMenuManager {
  private JPopupMenu _contextMenu;
  private JComponent _parent;

  private static final Font DEF_FNT = new Font("Arial", Font.PLAIN, 10);

  /**
   * Constructor.
   * 
   * @param parent the parent component of the popup menu. The menu is indeed
   * displayed relative to its parent.
   * @param actions the list of actions that have to be shown in the menu.
   */
  public ContextMenuManager(JComponent parent, List<ContextMenuElement> actions){
    setParent(parent);
    _contextMenu = new JPopupMenu();
    addActions(actions);
  }
  /**
   * Set the parent of this popup menu
   * 
   * @param parent the parent of this menu
   * */
  public void setParent(JComponent parent){
    _parent = parent;    	
  }
  /**
   * This method can be used to add actions to the contextual menu.
   * 
   * @param actions the list of actions to add to the popup menu
   */
  public void addActions(List<ContextMenuElement> actions){
    JMenuItem item;
    JMenu     mnu;
    for(ContextMenuElement act : actions){
      if (act != null){
        if (act.getSubActions()!=null){
          mnu = new JMenu(act.getAct());
          mnu.setFont(DEF_FNT);
          //as for now, only one level of sub-menus is allowed
          for(ContextMenuElement cme : act.getSubActions()){
            item = new JMenuItem(cme.getAct());
            item.setFont(DEF_FNT);
            mnu.add(item);
          }
          _contextMenu.add(mnu);
        }
        else{
          item = new JMenuItem(act.getAct());
          item.setFont(DEF_FNT);
          _contextMenu.add(item);
        }
      }
      else{
        _contextMenu.addSeparator();
      }
    }	
  }
  /**
   * Use this method to show the contextual menu.
   * 
   * @param x x position of the menu. Relative to parent's coordinate space.
   * @param y y position of the menu. Relative to parent's coordinate space.
   */
  public void showContextMenu(int x, int y){
    _contextMenu.show(_parent, x, y);
  }
}
