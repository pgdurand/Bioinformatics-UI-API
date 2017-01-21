package test;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import bzh.plealog.bioinfo.ui.logo.LogoCell;
import bzh.plealog.bioinfo.ui.logo.LogoLetter;
import bzh.plealog.bioinfo.ui.logo.LogoPanel;

public class LogoDrawTest extends JFrame {
  private static final long serialVersionUID = -5430644293800604275L;
  private LogoPanel         _mainPanel;

  public LogoDrawTest() {
    super("Logo");
    _mainPanel = new LogoPanel();
    _mainPanel.setBackground(Color.WHITE);
    _mainPanel.setType(LogoPanel.Type.LogoLetter);
    this.getContentPane().add(_mainPanel);
  }

  private void initLogoCell() {
    LogoCell logoCell;
    LogoLetter logoL;
    
    logoCell = new LogoCell(2.0);
    logoL = new LogoLetter("G", 0.5);
    logoL.setSymbFgColor(Color.GREEN);
    logoCell.addLogoLetter(logoL);
    logoL = new LogoLetter("A", 1.0);
    logoL.setSymbFgColor(Color.blue);
    logoCell.addLogoLetter(logoL);
    logoL = new LogoLetter("C", 0.1);
    logoCell.addLogoLetter(logoL);
    logoL.setSymbFgColor(Color.orange);
    logoL = new LogoLetter("T", 0.2);
    logoCell.addLogoLetter(logoL);
    logoL.setSymbFgColor(Color.BLACK);
    logoCell.orderLogoLetter();
    _mainPanel.setLogoCell(logoCell);
  }

  public static void main(String[] argv) {
    LogoDrawTest frame;

    frame = new LogoDrawTest();
    frame.initLogoCell();
    frame.setSize(640, 480);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new MainWindowAdapter());
    frame.setVisible(true);
  }

  private static class MainWindowAdapter extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
      System.exit(0);
    }
  }
}
