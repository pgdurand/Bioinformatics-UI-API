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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bzh.plealog.bioinfo.ui.blast.resulttable.sort.DoubleEntity;
import bzh.plealog.bioinfo.ui.blast.resulttable.sort.Entity;
import bzh.plealog.bioinfo.ui.blast.resulttable.sort.IntegerEntity;
import bzh.plealog.bioinfo.ui.blast.resulttable.sort.SerialEntityBag;
import bzh.plealog.bioinfo.ui.blast.resulttable.sort.StringEntity;
import bzh.plealog.bioinfo.util.CoreUtil;

/**
 * 
 * Sorter class utility for JKTable associated to a JKTableModel.
 * 
 * @author Patrick G. Durand
 *
 * @param <T>
 */

public abstract class JKTableModelSorter<T> {

	private static final Log	LOGGER	= LogFactory.getLog("kb.JKTableModelSorter");

	protected static enum ENTITY_TYPE {
		tInteger, tDouble, tString
	};

	private String	filePrefix;
	private String	directoryPath;

	public JKTableModelSorter(String directoryPath, String filePrefix) {
		this.directoryPath = directoryPath;
		this.filePrefix = filePrefix;
	}

	protected abstract Object getValue(T item, int sortColumn);

	protected abstract ENTITY_TYPE getEntityType(int sortColumn);

	protected abstract List<Entity> getEntities(ProgressTinyDialog monitor, int sortColumn);

	protected abstract int getSize();

	protected boolean canSave() {
		return this.directoryPath!=null && new File(this.directoryPath).exists();
	}

	/**
	 * Returns the file used to serialize the list of Entities created during the sort operation.
	 */
	private String getSerializeFile(int sortColumn) {
		return FilenameUtils.concat(this.directoryPath, this.filePrefix + String.valueOf(sortColumn) + "_a.lst");
	}

	/**
	 * Serializes the list of Entities created during the sort operation.
	 */
	private SerialEntityBag serializeEntities(List<Entity> entities, int sortColumn, boolean save) {
		SerialEntityBag bag = null;

		bag = new SerialEntityBag(true, sortColumn, entities);

		if (save) {
			String f = getSerializeFile(sortColumn);
			OutputStream os = null;
			BufferedOutputStream buffer = null;
			ObjectOutputStream output = null;
			try {
				os = new FileOutputStream(f);
				buffer = new BufferedOutputStream(os);
				output = new ObjectOutputStream(buffer);
				output.writeObject(bag);
				output.flush();
			} catch (Exception ex) {
				LOGGER.warn("unable to serialize entities in: " + f + ": " + ex.toString());
			} finally {
				IOUtils.closeQuietly(os);
				IOUtils.closeQuietly(buffer);
				IOUtils.closeQuietly(output);
			}
		}
		return bag;
	}

	/**
	 * Deserializes the list of Entities created during the sort operation.
	 */
	private SerialEntityBag deSerializeEntities(ProgressTinyDialog monitor, int sortColumn) {
		InputStream is = null;
		String f;
		SerialEntityBag bag = null;
		InputStream buffer = null;
		ObjectInput input = null;

		f = getSerializeFile(sortColumn);
		if (new File(f).exists() == false) {
			return null;
		}
		if (monitor != null) {
			monitor.setMessage("Reloading sorted table data...");
		}
		try {
			is = new FileInputStream(f);
			buffer = new BufferedInputStream(is);
			input = new ObjectInputStream(buffer);
			bag = (SerialEntityBag) input.readObject();
		} catch (Exception e) {
			LOGGER.warn("unable to deserialize entities from: " + f + ": " + e.toString());
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(buffer);

			try {
				if (input != null) {
					input.close();
				}
			} catch (Exception e) {
			}
		}
		return bag;
	}

	/**
	 * Creates a new Entity given the associated item data block.
	 */
	protected Entity getEntity(T item, ENTITY_TYPE eType, int id, int sortColumn) {
		Entity entity;
		if (eType == ENTITY_TYPE.tDouble) {
			entity = new DoubleEntity(id, getDoubleValue(item, sortColumn));
		} else if (eType == ENTITY_TYPE.tInteger) {
			entity = new IntegerEntity(id, getIntegerValue(item, sortColumn));
		} else {
			entity = new StringEntity(id, getStringValue(item, sortColumn));
		}

		return entity;
	}

	/**
	 * Get a specific string value from a BFileSummary data block.
	 * 
	 * @param item
	 *        the item from where to retrieve the data value
	 * 
	 * @param sortColumn
	 *        id of the column that targets the value to retrieve. 
	 */
	private String getStringValue(T item, int sortColumn) {
		if (item != null) {
			Object value = getValue(item, sortColumn);
			if (value != null) {
				return value.toString();
			}
		}
		return "?";
	}

	/**
	 * Get a specific integer value from a BFileSummary data block.
	 * 
	 * @param summary
	 *        the item from where to retrieve the data value
	 * 
	 * @param sortColumn
	 *        id of the column that targets the value to retrieve. 
	 */
	private int getIntegerValue(T item, int sortColumn) {
		Object value;
		int iValue = 0;
		if (item != null) {
			value = getValue(item, sortColumn);
			if (value != null) {
				try {
					iValue = Integer.valueOf(value.toString());
				} catch (NumberFormatException ex) {
					iValue = -1;
				}
			} else {
				iValue = -1;
			}

		}
		return iValue;
	}

	/**
	 * Get a specific double value from a BFileSummary data block.
	 * 
	 * @param summary
	 *        the item from where to retrieve the data value
	 * 
	 * @param sortColumn
	 *        id of the column that targets the value to retrieve. 
	 */
	private double getDoubleValue(T item, int sortColumn) {
		Object value;
		double dValue = 0d;
		String str, str2;

		if (item != null) {
			value = getValue(item, sortColumn);
			if (value != null) {
				try {
					str = value.toString();
					//double value (see BFileSummary class) uses decimal formatter and current Locale
					//when dealing with French-based Locale, decimal separator is a comma: so replace it
					//this solution was chosen instead of using parse() methods from BFileSummary class
					//formatter. Indeed, that class uses several formatters, and here it is quite difficult
					//to figure out which one to use.
					str2 = CoreUtil.replaceFirst(str, ",", ".");// if "," not found, then method returns null
					if (str2 != null)
						dValue = Double.valueOf(str2);
					else
						dValue = Double.valueOf(str);
				} catch (NumberFormatException ex) {
					dValue = -1d;
				}
			} else {
				dValue = -1.0d;
			}

		}
		return dValue;
	}

	public boolean stillSorted(int column) {
		return new File(getSerializeFile(column)).exists();
	}

	/**
	 * Sort data.
	 * 
	 * @param sortColumn
	 *        id of the column on which relies the sort operation. This parameter must be one of the RES_XXX constants
	 *        defined in the ResultTableModel class.
	 * 
	 * @param force
	 *        figure out whether or not to force sort computing. So, even if the system has saved a sort result, it will
	 *        recompute it.
	 * 
	 * @return a list of sorted Entities
	 * */
	public SerialEntityBag sort(ProgressTinyDialog monitor, int sortColumn, boolean force) {
		List<Entity> entities;
		SerialEntityBag bag = null;
		ENTITY_TYPE eType = this.getEntityType(sortColumn);

		//check for an existing serialized list of Entites
		if (!force) {
			bag = deSerializeEntities(monitor, sortColumn);
			if (bag != null) {
				if (monitor != null) {
					monitor.dispose();
				}
				return bag;
			}
		}

		entities = this.getEntities(monitor, sortColumn);
		if (entities != null) {
			//Sort data, always in ascending order
			if (eType.equals(ENTITY_TYPE.tInteger)) {
				Collections.sort(entities, new IntegerEntityComparator(true));
			} else if (eType.equals(ENTITY_TYPE.tDouble)) {
				Collections.sort(entities, new DoubleEntityComparator(true));
			} else {
				Collections.sort(entities, new StringEntityComparator(true));
			}

			//since the above steps may be quite time consuming for huge job, we save the results
			bag = serializeEntities(entities, sortColumn, this.canSave());
		}
		if (monitor != null) {
			monitor.dispose();
		}
		return bag;
	}

	public class IntegerEntityComparator implements Comparator<Entity> {
		private boolean	ascending;

		public IntegerEntityComparator(boolean ascending) {
			this.ascending = ascending;
		}

		@Override
		public int compare(Entity arg0, Entity arg1) {
			if (((IntegerEntity) arg0).getValue() > ((IntegerEntity) arg1).getValue()) {
				return (ascending ? +1 : -1);
			} else if (((IntegerEntity) arg0).getValue() < ((IntegerEntity) arg1).getValue()) {
				return (ascending ? -1 : +1);
			} else {
				return 0;
			}
		}
	}

	public class DoubleEntityComparator implements Comparator<Entity> {
		private boolean	ascending;

		public DoubleEntityComparator(boolean ascending) {
			this.ascending = ascending;
		}

		@Override
		public int compare(Entity arg0, Entity arg1) {
			if (((DoubleEntity) arg0).getValue() > ((DoubleEntity) arg1).getValue()) {
				return (ascending ? +1 : -1);
			} else if (((DoubleEntity) arg0).getValue() < ((DoubleEntity) arg1).getValue()) {
				return (ascending ? -1 : +1);
			} else {
				return 0;
			}
		}
	}

	public class StringEntityComparator implements Comparator<Entity> {
		private boolean	ascending;

		public StringEntityComparator(boolean ascending) {
			this.ascending = ascending;
		}

		@Override
		public int compare(Entity arg0, Entity arg1) {
			int value = ((StringEntity) arg0).getValue().compareTo(((StringEntity) arg1).getValue());
			return (ascending ? value : -value);
		}
	}

}
