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
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import com.plealog.genericapp.ui.common.ContextMenuElement;

import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.sequence.DLocation;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.stat.AlphabetCounter;
import bzh.plealog.bioinfo.api.data.sequence.stat.StatUtils;
import bzh.plealog.bioinfo.ui.feature.FeatureViewer;
import bzh.plealog.bioinfo.ui.sequence.basic.AlphabetCounterDualViewer;
import bzh.plealog.bioinfo.ui.sequence.event.DDSequenceViewerConn;
import bzh.plealog.bioinfo.ui.sequence.event.DSelectionListenerSupport;

/**
 * This is a extended version of the CombinedSequenceViewer. It adds an AlphabetCounterDualViewer
 * to display sequence composition.
 * 
 * @author Patrick G. Durand
 */
public class CombinedSequenceViewerExt extends JPanel implements DDSequenceViewerConn{
  private static final long serialVersionUID = -4736305471177889587L;
  private CombinedSequenceViewer     _seqViewer;
  private AlphabetCounterDualViewer _alphViewer;

  /**
   * Default constructor.
   */
  public CombinedSequenceViewerExt(){
    this(true, true, true, true, true);
  }
  /**
   * Constructor.
   * 
   * @param showComposition pass true to show sequence composition viewer. false otherwise.
   */
  public CombinedSequenceViewerExt(boolean showComposition){
    this(showComposition, true, true, true, true);
  }

  public CombinedSequenceViewerExt(boolean showComposition, boolean showHCA, boolean showSequence, 
      boolean showDefaultToolbar, boolean showPatternSearch){
    super();
    buildGUI(showComposition, showHCA, showSequence, showDefaultToolbar, showPatternSearch);
  }
  /**
   * Figures out whether or not the viewer has to show a drawing grid. 
   * Such a grid may help the user to analyze the data.
   */
  public void setDrawGrid(boolean b){
    _seqViewer.setDrawGrid(b);
  }
  /**
   * Turn on or off visibility status of all features contained in 
   * this drawing lane.
   * 
   *  @param visible visibility status
   */
  public void setFeaturesVisible(boolean visible){
    _seqViewer.setFeaturesVisible(visible);
  }
  
  /**
   * Turn on or off visibility status of a particular feature.
   * 
   * @param feat feature for which to switch visibility status
   * @param visible visibility status
   * 
   * @return true if Feature was found in this lane, false otherwise.
   */
  public void setFeatureVisible(Feature feat, boolean visible){
    _seqViewer.setFeatureVisible(feat, visible);
  }

  private void buildGUI(boolean showComposition, boolean showHCA, boolean showSequence, 
      boolean showDefaultToolbar, boolean showPatternSearch){
    JTabbedPane jtp;
    JComponent  compo;

    _seqViewer = new CombinedSequenceViewer(showHCA, showSequence, showDefaultToolbar, showPatternSearch);

    if(showComposition){
      _alphViewer = new AlphabetCounterDualViewer();
      jtp = new JTabbedPane();
      jtp.setFocusable(false);
      jtp.add("Sequence", _seqViewer);
      jtp.add("Composition", _alphViewer);
      compo = jtp;
    }
    else{
      compo = _seqViewer;
    }
    this.setLayout(new BorderLayout());
    this.add(compo, BorderLayout.CENTER);

  }
  /**
   * Register the component used by this viewer to broadcast selection events.
   */
  public void registerSelectionListenerSupport(DSelectionListenerSupport lSupport){
    _seqViewer.registerSelectionListenerSupport(lSupport);
    if (_alphViewer!=null){
      lSupport.addDSequenceSelectionListener(_alphViewer);
    }
  }
  /**
   * Adds a special command panel on the right of the header of the viewer. 
   * Can be used to add a toolbar, for example.
   */
  public void setCommandPanel(JComponent pnl){
    _seqViewer.setCommandPanel(pnl);
  }
  /**
   * Sets the sequence to be displayed by this viewer. Pass in null to reset the viewer content.
   */
  public void setSequence(DSequence sequence){
    _seqViewer.setSequence(sequence);

    if (_alphViewer!=null){
      AlphabetCounter alphC;
      if (sequence!=null){
        alphC = StatUtils.computeComposition(sequence);
      }
      else{
        alphC = null;
      }
      _alphViewer.setFullSeqAlphabetCounter(alphC);
    }
  }
  /**
   * Returns the sequence currently displayed by this viewer.
   */
  public DSequence getSequence(){
    return _seqViewer.getSequence();
  }
  /**
   * Returns the selected region of the sequence currently displayed by this viewer.
   * Returns null if nothing is selected.
   */
  public DSequence getSelectedSequence(){
    return _seqViewer.getSelectedSequence();
  }
  /**
   * Sets the selected region of the sequence displayed in the viewer.
   * Values have to be zero-based and absolute, so use the DSequence DRulerModel to get
   * an absolute position from a sequence coordinate. Set from and to to -1 to reset
   * selection.
   */
  public void setSelectedSequenceRange(int from, int to){
    _seqViewer.setSelectedSequenceRange(from, to);
  }
  /**
   * Sets a list of selected segments over the sequence.  Value are
   * zero-based and absolute: use the DRulerModel from the DSequence to 
   * switch to the sequence coordinate system. 
   */
  public void setSelectionRanges(List<DLocation> locs){
    _seqViewer.setSelectionRanges(locs);
  }
  /**
   * Returns a list of selected segments over the sequence.  Value are
   * zero-based and absolute: use the DRulerModel from the DSequence to 
   * switch to the sequence coordinate system. Returns null if nothing is selected.
   */
  public List<DLocation> getSelectedRanges(){
    return _seqViewer.getSelectedRanges();
  }

  /**
   * Returned the global selected region of the sequence displayed in the viewer.
   * The method returns null if nothing is selected, otherwise the array contains
   * the selected region. Index zero contains from and index one contains to. Values
   * are zero-based and absolute, so use the DSequence DRulerModel to get
   * an from a sequence coordinate from an absolute position.
   */
  public int[] getSelectedSequenceRange(){
    return _seqViewer.getSelectedSequenceRange();
  }
  /**
   * Sets a contextual popup menu to this viewer.
   */
  public void setContextMenu(List<ContextMenuElement> actions){
    _seqViewer.setContextMenu(actions);
  }
  public void registerFeatureViewer(FeatureViewer fv){
    _seqViewer.registerFeatureViewer(fv);
  }
  public void setFeaturesForCartoView(FeatureTable fTable){
    _seqViewer.setFeaturesForCartoView(fTable);
  }
  public void plugActions(JToolBar tBar, boolean displayBtnLabel){
    _seqViewer.plugActions(tBar, displayBtnLabel);
  }
}
