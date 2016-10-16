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
package bzh.plealog.bioinfo.ui.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.plealog.genericapp.api.EZEnvironment;

/**
 * This class defines a simple retractable panel.
 *
 * @author Patrick G. Durand
 * @since 2003
 */
public class RetractPanel extends JPanel {

  private static final long serialVersionUID = 656640209536109790L;
  private JPanel  containerPanel_;
	private JPanel  titlePanel_;
	private JPanel  optionPanel_;
	private JLabel  titleLbl_;
	private FolderButton folderBtn_;
	private Color   titleBkClr_ = new Color(184,207,229);//new Color(230,230,255);

	private boolean isDownPanel_ = true;


	private static final ImageIcon DOWN_PANEL = EZEnvironment.getImageIcon("foldedarrow.png");
	private static final ImageIcon UP_PANEL = EZEnvironment.getImageIcon("unfoldedarrow.png");

	public RetractPanel(String titre, Container options, boolean foldable, boolean folded) {
		super();

		JPanel btnPanel, optionPanel;

		containerPanel_=new JPanel();
		titlePanel_=new JPanel();
		optionPanel_=new JPanel();

		containerPanel_.setLayout(new BoxLayout(containerPanel_, BoxLayout.Y_AXIS));
		titlePanel_.setLayout(new BorderLayout());
		optionPanel_.setLayout(new BoxLayout(optionPanel_, BoxLayout.Y_AXIS));

		titlePanel_.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		titlePanel_.setMaximumSize(new java.awt.Dimension(2000, 50));
		titlePanel_.setOpaque(true);
		titlePanel_.setBackground(titleBkClr_);

		titleLbl_=new JLabel();
		titleLbl_.setText(titre);
		titleLbl_.setBorder(BorderFactory.createEmptyBorder(3,5,3,0));
		titleLbl_.setForeground(Color.BLACK);
		titlePanel_.add(titleLbl_, BorderLayout.CENTER);

		optionPanel=new JPanel();
		optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));
		optionPanel.add(options);


		folderBtn_ = new FolderButton(folded?UP_PANEL:DOWN_PANEL);
		isDownPanel_ = folded;

		folderBtn_.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				boutonActionPerformed(evt);
			}
		});
		btnPanel = new JPanel(new BorderLayout());
		btnPanel.setOpaque(false);
		btnPanel.add(folderBtn_,BorderLayout.CENTER);
		btnPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,4));
		titlePanel_.add(btnPanel,BorderLayout.WEST);
		containerPanel_.add(titlePanel_);
		optionPanel_.setLayout(new BorderLayout());

		optionPanel_.add(optionPanel, BorderLayout.CENTER);
		containerPanel_.add(optionPanel_);

		folderBtn_.setVisible(foldable);
		if (foldable)
			optionPanel_.setVisible(folded);

		this.setLayout(new BorderLayout());
		this.add(containerPanel_, BorderLayout.CENTER);
	}
	public Dimension getMinimumSize(){
		Dimension dim2;

		dim2 = optionPanel_.getPreferredSize();
		return new Dimension(dim2.width/2, dim2.width/2);
	}
	public void setTitleBackground(Color clr){
		titleBkClr_ = clr;
		folderBtn_.backColor = clr;
		folderBtn_.lineColor1 = clr.darker();
		titlePanel_.setBackground(titleBkClr_);
	}
	public void setTitleForeground(Color clr){
		titleLbl_.setForeground(clr);
	}
	public void setTitleBorder(Border border){
		titlePanel_.setBorder(border);
	}
	public void setFont(Font font){
		super.setFont(font);
		if (titleLbl_!=null)
			titleLbl_.setFont(font);
	}
	public void setTitle(String title){
		titleLbl_.setText(title);
	}
	private class FolderButton extends JButton{
	    /**
     * 
     */
    private static final long serialVersionUID = 9091013906508925806L;

      private static final int BUTTON_WIDTH = 16;

	    private Color backColor = titleBkClr_;
	    private Color lineColor1 = titleBkClr_.darker();
        private boolean pressedState;
        private boolean entered;

        public FolderButton(Icon icon){
        	super (icon);
        	addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e){
                	if (pressedState)
                		backColor = titleBkClr_.darker();
                    entered = true;
                    repaint();
                }
                public void mouseExited(MouseEvent e){
                    backColor = titleBkClr_;
                    entered = false;
                    repaint();
                }
				public void mousePressed(MouseEvent e){
					backColor = titleBkClr_.darker();
					pressedState=true;
					repaint();
				}
				public void mouseReleased(MouseEvent e){
					backColor = titleBkClr_;
					pressedState=false;
					repaint();
				}
            });
        }
        public Dimension getPreferredSize(){
            return (new Dimension(BUTTON_WIDTH, BUTTON_WIDTH));
        }
        public void paintBorder(Graphics g){

        }
        public void paintComponent(Graphics g){
        	Rectangle r = getBounds();
            g.setColor(backColor);
            g.clearRect(0, 0, r.width, r.height);
            g.fillRect(0, 0, r.width, r.height);
            if (entered){
                g.setColor(pressedState?lineColor1:Color.white);
                g.drawLine(0, 0, 0, r.height);
                g.drawLine(0, 0, r.width, 0);
                g.setColor(pressedState?Color.white:lineColor1);
                g.drawLine(r.width - 1, 0, r.width - 1, r.height);
                g.drawLine(r.width, r.height - 1, 0, r.height - 1);

            }
            if(isDownPanel_){
            	g.drawImage(UP_PANEL.getImage(), 0, (r.height-BUTTON_WIDTH)/2, null, null);
            }
            else {
            	g.drawImage(DOWN_PANEL.getImage(), 0, (r.height-BUTTON_WIDTH)/2, null, null);
            }
        }
	}
	private void boutonActionPerformed(ActionEvent evt) {
		if (isDownPanel_)
			folderBtn_.setIcon(DOWN_PANEL);
		else
			folderBtn_.setIcon(UP_PANEL);
		isDownPanel_ = !isDownPanel_;
		this.optionPanel_.setVisible(!this.optionPanel_.isVisible());
	}

}
