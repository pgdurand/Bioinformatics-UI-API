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
package bzh.plealog.bioinfo.ui.hca;
//This code from: Turtle. A Java2 applet to display a HCA plot.
//(c) January 2000, Patrick Durand.

import java.awt.Container;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class FrameHca extends JFrame
{

  private static final long serialVersionUID = -5066749236088461753L;

  public FrameHca()
  {
    this("NOSEQUENCE");
  }

  public FrameHca(String s)
  {
    super("Turtle");
    Container c;
    PanelHca panelHCA;
    JScrollPane scroll;

    c = getContentPane();
    panelHCA = new PanelHca(s);
    scroll = new JScrollPane(panelHCA);
    JScrollBar hsb = scroll.getHorizontalScrollBar();
    hsb.setUnitIncrement(30);
    c.add(scroll);
    setLocation(new Point(200, 200));
    pack();
    setSize(400, 250);
    setVisible(true);
  }

}
