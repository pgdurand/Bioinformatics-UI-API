package example;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.StringReader;

import javax.swing.SwingConstants;
import javax.swing.UIManager;

import bzh.plealog.bioinfo.api.core.config.CoreSystemConfigurator;
import bzh.plealog.bioinfo.api.data.sequence.DRulerModel;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSequenceModel;
import bzh.plealog.bioinfo.api.data.sequence.DViewerSystem;
import bzh.plealog.bioinfo.ui.config.UISystemConfigurator;
import bzh.plealog.bioinfo.ui.sequence.basic.DRulerViewer;
import bzh.plealog.bioinfo.ui.sequence.basic.DSequenceListViewer;
import bzh.plealog.bioinfo.ui.sequence.basic.DSequenceViewer;
import bzh.plealog.bioinfo.ui.sequence.basic.DViewerScroller;

import com.plealog.genericapp.api.EZApplicationBranding;
import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.api.EZGenericApplication;
import com.plealog.genericapp.api.EZUIStarterListener;

/**
 * A sample application to illustrate how to create a simple sequence viewer.
 * 
 * @author Patrick G. Durand
 */
public class BasicSequenceViewer {

  // We will force the Font so that we have a nice sequence viewer
  private static Font   _fnt = new Font("Arial", Font.PLAIN, 12);

  // This is a sample app: we force the sequence (a protein in this sample)
  private static String _protein = 
      "MASEFKKKLFWRAVVAEFLATTLFVFISIGSALGFKYPVGNNQTAVQDNVKVS"+
          "LAFGLSIATLAQSVGHISGAHLNPAVTLGLLLSCQISIFRALMYIIAQCVGAI"+
          "VATAILSGITSSLTGNSLGRNDLADGVNSGQGLGIEIIGTLQLVLCVLATTDR"+
          "RRRDLGGSAPLAIGLSVALGHLLAIDYTGCGINPARSFGSAVITHNFSNHWIFW"+
          "VGPFIGGALAVLIYDFILAPRSSDLTDRVKVWTSGQVEEYDLDADDINSRVEMKPK";

  /**
   * Start application. Relies on the Java Generic Application Framework.
   * See https://github.com/pgdurand/jGAF
   */
  public static void main(String[] args) {
    // This has to be done at the very beginning, i.e. first method call within
    // main().
    EZGenericApplication.initialize("BasicSequenceViewer");
    // Add application branding
    EZApplicationBranding.setAppName("Simple Sequence Viewer");
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

    // Create a sequence viewer. Only displays the sequence, i.e. letters.
    private DSequenceListViewer createViewer(String str){
      DSequenceListViewer viewer;
      DSequenceModel      model;
      DSequence           seq;

      // we use the DViewerSystem to get a DSequence factory. It aims at creating
      // a DSequence
      seq = DViewerSystem.getSequenceFactory().getSequence(new StringReader(str),
          DViewerSystem.getIUPAC_Protein_Alphabet());
      // by default, a DSequence is never associated to a coordinate system. So, we
      // specifically creates a coordinate system starting at one.
      seq.createRulerModel(1,1);

      // In turn, the DSequence is embedded within a DSequenceModel which is a
      // ListModel...
      model = new DSequenceModel(seq);

      // ... indeed, the Sequence Viewer is actually a JList converted to be a
      // viewer of DSequence
      viewer = new DSequenceListViewer();
      viewer.setModel( model );

      // finally, we set a font
      viewer.setFont(_fnt);

      return (viewer);
    }

    // Create a ruler viewer. Display the sequence coordinate system of the sequence.
    private DRulerViewer createRuler(DSequenceListViewer seqViewer){
      // we retrieve the ruler of the sequence
      DRulerModel  rulerModel = ((DSequenceModel)seqViewer.getModel()).getSequence().getRulerModel();
      // we create the corresponding viewer
      DRulerViewer rulerViewer = new DRulerViewer(
          rulerModel, 
          seqViewer.getFontMetrics(seqViewer.getFont()).getHeight(), 
          SwingConstants.HORIZONTAL, 
          SwingConstants.BOTTOM);

      return rulerViewer;
    }

    // Assemble an entire working horizontal sequence viewer.
    private Component prepareViewer(){
      DSequenceViewer     viewer;
      DSequenceListViewer seqViewer;
      DRulerViewer        rulerViewer;
      DViewerScroller     scroller;


      // A Sequence viewer is made of two parts:
      // 1. the Sequence Viewer itself which is a derivative of a JList. So this viewer only
      //    displays the sequence itself, i.e. the letters.
      seqViewer = createViewer (_protein); 
      // 2. a Ruler Viewer which aims at displaying the sequence coordinate system (called a 'ruler') .
      rulerViewer = createRuler(seqViewer);

      // Then, we assemble simple sequence viewer and its ruler within a DSequenceViewer 
      viewer = new DSequenceViewer(seqViewer, rulerViewer, false);
      viewer.setAlignmentX(0); //don't let Swing controls the vertical alignment of the component

      // Prepare the scroll component: its aims at controlling the scrolling of the sequence viewer.
      // It is worth noting that we do not use a standard JScrollPane. 
      // Why? See the Pairwise Sequence alignment Viewer for an answer!
      scroller = new DViewerScroller(viewer);

      // Set scroller dimension so that all internal viewers (sequence and ruler) scroll
      // is a same way.
      scroller.setCellWidth(viewer.getSequenceList().getFixedCellWidth());
      scroller.setCellHeight(viewer.getSequenceList().getFixedCellHeight());
      int       cellH       = viewer.getSequenceList().getFixedCellWidth();
      int       scrollWidth = UIManager.getDefaults().getInt("ScrollBar.width");
      Dimension dim         = new Dimension(120, 7*cellH+scrollWidth);
      scroller.getHorizontalScrollBar().setBlockIncrement(50*cellH);
      scroller.getHorizontalScrollBar().setUnitIncrement(cellH);
      scroller.setPreferredSize(dim);
      scroller.setMinimumSize(dim);
      
      // we force white background (optional, of course)
      scroller.setOpaque(true);
      scroller.setBackground(Color.white);

      return scroller;
    }

    @Override
    public Component getApplicationComponent() {
      return prepareViewer();
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
