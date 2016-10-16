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
package bzh.plealog.bioinfo.ui.util;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.AbstractAction;
import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.apache.commons.io.IOUtils;

import com.plealog.genericapp.api.EZEnvironment;
import com.plealog.genericapp.api.file.EZFileFilter;
import com.plealog.genericapp.api.file.EZFileManager;
import com.plealog.genericapp.api.log.EZLogger;

/**
 * This class handles the action allowing a user to save to a CSV file
 * the content of a JTable.
 * 
 * @author Patrick G. Durand
 * @since 2006
 * */
public class SaveTableToCSVFileAction extends AbstractAction {
  private static final long serialVersionUID = -2591669837855917953L;
  private JTable				_resultTable;
	private boolean				_force;

	/**
	 * Action constructor.
	 * 
	 * @param name the name of the action.
	 */
	public SaveTableToCSVFileAction(String name) {
		super(name);
	}

	/**
	 * Action constructor.
	 * 
	 * @param name the name of the action.
	 * @param icon the icon of the action.
	 */
	public SaveTableToCSVFileAction(String name, Icon icon) {
		super(name, icon);
	}

	/**
	 * Passes in the table containing the sequences associated to a query.
	 */
	public void setResultTable(JTable qt) {
		_resultTable = qt;
	}

	/**
	 * Passes true to force saving all the table rows whatever the selection is.
	 */
	public void forceToSaveAll(boolean force) {
		_force = force;
	}

	public void actionPerformed(ActionEvent event) {
		FileOutputStream fos = null;
		File f;
		CSVExport exporter;

		f = chooseFile();

		if (f == null)
			return;
		exporter = new CSVExport();
		try {
			fos = new FileOutputStream(f);
			ListSelectionModel selModel;
			selModel = _resultTable.getSelectionModel();
			if (selModel instanceof DefaultListSelectionModel) {
				selModel = (DefaultListSelectionModel) ((DefaultListSelectionModel) selModel).clone();
			}
			exporter.export(fos, _resultTable.getModel(), _force ? null : selModel);
			fos.flush();
			fos.close();
		} catch (Exception ex) {
			String msg = "Unable to export data";
			EZLogger.warn(msg + ": " + ex);
			EZEnvironment.displayWarnMessage(EZEnvironment.getParentFrame(), msg + ".");
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	/**
	 * Allows the user to choose a file name.
	 */
	private File chooseFile() {
		return EZFileManager.chooseFileForSaveAction(
		    EZEnvironment.getParentFrame(), 
		    "Save Table (selected rows)",
		    new EZFileFilter(".csv", "Comma separated values"));

	}

}
