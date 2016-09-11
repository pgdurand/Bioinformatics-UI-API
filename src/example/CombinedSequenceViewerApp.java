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

import java.awt.Component;
import java.io.StringReader;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.api.EZGenericApplication;
import com.plealog.genericapp.api.EZUIStarterListener;

import bzh.plealog.bioinfo.api.core.config.CoreSystemConfigurator;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DViewerSystem;
import bzh.plealog.bioinfo.ui.config.UISystemConfigurator;
import bzh.plealog.bioinfo.ui.sequence.extended.CombinedSequenceViewer;

/**
 * A simple class showing how to create, start and display a Combined Sequence Viewer.
 * 
 * @author Patrick G. Durand
 */
public class CombinedSequenceViewerApp {
  /**
   * Start application. Relies on the Java Generic Application Framework.
   * See https://github.com/pgdurand/jGAF
   */
  public static void main(String[] args) {
    // This has to be done at the very beginning, i.e. first method call within
    // main().
    EZGenericApplication.initialize("CombinedSequenceViewerExample");
    // Add application branding
    EZApplicationBranding.setAppName("Combined Sequence Viewer");
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
   * Implementation of the jGAF API.
   */
  private static class MyStarterListener implements EZUIStarterListener {

    @Override
    public Component getApplicationComponent() {
      CombinedSequenceViewer viewer;
      DSequence                 seq;

      String                    prot = 
          "MASEFKKKLFWRAVVAEFLATTLFVFISIGSALGFKYPVGNNQTAVQDNVKVS"+
              "LAFGLSIATLAQSVGHISGAHLNPAVTLGLLLSCQISIFRALMYIIAQCVGAI"+
              "VATAILSGITSSLTGNSLGRNDLADGVNSGQGLGIEIIGTLQLVLCVLATTDR"+
              "RRRDLGGSAPLAIGLSVALGHLLAIDYTGCGINPARSFGSAVITHNFSNHWIFW"+
              "VGPFIGGALAVLIYDFILAPRSSDLTDRVKVWTSGQVEEYDLDADDINSRVEMKPK";
      seq = DViewerSystem.getSequenceFactory().getSequence(new StringReader(prot), DViewerSystem.getIUPAC_Protein_Alphabet());

      //use the following to display a DNA sequence sample
      @SuppressWarnings("unused")
      String                    nuc = 
      "CATTCACAGCAAAAAATGCGCACGTCGCTCGTCGTGTGCCTTTTTTGGCTTCTATTTCAATTACATACA"+
          "ACACATGGCTACAATACGCTCGTTAACCTCGCTGGAAACTGGGAGTTCTCTTCAAGTAACAAAACTGTC"+
          "AATGGAACAGGAACCGTTCCCGGAGACATTTATTCGGATTTATACGCCTCGGGAATCATCGACAATCCGC"+
          "TTTTTGGGGAGAATCATCTGAATCTAAAGTGGATTGCCGAGGATGATTGGACGTATAGCAGAAAGTTTCG"+
          "ATTGATAGATCTAGACGACACGGTTGGCGCCTTCCTCGAAATTGAGAGCGTTGACACAATTGCCACCGTG"+
          "TATGTGAACGGACAAAAAGTCTTGCATTCAAGAAACCAATTTCTGCCCTATCATGTCAACGTGACGGACA"+
          "TCATTGCACTCGGTGAAAATGACATAACGATCAAGTTCAAAAGTTCAGTGAAATATGCGGAAAAGCGAGC"+
          "GGATGAGTACAAAAAAATATTCGGGCATTCTCTTCCACCAGATTGCAACCCGGACATTTATCATGGAGAA"+
          "TGTCATCAAAACTTTATTAGAAAGGCCCAATACAGTTTTGCCTGGGATTGGGGACCGTCTTTTCCAACAG"+
          "TTGGAATCCCAAGCACTATCACTATAAATATCTACAGAGGACAATATTTCCATGATTTCAATTGGAAAAC"+
          "TAGATTTGCTCATGGAAAATGGAAAGTCGCTTTTGAATTCGACACATTCCACTATGGTGCAAGAACCATT"+
          "GAGTACTCTGTTCAAATTCCTGAGCTCGGAATCAAGGAGTCTGATTACTATAGACTATCAGCCACCAAGA"+
          "GTTTGCAAACAAGATCAAAAAACATCATGTCCCTATCAATTCCAATGGAACACGAACCAGAACGTTGGTG"+
          "GCCAAATGGAATGGGAGAGCAGAAACTTTATGACGTTGTGGTGTCAATGGGAGGCCAAGTGAAAGAAAAG"+
          "AAAATTGGATTCAAGACAGTTGAGCTAGTTCAAGATTTAATTGATCCTAAGAAGCCAGAGAAGGGAAGAA"+
          "ATTTCTATTTTAAAATCAACGATGAGCCTGTTTTCCTAAAAGGAACAAATTGGATTCCTGTTTCAATGTTCC";
      //seq = DViewerSystem.getSequenceFactory().getSequence(new StringReader(nuc), DViewerSystem.getIUPAC_DNA_Alphabet());

      //sequence coordinate system starts at 1
      seq.createRulerModel(1, 1);

      //create the viewer
      viewer = new CombinedSequenceViewer();
      //set a sequence
      viewer.setSequence(seq);
      //no features, but this call is required (reason: old "bad" UI design... ;-) )
      viewer.setFeaturesForCartoView(null);

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
