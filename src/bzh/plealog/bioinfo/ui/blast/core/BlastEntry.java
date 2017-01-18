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

import java.io.Serializable;

import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.searchresult.SRRequestInfo;
import bzh.plealog.bioinfo.api.data.sequence.DSeqUtils;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;
import bzh.plealog.bioinfo.api.data.sequence.DSequenceInfo;

/**
 * This interface defines a BLAST entry. Such an entry wraps a Blast Result
 * file, i.e. a SROutput object.
 * 
 * @author Patrick G. Durand
 */
public class BlastEntry implements Serializable {

  private static final long serialVersionUID = -7666414152537745109L;
  private String            _blastClientName;
  private String            _queryName;
  private String            _name;
  private String            _path;
  private String            _repr;
  private String            _filterName;
  private String            _dbName;
  private int               _entryOrderNum;
  private SROutput          _result;
  private DSequence         _query;
  private boolean           _view;

  // unable to serialize a DSequence object => too many cross references
  // so to store a BlastEntry : set a FastaSequence and create a DSequence when
  // asked
  private FastaSequence     sequence         = null;

  /**
   * Create a BlastEntry.
   * 
   * @param bClientName
   *          name of the client used to create this Blast result
   * @param name
   *          the name of this Blast result
   * @param path the
   *          absolute path where the file is located
   * @param result
   *          a Blast Result
   * @param query
   *          the query sequence
   * @param dbName
   *          name of the subject databank
   * @param view
   *          true if this is a simple view, false otherwise.
   */
  public BlastEntry(String bClientName, String name, String path,
      SROutput result, DSequence query, String dbName, Boolean view) {
    setBlastClientName(bClientName);
    setName(name);
    setAbsolutePath(path);
    setResult(result);
    setQuery(query);
    setDbName(dbName);
    setView(view);
    setQueryName(name);
  }

  public BlastEntry(QueryBase query, SROutput bo, DSequence seq, Boolean view) {
    this(query.getEngineSysName(), query.getJobName(), query.getQueryPath(),
        bo, seq, query.getDatabankName(), view);
  }

  public BlastEntry(QueryBase query, SROutput bo, FastaSequence seq,
      Boolean view) {
    this(query.getEngineSysName(), query.getJobName(), query.getQueryPath(),
        bo, null, query.getDatabankName(), view);
    this.sequence = seq;
  }

  public BlastEntry(BlastEntry entry, SROutput bo) {
    this(entry.getBlastClientName(), entry.getQueryName(), entry
        .getAbsolutePath(), bo, entry.getQuery(), entry.getDbName(), true);
    this.setEntryOrderNum(entry.getEntryOrderNum());
    this.setFilterName(entry.getFilterName());
  }

  /**
   * Return the name of this Blast entry.
   */
  public String getName() {
    return _name;
  }

  /**
   * Return the name of the Blast Client that produced this Blast entry.
   */
  public String getBlastClientName() {
    return _blastClientName;
  }

  /**
   * Return the name of the query from which this Blast entry comes from.
   */
  public String getQueryName() {
    return _queryName;
  }

  /**
   * Return the Blast result data of this Blast entry.
   */
  public SROutput getResult() {
    return _result;
  }

  /**
   * Return the absolute path pointing to the file containing the Blast result
   * file.
   */
  public String getAbsolutePath() {
    return _path;
  }

  /**
   * Set the name of this Blast entry.
   */
  public void setName(String string) {
    _name = string;
    _repr = null;
  }

  /**
   * Set the name of the Blast Client that produced this Blast entry.
   */
  public void setBlastClientName(String string) {
    _blastClientName = string;
    _repr = null;
  }

  /**
   * Set the name of the query from which this Blast entry comes from.
   */
  public void setQueryName(String qName) {
    _queryName = qName;
    _repr = null;
  }

  /**
   * Return the Blast result data of this Blast entry.
   */
  public void setResult(SROutput output) {
    _result = output;
    _repr = null;
  }

  /**
   * Return the absolute path pointing to the file containing the Blast result
   * file.
   */
  public void setAbsolutePath(String path) {
    _path = path;
    _repr = null;
  }

  /**
   * Set the filter name used to create this entry from another one.
   */
  public void setFilterName(String fName) {
    _filterName = fName;
  }

  /**
   * Return the filter name used to create this entry from another one.
   */
  public String getFilterName() {
    return _filterName;
  }

  /**
   * Set the query sequence that was used to create this result.
   */
  public DSequence getQuery() {
    if ((_query == null) && (this.sequence != null)) {
      // create the DSequence object
      _query = DSeqUtils.getSequence(this.sequence.getSequence(), this
          .getResult().getQuerySeqType() == SROutput.AA_SEQ ? true : false);
      DSequenceInfo dsi = new DSequenceInfo();
      dsi.setId(this.sequence.getId());
      dsi.setName(this.sequence.getName());
      _query.setSequenceInfo(dsi);
    }
    return _query;
  }

  /**
   * Return the query sequence ID.
   */
  public String getSequenceId() {
    if (this.sequence != null) {
      return this.sequence.getId();
    }
    if (this.getQuery() != null) {
      return this.getQuery().getSequenceInfo().getId();
    } else {
      return "";
    }
  }

  /**
   * Return the query sequence name.
   */
  public String getSequenceName() {
    if (this.sequence != null) {
      return this.sequence.getName();
    }
    if (this.getQuery() != null) {
      return this.getQuery().getSequenceInfo().getName();
    } else {
      return "";
    }
  }

  /**
   * Return the query sequence size.
   */
  public int getSequenceSize() {
    if (this.sequence != null) {
      return this.sequence.getSize();
    }
    if (this.getQuery() != null) {
      return this.getQuery().size();
    } else {
      return 0;
    }
  }

  /**
   * Return the query sequence that was used to create this result.
   */
  public void setQuery(DSequence query) {
    _query = query;
  }

  /**
   * Return the ordering number of this BlastEntry within a whole BlastQuery.
   */
  public int getEntryOrderNum() {
    return _entryOrderNum;
  }

  /**
   * Set the ordering number of this BlastEntry within a whole BlastQuery.
   */
  public void setEntryOrderNum(int orderNum) {
    _entryOrderNum = orderNum;
  }

  /**
   * Specify whether or not this BlastEntry is a view. A view contains a subset
   * of hits and hsps from another view or reference result.
   */
  public void setView(boolean view) {
    _view = view;
  }

  /**
   * Figure out whether or not this is a view.
   */
  public boolean isView() {
    return _view;
  }

  /**
   * Return the subject databank name.
   */
  public String getDbName() {
    return _dbName;
  }

  /**
   * Set the subject databank name.
   */
  public void setDbName(String name) {
    _dbName = name;
  }

  public String toString() {
    StringBuffer buf;
    String prg;

    if (_repr != null) {
      return _repr;
    }
    buf = new StringBuffer();
    prg = (String) _result.getRequestInfo().getValue(
        SRRequestInfo.PROGRAM_DESCRIPTOR_KEY);
    buf.append(getQueryName());
    buf.append(": ");
    buf.append(_name);
    buf.append(" (");
    buf.append(prg != null ? prg : "?");
    buf.append(")");
    _repr = buf.toString();
    return _repr;
  }
}
