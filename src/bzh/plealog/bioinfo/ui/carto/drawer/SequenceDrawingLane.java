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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSymbol;
import bzh.plealog.bioinfo.api.data.sequence.DSymbolGraphics;
import bzh.plealog.bioinfo.ui.carto.event.SViewerSelectionEvent;

/**
 * This class is used to draw the letters of a DSequence.
 * 
 * @author Patrick G. Durand
 */
public class SequenceDrawingLane extends DrawingLaneBase {
  //These two values are used to highlight selected sequence region
  private int selSeqFrom = -1;
  private int selSeqTo = -1;

  public SequenceDrawingLane(DSequence seq){
    super(seq);
  }

  private void paintRuler(Graphics2D g, double xFactor, Rectangle drawingArea){
    String          str;
    FontMetrics     fm;
    DSymbol         symbol;
    DSymbolGraphics gr;
    Color           curClr, bk, fg, oldClr;
    Rectangle       txtBox;
    int             i, x3, yBase, yBase2, yBase3, from, to, sFrom, sTo, txtWidth, chDecal, sSize, lastX;
    boolean          canDisplayLetter;

    g.setColor(Color.black);
    g.setFont(this.getFont());
    fm = g.getFontMetrics(this.getFont());
    yBase = drawingArea.y + drawingArea.height / 2+fm.getHeight()/2;
    from = drawingArea.x;
    to = from + drawingArea.width;
    txtWidth=fm.stringWidth("W")-2;
    canDisplayLetter = txtWidth<=(int)xFactor;
    sSize = this.getSequence().size()-1;

    //use computation similar to ruler drawer
    int xTick = getTickSpacer(drawingArea.width, xFactor);
    sFrom = (int)((double)from / xFactor); 
    sFrom = Math.min(Math.max(0,sFrom), sSize);
    sFrom = sFrom - (sFrom%xTick) - 2*xTick;
    sTo = (int)((double)to / xFactor);
    sTo = Math.min(Math.max(0,sTo), sSize);
    sTo = sTo - (sTo%xTick) + 2*xTick;

    curClr = Color.white;
    //compute txt box that surrounds a letter
    txtBox = new Rectangle();
    txtBox.y = drawingArea.y + this.getTopMargin();
    txtBox.width = txtWidth;
    txtBox.height = drawingArea.height - this.getBottomMargin();
    yBase2 = txtBox.y+txtBox.height-3;
    yBase3 = yBase2 + 2;
    if (canDisplayLetter){
      chDecal = txtWidth/2;
    }
    else{
      chDecal = 0;
    }
    g.setColor(curClr);
    lastX = -1;
    for(i=sFrom;i<=sTo;i++){
      if (i<0) 
        continue;
      if (i>sSize)
        break;
      x3 = this.getLeftMargin() + (int)(xFactor * (double) (i));
      if (x3>lastX == false)
        continue;
      lastX = x3;
      symbol = this.getSequence().getSymbol(i);
      str = symbol.toString();

      gr = symbol.getGraphics();
      if (gr!=null){
        //draw background only if it is possible to draw letters
        if (canDisplayLetter){
          bk = gr.getBkColor();
          if (!bk.equals(curClr)){
            curClr = bk;
            g.setColor(curClr);
          }
          txtBox.x = x3 - chDecal;
          g.fill(txtBox);
        }
        fg = gr.getTextColor();
        if (!fg.equals(curClr)){
          curClr = fg;
          g.setColor(curClr);
        }
      }
      else{
        if (!curClr.equals(Color.black)){
          curClr = Color.black;
          g.setColor(curClr);
        }
      }
      if (canDisplayLetter){
        g.drawString(str, x3 - chDecal, yBase);
      }
      else {
        g.drawLine(x3, txtBox.y, x3, yBase2);
      }
      //selection
      if (i>=selSeqFrom && i<=selSeqTo){
        oldClr = g.getColor();
        g.setColor(Color.red);
        if (canDisplayLetter)
          g.drawLine(x3 - chDecal+1, yBase3, x3 + chDecal+1, yBase3);
        else
          g.drawLine(x3, yBase3, x3, yBase3);
        g.setColor(oldClr);
      }
    }
  }
  /*private void copyToClipBoard(Object obj){
		ByteArrayOutputStream baos;
        ClipBoardTextTransfer cbtt;
        FastaExport           exporter;
    	DSequence[]           seqs;

    	seqs = new DSequence[1];
    	seqs[0] = (DSequence)obj;
    	exporter = new FastaExport();
        try{
            baos = new ByteArrayOutputStream();
            exporter.export(baos, seqs, false);
            baos.flush();
            cbtt = new ClipBoardTextTransfer();
            cbtt.setClipboardContents(baos.toString());
        }
        catch(Exception ex){
        }
	}*/
  public void paintLane(Graphics2D g, Rectangle drawingArea) {
    super.paintLane(g, drawingArea);
    paintRuler(g, this.computeScaleFactor(), drawingArea);
  }
  public void objectSelected(SViewerSelectionEvent event){
    Object obj;

    obj = event.getSelectionObject();
    if (obj == null || !(obj instanceof DSequence)){
      selSeqFrom = selSeqTo = -1;
      return;
    }
    //copyToClipBoard(obj);
    selSeqFrom = event.getFrom();//ruler coordinates [0..seqSize-1]. -1 if undefined
    selSeqTo = event.getTo();
  }

}
