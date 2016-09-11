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
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bzh.plealog.bioinfo.api.data.feature.FeatureTable;

/**
 * This component is used to display the status information of a FeatureTable.
 * 
 * @author Patrick G. Durand
 */
public class FeatureStatusViewer extends JPanel {
  private static final long serialVersionUID = 9153946953865147941L;
  private JTextField _date;
  private JTextField _source;
  private JTextField _status;
  private JTextField _message;

  private static final SimpleDateFormat DATE_FOMATTER1 = 
      new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);

  private static final SimpleDateFormat DATE_FOMATTER2 = 
      new SimpleDateFormat("yyyyMMdd");

  public FeatureStatusViewer(){
    JLabel lbl;
    JPanel pnl1, pnl2, pnl3, pnl4, pnl5;

    pnl1 = new JPanel(new BorderLayout());
    lbl = new JLabel("Retrieval date:");
    _date = createTextField();
    pnl1.add(lbl, BorderLayout.WEST);
    pnl1.add(_date, BorderLayout.CENTER);
    lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    pnl1.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));

    pnl2 = new JPanel(new BorderLayout());
    lbl = new JLabel("Feature source:");
    _source = createTextField();
    pnl2.add(lbl, BorderLayout.WEST);
    pnl2.add(_source, BorderLayout.CENTER);
    lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    pnl2.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));

    pnl3 = new JPanel(new BorderLayout());
    lbl = new JLabel("Message:");
    _message = createTextField();
    pnl3.add(lbl, BorderLayout.WEST);
    pnl3.add(_message, BorderLayout.CENTER);
    lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    pnl3.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));

    pnl4 = new JPanel(new BorderLayout());
    lbl = new JLabel("Feature status:");
    _status = createTextField();
    pnl4.add(lbl, BorderLayout.WEST);
    pnl4.add(_status, BorderLayout.CENTER);
    lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    pnl4.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));

    pnl5 = new JPanel();
    pnl5.setLayout(new BoxLayout(pnl5, BoxLayout.Y_AXIS));
    pnl5.add(pnl1);
    pnl5.add(pnl2);
    pnl5.add(pnl4);
    pnl5.add(pnl3);
    this.setLayout(new BorderLayout());
    this.add(pnl5, BorderLayout.NORTH);
  }

  /**
   * Utility method to create a JTextField.
   */
  private JTextField createTextField(){
    JTextField tf;

    tf = new JTextField();
    tf.setEditable(false);
    tf.setBorder(null);
    tf.setOpaque(false);
    //tf.setForeground(DDResources.getSystemTextColor());
    return tf;
  }

  /**
   * Reset the viewer.
   */
  public void clear(){
    setData(null);
  }
  /**
   * Utility method to conevert date formats.
   */
  private String transformDate(String d){
    String  date="-";

    try {
      date = DATE_FOMATTER1.format(DATE_FOMATTER2.parse(d));
    } catch (Exception e) {
    }
    return date;
  }
  /**
   * Sets a new FeatureTable.
   * 
   * @param fTable the feature table to display in this component.
   */
  public void setData(FeatureTable fTable){
    String val;
    if (fTable==null){
      _date.setText("");
      _source.setText("");
      _status.setText("");
      _message.setText("");
    }
    else{
      val = fTable.getDate();
      _date.setText(val!=null?transformDate(val):"?");
      val = fTable.getSource();
      _source.setText(val!=null?val:"?");
      _status.setText(fTable.getStatus()!=FeatureTable.ERROR_STATUS ?
          FeatureTable.OK_STATUS_S : FeatureTable.ERROR_STATUS_S);
      val = fTable.getMessage();
      _message.setText((val!=null && !val.equals("-"))?val:"None");
    }
  }
}
