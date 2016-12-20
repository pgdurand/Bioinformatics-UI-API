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

/**
 * This class is a BlastQuery.
 * 
 * @author Patrick G. Durand
 */
public abstract class QueryBase {

	public QueryBase() {}

	/**
	 * Return the path to the place containing the data of this query.
	 */
	public abstract String getQueryPath();

	/**
	 * Return the name of the Blast Job.
	 */
	public abstract String getJobName();

	/**
	 * Return the name of the databank.
	 **/
	public abstract String getDatabankName();

	/**
	 * Return the name of the search engine.
	 */
	public abstract String getEngineSysName();

}