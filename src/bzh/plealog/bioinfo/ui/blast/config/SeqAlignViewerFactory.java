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
package bzh.plealog.bioinfo.ui.blast.config;

import bzh.plealog.bioinfo.ui.blast.saviewer.SeqAlignViewer;

/**
 * Factory to use to create instances of SeqAlignViewer.
 * 
 * @author Patrick G. Durand
 */
public interface SeqAlignViewerFactory {

  /**
   * Create a new instance of a SeqAlignViewer.
   * 
   * @param id unique identifier of the table. If only one viewer is in use in
   * a software then this value can be set to null.
   */
  public SeqAlignViewer createViewer();
}
