/* Copyright (C) 2006-2019 Patrick G. Durand
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
package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import org.apache.log4j.BasicConfigurator;

import bzh.plealog.bioinfo.api.core.config.CoreSystemConfigurator;
import bzh.plealog.bioinfo.api.data.searchjob.QueryBase;
import bzh.plealog.bioinfo.api.data.searchresult.SROutput;
import bzh.plealog.bioinfo.api.data.searchresult.io.SRLoader;
import bzh.plealog.bioinfo.data.searchjob.InMemoryQuery;
import bzh.plealog.bioinfo.data.searchresult.SRUtils;
import bzh.plealog.bioinfo.io.searchresult.SerializerSystemFactory;
import bzh.plealog.bioinfo.ui.blast.core.QueryBaseUI;
import bzh.plealog.bioinfo.ui.blast.resulttable.SummaryTable;
import bzh.plealog.bioinfo.ui.blast.resulttable.SummaryTableModel;
import bzh.plealog.bioinfo.ui.config.UISystemConfigurator;
import bzh.plealog.bioinfo.ui.util.TableColumnManager;

/**
 * This class shows how to start the Blast Result Summary Viewer panel.
 * 
 * @author Patrick G. Durand
 */
public class BlastResultTableViewerTest {

  /**
   * Prepare a viewer.
   * 
   * @param blastQuery the query to display
   */
	private static JComponent prepareViewer(QueryBase blastQuery) {
		JPanel pnl;
		SummaryTableModel resultTableModel;
		SummaryTable resultTable;
		JScrollPane scrollPaneRT;
		TableColumnManager tcm;
		QueryBaseUI qBaseUI;
		
		pnl = new JPanel(new BorderLayout());

		qBaseUI = new QueryBaseUI(blastQuery);
		// Result Table
		resultTableModel = new SummaryTableModel();
		resultTableModel.setQuery(qBaseUI);
		resultTable = new SummaryTable(resultTableModel);
		resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		resultTable.getTableHeader().setReorderingAllowed(false);
		resultTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		resultTable.setColumnSelectionAllowed(false);
		resultTable.setRowSelectionAllowed(true);
		resultTable.setGridColor(Color.LIGHT_GRAY);

		// Top Scroll Pane
		scrollPaneRT = new JScrollPane(resultTable);
		scrollPaneRT.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneRT.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		tcm = new TableColumnManager(resultTable, resultTableModel.getReferenceColumnHeaders());
		scrollPaneRT.setCorner(JScrollPane.UPPER_RIGHT_CORNER, tcm.getInvoker());

		pnl.add(scrollPaneRT, BorderLayout.CENTER);

		return pnl;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			//init logger for standalone application
			BasicConfigurator.configure();

	    // Required to use Plealog Bioinformatics Core objects such as BLAST results
	    CoreSystemConfigurator.initializeSystem();

	    // Required to use the Plealog Bioinformatics UI library
	    UISystemConfigurator.initializeSystem();

			//Load data file and make a Model
			//File blastFile = new File("data/blastp.xml");
	    File blastFile = new File("data/blastp-71queries-swissprot.xml");
			SRLoader ncbiBlastLoader = SerializerSystemFactory.getLoaderInstance(
			    SerializerSystemFactory.NCBI_LOADER);
			SROutput bo = ncbiBlastLoader.load(blastFile);
			
			assert bo != null;
			
			//Prepare a View from the Model
			InMemoryQuery query;
			query = new InMemoryQuery();
			List<SROutput> results = SRUtils.splitMultiResult(bo);
			for(SROutput sro : results) {
			  query.addResult(sro);
			}
			// following is done manually, but real data can be retrieved 
			// from bo object (not shown here)
			query.setDatabankName("SwissProt");
			query.setEngineSysName("blastp");
			query.setJobName(blastFile.getName());
      // a Blast result loaded from a file is always OK
      query.setStatus(QueryBase.OK);
			// query not provided in blastFile
			query.setQueyPath("n/a");
			// not appropriate here
			query.setRID("n/a");
			
			//Setup the Controller for the View
			JFrame frame = new JFrame();
			frame.setTitle("Blast Result Viewer");
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.getContentPane().add(prepareViewer(query));
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			System.err.println("Error: " + e);
		}
	}

}
