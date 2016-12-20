/* Copyright (C) 2003-2016 Patrick G. Durand
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

import java.awt.Component;
import java.io.File;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.api.EZGenericApplication;
import com.plealog.genericapp.api.EZUIStarterListener;

import bzh.plealog.bioinfo.api.core.config.CoreSystemConfigurator;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.searchresult.io.SRLoader;
import bzh.plealog.bioinfo.io.searchresult.SerializerSystemFactory;
import bzh.plealog.bioinfo.ui.blast.BlastViewerPanelBase;
import bzh.plealog.bioinfo.ui.config.UISystemConfigurator;

/**
 * A sample application to illustrate how to create a simple BLAST viewer.
 * 
 * @author Patrick G. Durand
 */
public class BasicBlastViewer {

  // Note: the "real" BlastViewer application is a separate and autonomous
  // Github project; check out https://github.com/pgdurand/BlastViewer
  // The present code snippet is just an illustration to show how easy
  // one can setup a simple BlastViewer component

  // We will force the Font so that we have a nice sequence viewer
  private static File _blastFile = new File("./data/blastp.xml");

  /**
   * Start application. Relies on the Java Generic Application Framework. See
   * https://github.com/pgdurand/jGAF
   */
  public static void main(String[] args) {
    // This has to be done at the very beginning, i.e. first method call within
    // main().
    EZGenericApplication.initialize("BasicBLASTViewer");
    // Add application branding
    EZApplicationBranding.setAppName("Basic BLAST Viewer");
    EZApplicationBranding.setAppVersion("1.0");
    EZApplicationBranding.setCopyRight("P. Durand");
    EZApplicationBranding.setProviderName("Plealog Software");

    // Add a listener to application startup cycle (see below)
    EZEnvironment.setUIStarterListener(new MyStarterListener());

    // Required to use Plealog Bioinformatics Core objects such as Features,
    // FeatureTables, Sequences
    CoreSystemConfigurator.initializeSystem();

    // Required to use the Plealog Bioinformatics UI library (CartoViewer
    // default graphics)
    UISystemConfigurator.initializeSystem();

    // Start the application
    EZGenericApplication.startApplication(args);
  }

  /**
   * Implementation of the jGAF API.
   */
  private static class MyStarterListener implements EZUIStarterListener {

    private SROutput readBlastFile() {
      // setup an NCBI Blast Loader (XML)
      SRLoader ncbiBlastLoader = SerializerSystemFactory.getLoaderInstance(SerializerSystemFactory.NCBI_LOADER);
      return ncbiBlastLoader.load(_blastFile);
    }


    @Override
    public Component getApplicationComponent() {
      BlastViewerPanelBase viewer = new BlastViewerPanelBase();
      viewer.setContent(readBlastFile());
      return viewer;
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

}
