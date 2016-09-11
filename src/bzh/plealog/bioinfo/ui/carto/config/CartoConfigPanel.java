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
package bzh.plealog.bioinfo.ui.carto.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import bzh.plealog.bioinfo.api.data.feature.FPosition;
import bzh.plealog.bioinfo.api.data.feature.FRange;
import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureLocation;
import bzh.plealog.bioinfo.api.data.feature.FeatureTable;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureSystem;
import bzh.plealog.bioinfo.api.data.feature.utils.FeatureTableFactory;
import bzh.plealog.bioinfo.api.data.sequence.DViewerSystem;
import bzh.plealog.bioinfo.data.sequence.EmptySequence;
import bzh.plealog.bioinfo.ui.carto.core.CartoViewerPanel;
import bzh.plealog.bioinfo.ui.carto.core.FeatureGraphics;
import bzh.plealog.bioinfo.ui.carto.data.BasicFeatureOrganizer;
import bzh.plealog.bioinfo.ui.carto.data.FGraphics;
import bzh.plealog.bioinfo.ui.carto.drawer.BasicFeatureDrawingLane;
import bzh.plealog.bioinfo.ui.carto.drawer.DrawingLane;
import bzh.plealog.bioinfo.ui.carto.painter.FeaturePainter;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This class implements the control panel of the Cartographic Viewer.
 * 
 * @author Patrick G. Durand
 */
public class CartoConfigPanel extends JPanel {
  private static final long serialVersionUID = -6728520472908131422L;
  private CartoViewerPanel  previewPanel;
  private ClrChooserBtn bkColorBtn;
  private ClrChooserBtn fgColorBtn;
  private JComboBox<FeaturePainter>     painters;

  private static FeatureTableFactory ftFactory = FeatureSystem.getFeatureTableFactory();
  private static final String MSG_P1_TIP = "Click to change colour";
  private enum COLOR_ELEMENT {background, border};

  public CartoConfigPanel() {
    DefaultFormBuilder builder;
    FormLayout         layout;

    bkColorBtn = createClrButton(COLOR_ELEMENT.background);
    fgColorBtn = createClrButton(COLOR_ELEMENT.border);
    preparePaintersCombo();
    prepareFakeViewer();

    layout = new FormLayout("right:75dlu, 2dlu, 65dlu", "");
    builder = new DefaultFormBuilder(layout);
    builder.setDefaultDialogBorder();
    builder.append("Fill colour:", bkColorBtn);
    builder.nextLine();
    builder.append("Border colour:", fgColorBtn);
    builder.nextLine();
    builder.append("Painter:", painters);

    this.setLayout(new BorderLayout());
    this.add(previewPanel, BorderLayout.CENTER);
    this.add(builder.getContainer(), BorderLayout.SOUTH);
  }
  private static FeatureTable createFT(){
    FeatureTable        ft;
    Feature             feat;
    FeatureLocation     locs;

    ft = ftFactory.getFTInstance();

    //simple feature
    feat = ftFactory.getFInstance();
    feat.setFrom(10);
    feat.setTo(10);
    feat.setKey("site");
    feat.setStrand(Feature.PLUS_STRAND);
    ft.addFeature(feat);

    //simple feature
    feat = ftFactory.getFInstance();
    feat.setFrom(1);
    feat.setTo(20);
    feat.setKey("site");
    feat.setStrand(Feature.PLUS_STRAND);
    feat.addQualifier("source", "inconnue");
    ft.addFeature(feat);

    //multiple ranges
    feat = ftFactory.getFInstance();
    feat.setKey("gene");
    feat.setStrand(Feature.PLUS_STRAND);
    locs = new FeatureLocation();
    locs.addRange(new FRange(new FPosition(1), new FPosition(20)));
    locs.addRange(new FRange(new FPosition(30), new FPosition(50)));
    locs.addRange(new FRange(new FPosition(60), new FPosition(70)));
    feat.setFeatureLocation(locs);
    //feat.setFrom(1);
    //feat.setTo(70);
    ft.addFeature(feat);

    return ft;
  }

  private void prepareFakeViewer(){

    EmptySequence seqStd = new EmptySequence(DViewerSystem.getIUPAC_DNA_Alphabet(), 80);
    seqStd.createRulerModel(1, 1);
    previewPanel = new CartoViewerPanel();
    previewPanel.setReferenceSequence(seqStd);
    BasicFeatureOrganizer.organizeFeatures(previewPanel, createFT(), seqStd, false, 100);
    previewPanel.setWidth(100);
    previewPanel.setMargins(10, 10);
    previewPanel.setPreferredSize(new Dimension(150,50));
    previewPanel.setMaximumSize(new Dimension(150,50));
    previewPanel.setMinimumSize(new Dimension(150,50));
  }

  private void preparePaintersCombo(){
    painters = new JComboBox<FeaturePainter>();

    Iterator<String> painterNames = FeaturePaintersConfig.getPainterNames();
    while(painterNames.hasNext()){
      painters.addItem(FeaturePaintersConfig.getPainter(painterNames.next()));
    }
    painters.addActionListener(new PaintersComboActionListener());
  }
  private ClrChooserBtn createClrButton(COLOR_ELEMENT ce){
    ClrChooserBtn btn;
    FontMetrics fm;
    Dimension   dim;
    int         h;

    btn = new ClrChooserBtn(ce);
    btn.setOpaque(true);
    btn.setBorder(BorderFactory.createRaisedBevelBorder());
    btn.setToolTipText(MSG_P1_TIP);
    fm = btn.getFontMetrics(btn.getFont());
    h = fm.getHeight();
    dim = new Dimension(6*h, h);
    btn.setPreferredSize(dim);
    return btn;
  }
  private void updateProperties(COLOR_ELEMENT ce, Color clr){
    DrawingLane lane;
    FGraphics   fg;

    lane = previewPanel.getDrawingLane(0);
    if (lane instanceof BasicFeatureDrawingLane){
      List<FeatureGraphics> feats = ((BasicFeatureDrawingLane)lane).getFeatureTable();
      //y'a un probl�me avec les FGraphics statiques... voir comment faire
      for(FeatureGraphics fgs : feats){
        fg = new FGraphics();
        if (ce==COLOR_ELEMENT.background){
          fg.setBackgroundColor(clr);
        }
        else{
          fg.setLineColor(clr);
        }
        fgs.setFGraphics(fg);
      }
      previewPanel.repaint();
    }
  }
  private class PaintersComboActionListener implements ActionListener{
    public void actionPerformed(ActionEvent ae){

    }
  }
  private class ClrChooserBtn extends JButton implements ActionListener{
    private static final long serialVersionUID = 7455921511878289907L;
    private COLOR_ELEMENT ce;
    public ClrChooserBtn(COLOR_ELEMENT ce){
      super();
      this.ce = ce;
      addActionListener(this);
    }
    @SuppressWarnings("unused")
    public ClrChooserBtn(String lbl){
      super(lbl);
      addActionListener(this);
    }
    @SuppressWarnings("unused")
    public Color getColor(){
      return getBackground();
    }
    public void actionPerformed(ActionEvent ae){
      Color bg = JColorChooser.showDialog(
          CartoConfigPanel.this,
          "Choose a color",
          ((JButton)ae.getSource()).getBackground());
      if (bg!=null){
        ((JButton)ae.getSource()).setBackground(bg);
        updateProperties(ce, bg);
      }
    }
  }
}
