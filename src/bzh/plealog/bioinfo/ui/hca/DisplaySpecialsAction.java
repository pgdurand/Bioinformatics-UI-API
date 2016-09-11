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
package bzh.plealog.bioinfo.ui.hca;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;


/**
 * This class handles the action allowing to display special amino acids
 * using standard HCA symbols.
 * 
 * @author Patrick G. Durand
 * */
public class DisplaySpecialsAction extends AbstractAction {
  private static final long serialVersionUID = 9016774138060105752L;

  private PanelHca     _pHca;

  public static final String DISP_ON = "Display special symbols";
  public static final String DISP_OFF = "Do not display special symbols";

  /**
   * Action constructor.
   * 
   * @param name the name of the action.
   */
  public DisplaySpecialsAction(String name){
    super(name);
  }

  /**
   * Action constructor.
   * 
   * @param name the name of the action.
   * @param icon the icon of the action.
   */
  public DisplaySpecialsAction(String name, Icon icon){
    super(name, icon);
  }

  /**
   * Passes in the HCA component.
   */
  public void setPanelHca(PanelHca p){
    _pHca = p;
  }

  public void actionPerformed(ActionEvent event){
    if (_pHca==null)
      return;
    if (_pHca.isDisplaySpecialAsSymbols()){
      this.putValue(Action.NAME, DISP_ON);
    }
    else{
      this.putValue(Action.NAME, DISP_OFF);
    }
    _pHca.setDisplaySpecialAsSymbols(!_pHca.isDisplaySpecialAsSymbols());
    _pHca.repaint();
  }
}
