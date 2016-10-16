package bzh.plealog.bioinfo.ui.sequence.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import bzh.plealog.bioinfo.api.data.sequence.DLocation;

/**
 * This class can be used to create an ordered array of integers built from a
 * formatted string. This string is a comma separated list of tokens. Each token
 * can be a positive integer or a range of two positive integers. To separate
 * the integers within a range, use the minus character. Example of valid
 * strings: <br>
 * 1,2,6,5<br>
 * 12-65,89-125,256<br>
 * <br>
 * This system handles values that are provided several times as well as
 * overlapping intervals.
 * 
 * @author Patrick G. Durand
 */
public class IntervalBuilder {
  private String errMsg_;

  private Integer getNumber(String s) {
    Integer val;

    try {
      val = Integer.valueOf(s);
    } catch (NumberFormatException e) {
      val = null;
    }
    return val;
  }

  private void addPos(ArrayList<Integer> posList, Integer pos) {
    if (posList.contains(pos)) {
      return;
    }
    posList.add(pos);
  }

  private boolean addPos(ArrayList<Integer> posList, String sPos) {
    Integer pos;
    pos = getNumber(sPos);
    if (pos == null) {
      return false;
    } else {
      addPos(posList, pos);
      return true;
    }
  }

  private boolean addPos2(ArrayList<DLocation> posList, String sPos, int decal) {
    Integer pos;
    pos = getNumber(sPos);
    if (pos == null) {
      return false;
    } else {
      posList.add(new DLocation(pos + decal, pos + decal));
      return true;
    }
  }

  private boolean addPosInterval(ArrayList<Integer> posList, String sPos1, String sPos2) {
    Integer i, pos1, pos2, from, to;

    pos1 = getNumber(sPos1);
    if (pos1 == null) {
      return false;
    }
    pos2 = getNumber(sPos2);
    if (pos2 == null) {
      return false;
    }
    from = Math.min(pos1, pos2);
    to = Math.max(pos1, pos2);
    for (i = from; i <= to; i++) {
      addPos(posList, i);
    }
    return true;
  }

  private boolean addPosInterval2(ArrayList<DLocation> posList, String sPos1, String sPos2, int decal) {
    Integer pos1, pos2, from, to;

    pos1 = getNumber(sPos1);
    if (pos1 == null) {
      return false;
    }
    pos2 = getNumber(sPos2);
    if (pos2 == null) {
      return false;
    }
    from = Math.min(pos1, pos2);
    to = Math.max(pos1, pos2);
    posList.add(new DLocation(from + decal, to + decal));
    return true;
  }

  private int[] prepareData(String data) {
    StringTokenizer tokenizer1, tokenizer2;
    String token;
    ArrayList<Integer> posList;
    int[] selectedPos = null;

    if (data == null || data.length() == 0) {
      errMsg_ = "Error: no data provided";
      return null;
    }
    posList = new ArrayList<Integer>();
    tokenizer1 = new StringTokenizer(data, ",");
    while (tokenizer1.hasMoreTokens()) {
      token = tokenizer1.nextToken();
      if (token.indexOf('-') == -1) {
        if (!addPos(posList, token)) {
          errMsg_ = "Error: " + token + ": not a number";
          return null;
        }
      } else {
        tokenizer2 = new StringTokenizer(token, "-");
        if (tokenizer2.countTokens() != 2) {
          errMsg_ = "Error: " + token + ": invalid interval";
          return null;
        }
        if (!addPosInterval(posList, tokenizer2.nextToken(), tokenizer2.nextToken())) {
          errMsg_ = "Error: " + token + ": interval does not contain numbers";
          return null;
        }
      }
    }

    if (!posList.isEmpty()) {
      Collections.sort(posList);
      selectedPos = new int[posList.size()];
      for (int i = 0; i < posList.size(); i++) {
        selectedPos[i] = posList.get(i).intValue();
      }
    }
    return selectedPos;
  }

  private List<DLocation> prepareData2(String data, int decal) {
    StringTokenizer tokenizer1, tokenizer2;
    String token;
    ArrayList<DLocation> posList;

    if (data == null || data.length() == 0) {
      errMsg_ = "Error: no data provided";
      return null;
    }
    posList = new ArrayList<DLocation>();
    tokenizer1 = new StringTokenizer(data, ",");
    while (tokenizer1.hasMoreTokens()) {
      token = tokenizer1.nextToken();
      if (token.indexOf('-') == -1) {
        if (!addPos2(posList, token, decal)) {
          errMsg_ = "Error: " + token + ": not a number";
          return null;
        }
      } else {
        tokenizer2 = new StringTokenizer(token, "-");
        if (tokenizer2.countTokens() != 2) {
          errMsg_ = "Error: " + token + ": invalid interval";
          return null;
        }
        if (!addPosInterval2(posList, tokenizer2.nextToken(), tokenizer2.nextToken(), decal)) {
          errMsg_ = "Error: " + token + ": interval does not contain numbers";
          return null;
        }
      }
    }

    if (!posList.isEmpty()) {
      Collections.sort(posList, new DLocationComparator());
    }
    return posList;
  }

  /**
   * Returns an error message. Call this method only when interpret() returns
   * null.
   */
  public String getErrorMessage() {
    return errMsg_;
  }

  /**
   * Converts a string into an ordered list of integers. See class description
   * from the accepted string format. This method can return null if string is
   * wrongly formatted. In such case, call getErrorMessage() to figure out
   * what's wrong.
   */
  public int[] interpret(String data) {
    errMsg_ = null;
    return prepareData(data);
  }

  public List<DLocation> interpret2(String data) {
    errMsg_ = null;
    return prepareData2(data, 0);
  }

  public List<DLocation> interpret2(String data, int decal) {
    errMsg_ = null;
    return prepareData2(data, decal);
  }

  private class DLocationComparator implements Comparator<DLocation> {

    public int compare(DLocation o1, DLocation o2) {
      if (o1.getFrom() > o2.getFrom()) {
        return +1;
      } else if (o1.getFrom() < o2.getFrom()) {
        return -1;
      } else {
        return 0;
      }
    }
  }
}
