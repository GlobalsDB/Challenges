package com.xlab.glodocs.gui.frames;

import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;

import com.xlab.glodocs.api.journal.HistoryEvent;
import com.xlab.glodocs.api.journal.Journal;
import com.xlab.glodocs.api.utils.GlodocsException;
import com.xlab.glodocs.gui.Configuration;
import com.xlab.glodocs.gui.smartgrid.SortableTable;
import com.xlab.glodocs.gui.smartgrid.SortableTableModel;

public class JournalPanel extends JPanel {

	private SortableTableModel model;

	private String[] headerStr = { "ID", "Type", "Date", "UserId",
			"CollectionID", "DocID" };

	private int[] columnWidth = { 50, 100, 150, 100, 100, 100 };

	private SortableTableModel dm;

	/**
	 * Create the panel.
	 */
	@SuppressWarnings("serial")
	public JournalPanel() {
		MigLayout layout = new MigLayout("flowx", "[]",
				"[c, grow, fill]15[c,pref!]");

		try {
			setLayout(layout);
			ArrayList<HistoryEvent> temp = Journal.getHistory();

			dm = new SortableTableModel() {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public Class getColumnClass(int col) {
					switch (col) {
					case 0:
						return Integer.class;
					case 1:
						return Integer.class;
					case 2:
						return Date.class;
					case 3:
						return String.class;
					case 4:
						return String.class;
					case 5:
						return String.class;
					default:
						return Object.class;
					}
				}

				public boolean isCellEditable(int row, int col) {
					return false;
				}

				public void setValueAt(Object obj, int row, int col) {
					switch (col) {
					case 2:
						super.setValueAt(new Integer(obj.toString()), row, col);
						return;
					default:
						super.setValueAt(obj, row, col);
						return;
					}
				}
			};

			SortableTable panel = new SortableTable(headerStr, columnWidth, dm);
			final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
					panel.getModel());
			panel.getTable().setRowSorter(sorter);
			model = panel.getModel();
			add(panel, "wrap, grow");

			model.setDataVector(new Object[][] {}, headerStr);
			Vector<Object> vector;
			HistoryEvent event;

			for (int i = 0; i < temp.size(); ++i) {
				vector = new Vector<Object>();
				event = temp.get(i);
				vector.clear();
				vector.add(event.getId());
				vector.add(event.getOperationType());
				vector.add(event.getDate());
				vector.add(event.getUserId());
				vector.add(event.getCollectionId());
				vector.add(((event.getDocId() != null) ? event.getDocId() : ""));

				model.addRow(vector);
			}

		} catch (GlodocsException e) {
			e.printStackTrace();
		}
	}
}
