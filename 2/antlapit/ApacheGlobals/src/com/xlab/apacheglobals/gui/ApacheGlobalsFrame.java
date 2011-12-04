package com.xlab.apacheglobals.gui;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;

import com.toedter.calendar.JDateChooser;
import com.xlab.apacheglobals.utils.Configuration;
import com.xlab.apacheglobals.utils.LogRecord;
import com.xlab.apacheglobals.utils.Parser;
import com.xlab.apacheglobals.utils.ParserThread;
import com.xlab.apacheglobals.utils.SearchEngine;

@SuppressWarnings("serial")
public class ApacheGlobalsFrame extends JFrame {

	private JPanel contentPane;
	private JToggleButton refreshAutoButton;
	private JButton refreshButton;
	private JLabel modeLabel;
	private JLabel dataLabel;
	private JLabel fileLabel;
	private final ApacheGlobalsFrame self;
	private boolean valid = true;
	private final SortableTableModel model;
	private String file;
	private Timer timer;
	private JTextField filterText;
	private JTextField filterTextTo;
	private JDateChooser filterText2;
	private JDateChooser filterTextTo2;
	private JTextField filterText3;
	private JTextField filterTextTo3;
	private JTextField filterText4;
	private JTextField filterTextTo4;

	private String[] headerStr = { "ID", "IP", "Date", "Method", "Result",
			"Size", "Request" };

	/**
	 * Launch the application.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApacheGlobalsFrame frame = new ApacheGlobalsFrame();
					frame.setVisible(true);

					Configuration.loadConfig();
					Parser.load();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ApacheGlobalsFrame() {
		self = this;
		setTitle("ApacheGlobals");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 750);

		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		refreshButton = new JButton("");
		refreshButton.setIcon(new ImageIcon("images/refresh.png"));
		refreshButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});

		toolBar.add(refreshButton);

		refreshAutoButton = new JToggleButton("");
		refreshAutoButton.setIcon(new ImageIcon("images/autorefresh.png"));
		refreshAutoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (refreshAutoButton.isSelected()) {
					timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							if (!valid)
								refresh();
						}
					}, 0, 5000);
				}
			}
		});

		toolBar.add(refreshAutoButton);

		contentPane = new JPanel();

		MigLayout layout = new MigLayout(
				"flowx",
				"[]15[]1[]1[]",
				"[c, grow]15[c,pref!]5[c,pref!]5[c,pref!]5[c,pref!]15[c,pref!]5[c,pref!]5[c,pref!]5[c,pref!]15[c,pref!]5[c,pref!]5[c,pref!]5[c,pref!]5[c,pref!]5[c,pref!]5[c,pref!]5[c,grow,fill]");

		contentPane.setLayout(layout);

		SortableTable panel = new SortableTable();

		contentPane.add(toolBar, "span 4, wrap, growx");

		contentPane.add(new JLabel("Status"), "span 4, wrap, grow");
		contentPane.add(new JLabel("Current Mode:"));
		modeLabel = new JLabel("Static");
		contentPane.add(modeLabel);
		contentPane.add(new JLabel("Data is valid:"));
		dataLabel = new JLabel("Yes");
		contentPane.add(dataLabel, "wrap");
		contentPane.add(new JLabel("Current File:"));
		fileLabel = new JLabel(Configuration.logFileName);
		contentPane.add(fileLabel, "wrap");

		contentPane.add(new JSeparator(), "span 4, wrap, grow");
		contentPane.add(new JLabel("Mode"), "span 4, wrap, grow");

		JButton fileChooser = new JButton("...");
		fileChooser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Apache Access Log", "log");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(self);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile().getAbsoluteFile()
							.toString();
				}

			}
		});

		contentPane.add(new JLabel("Choose log file"));
		contentPane.add(fileChooser);

		contentPane.add(new JLabel("Choose Mode:"));
		JPanel panel1 = new JPanel(new GridLayout(0, 1));
		ButtonGroup group = new ButtonGroup();

		final JRadioButton abstract1 = new JRadioButton("Static");
		abstract1.setSelected(true);
		panel1.add(abstract1);
		group.add(abstract1);

		final JRadioButton abstract2 = new JRadioButton("Dynamic");
		panel1.add(abstract2);
		group.add(abstract2);
		contentPane.add(panel1, "wrap");

		JButton loadButton = new JButton("Load file");
		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Configuration.logFileName = file;
				fileLabel.setText(file);
				try {
					Configuration.saveConfig();
					ParserThread pt = new ParserThread(!abstract1.isSelected());
					pt.run();
					setDataValid(false);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(loadButton, "wrap");

		contentPane.add(new JSeparator(), "span 4, wrap, grow");
		contentPane.add(new JLabel("Filters"), "span 4, wrap, grow");

		JLabel labelSpacer = new JLabel("---");
		JLabel labelSpacer2 = new JLabel("---");
		JLabel labelSpacer3 = new JLabel("---");
		JLabel labelSpacer4 = new JLabel("---");

		final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				panel.getModel());
		panel.getTable().setRowSorter(sorter);
		model = panel.getModel();
		JLabel label = new JLabel("IP" + ":");
		filterText = new JTextField("");
		filterTextTo = new JTextField("");
		contentPane.add(label);
		contentPane.add(filterText, "w 100::");
		contentPane.add(labelSpacer);
		contentPane.add(filterTextTo, "wrap, w 100::");

		JLabel label2 = new JLabel("Date" + ":");
		filterText2 = new JDateChooser();
		filterTextTo2 = new JDateChooser();
		contentPane.add(label2);
		contentPane.add(filterText2, "w 100::");
		contentPane.add(labelSpacer2);
		contentPane.add(filterTextTo2, "wrap, w 100::");

		JLabel label3 = new JLabel("Method" + ":");
		filterText3 = new JTextField("");
		filterTextTo3 = new JTextField("");
		contentPane.add(label3);
		contentPane.add(filterText3, "w 100::");
		contentPane.add(labelSpacer3);
		contentPane.add(filterTextTo3, "wrap, w 100::");

		JLabel label4 = new JLabel("Result" + ":");
		filterText4 = new JTextField("");
		filterTextTo4 = new JTextField("");
		contentPane.add(label4);
		contentPane.add(filterText4, "w 100::");
		contentPane.add(labelSpacer4);
		contentPane.add(filterTextTo4, "wrap, w 100::");

		JButton button = new JButton("Filter");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filter();
			}
		});
		contentPane.add(button, "span 4, grow, wrap");

		contentPane.add(panel, "span 4, grow, wrap");

		setContentPane(contentPane);
	}

	protected void filter() {
		HashMap<String, String> min = new HashMap<String, String>();
		HashMap<String, String> max = new HashMap<String, String>();

		Calendar cal = Calendar.getInstance();
		if (filterText2.getDate() != null) {
			cal.setTime(filterText2.getDate());
			min.put("year", cal.get(Calendar.YEAR) + "");
			min.put("month", cal.get(Calendar.MONTH) + "");
			min.put("day", cal.get(Calendar.DAY_OF_MONTH) + "");
		} else {
			min.put("year", "");
			min.put("month", "");
			min.put("day", "");
		}

		min.put("ip", filterText.getText());
		min.put("method", filterText3.getText());
		min.put("response", filterText4.getText());

		if (filterTextTo2.getDate() != null) {
			cal.setTime(filterTextTo2.getDate());
			max.put("year", cal.get(Calendar.YEAR) + "");
			max.put("month", cal.get(Calendar.MONTH) + "");
			max.put("day", cal.get(Calendar.DAY_OF_MONTH) + "");
		} else {
			max.put("year", "");
			max.put("month", "");
			max.put("day", "");
		}

		max.put("ip", filterTextTo.getText());
		max.put("method", filterTextTo3.getText());
		max.put("response", filterTextTo4.getText());

		try {

			for (int i = 0; i < model.getRowCount(); ++i)
				model.removeRow(0);

			model.setDataVector(new Object[][] {}, headerStr);

			ArrayList<Integer> ids = SearchEngine
					.getRangeByManyFields(min, max);
			LogRecord temp;
			Vector<Object> vector;
			if (ids.size() == 0) {
				int recordsCount = Parser.recordImported;
				for (int i = 1; i < recordsCount; ++i) {
					vector = new Vector<Object>();
					temp = new LogRecord(i);
					vector.clear();
					vector.add(temp.getId());
					vector.add(temp.getIp());
					vector.add(temp.getParsedDate());
					vector.add(temp.getMethod());
					vector.add(temp.getResponse());
					vector.add(temp.getBytessend());
					vector.add(temp.getRequest());
					model.addRow(vector);
				}
			} else {
				for (int i = 0; i < ids.size(); ++i) {
					vector = new Vector<Object>();
					temp = new LogRecord(ids.get(i));
					vector.clear();
					vector.add(temp.getId());
					vector.add(temp.getIp());
					vector.add(temp.getParsedDate());
					vector.add(temp.getMethod());
					vector.add(temp.getResponse());
					vector.add(temp.getBytessend());
					vector.add(temp.getRequest());
					model.addRow(vector);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void refresh() {
		/*
		 * for (int i = 0; i < model.getRowCount(); ++i) model.removeRow(0);
		 * 
		 * model.setDataVector(new Object[][] {}, headerStr);
		 * 
		 * int recordsCount = Parser.recordImported; LogRecord temp;
		 * Vector<Object> vector; for (int i = 1; i < recordsCount; ++i) {
		 * vector = new Vector<Object>(); temp = new LogRecord(i);
		 * vector.clear(); vector.add(temp.getId()); vector.add(temp.getIp());
		 * vector.add(temp.getParsedDate()); vector.add(temp.getMethod());
		 * vector.add(temp.getResponse()); vector.add(temp.getBytessend());
		 * vector.add(temp.getRequest()); model.addRow(vector); }
		 */

		filter();
		setDataValid(true);
	}

	public void setDataValid(boolean b) {
		valid = b;
		dataLabel.setText(valid ? "Yes" : "No");
	}
}
