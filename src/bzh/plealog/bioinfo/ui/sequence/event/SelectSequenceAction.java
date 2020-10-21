package bzh.plealog.bioinfo.ui.sequence.event;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.plealog.genericapp.api.EZEnvironment;

import bzh.plealog.bioinfo.api.data.sequence.DLocation;
import bzh.plealog.bioinfo.api.data.sequence.DRulerModel;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.ui.resources.SVMessages;
import bzh.plealog.bioinfo.ui.util.Selection;

/**
 * Handles the selection of sequence on the sequence viewer.
 * 
 * @author Patrick G. Durand
 */
public class SelectSequenceAction extends AbstractAction {
  private static final long serialVersionUID = -3165198883879337438L;
  private DSelectionListenerSupport _lSupport;
  private DSequence _sequence;
  private Selection.SelectType _selectType;
  private boolean _running;

  /**
   * Action constructor.
   * 
   * @param name
   *          the name of the action.
   */
  public SelectSequenceAction(String name, Selection.SelectType selType) {
    super(name);
    _selectType = selType;
  }

  /**
   * Action constructor.
   * 
   * @param name
   *          the name of the action.
   * @param icon
   *          the icon of the action.
   */
  public SelectSequenceAction(String name, Icon icon, Selection.SelectType selType) {
    super(name, icon);
    _selectType = selType;
  }

  /**
   * Sets the sequence viewer from which to retrieve the sequence.
   */
  public void setListenerSupport(DSelectionListenerSupport lSupport) {
    _lSupport = lSupport;
  }

  public void setSequence(DSequence seq) {
    _sequence = seq;
  }

  public void actionPerformed(ActionEvent event) {
    if (_running)
      return;
    new TaskRunner().start();
  }

  private void handleRangeSelection() {
    IntervalBuilder ib;
    String ranges, err;
    List<DLocation> locs;
    ArrayList<DLocation> locs2;
    ;
    DRulerModel rModel;
    int from, to;

    ranges = EZEnvironment.inputValueMessage(EZEnvironment.getParentFrame(),
        SVMessages.getString("SelectSequenceAction.msg1"));
    if (ranges == null)
      return;

    ib = new IntervalBuilder();
    locs = ib.interpret2(ranges);
    err = ib.getErrorMessage();
    if (err != null) {
      EZEnvironment.displayWarnMessage(EZEnvironment.getParentFrame(), err);
      return;
    }
    if (locs.isEmpty())
      return;
    // convert seq coord to absolute ones
    rModel = _sequence.getRulerModel();
    if (rModel == null)
      return;
    locs2 = new ArrayList<DLocation>();
    for (DLocation loc : locs) {
      from = rModel.getRulerPos(loc.getFrom());
      to = rModel.getRulerPos(loc.getTo());
      if (from != -1 && to != -1) {
        locs2.add(new DLocation(from, to));
      }
    }
    _lSupport.setSelectionRanges(this, _sequence, locs2);
  }

  /**
   * Export the sequence.
   */
  private void selectSequence() {
    if (_sequence == null)
      return;

    switch (_selectType) {
    case ALL:
      _lSupport.setSelectedSequenceRange(this, _sequence, 0, _sequence.size() - 1);
      break;
    case CLEAR:
      _lSupport.setSelectedSequenceRange(this, _sequence, -1, -1);
      break;
    case RANGE:
      handleRangeSelection();
      break;
      default:
    }
  }

  private class TaskRunner extends Thread {
    public void run() {
      _running = true;
      selectSequence();
      _running = false;
    }
  }
}
