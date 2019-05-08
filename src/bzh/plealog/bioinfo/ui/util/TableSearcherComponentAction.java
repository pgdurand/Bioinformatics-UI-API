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
package bzh.plealog.bioinfo.ui.util;

import javax.swing.Action;

public interface TableSearcherComponentAction extends Action {
	/**
	 * Sets the search text. This method is used to transfer the search text from the UI
	 * to the class implementing this interface.
	 */
	public void setSearchText(String text);
	
	/**
	 * Sets the caller of this method. This method is used to transfer the caller to the class
	 * implementing this interface; the caller is the UI containing the search system.
	 */
	public void setTableSearcherComponentActionGateway(TableSearcherComponentAPI caller);
}
