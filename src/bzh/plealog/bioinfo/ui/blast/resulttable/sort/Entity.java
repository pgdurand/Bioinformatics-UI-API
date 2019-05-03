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
package bzh.plealog.bioinfo.ui.blast.resulttable.sort;

/**
 * Utility interface for the ResultTableModelSorter framework.
 */
public interface Entity {
	/**
	 * Returns the absolute index position of a BFileSummary within the QueryBase Data Storage. That
	 * index position is used an an ID to uniquely identify the position of a BFileSummary within a reordered
	 * list of BFileSummary.
	 */
	public int getId();
}