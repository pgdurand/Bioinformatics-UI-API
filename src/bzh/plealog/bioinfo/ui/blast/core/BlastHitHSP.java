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

import bzh.plealog.bioinfo.api.data.searchresult.SRHit;

/**
 * This class defines a wrapper to display SRHit in a dedicated viewer.
 * 
 * @author Patrick G. Durand
 * */
public interface BlastHitHSP {

  /**
   * Return a hit.
   * 
   * @return a hit
   * */
  public abstract SRHit getHit();

  /**
   * Return a hit ordering number within its SRIteration.
   * 
   * @return a hit ordering number
   * */
  public abstract int getHspNum();

  /**
   * Set a hit.
   * 
   * @param hit
   *          a hit
   * */
  public abstract void setHit(SRHit hit);

  /**
   * Set a hit ordering number within its SRIteration.
   * 
   * @param i
   *          a hit ordering number
   * */
  public abstract void setHspNum(int i);

  /**
   * Return query size.
   * 
   * @return query size
   * */
  public abstract int getQuerySize();

  /**
   * Set query size.
   * 
   * @param size
   *          query size
   * */
  public abstract void setQuerySize(int size);

  /**
   * Return Blast type.
   * 
   * @return one of SROutput.XXX values
   * */
  public abstract int getBlastType();

  /**
   * Set Blast type.
   * 
   * @param type one of SROutput.XXX values
   * */
  public abstract void setBlastType(int type);

  /**
   * Set Blast Client name.
   * 
   * @param bc Blast Client name
   * */
  public abstract void setBlastClient(String bc);

  /**
   * Return Blast Client name.
   * 
   * @return Blast Client name
   * */
  public abstract String getBlastCLient();

  /**
   * Return the query sequence type. This is one of the AA_SEQ or NUC_SEQ.
   */
  public abstract int getQuerySeqType();

  /**
   * Return the hit sequence type. This is one of the AA_SEQ or NUC_SEQ.
   */
  public abstract int getHitSeqType();

  /**
   * Figure out whether or not two BlastHitHSP are equal.
   */
  public abstract boolean equals(Object obj);

}