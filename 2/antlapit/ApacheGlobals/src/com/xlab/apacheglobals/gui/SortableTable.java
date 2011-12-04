package com.xlab.apacheglobals.gui;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * @version 1.0 02/25/99
 */
@SuppressWarnings("serial")
public class SortableTable extends JPanel {
	private JTable table;
	private SortableTableModel dm;

	public SortableTable() {
		setLayout(new BorderLayout());
		String[] headerStr = { "ID", "IP", "Date", "Method", "Result", "Size",
				"Request" };
		int[] columnWidth = { 50, 100, 150, 100, 100, 100, 300 };

		dm = new SortableTableModel() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int col) {
				switch (col) {
				case 0:
					return Integer.class;
				case 1:
					return String.class;
				case 2:
					return Date.class;
				case 3:
					return String.class;
				case 4:
					return Integer.class;
				case 5:
					return Integer.class;
				case 6:
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
		dm.setDataVector(new Object[][] {}, headerStr);

		table = new JTable(dm);
		// table.setShowGrid(false);
		table.setShowVerticalLines(true);
		table.setShowHorizontalLines(false);
		SortButtonRenderer renderer = new SortButtonRenderer();
		TableColumnModel model = table.getColumnModel();
		int n = headerStr.length;
		for (int i = 0; i < n; i++) {
			model.getColumn(i).setHeaderRenderer(renderer);
			model.getColumn(i).setPreferredWidth(columnWidth[i]);
		}

		JTableHeader header = table.getTableHeader();
		header.addMouseListener(new HeaderListener(header, renderer));
		JScrollPane pane = new JScrollPane(table);
		add(pane, BorderLayout.CENTER);
	}

	public JTable getTable() {
		return table;
	}

	public SortableTableModel getModel() {
		return dm;
	}

	private static DateFormat dateFormat = DateFormat.getDateInstance(
			DateFormat.SHORT, Locale.US);

	@SuppressWarnings("unused")
	private static Date getDate(String dateString) {
		Date date = null;
		try {
			date = dateFormat.parse(dateString);
		} catch (ParseException ex) {
			date = new Date();
		}
		return date;
	}

}
