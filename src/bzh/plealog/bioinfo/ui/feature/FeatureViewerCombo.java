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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;

import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSelectionListener;

/**
 * This class implements a FeatureTable viewer. It adds to this view a
 * Feature Type controller relying on a combo-box.
 * 
 * @author Patrick G. Durand
 */
public class FeatureViewerCombo extends FeatureViewer {
  private static final long serialVersionUID = -4528182274441928087L;
  private JComboBox<FeatureType>  _featTypes;

  private String                  _lastSelectedFeatType = FeatureSelectionListener.ALL_TYPE;
  
  /**
   * Default constructor.
   * 
   * @param fwl a FeatureWebLinker instance
   */
  public FeatureViewerCombo(FeatureWebLinker fwl){
    super(fwl, true);
    setUI();
  }

  /**
   * Default constructor.
   * 
   * @param fwl a FeatureWebLinker instance
   * @param showQualTable figures out whether or not the Qualifier table
   * has to be displayed
   */
  public FeatureViewerCombo(FeatureWebLinker fwl, boolean showQualTable){
    super(fwl, showQualTable);
    setUI();
  }

  /**
   * Prepare the UI.
   */
  private void setUI(){
    _featTypes = new JComboBox<FeatureType>();
    _featTypes.addActionListener(new FeatureTypeDisplayComboListener());
    _featTypes.setEnabled(false);
    setFeatureTypeController(_featTypes, CONTROLLER_LOCATION.TOP);
  }

  /**
   * Update the list of FeatureTypes.
   * 
   * @param ft a FeatureTable
   */
  protected void updateFeatureTypeCombo(FeatureTable ft){
    FeatureType type;
    int         i, size, selIdx = 0;
    List<FeatureType> featNames;
    
    featNames = getFeatureNamesList(ft);
    if (featNames.isEmpty()){
      _featTypes.setEnabled(false);
      return;
    }
    _updating = true;
    _featTypes.setEnabled(true);
    featNames.add(0, new FeatureType(FeatureSelectionListener.ALL_TYPE, ft.features()));
    size = featNames.size();
    for(i=0;i<size;i++){
      type = featNames.get(i);
      _featTypes.addItem(type);
      if (type.getName().equals(_lastSelectedFeatType)){
        selIdx = i;
      }
    }
    _updating = false;
    _featTypes.setSelectedIndex(selIdx);
  }

   /**
    * Sets a new FeatureTable.
    */
    public void setData(FeatureTable fTable){
      _featTypes.removeAllItems();
      _featTypes.setEnabled(false);
      super.setData(fTable);
      updateFeatureTypeCombo(fTable);
    }
    
    private class FeatureTypeDisplayComboListener implements ActionListener{
      public void actionPerformed(ActionEvent e){
        Object obj;

        if (_updating)
          return;
        JComboBox<?> cb = (JComboBox<?>) e.getSource();
        obj = cb.getSelectedItem();
        if (obj==null)
          return;
        _lastSelectedFeatType = ((FeatureType)obj).getName();
        String[] sel = new String[]{_lastSelectedFeatType};
        updateFeatureList(sel);
        fireFeatureTypesSelectedEvent(sel);
      }
    }

}
