/* Copyright (C) 2003-2019 Patrick G. Durand
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

import java.util.Enumeration;

import bzh.plealog.bioinfo.api.data.searchjob.BFileSummary;
import bzh.plealog.bioinfo.api.data.searchjob.QueryBase;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;

/**
 * Implementation of a QueryBase to be used in UI components.
 * 
 * @author Patrick G. Durand
 */
public class QueryBaseUI extends QueryBase {
  
  private QueryBaseListener listener;
  private QueryBase _query;
  
  private QueryBaseUI() {
    super();
  }

  /**
   * Constructor.
   * 
   * @param qb QueryBase object to embed here.
   */
  public QueryBaseUI(QueryBase qb) {
    this();
    _query = qb;
  }

  /**
   * Set a listener to this QueryBase.
   */
  public void setListener(QueryBaseListener queryBaseListener) {
    this.listener = queryBaseListener;
  }

  /**
   * Reset listener of this QueryBase.
   */
  public void resetInternalData() {
    if (this.listener != null) {
      this.listener.refresh();
    }
  }

  @Override
  public boolean allSequencesIndexed() {
    return _query.allSequencesIndexed();
  }

  @Override
  public int countStatuses(byte arg0) {
    return _query.countStatuses(arg0);
  }

  @Override
  public String getDatabankName() {
    return _query.getDatabankName();
  }

  @Override
  public String getEngineSysName() {
    return _query.getEngineSysName();
  }

  @Override
  public String getJobName() {
    return _query.getJobName();
  }

  @Override
  public String getQueryPath() {
    return _query.getQueryPath();
  }

  @Override
  public String getRID() {
    return _query.getRID();
  }

  @Override
  public SROutput getResult(int arg0) {
    return _query.getResult(arg0);
  }

  @Override
  public DSequence getSequence(int arg0) {
    return _query.getSequence(arg0);
  }

  @Override
  public int getStatus() {
    return 0;
  }

  @Override
  public String getStatus(int arg0) {
    return _query.getStatus(arg0);
  }

  @Override
  public Enumeration<BFileSummary> getSummaries() {
    return _query.getSummaries();
  }

  @Override
  public BFileSummary getSummary(int arg0) {
    return _query.getSummary(arg0);
  }

  @Override
  public boolean hasHits(int arg0) {
    return _query.hasHits(arg0);
  }

  @Override
  public int sequences() {
    return _query.sequences();
  }

}
