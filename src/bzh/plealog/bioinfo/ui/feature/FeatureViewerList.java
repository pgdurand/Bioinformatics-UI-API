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

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import com.plealog.genericapp.api.log.EZLogger;

import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.ui.util.CheckBoxList;
import bzh.plealog.bioinfo.ui.util.CheckBoxListItem;
import bzh.plealog.bioinfo.ui.util.CheckBoxListSelectionListener;

/**
 * This class implements a FeatureTable viewer. It adds to this view a
 * Feature Type controller relying on a list.
 * 
 * @author Patrick G. Durand
 */
public class FeatureViewerList extends FeatureViewer {
  private static final long serialVersionUID = -4528182274441928087L;
  private CheckBoxList  _featTypes;
  
  /**
   * Default constructor.
   * 
   * @param fwl a FeatureWebLinker instance
   */
  public FeatureViewerList(FeatureWebLinker fwl){
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
  public FeatureViewerList(FeatureWebLinker fwl, boolean showQualTable){
    super(fwl, showQualTable);
    setUI();
  }

  /**
   * Prepare the UI.
   */
  private void setUI(){
    _featTypes = new CheckBoxList();
    _featTypes.addCheckBoxListSelectionListener(new MyCheckBoxListSelectionListener());
    _featTypes.setEnabled(false);
    setFeatureTypeController(_featTypes, CONTROLLER_LOCATION.LEFT);
  }
  /**
   * Update the list of FeatureTypes.
   * 
   * @param ft a FeatureTable
   */
  protected void updateFeatureType(FeatureTable ft){
    DefaultListModel<CheckBoxListItem> model;
    ArrayList<String> names;
    List<FeatureType> featNames;
    
    featNames = getFeatureNamesList(ft);
    if (featNames.isEmpty()){
      _featTypes.setEnabled(false);
      return;
    }
    names = new ArrayList<>();
    model = new DefaultListModel<>();
    _updating = true;
    _featTypes.setEnabled(true);
    for(FeatureType type : featNames){
      type.setSelected(true);
      model.addElement(type);
      names.add(type.getName());
    }
    _featTypes.setModel(model);
    _updating = false;
    String[] sel = names.toArray(new String[0]);
    updateFeatureList(sel);
    fireFeatureTypesSelectedEvent(sel);
  }

   /**
    * Sets a new FeatureTable.
    * 
    * @param fTable a FeatureTable
    */
    public void setData(FeatureTable fTable){
      _featTypes.removeAll();
      _featTypes.setEnabled(false);
      super.setData(fTable);
      updateFeatureType(fTable);
    }

    private class MyCheckBoxListSelectionListener implements CheckBoxListSelectionListener{

      @Override
      public void itemSelected(CheckBoxListItem item) {
        EZLogger.info(item.toString()+": "+item.isSelected());
        ListModel<CheckBoxListItem> model = _featTypes.getModel();
        ArrayList<String> names;
        CheckBoxListItem it;
        int size = model.getSize();
        String[] sel;
        
        names = new ArrayList<>();
        for(int i=0;i<size;i++){
          it = model.getElementAt(i);
          if (it.isSelected()){
            names.add(it.getLabel());
          }
        }
        sel = names.toArray(new String[0]);
        updateFeatureList(sel);
        fireFeatureTypesSelectedEvent(sel);
      }
    }
}
