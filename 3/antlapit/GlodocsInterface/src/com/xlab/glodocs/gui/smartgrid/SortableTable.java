package com.xlab.glodocs.gui.smartgrid;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.xlab.glodocs.gui.Configuration;

/**
 * 
 */
@SuppressWarnings("serial")
public class SortableTable extends JPanel {
	private JTable table;
	private SortableTableModel dm;

	public SortableTable(String[] headerStr, int[] columnWidth,
			SortableTableModel dm) {
		setLayout(new BorderLayout());
		this.dm = dm;

		this.dm.setDataVector(new Object[][] {}, headerStr);

		table = new JTable(this.dm);
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
			DateFormat.SHORT, Configuration.currentLocale);

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
