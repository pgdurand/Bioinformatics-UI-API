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
package bzh.plealog.bioinfo.ui.sequence.extended;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import bzh.plealog.bioinfo.api.data.feature.FRange;
import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureLocation;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSelectionEvent;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSelectionListener;
import bzh.plealog.bioinfo.api.data.sequence.BankSequenceDescriptor;
import bzh.plealog.bioinfo.api.data.sequence.BankSequenceInfo;
import bzh.plealog.bioinfo.api.data.sequence.DLocation;
import bzh.plealog.bioinfo.api.data.sequence.DRulerModel;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.ui.feature.FeatureStatusViewer;
import bzh.plealog.bioinfo.ui.feature.FeatureViewer;
import bzh.plealog.bioinfo.ui.feature.FeatureWebLinker;
import bzh.plealog.bioinfo.ui.seqinfo.SequenceInfoViewer;
import bzh.plealog.bioinfo.ui.sequence.event.DSelectionListenerSupport;
import bzh.plealog.bioinfo.ui.util.ContextMenuElement;

import com.plealog.genericapp.api.EZEnvironment;

/**
 * This is av extended version of the CombinedSequenceViewerExt. It adds a FeatureViewer to
 * CombinedSequenceViewerExt.
 * 
 * @author Patrick G. Durand
 */
public class CombinedAnnotatedSequenceViewer extends JPanel {
  private static final long serialVersionUID = 867341572122770228L;
  private CombinedSequenceViewerExt            _seqViewer;
  private FeatureViewer                 _featViewer;
  private SequenceInfoViewer            _seqInfoViewer;
  private FeatureStatusViewer           _featStatViewer;
  private JTabbedPane                   _featTabbedPane;
  private JSplitPane                    _jsp;
  private DSelectionListenerSupport     _lSupport;

  protected static final MessageFormat ID_FORMATTER = new MessageFormat("{0} [{1}-{2}]");

  protected static final SimpleDateFormat TIME_STAMP  = new SimpleDateFormat("yyyyMMdd");

  public CombinedAnnotatedSequenceViewer(){
    this(null, true);
  }
  /**
   * Constructor.
   * 
   * @param confPath absolute path to the configuration path of the software. Can be null.
   * @param showComposition figures out whether or not the Composition panel has to be shown.
   */
  public CombinedAnnotatedSequenceViewer(String confPath, boolean showComposition){
    super();

    JPanel      seqPanel, tBarPnl;
    JToolBar    tBar;

    _seqViewer = new CombinedSequenceViewerExt(showComposition);
    _lSupport = new DSelectionListenerSupport();
    _seqViewer.registerSelectionListenerSupport(_lSupport);

    tBar = getToolbar();
    _seqViewer.plugActions(tBar, true);
    tBarPnl = new JPanel(new BorderLayout());

    seqPanel = new JPanel(new BorderLayout());
    seqPanel.add(_seqViewer, BorderLayout.CENTER);
    tBarPnl.add(tBar, BorderLayout.EAST);
    _seqViewer.setCommandPanel(tBarPnl);

    _jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, seqPanel, getFeatureInfoPanel(confPath));
    _jsp.setResizeWeight(1.0);
    _jsp.setOneTouchExpandable(true);

    this.setLayout(new BorderLayout());
    this.add(_jsp, BorderLayout.CENTER);
    enableToolbar(false);
    _seqViewer.setVisible(false);

  }
  /**
   * Utility method to create the Feature panel.
   */
  private JComponent getFeatureInfoPanel(String confPath){
    JPanel      featP, siP;

    JTabbedPane jtp2 = new JTabbedPane();
    jtp2.setFocusable(false);

    _featViewer = new FeatureViewer(
        new FeatureWebLinker(confPath==null ? null : "file:/"+confPath),
        true);
    _featViewer.setAutoSelectFirstFeature(false);
    _seqViewer.registerFeatureViewer(_featViewer);
    _featViewer.addFeatureSelectionListener(new SeqFeatureSelectionListener());
    _seqInfoViewer = new SequenceInfoViewer();
    _featStatViewer = new FeatureStatusViewer();

    featP = new JPanel(new BorderLayout());
    featP.add(_featViewer, BorderLayout.CENTER);

    siP = new JPanel(new BorderLayout());
    siP.add(_seqInfoViewer, BorderLayout.CENTER);

    jtp2.addTab("Features", 
        EZEnvironment.getImageIcon("feature.png"), featP);
    jtp2.addTab("SeqInfo", siP);
    jtp2.addTab("Status", _featStatViewer);
    _featTabbedPane = jtp2;
    _featTabbedPane.setPreferredSize(new Dimension(150,150));
    return jtp2;
  }
  protected void cleanViewer(){
    _seqViewer.setSequence(null);
    _featViewer.clear();
    _seqInfoViewer.clear();
    _featStatViewer.clear();
    _seqViewer.setVisible(false);
    System.gc();
  }
  protected void enableToolbar(boolean e){
  }
  /**
   * Sets new data in this viewer.
   * 
   * @param sd the data to display
   * @param source origin of the data
   * @param from starting sequence position that is really shown here. This parameter
   * is only used for display purpose, i.e. the data structure is not processed to show
   * that region.
   * @param to ending sequence position that is really shown here. This parameter
   * is only used for display purpose, i.e. the data structure is not processed to show
   * that region.
   */
  public void setData(BankSequenceDescriptor sd, File source, int from, int to){
    DSequence    sequence;
    String       seqName;
    FeatureTable fTable;
    BankSequenceInfo si;

    //get and display data
    if (sd!=null)
      sequence = sd.getSequence();
    else
      sequence = null;
    if (sequence!=null){
      seqName = sequence.getSequenceInfo().getName();
      if (from>0 && to>0){
        seqName = ID_FORMATTER.format(new Object[]{
            seqName, from, to});
        sequence.getSequenceInfo().setName(seqName);
      }
    }
    _seqViewer.setVisible(sequence!=null);
    _seqViewer.setSequence(sequence);
    enableToolbar(sequence!=null);
    _featViewer.clear();
    if (sd!=null)
      fTable = sd.getFeatureTable();
    else
      fTable=null;
    _seqViewer.setFeaturesForCartoView(fTable);
    if (fTable!=null){
      if (source!=null){
        fTable.setSource(source.getAbsolutePath());
        fTable.setDate(TIME_STAMP.format(new Date(source.lastModified())));
      }
      fTable.sort(FeatureTable.POS_SORTER);
      /*_featTabbedPane.setTitleAt(0, 
                    Messages.getString("BlastSeqAlignViewer.14")+" ("+
                    fTable.features()+")");*/
    }
    /*else{
        	_featTabbedPane.setTitleAt(0, 
                    Messages.getString("BlastSeqAlignViewer.14"));
        }*/
    _featViewer.setData(fTable);
    _featStatViewer.setData(fTable);
    _seqInfoViewer.clear();
    if (sd!=null)
      si = sd.getSequenceInfo();
    else
      si = null;
    if (si!=null)
      _seqInfoViewer.setData(si);
    /*if (fTable==null && si==null){
        	_featTabbedPane.setVisible(false);
        	_jsp.setDividerLocation(1.0);
        }*/
  }
  /**
   * Listener to the selections made on the Feature table.
   */
  private class SeqFeatureSelectionListener implements FeatureSelectionListener{
    private List<DLocation> getLocations(DRulerModel drm, FeatureLocation fLoc){
      ArrayList<DLocation> locs;
      FRange      range;
      int         i, size, from, to;

      locs = new ArrayList<DLocation>();
      size = fLoc.elements();
      for(i=0;i<size;i++){
        range = fLoc.getRange(i);
        if (range.getDbXref()!=null)
          continue;
        from = range.getFrom().getStart();
        to = range.getTo().getEnd();
        from = drm.getRulerPos(from);
        to = drm.getRulerPos(to);
        locs.add(new DLocation(Math.min(from, to), Math.max(from, to)));
      }
      return locs;
    }
    public void featureSelected(FeatureSelectionEvent event){
      Feature                feature;
      DRulerModel            drm;
      DSequence              seq;
      int                    sFrom, sTo;

      feature = event.getFeature();
      if (feature==null){
        _seqViewer.setSelectedSequenceRange(-1, -1);
      }
      seq = _seqViewer.getSequence();
      if (seq==null)
        return;
      drm = seq.getRulerModel();
      if (feature!=null){
        if (feature.getFeatureLocation()!=null){
          _lSupport.setSelectionRanges(_featViewer, seq, getLocations(drm, feature.getFeatureLocation()));
        }
        else{
          sFrom = Math.min(feature.getFrom(), feature.getTo());
          sTo = Math.max(feature.getFrom(), feature.getTo());

          _lSupport.setSelectedSequenceRange(_featViewer, seq, drm.getRulerPos(sFrom), drm.getRulerPos(sTo));
        }
      }
    }
    public void featureTypesSelected(String[] types) {
    }

  }
  /**
   * Creates a toolbar with additional functions.
   */
  protected JToolBar getToolbar(){
    JToolBar  tBar;

    tBar = new JToolBar();
    tBar.setFloatable(false);
    return tBar;
  }
  protected void installContextMenu(ArrayList<ContextMenuElement> actions){
    _seqViewer.setContextMenu(actions);
  }

}
