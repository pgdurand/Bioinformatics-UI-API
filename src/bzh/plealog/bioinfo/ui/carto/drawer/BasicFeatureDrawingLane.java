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
package bzh.plealog.bioinfo.ui.carto.drawer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import bzh.plealog.bioinfo.api.data.feature.FRange;
import bzh.plealog.bioinfo.api.data.feature.Feature;
import bzh.plealog.bioinfo.api.data.feature.FeatureLocation;
import bzh.plealog.bioinfo.api.data.sequence.DRulerModel;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.ui.carto.core.FeatureGraphics;
import bzh.plealog.bioinfo.ui.carto.data.FGraphics;
import bzh.plealog.bioinfo.ui.carto.event.SViewerSelectionEvent;
import bzh.plealog.bioinfo.ui.carto.painter.FeaturePainter;

/**
 * This class is used to draw a features.
 * 
 * @author Patrick G. Durand
 */
public class BasicFeatureDrawingLane extends DrawingLaneBase implements FeatureDrawingLane{
  // used for drawing purpose (features are ordered by location)
  private List<FeatureGraphics> features;
  // used for fast lookup
  private Hashtable<Feature, FeatureGraphics> hfeatures;
  private Feature               selectedFeature;

  /**this is the minimum width of any feature drawn on a viewer.*/
  private static final int MIN_WIDTH = 3;

  private static final int DECAL_COORD = 0;

  /**
   * Standard constructor.
   * 
   * @param seq the sequence to which belongs the features
   * @param features the list of graphical features to draw
   */
  public BasicFeatureDrawingLane(DSequence seq, List<FeatureGraphics> features){
    super(seq);
    setFeatureTable(features);
  }

  /**
   * @see FeatureDrawingLane#getFeatureTable()
   */
  public List<FeatureGraphics> getFeatureTable() {
    return features;
  }

  /**
   * @see FeatureDrawingLane#setFeatureTable(List)
   */
  public void setFeatureTable(List<FeatureGraphics> features) {
    this.features = features;
    hfeatures = new Hashtable<>(features.size());
    for(FeatureGraphics fg : features){
      hfeatures.put(fg.getFeature(), fg);
    }
  }

  /**
   * @see FeatureDrawingLane#setFeaturesVisible(boolean)
   */
  public void setFeaturesVisible(boolean visible){
    for(FeatureGraphics fg : features){
      fg.setVisible(visible);
    }
  }
  
  /**
   * @see FeatureDrawingLane#setFeatureVisible(Feature, boolean)
   */
  public boolean setFeatureVisible(Feature feat, boolean visible){
    FeatureGraphics fg = hfeatures.get(feat);
    if (fg==null){
      return false;
    }
    fg.setVisible(visible);
    return true;
  }
  
  /**
   * Draw a simple line. Used for segmented features.
   */
  private void drawLine(Graphics2D g, Rectangle fbox, Rectangle dbox){
    Rectangle r;
    int       y;

    g.setColor(Color.black);
    y = fbox.y+fbox.height/2;
    r = dbox.intersection(fbox);
    g.drawLine(r.x, y, r.x+r.width, y);
  }
  /**
   * Highlight the selected feature.
   */
  private void drawSelectedFeatureLine(Graphics2D g, Rectangle fbox, Rectangle dbox){
    Rectangle r;
    int       y;
    g.setColor(Color.red);
    y = fbox.y+fbox.height/2;
    r = dbox.intersection(fbox);
    g.drawLine(r.x-3, y-2, r.x+r.width+2, y-2);
    g.drawLine(r.x-3, y+2, r.x+r.width+2, y+2);
  }
  /**
   * Highlight the selected feature.
   */
  private void drawSelectedFeatureBox(Graphics2D g, Rectangle fbox, Rectangle dbox){
    g.setColor(Color.red);
    g.drawRect(fbox.x-3, dbox.y-1, fbox.width+5, dbox.height+1);
  }
  /**
   * Compute the rectangle in which a feature will be drawn.
   */
  private void computeDrawingBox(Rectangle drawingArea, Rectangle box, DRulerModel rModel, 
      double xFactor, int sFrom, int sTo){
    int         from, to, decal;

    //from = this.getLeftMargin() + (int)(xFactor * (double) (rModel.getRulerPos(sFrom)+DECAL_COORD));
    //to = this.getLeftMargin() + (int)(xFactor * (double) (rModel.getRulerPos(sTo)+DECAL_COORD));
    decal = rModel.getStartPos();
    from = this.getLeftMargin() + (int)(xFactor * (double) (sFrom-decal+DECAL_COORD));
    to = this.getLeftMargin() + (int)(xFactor * (double) (sTo-decal+DECAL_COORD));
    box.x = from;
    box.y = drawingArea.y + this.getTopMargin();
    //a feature has a minimum width of MIN_WIDTH pix to be visible somehow
    box.width = Math.max(to-from+1, MIN_WIDTH);
    box.height = drawingArea.height-(this.getTopMargin()+this.getBottomMargin());
  }
  /**
   * Utility method used to return a feature type. Sometimes feature type can be compound with
   * two strings separated by a colon. In that case, the left string is considered as the feature 
   * key.
   */
  @SuppressWarnings("unused")
  private String getFeatureKey(Feature feat){
    String key = feat.getKey();
    int    idx = key.indexOf(':');
    if (idx<0)
      return key;
    else
      return key.substring(0, idx);
  }
  
  private void drawFeatures(Graphics2D g, double xFactor, Rectangle drawingArea){
    Iterator<FeatureGraphics> feats;
    FeatureGraphics   fGraphics;
    FeaturePainter    painter;
    FGraphics         fg;
    Feature           feature;
    FeatureLocation   locs;
    FRange            range;
    DRulerModel       rModel;
    Rectangle         fBox, dBox;
    ArrayList<FRange>         fLocs;
    ArrayList<Rectangle> dBoxes, fBoxes;    
    Stroke            gStroke, stroke;
    int               i, size, strand, from, to, lastX, curX;

    rModel = this.getSequence().getRulerModel();
    feats = features.iterator();
    fBox = new Rectangle();
    from = drawingArea.x;
    to = from + drawingArea.width;// - this.getRightMargin();
    //from += this.getLeftMargin();
    dBox = new Rectangle(
        from, 
        drawingArea.y+this.getTopMargin(), 
        to-from+1, 
        drawingArea.height-(this.getTopMargin()+this.getBottomMargin()));
    //g.clipRect(dBox.x, drawingArea.y, dBox.width, drawingArea.height);
    lastX = -1;
    dBoxes = new ArrayList<Rectangle>();
    fBoxes = new ArrayList<Rectangle>();
    gStroke = g.getStroke();//get standard stroke
    
    drawGrid(g, xFactor, drawingArea);

    while(feats.hasNext()){
      fGraphics = feats.next();
      if (fGraphics.isVisible()==false){
        continue;
      }
      feature = fGraphics.getFeature();
      fg = fGraphics.getFGraphics();
      painter = fGraphics.getFPainter();
      locs = feature.getFeatureLocation();
      strand = feature.getStrand();
      stroke = fg.getStroke();
      if (locs==null || locs.elements()==1){
        //single range
        computeDrawingBox(drawingArea, fBox, rModel, xFactor, feature.getFrom(), feature.getTo());
        if (dBox.intersects(fBox)){
          //speedup: draw a feature only if its ending x coord goes beyond last drawn feature
          curX = fBox.x+fBox.width;
          if (curX>lastX){
            if (stroke!=null)
              g.setStroke(stroke);
            else
              g.setStroke(gStroke);
            painter.setUserData(feature);
            painter.paintFeature(g, fBox, fg, strand);
          }
          //there is only one feature selected: always draws it
          if (selectedFeature == feature){
            fBoxes.add(new Rectangle(fBox));
            dBoxes.add(new Rectangle(dBox));
          }
          lastX = curX;
        }
      }
      else{
        //multiple ranges:draw line spanning all feature elements, then draw elements
        fLocs = locs.getAscentSortedElements();
        size = fLocs.size();
        computeDrawingBox(drawingArea, fBox, rModel, xFactor, 
            ((FRange)fLocs.get(0)).getFrom().getStart(), 
            ((FRange)fLocs.get(size-1)).getTo().getEnd());
        drawLine(g, fBox, dBox);
        if (selectedFeature == feature){
          drawSelectedFeatureLine(g, fBox, dBox);
        }
        for(i=0;i<size;i++){
          range = (FRange)fLocs.get(i);
          computeDrawingBox(drawingArea, fBox, rModel, xFactor, range.getFrom().getStart(), range.getTo().getEnd());
          if (dBox.intersects(fBox)){
            curX = fBox.x+fBox.width;
            if (curX>lastX){
              if (stroke!=null)
                g.setStroke(stroke);
              else
                g.setStroke(gStroke);
              painter.setUserData(feature);
              painter.paintFeature(g, fBox, fg, strand);
            }
            //there is only one feature selected: always draws it
            if (selectedFeature == feature){
              fBoxes.add(new Rectangle(fBox));
              dBoxes.add(new Rectangle(dBox));
            }
            lastX = curX;
          }
        }
      }
    }
    g.setStroke(gStroke);//restore standard Stroke
    //draw selection boxes after feature boxes so that selection if better visible
    size = fBoxes.size();
    g.setColor(Color.red);
    for(i=0 ; i<size ; i++){
      drawSelectedFeatureBox(g, fBoxes.get(i), dBoxes.get(i));
    }
  }

  public Object getClickedObject(int x){
    DRulerModel rModel;
    Feature     feat;
    int         from, to;
    double      xFactor = this.computeScaleFactor();

    rModel = this.getSequence().getRulerModel();
    for(FeatureGraphics fg : features){
      if(fg.isVisible()==false)
        continue;
      feat = fg.getFeature();
      from = rModel.getRulerPos(feat.getFrom())+DECAL_COORD;
      from = this.getLeftMargin() + (int)(xFactor * (double) from);
      to = rModel.getRulerPos(feat.getTo())+DECAL_COORD;
      to =  this.getLeftMargin() + (int)(xFactor * (double) to);
      to = from + Math.max(to-from+1, MIN_WIDTH);

      if (x>=from && x<=to){
        return feat;
      }
    }
    return null;
  }
  /**
   * For now this implementation always return null.
   */
  public Object getObjectFromSection(int xFrom, int xTo){
    return null;
  }
  public void paintLane(Graphics2D g, Rectangle drawingArea) {
    //super.paintLane(g, drawingArea);
    if (features==null || features.isEmpty())
      return;
    drawFeatures(g, this.computeScaleFactor(), drawingArea);
  }
  /**
   * Implementation of SViewerSelectionListener interface used to highlight a particular
   * feature.*/
  public void objectSelected(SViewerSelectionEvent event){
    if (event.getType()==SViewerSelectionEvent.SEL_TYPE.EMPTY){
      selectedFeature = null;
      return;
    }
    if (event.getSelectionObject() instanceof Feature){
      selectedFeature = (Feature) event.getSelectionObject();
    }
    else{
      selectedFeature = null;
    }
  }
}
