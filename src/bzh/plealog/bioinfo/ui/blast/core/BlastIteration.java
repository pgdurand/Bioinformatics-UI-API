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
package bzh.plealog.bioinfo.ui.blast.core;

import bzh.plealog.bioinfo.api.data.searchresult.SRIteration;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.searchresult.SRRequestInfo;

/**
 * This class is used to handle a SRIteration object within the BlastSummary
 * panel.
 * 
 * @author Patrick G. Durand
 */
public class BlastIteration {
  private BlastEntry _entry;
  private int        _querySize;
  private int        _iter;
  private String     _stringRepr;

  /**
   * Constructor.
   * 
   * @param iter
   *          the SRIteration to store here
   * @param qSize
   *          the query sequence size
   * @param bType
   *          the Blast type. Use one of the SROutput.BLAST_XXX constants.
   */
  public BlastIteration(BlastEntry entry, int iter) {
    SROutput bo;
    SRRequestInfo bri;
    Object obj;

    bo = entry.getResult();
    bri = bo.getRequestInfo();
    _iter = iter;
    _entry = entry;

    // How to get query size ? Usually, a standard BlastOutput XML file reports
    // that size for every single Blast_Iteration. Sometimes, this is not the
    // case, but
    // that size can then be retrieved from the Blast Parameters data.
    _querySize = bo.getIteration(iter).getIterationQueryLength();
    if (_querySize == 0) {
      obj = bri.getValue(SRRequestInfo.QUERY_LENGTH_DESCRIPTOR_KEY);
      if (obj != null) {
        _querySize = ((Integer) obj).intValue();
      }
    }
    if (_querySize == 0)
      _querySize = 1;
  }

  /**
   * Return the iteration.
   * 
   * @return an iteration
   */
  public SRIteration getIteration() {
    return _entry.getResult().getIteration(_iter);
  }

  /**
   * Return the BlastEntry containing this iteration.
   */
  public BlastEntry getEntry() {
    return _entry;
  }

  /**
   * Return the query size.
   */
  public int getQuerySize() {
    return _querySize;
  }

  /**
   * Return the iteration ordering number.
   **/
  public int getIterNum() {
    return _iter;
  }

  /**
   * Return the Blast type.
   * 
   * @return one of SROutput.BLASTXXX integer constants
   * */
  public int getBlastType() {
    return _entry.getResult().getBlastType();
  }

  public String toString() {
    if (_stringRepr != null)
      return _stringRepr;
    StringBuffer buf = new StringBuffer();
    if (getBlastType() == SROutput.PSIBLAST) {
      buf.append("Iteration ");
      buf.append((_iter + 1));
    } else {
      SRIteration iteration = getIteration();
      String def = iteration.getIterationQueryDesc();
      if (def != null) {
        int idx = -1;// def.indexOf(' ');
        if (idx != -1)
          def = def.substring(0, idx);
        buf.append("[");
        buf.append((_iter + 1));
        buf.append("] ");
        if (def.length() > 50)
          buf.append(def.substring(0, 50));
        else
          buf.append(def);
      } else {
        buf.append("Seq ");
        buf.append((_iter + 1));
      }
    }
    buf.append(": ");
    buf.append(getIteration().countHit());
    buf.append(" hits");
    _stringRepr = buf.toString();
    return _stringRepr;
  }

}
