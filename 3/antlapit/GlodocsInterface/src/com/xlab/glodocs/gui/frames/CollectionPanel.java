package com.xlab.glodocs.gui.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.db.Collection;
import com.xlab.glodocs.api.db.Scheme;
import com.xlab.glodocs.gui.Configuration;
import com.xlab.glodocs.gui.Window.Winda;

@SuppressWarnings("serial")
public class CollectionPanel extends JPanel {
	private JTextField textField;
	private JButton addField;
	private JButton addIndex;
	private JButton deleteField;
	private JButton deleteIndex;
	private JList fieldList;
	private JList indexList;
	private JButton ok;
	private JButton cancel;
	private boolean isNew = true;
	private Collection collection;

	/**
	 * Create the panel.
	 */
	public CollectionPanel() {
		// setLayout(new GridLayout(0, 1, 0, 0));
		MigLayout layout = new MigLayout(
				"flowx",
				"[]15[]",
				"[c,pref!]30[c,pref!]15[c,grow 50,fill]15[c,pref!]30[c,pref!]15[c,grow 50,fill]15[c,pref!]30[c,pref!]");

		setLayout(layout);

		// JPanel panel = new JPanel();
		// add(panel);

		JLabel collectionTitle = new JLabel(
				Configuration.localization.getString("collectionName"));

		add(collectionTitle);

		textField = new JTextField();
		textField.setColumns(10);

		add(textField, "wrap");
		// panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		// panel.add(collectionTitle);
		// panel.add(textField);

		// JPanel panel_2 = new JPanel();
		// add(panel_2);
		/*
		 * panel_2.setBorder(new TitledBorder( null,
		 * "\u041E\u0431\u044F\u0437\u0430\u0442\u0435\u043B\u044C\u043D\u044B\u0435 \u043F\u043E\u043B\u044F"
		 * , TitledBorder.LEADING, TitledBorder.TOP, null, null));
		 */
		// panel_2.setLayout(new GridLayout(0, 1, 0, 0));

		add(new JLabel(Configuration.localization.getString("requiredFields")),
				"wrap");

		JScrollPane scrollPane = new JScrollPane();
		// panel_2.add(scrollPane);

		fieldList = new JList(new DefaultComboBoxModel());
		fieldList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (fieldList.getSelectedValue() == null)
					deleteField.setEnabled(false);
				else
					deleteField.setEnabled(true);
			}
		});

		scrollPane.setViewportView(fieldList);

		add(scrollPane, "span 2, wrap, growx");

		// JPanel panel_5 = new JPanel();
		// panel_2.add(panel_5);

		addField = new JButton(Configuration.localization.getString("add"));
		addField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				Object result = JOptionPane.showInputDialog(frame,
						"Enter field:");

				((DefaultComboBoxModel) fieldList.getModel())
						.addElement(result);
			}
		});
		// panel_5.add(addField);
		add(addField);

		deleteField = new JButton(
				Configuration.localization.getString("delete"));
		deleteField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fieldList.getSelectedValue() != null) {
					DefaultComboBoxModel model = (DefaultComboBoxModel) fieldList
							.getModel();
					model.removeElement(fieldList.getSelectedValue());
				}
			}
		});
		deleteField.setEnabled(false);
		// panel_5.add(deleteField);
		add(deleteField, "wrap");

		// JPanel panel_3 = new JPanel();
		// add(panel_3);
		/*
		 * panel_3.setBorder(new TitledBorder(null,
		 * "\u0418\u043D\u0434\u0435\u043A\u0441\u044B", TitledBorder.LEADING,
		 * TitledBorder.TOP, null, null)); panel_3.setLayout(new GridLayout(0,
		 * 1, 0, 0));
		 */

		add(new JLabel(Configuration.localization.getString("indexes")), "wrap");

		JScrollPane scrollPane_1 = new JScrollPane();
		// panel_3.add(scrollPane_1);

		indexList = new JList(new DefaultComboBoxModel());
		indexList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (indexList.getSelectedValue() == null)
					deleteIndex.setEnabled(false);
				else
					deleteIndex.setEnabled(true);
			}
		});
		scrollPane_1.setViewportView(indexList);

		add(scrollPane_1, "span 2, wrap, growx");

		addIndex = new JButton(Configuration.localization.getString("add"));
		addIndex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				int size = ((DefaultComboBoxModel) fieldList.getModel())
						.getSize();
				if (size == 0) {
					JOptionPane
							.showMessageDialog(frame,
									Configuration.localization
											.getString("noindexedfields"),
									Configuration.localization
											.getString("error_title"),
									JOptionPane.ERROR_MESSAGE);
					return;
				}

				int size2 = ((DefaultComboBoxModel) indexList.getModel())
						.getSize();

				ArrayList<Object> list2 = new ArrayList<Object>();

				for (int i = 0; i < size2; ++i) {
					list2.add(indexList.getModel().getElementAt(i));
				}

				if (size <= size2) {
					JOptionPane
							.showMessageDialog(frame,
									Configuration.localization
											.getString("noindexedfields"),
									Configuration.localization
											.getString("error_title"),
									JOptionPane.ERROR_MESSAGE);
					return;
				}

				ArrayList<Object> arr = new ArrayList<Object>();
				for (int i = 0; i < size; ++i) {
					if (!list2.contains(fieldList.getModel().getElementAt(i)))
						arr.add(fieldList.getModel().getElementAt(i));
				}
				Object result = JOptionPane.showInputDialog(frame,
						Configuration.localization.getString("enter_index")
								+ " :",
						Configuration.localization.getString("enter_index"),
						JOptionPane.PLAIN_MESSAGE, null, arr.toArray(),
						arr.get(0));

				((DefaultComboBoxModel) indexList.getModel())
						.addElement(result);
			}
		});
		add(addIndex);

		deleteIndex = new JButton(
				Configuration.localization.getString("delete"));
		deleteIndex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (indexList.getSelectedValue() != null) {
					DefaultComboBoxModel model = (DefaultComboBoxModel) indexList
							.getModel();
					model.removeElement(indexList.getSelectedValue());
				}
			}
		});
		deleteIndex.setEnabled(false);
		add(deleteIndex, "wrap");

		// JPanel panel_4 = new JPanel();
		// add(panel_4);

		ok = new JButton(Configuration.localization.getString("ok"));
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					JSONObject obj = new JSONObject();
					JSONArray indexes = new JSONArray();
					JSONArray fields = new JSONArray();

					DefaultComboBoxModel model = (DefaultComboBoxModel) indexList
							.getModel();

					for (int i = 0; i < model.getSize(); ++i) {
						indexes.put(model.getElementAt(i));
					}

					model = (DefaultComboBoxModel) fieldList.getModel();

					for (int i = 0; i < model.getSize(); ++i) {
						fields.put(model.getElementAt(i));
					}

					obj.put("indexes", indexes);
					obj.put("fields", fields);
					if (isNew || collection == null)
						DBConnection.getDatabase().createCollection(
								textField.getText(),
								DBConnection.getLoggedUser().getLogin(),
								new Scheme(obj));
					else {
						collection.editScheme(new Scheme(obj));
					}

				} catch (Exception e) {
					/*
					 * JOptionPane.showMessageDialog(CollectionPanel.this,
					 * e.getMessage(),
					 * Configuration.localization.getString("error_title"),
					 * JOptionPane.ERROR_MESSAGE);
					 */
					// TODO ошибку показывать
				}
				Winda.getSingleton().loadData();
			}
		});
		add(ok);

		cancel = new JButton(Configuration.localization.getString("cancel"));
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = Winda.indexVisible;
				System.out.println("i=" + i);
				if (i != -1) {
					Winda.tabbedPane.remove(i);
				}
			}
		});
		add(cancel, "wrap");

	}

	public void setSource(Collection collection) {
		this.collection = collection;
		textField.setText(collection.getId());
		textField.setEnabled(false);

		Scheme scheme = collection.getScheme();
		ArrayList<String> fields = scheme.getFields();
		ArrayList<String> indexes = scheme.getIndexes();
		addField.setEnabled(false);

		DefaultComboBoxModel model = (DefaultComboBoxModel) fieldList
				.getModel();
		model.removeAllElements();
		for (int i = 0; i < fields.size(); ++i) {
			model.addElement(fields.get(i));
		}

		model = (DefaultComboBoxModel) indexList.getModel();
		model.removeAllElements();
		for (int i = 0; i < indexes.size(); ++i) {
			model.addElement(indexes.get(i));
		}

		isNew = false;

	}
}
