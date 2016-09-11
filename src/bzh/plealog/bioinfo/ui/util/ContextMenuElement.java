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

import java.util.List;

import javax.swing.Action;

/**
 * This class defines an element to be used with a ContextMenuManager.
 * 
 * @author Patrick G. Durand
 */
public class ContextMenuElement {
  private Action       act;
  private List<ContextMenuElement> subActions;

  /**
   * Constructor.
   */
  public ContextMenuElement(){}

  /**
   * Constructor.
   * 
   * @param act action to wrap into a menu element
   */
  public ContextMenuElement(Action act) {
    super();
    this.act = act;
  }

  /**
   * Constructor.
   * 
   * @param act action to wrap into a menu element
   * @param subActions list of sub-actions of this menu element
   */
  public ContextMenuElement(Action act, List<ContextMenuElement> subActions) {
    super();
    this.act = act;
    this.subActions = subActions;
  }
  /**
   * Get the action that will be used to create the menu item.
   * 
   * @return the action wrapped in this menu element
   */
  public Action getAct() {
    return act;
  }
  /**
   * Set the action that will be used to create the menu item.
   * 
   * @param act the action wrapped in this menu element
   */
  public void setAct(Action act) {
    this.act = act;
  }
  /**
   * Get the list of sub-items. Please note that this release of
   * ContextMenuManager only handles one level of sub-items.
   * 
   * @return a list of sub-items or null if none are available
   */
  public List<ContextMenuElement> getSubActions() {
    return subActions;
  }
  /**
   * Set the list of sub-items. Please note that this release of
   * ContextMenuManager only handles one level of sub-items.
   * 
   * @param subActions a list of sub-items
   */
  public void setSubActions(List<ContextMenuElement> subActions) {
    this.subActions = subActions;
  }


}
