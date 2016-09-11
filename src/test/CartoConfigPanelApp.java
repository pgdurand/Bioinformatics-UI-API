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
package test;

import java.awt.Container;

import javax.swing.JFrame;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;

import bzh.plealog.bioinfo.api.core.config.CoreSystemConfigurator;
import bzh.plealog.bioinfo.ui.carto.config.CartoConfigPanel;
import bzh.plealog.bioinfo.ui.config.UISystemConfigurator;

/**
 * This class shows how to start the CartoViewer Configuration panel.
 * 
 * @author Patrick G. Durand
 */
public class CartoConfigPanelApp {

  public static void main(String[] args) {
    // required to init jGAF system
    EZApplicationBranding.setAppName("Sequence Viewer");

    // Required to use Plealog Bioinformatics Core objects such as Features, FeatureTables, Sequences
    CoreSystemConfigurator.initializeSystem();

    // Required to use the Plealog Bioinformatics UI library (CartoViewer default graphics)
    UISystemConfigurator.initializeSystem();

    //starts a basic frame
    JFrame frame = new JFrame("CartoConfigPanel");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Container contentPane = frame.getContentPane();
    contentPane.add(new CartoConfigPanel());
    frame.pack();
    frame.setSize(800, 600);
    frame.setVisible(true);
    EZEnvironment.setParentFrame(frame);

  }


}
