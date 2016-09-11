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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import com.Ostermiller.util.Browser;
import com.plealog.genericapp.api.EZEnvironment;

import bzh.plealog.bioinfo.ui.resources.SVMessages;

/**
 * This is a basic component that can be used within a table cell to
 * display an action button. This button, when clicked, will start a
 * web browser to display some data.
 * 
 * @author Patrick G. Durand
 */
public class TableCellButtonLinker extends DefaultCellEditor {
  private static final long serialVersionUID = -484976897199163455L;
  private JButton          button;
  private String           url;
  private FeatureWebLinker linker;

  private static final String EMPTY_STR = "";

  public TableCellButtonLinker(FeatureWebLinker fwl, JCheckBox checkBox) {
    super(checkBox);
    linker = fwl;
    button = new JButton();
    button.setOpaque(true);
    button.setText("");
    button.setIcon(EZEnvironment.getImageIcon("small_earth.png"));
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (url==null){
          return;
        }
        try {
          Browser.init();
          EZEnvironment.setWaitCursor();
          Browser.displayURL(url);
          EZEnvironment.setDefaultCursor();
        } catch (IOException ex) {
          String msg = SVMessages.getString("FeatureViewer.7");
          FeatureWebLinker.LOGGER.warn(msg+": "+ex);
          EZEnvironment.displayWarnMessage(EZEnvironment.getParentFrame(), msg+".");
        }
        fireEditingStopped();
      }
    });
  }

  public FeatureWebLinker getFeatureWebLinker(){
    return linker;
  }
  public Component getTableCellEditorComponent(JTable table, Object value,
      boolean isSelected, int row, int column) {
    if (isSelected) {
      button.setForeground(table.getSelectionForeground());
      button.setBackground(table.getSelectionBackground());
    } else {
      button.setForeground(table.getForeground());
      button.setBackground(table.getBackground());
    }
    if (linker!=null){
      url = linker.getURLFromQualifier(
          table.getValueAt(row, FeatureViewer.QUAL_NAME).toString(),
          table.getValueAt(row, FeatureViewer.QUAL_VALUE).toString());
    }
    else{
      url=null;
    }
    return button;
  }

  public Object getCellEditorValue() {
    return EMPTY_STR;
  }

  public boolean stopCellEditing() {
    return super.stopCellEditing();
  }

  protected void fireEditingStopped() {
    super.fireEditingStopped();
  }
}
