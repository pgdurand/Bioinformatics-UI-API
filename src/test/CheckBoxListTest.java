package test;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.plealog.genericapp.api.log.EZLogger;

import bzh.plealog.bioinfo.ui.util.CheckBoxList;
import bzh.plealog.bioinfo.ui.util.CheckBoxListItem;
import bzh.plealog.bioinfo.ui.util.CheckBoxListSelectionListener;

/**
 * Simple test for CheckBoxList class.
 * 
 * @author Patrick G. Durand
 * */
public class CheckBoxListTest {

  public static void main(String args[]) {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    CheckBoxListItem[] data = new CheckBoxListItem[] { 
        new CheckBoxListItem("dna"), 
        new CheckBoxListItem("rna"),
        new CheckBoxListItem("protein"), 
        new CheckBoxListItem("structure")};
    
    CheckBoxList list = new CheckBoxList(data);
    list.addCheckBoxListSelectionListener(new MyCheckBoxListSelectionListener());
    frame.getContentPane().add(new JScrollPane(list));
    frame.pack();
    frame.setVisible(true);
  }

  private static class MyCheckBoxListSelectionListener implements CheckBoxListSelectionListener{

    @Override
    public void itemSelected(CheckBoxListItem item) {
      EZLogger.info(item.toString()+": "+item.isSelected());
    }
  }
}