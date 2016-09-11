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
package bzh.plealog.bioinfo.ui.hca;

import bzh.plealog.bioinfo.api.data.sequence.DRulerModel;
import bzh.plealog.bioinfo.api.data.sequence.DSequence;

/**
 * An adapted sequence that can be used to display a DSequence
 * using the PanelHca control.
 * 
 * @author Patrick G. Durand
 */
public class DDBioSeq extends BioSeq{

  public DDBioSeq(DSequence sequence)
  {
    int         len = sequence.size();
    int         nlet = 0;
    int         j = 0;
    char        c;
    DRulerModel rModel;
    for(int i = 0; i < len; i++)
    {
      c = sequence.getSymbol(i).getChar();
      if(c >= 'A' && c <= 'Z')
        nlet++;
    }

    bioseq = new AminoAcid[nlet];
    rModel = sequence.getRulerModel();
    for(int i = 0; i < len; i++)
    {
      c = sequence.getSymbol(i).getChar();
      if(c >= 'A' && c <= 'Z')
      {
        bioseq[j] = new AminoAcid(c);
        if (rModel!=null){
          bioseq[j].setSeqPos(rModel.getSeqPos(i));
        }
        j++;
      }
    }

    length = nlet;
  }

}
