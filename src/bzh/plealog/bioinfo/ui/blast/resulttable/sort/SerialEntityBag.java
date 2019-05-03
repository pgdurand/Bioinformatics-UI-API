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

import java.io.Serializable;
import java.util.List;

/**
 * Utility class for the ResultTableModelSorter framework.
 */
public class SerialEntityBag implements Serializable{
	private static final long serialVersionUID = -6231973115632303472L;

	private boolean      ascending;
	private int          sortColumn;
	private List<Entity> entities;
	
	public SerialEntityBag(){}
	
	public SerialEntityBag(boolean ascending, int sortColumn,
			List<Entity> entities) {
		super();
		this.ascending = ascending;
		this.sortColumn = sortColumn;
		this.entities = entities;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public int getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(int sortColumn) {
		this.sortColumn = sortColumn;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
	public int size(){
		return entities.size();
	}
	public Entity getEntity(int idx, boolean ascending){
		if (ascending){
			return entities.get(idx);
		}
		else{
			return entities.get(entities.size()-1-idx);
		}
	}
	
}
