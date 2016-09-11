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

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;

/**
 * This is a basic component that can be used within a table cell to
 * display an action button. This button, when clicked, will then display
 * within a dialog box the content of the cell located in the column
 * preceding this cell editor in the table. So, do not use this editor in
 * column zero of a table.
 * 
 * @author Patrick G. Durand
 */
public class TableCellButtonEditor extends DefaultCellEditor {
  private static final long serialVersionUID = 1951907438855785768L;
  private JButton          button;
  private String           label;
  private SimpleTextViewer stv;

  public TableCellButtonEditor(JCheckBox checkBox) {
    super(checkBox);
    button = new JButton();
    button.setOpaque(true);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(
            EZEnvironment.getParentFrame(), 
            stv,
            EZApplicationBranding.getAppName(),
            JOptionPane.PLAIN_MESSAGE,
            null);
        fireEditingStopped();
      }
    });
    stv = new SimpleTextViewer();
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
    label = (value == null) ? "" : value.toString();
    button.setText(label);
    if (column>0){
      Object val = table.getValueAt(row, FeatureViewer.QUAL_VALUE);
      stv.setText(val!=null?val.toString():"");
    }
    return button;
  }

  public Object getCellEditorValue() {
    return new String(label);
  }

  public boolean stopCellEditing() {
    return super.stopCellEditing();
  }

  protected void fireEditingStopped() {
    super.fireEditingStopped();
  }
}
