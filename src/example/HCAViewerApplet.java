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
//This code from: Turtle. A Java2 applet to display a HCA plot.
//(c) January 2000, Patrick Durand.

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bzh.plealog.bioinfo.ui.hca.FrameHca;

/**
 * Enables to start a HCA Viewer as an applet. It is worth noting that you can
 * also start it as a standalone application.
 * 
 * @author Patrick G. Durand
 */
public class HCAViewerApplet extends JApplet
{
  private static final long serialVersionUID = 816847313567279480L;
  private JButton showHCAWin;
  private JButton fetchExpasy;
  private JButton resetTextArea;
  private JButton aboutApplet;
  private JTextArea seqTxt;
  private JLabel label;
  private Container c;
  private JPanel btnPanel;
  private String aboutText;
  private boolean isApplet;
  boolean isStandalone;

  /**
   * Constructor of the applet. 
   */
  public HCAViewerApplet()
  {
    aboutText = "";
    isApplet = false;
    isStandalone = false;
  }

  /**
   * A very basic way of retrieving a protein sequence from Uniprot server.
   * */
  private void fetchSeqEntry()
  {
    String AccCode = JOptionPane.showInputDialog(null, "Enter a SwissPort accession code", "Turtle - download from www.uniprot.org", 3);
    try
    {
      String getText = "http://www.uniprot.org/uniprot/";
      String inputLine = "";
      getText = getText + AccCode + ".fasta";
      setCursor(Cursor.getPredefinedCursor(3));
      showHCAWin.setEnabled(false);
      fetchExpasy.setEnabled(false);
      resetTextArea.setEnabled(false);
      URL url = new URL(getText);
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      boolean bRead = false;
      do
      {
        inputLine = in.readLine();
        if(inputLine == null || inputLine.startsWith("</PRE>"))
          break;
        if(inputLine.startsWith(">sp|"))
        {
          bRead = true;
          seqTxt.setText("");
        } else
          if(bRead)
          {
            seqTxt.append(inputLine);
            seqTxt.append("\n");
          }
      } while(true);
      in.close();
      setCursor(Cursor.getPredefinedCursor(0));
      if(!bRead)
        JOptionPane.showMessageDialog(null, "Unable to read the entry. Please\ncheck your accession code\nand retry.", "Turtle", 1);
    }
    catch(MalformedURLException _ex)
    {
      JOptionPane.showMessageDialog(null, "Invalid URL. Connection aborted.", "Turtle", 0);
    }
    catch(IOException _ex)
    {
      JOptionPane.showMessageDialog(null, "I/O exception. Connection aborted.", "Turtle", 0);
    }
    catch(SecurityException _ex)
    {
      JOptionPane.showMessageDialog(null, "Security exception. Connection aborted.", "Turtle", 0);
    }
    finally
    {
      setCursor(Cursor.getPredefinedCursor(0));
      showHCAWin.setEnabled(true);
      fetchExpasy.setEnabled(true);
      resetTextArea.setEnabled(true);
    }
  }

  /**
   * Provide some information about this code.
   */
  public String getAppletInfo()
  {
    return "Turtle. (c) January 2000 by Patrick Durand";
  }

  /**
   * Initialize the UI.
   */
  public void initComponents()
      throws Exception
      {
    c = getContentPane();
    setSize(new Dimension(400, 250));
    c.setLayout(new BorderLayout());
    setLocation(new Point(0, 0));
    label = new JLabel("Turtle");
    seqTxt = new JTextArea("[Paste a protein sequence here...]", 10, 15);
    showHCAWin = new JButton("Show HCA plot");
    showHCAWin.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e)
      {
        String s = seqTxt.getText();
        if(s.length() > 0){
          new FrameHca(s);
        }
      }

    });
    if(!isApplet)
    {
      fetchExpasy = new JButton("Fetch a seq.");
      fetchExpasy.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e)
        {
          fetchSeqEntry();
        }

      });
    }
    resetTextArea = new JButton("Clear text");
    resetTextArea.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e)
      {
        seqTxt.setText("");
      }

    });
    aboutApplet = new JButton("About...");
    aboutApplet.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e)
      {
        aboutText = aboutText + "Turtle. A Java2 applet to display a HCA plot.\n";
        aboutText = aboutText + "(c) January 2000, Patrick Durand.\n";
        aboutText = aboutText + "www.multimania.com/pdurand.\n";
        JOptionPane.showMessageDialog(null, aboutText, "Turtle", 1);
      }

    });
    btnPanel = new JPanel();
    btnPanel.setLayout(new GridLayout(1, isApplet ? 3 : 4));
    btnPanel.add(showHCAWin);
    if(!isApplet)
      btnPanel.add(fetchExpasy);
    btnPanel.add(resetTextArea);
    btnPanel.add(aboutApplet);
    c.add(label, "North");
    c.add(new JScrollPane(seqTxt), "Center");
    c.add(btnPanel, "South");
      }

  public void init()
  {
    isApplet = true;
  }

  public void start()
  {
    try
    {
      initComponents();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public void stop()
  {
  }

  public void destroy()
  {
  }

  /**
   * Enable standalone use.
   */
  public static void main(String args[])
  {
    HCAViewerApplet app = new HCAViewerApplet();
    app.start();
    JFrame f = new JFrame("Turtle");
    f.addWindowListener(new WindowAdapter() {

      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }

    });
    Container contentPane = f.getContentPane();
    contentPane.add(app);
    f.pack();
    f.setVisible(true);
  }

}
