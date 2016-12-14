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
package example;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.api.EZGenericApplication;
import com.plealog.genericapp.api.EZUIStarterListener;

import bzh.plealog.bioinfo.api.core.config.CoreSystemConfigurator;
import bzh.plealog.bioinfo.api.data.feature.FPosition;
import bzh.plealog.bioinfo.api.data.feature.FRange;
import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureLocation;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSelectionEvent;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSelectionListener;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureTableFactory;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.data.sequence.EmptySequence;
import bzh.plealog.bioinfo.ui.carto.core.CartoViewerPanel;
import bzh.plealog.bioinfo.ui.carto.data.BasicFeatureOrganizer;
import bzh.plealog.bioinfo.ui.carto.drawer.AxisDrawingLane;
import bzh.plealog.bioinfo.ui.carto.drawer.RulerDrawingLane;
import bzh.plealog.bioinfo.ui.carto.drawer.SequenceDrawingLane;
import bzh.plealog.bioinfo.ui.carto.event.SViewerSelectionEvent;
import bzh.plealog.bioinfo.ui.carto.event.SViewerSelectionListener;
import bzh.plealog.bioinfo.ui.config.UISystemConfigurator;
import bzh.plealog.bioinfo.util.DAlphabetUtils;

/**
 * A simple class showing how to create, start and display a Carto viewer.
 * 
 * @author Patrick G. Durand
 */
public class CartoViewerPanelApp {

  /**
   * Start application. Relies on the Java Generic Application Framework.
   * See https://github.com/pgdurand/jGAF
   */
  public static void main(String[] args) {
    // This has to be done at the very beginning, i.e. first method call within
    // main().
    EZGenericApplication.initialize("CartoViewerPanelExample");
    // Add application branding
    EZApplicationBranding.setAppName("Simple Carto Viewer");
    EZApplicationBranding.setAppVersion("1.0");
    EZApplicationBranding.setCopyRight("P. Durand");
    EZApplicationBranding.setProviderName("Plealog Software");

    // Add a listener to application startup cycle (see below)
    EZEnvironment.setUIStarterListener(new MyStarterListener());

    // Required to use Plealog Bioinformatics Core objects such as Features, FeatureTables, Sequences
    CoreSystemConfigurator.initializeSystem();

    // Required to use the Plealog Bioinformatics UI library (CartoViewer default graphics)
    UISystemConfigurator.initializeSystem();

    // Start the application
    EZGenericApplication.startApplication(args);
  }

  /**
   * Prepare a sample feature table.
   */
  private static FeatureTable createFT(int from, int to){
    FeatureTable        ft;
    Feature             feat;
    FeatureLocation     locs;

    FeatureTableFactory ftFactory = CoreSystemConfigurator.getFeatureTableFactory();
    ft = ftFactory.getFTInstance();

    //simple feature
    feat = ftFactory.getFInstance();
    feat.setFrom(5);
    feat.setTo(125);
    feat.setKey("site");
    feat.setStrand(Feature.MINUS_STRAND);
    ft.addFeature(feat);

    //simple feature
    feat = ftFactory.getFInstance();
    feat.setFrom(130);
    feat.setTo(230);
    feat.setKey("site");
    feat.setStrand(Feature.MINUS_STRAND);
    ft.addFeature(feat);

    //simple feature
    feat = ftFactory.getFInstance();
    feat.setFrom(500);
    feat.setTo(500);
    feat.setKey("site");
    feat.setStrand(Feature.PLUS_STRAND);
    feat.addQualifier("source", "inconnue");
    ft.addFeature(feat);

    //multiple ranges
    feat = ftFactory.getFInstance();
    feat.setKey("gene");
    feat.setStrand(Feature.PLUS_STRAND);
    locs = new FeatureLocation();
    locs.addRange(new FRange(new FPosition(1500), new FPosition(1700)));
    locs.addRange(new FRange(new FPosition(3500), new FPosition(3700)));
    locs.addRange(new FRange(new FPosition(6500), new FPosition(7700)));
    feat.setFeatureLocation(locs);
    feat.setFrom(1500);
    feat.setTo(7700);
    ft.addFeature(feat);

    feat = ftFactory.getFInstance();
    feat.setKey("CDS");
    feat.setStrand(Feature.PLUS_STRAND);
    locs = new FeatureLocation();
    locs.addRange(new FRange(new FPosition(1500), new FPosition(1700)));
    locs.addRange(new FRange(new FPosition(3500), new FPosition(3700)));
    locs.addRange(new FRange(new FPosition(6500), new FPosition(7700)));
    feat.setFeatureLocation(locs);
    ft.addFeature(feat);

    feat = ftFactory.getFInstance();
    feat.setKey("mRNA");
    feat.setStrand(Feature.PLUS_STRAND);
    feat.setFrom(1228);
    feat.setTo(3587);
    ft.addFeature(feat);

    return ft;
  }

  /**
   * Prepare the view.
   */
  private static JPanel startViewer(){
    RulerDrawingLane    rdl;
    SequenceDrawingLane sdl;
    int                 seqsize = 14000;

    //create a viewer
    MySequenceViewer viewer = new MySequenceViewer();
    viewer.setDrawGrid(true);
    
    //create en empty sequence model
    EmptySequence seqStd = new EmptySequence(DAlphabetUtils.getIUPAC_DNA_Alphabet(), seqsize);
    //sequence coordinate system starts at 1
    seqStd.createRulerModel(1, 1);

    //create a sample set of Features (i.e. annotations)
    FeatureTable ft = createFT(1, seqsize);

    //Organize features by types on several lanes
    BasicFeatureOrganizer.organizeFeatures(
        viewer, 
        ft, 
        seqStd, 
        false, 
        seqsize);

    int labelLength = String.valueOf(seqStd.getRulerModel().getSeqPos(14000-1)).length();

    //and now, illustrates how to add some particular lanes

    //one lane for the sequence
    sdl = new SequenceDrawingLane(seqStd);
    sdl.setReferenceLabelSize(labelLength);
    viewer.addDrawingLane(sdl);

    //one lane for the sequence numerical ruler
    rdl = new RulerDrawingLane(seqStd);
    rdl.setReferenceLabelSize(labelLength);
    rdl.setLeftLabel(String.valueOf(seqStd.getRulerModel().getSeqPos(0)));
    rdl.setRightLabel(String.valueOf(seqStd.getRulerModel().getSeqPos(seqStd.size()-1)));
    rdl.setLeftLabel("Pos rel.:");
    viewer.addDrawingLane(rdl);

    //one lane for the axis
    AxisDrawingLane adl = new AxisDrawingLane(seqStd, AxisDrawingLane.TICK_TYPE.BOTH);
    Dimension dim = adl.getPreferredSize();
    dim.height = 10;
    adl.setPreferredSize(dim);
    adl.setReferenceLabelSize(labelLength);
    viewer.addDrawingLane(adl);

    //another ruler
    rdl = new RulerDrawingLane(seqStd);
    rdl.setReferenceLabelSize(labelLength);
    rdl.setLeftLabel("Chr N:");
    viewer.addDrawingLane(rdl);

    //yet another ruler: relative coordinate system
    seqStd = new EmptySequence(DAlphabetUtils.getIUPAC_DNA_Alphabet(), seqsize);
    seqStd.createRulerModel(-10000, 1);
    rdl = new RulerDrawingLane(seqStd);
    rdl.setReferenceLabelSize(labelLength);
    rdl.setLeftLabel("Pos rel.:");
    viewer.addDrawingLane(rdl);

    //add a listener to follow selection (here, on the console, see below)
    viewer.addSViewerSelectionListener(new MyListener(seqStd));

    //set a default viewer size and margins
    viewer.setWidth(1024);
    viewer.setMargins(100,45);

    //put the viewer into a scrollable panel
    JPanel pnl = new JPanel(new BorderLayout());
    pnl.add(new JScrollPane(viewer), BorderLayout.CENTER);

    //select the first feature
    viewer.setSelectedObject(ft.enumFeatures().nextElement());

    //ensure that region is visible
    viewer.setVisibleLocation(7300);

    //set the mouse mode (selection or zoom; here : selection)
    viewer.setMouseMode(CartoViewerPanel.MOUSE_MODE.SELECTION);

    return pnl;
  }

  /**
   * Implementation of the jGAF API.
   */
  private static class MyStarterListener implements EZUIStarterListener {

    @Override
    public Component getApplicationComponent() {
      return startViewer();
    }

    @Override
    public boolean isAboutToQuit() {
      // You can add some code to figure out if application can exit.

      // Return false to prevent application from exiting (e.g. a background
      // task is still running).
      // Return true otherwise.

      // Do not add a Quit dialogue box to ask user confirmation: the framework
      // already does that for you.
      return true;
    }

    @Override
    public void postStart() {
      // This method is called by the framework just before displaying UI
      // (main frame).
    }

    @Override
    public void preStart() {
      // This method is called by the framework at the very beginning of
      // application startup.
    }

  }

  /**
   * Add a little additional behavior on the standard CartoViewerPanel.
   */
  private static class MySequenceViewer extends CartoViewerPanel implements FeatureSelectionListener{
    private static final long serialVersionUID = -6430834247518745176L;
    public MySequenceViewer(){
      super();
    }
    public void featureSelected(FeatureSelectionEvent event){
      this.setSelectedObject(event.getFeature());
      this.repaint();
    }
    @Override
    public void featureTypesSelected(String[] types) {
    }
  }
  /**
   * Listener to object selection made by the user on the carto viewer.
   */
  private static class MyListener implements SViewerSelectionListener{
    public DSequence seq;
    public MyListener(DSequence seq){
      this.seq = seq;
    }
    public void objectSelected(SViewerSelectionEvent event){
      if (event.getType()==SViewerSelectionEvent.SEL_TYPE.EMPTY)
        return;

      if (event.getType()==SViewerSelectionEvent.SEL_TYPE.OBJECT_WITH_RANGE){
        System.out.println("Object selected in range ["+
            seq.getRulerModel().getSeqPos(event.getFrom())+","+
            seq.getRulerModel().getSeqPos(event.getTo())+"]: "+
            event.getSelectionObject());

      }
      else{
        System.out.println("Object selected: "+event.getSelectionObject());
      }
    }
  }
}
