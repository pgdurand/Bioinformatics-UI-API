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

/**
 * Utility class for the ResultTableModelSorter framework.
 */
public class IntegerEntity implements Entity, Serializable{

	private static final long serialVersionUID = -4072272211469368866L;

	//id the absolute index position of a BFileSummary within the QueryBase Data Storage
	private int id;
	//this is the value used for the sorting position
	private int value;
	
	public IntegerEntity(){}
	
	public IntegerEntity(int id, int value) {
		super();
		this.id = id;
		this.value = value;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String toString(){
		return String.valueOf(value);
	}
}