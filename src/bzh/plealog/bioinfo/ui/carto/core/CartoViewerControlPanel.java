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
package bzh.plealog.bioinfo.ui.carto.core;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import bzh.plealog.bioinfo.ui.resources.SVMessages;

/**
 * This class handles the select and zoom buttons associated to the viewer.
 * 
 * @author Patrick G. Durand
 */
public class CartoViewerControlPanel extends JPanel {
  private static final long serialVersionUID = -4849122127650794819L;
  private JToggleButton          _selectionBtn;
  private JToggleButton          _zoomBtn;
  private CartoViewerPanel           _svPanel;

  public CartoViewerControlPanel(CartoViewerPanel pnl){
    _svPanel = pnl;

    this.setLayout(new BorderLayout());
    this.add(createToolBar(), BorderLayout.WEST);
  }
  public JToggleButton getSelectionBtn(){
    return _selectionBtn;
  }
  public JToggleButton getZoomBtn(){
    return _zoomBtn;
  }
  private JPanel createToolBar(){
    JPanel        pnl;
    JToggleButton tBtn;

    pnl = new JPanel();

    tBtn = new JToggleButton(SVMessages.getString("SViewerPanel.btn.select.lbl"));
    tBtn.setToolTipText(SVMessages.getString("SViewerPanel.btn.select.tip"));
    tBtn.addActionListener(new SelectionModeActionListener());
    pnl.add(tBtn);
    _selectionBtn = tBtn;

    tBtn = new JToggleButton(SVMessages.getString("SViewerPanel.btn.zoom.lbl"));
    tBtn.setToolTipText(SVMessages.getString("SViewerPanel.btn.zoom.tip"));
    tBtn.addActionListener(new SelectionModeActionListener());
    pnl.add(tBtn);
    _zoomBtn = tBtn;

    _selectionBtn.setSelected(true);

    return pnl;
  }
  /**
   * This class manages action coming from the Selection mode button.
   */
  private class SelectionModeActionListener implements ActionListener {
    public void actionPerformed(ActionEvent event){
      if (event.getSource()==_selectionBtn){
        _svPanel.setMouseMode(CartoViewerPanel.MOUSE_MODE.SELECTION);
        _selectionBtn.setSelected(true);
        _zoomBtn.setSelected(false);
      }
      else{
        _svPanel.setMouseMode(CartoViewerPanel.MOUSE_MODE.ZOOM);
        _selectionBtn.setSelected(false);
        _zoomBtn.setSelected(true);
      }
    }
  }
}
