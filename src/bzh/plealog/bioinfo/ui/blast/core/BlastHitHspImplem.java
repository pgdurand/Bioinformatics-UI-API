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
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;

/**
 * This is the default implementation of BlastHitHSP interface.
 * 
 * @author Patrick G. Durand
 */
public class BlastHitHspImplem implements BlastHitHSP {
  private SRHit _hit;
  private String _blastClient;
  private int _hspNum;
  private int _querySize;
  private int _blastType;

  /**
   * Creates a BlastHitHsp.
   * 
   * @param hit
   *          the hit containing the HSP to display
   * @param bc
   *          the BlastClient that produced this BlastHitHsp
   * @param hspNum
   *          the HSP to display, identified by its order number within the Hit
   * @param qSize
   *          the query size
   * @param bType
   *          the Blast type, one of SROutput.BLASTXXX integer constants
   */
  public BlastHitHspImplem(SRHit hit, String bc, int hspNum, int qSize, int bType) {
    _hit = hit;
    _blastClient = bc;
    _hspNum = hspNum;
    _querySize = qSize;
    _blastType = bType;
  }

  @Override
  public SRHit getHit() {
    return _hit;
  }

  @Override
  public int getHspNum() {
    return _hspNum;
  }

  @Override
  public void setHit(SRHit hit) {
    _hit = hit;
  }

  @Override
  public void setHspNum(int i) {
    _hspNum = i;
  }

  @Override
  public int getQuerySize() {
    return _querySize;
  }

  @Override
  public void setQuerySize(int i) {
    _querySize = i;
  }

  @Override
  public int getBlastType() {
    return _blastType;
  }

  @Override
  public void setBlastType(int i) {
    _blastType = i;
  }

  @Override
  public void setBlastClient(String bc) {
    _blastClient = bc;
  }

  @Override
  public String getBlastCLient() {
    return _blastClient;
  }

  @Override
  public int getQuerySeqType() {
    if (_blastType == SROutput.BLASTP || _blastType == SROutput.SCANPS || _blastType == SROutput.PSIBLAST
        || _blastType == SROutput.TBLASTN)
      return SROutput.AA_SEQ;
    else
      return SROutput.NUC_SEQ;
  }

  @Override
  public int getHitSeqType() {
    if (_blastType == SROutput.BLASTP || _blastType == SROutput.SCANPS || _blastType == SROutput.PSIBLAST
        || _blastType == SROutput.BLASTX)
      return SROutput.AA_SEQ;
    else
      return SROutput.NUC_SEQ;
  }

  @Override
  public boolean equals(Object obj) {
    BlastHitHSP src;
    if (obj instanceof BlastHitHspImplem == false)
      return false;
    src = (BlastHitHSP) obj;
    return (src.getHit() == this._hit && src.getHspNum() == this._hspNum);
  }
}
