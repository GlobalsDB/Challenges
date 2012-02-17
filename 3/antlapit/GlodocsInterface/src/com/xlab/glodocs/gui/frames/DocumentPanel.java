package com.xlab.glodocs.gui.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.json.JSONException;
import org.json.JSONObject;

import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.db.Collection;
import com.xlab.glodocs.api.db.Document;
import com.xlab.glodocs.api.utils.GlodocsException;
import com.xlab.glodocs.gui.Configuration;
import com.xlab.glodocs.gui.Window.Winda;

public class DocumentPanel extends JPanel {
	private JTextField textField;
	private DocumentAttributesFrame tree;
	private JComboBox comboBox;
	private Collection parent;
	private JButton ok;
	private JButton cancel;
	private final DocumentPanel self;

	/**
	 * Create the panel.
	 */
	public DocumentPanel() {
		MigLayout layout = new MigLayout("flowx", "[]15[]",
				"[c,pref!]30[c,pref!]30[c,grow,fill]30[c,pref!]");

		setLayout(layout);

		JLabel collectTitle = new JLabel(
				Configuration.localization.getString("collection"));
		add(collectTitle);

		comboBox = new JComboBox();
		Object[] arr = Winda.getSingleton().getCollectList();
		for (int i = 0; i < arr.length; ++i)
			comboBox.addItem(arr[i]);
		add(comboBox, "wrap");

		JLabel docTitle = new JLabel(Configuration.localization.getString("documentName"));
		add(docTitle);

		textField = new JTextField();
		add(textField, "wrap");
		textField.setColumns(10);

		tree = new DocumentAttributesFrame();
		// TODO
		tree.init("");
		try {
			JSONObject currentObject = new JSONObject("{}");
			tree.drawJSONObjectSubtree(currentObject, "");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		tree.drawSpoiler();
		tree.removeLastSpoiler();

		add(tree, "span 2, growx, wrap");

		ok = new JButton(Configuration.localization.getString("ok"));
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (parent == null)
					try {
						parent = DBConnection.getDatabase().getCollectionById(
								comboBox.getSelectedItem().toString());
					} catch (JSONException e) {
						e.printStackTrace();
						return;
					} catch (ParseException e) {
						e.printStackTrace();
						return;
					} catch (GlodocsException e) {
						e.printStackTrace();
						return;
					}

				if (parent == null)
					return;
				try {
					// JSONSave

					parent.addDocument(new Document(parent,
							textField.getText(), tree
									.getUpdatedJSONObjectsCollection()));

					Winda.getSingleton().loadDocuments(parent);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (GlodocsException e) {
					JFrame f = new JFrame();
					JOptionPane.showMessageDialog(f, e.getMessage(),
							Configuration.localization.getString("error_title"),
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}

			}
		});
		add(ok);

		cancel = new JButton(Configuration.localization.getString("cancel"));
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int i = Winda.indexVisible;
				System.out.println("i=" + i);
				if (i != -1) {
					Winda.tabbedPane.remove(i);
				}
			}
		});
		add(cancel);
		self = this;
	}

	public DocumentPanel(final Document document) {
		MigLayout layout = new MigLayout("flowx", "[]15[]",
				"[c,pref!]30[c,pref!]15[c,grow,fill]30[c,pref!]");

		setLayout(layout);

		JLabel collectTitle = new JLabel(Configuration.localization.getString("collection"));
		add(collectTitle);

		comboBox = new JComboBox();
		Object[] arr = Winda.getSingleton().getCollectList();
		for (int i = 0; i < arr.length; ++i)
			comboBox.addItem(arr[i]);
		add(comboBox, "wrap");

		JLabel docTitle = new JLabel(Configuration.localization.getString("documentName"));
		add(docTitle);

		textField = new JTextField();
		add(textField, "wrap");
		textField.setColumns(10);

		tree = new DocumentAttributesFrame();
		tree.init(document.getId());

		try {
			JSONObject currentObject = document.getJSONValue();
			tree.drawJSONObjectSubtree(currentObject, document.getId());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		tree.drawSpoiler();
		tree.removeLastSpoiler();

		add(tree, "span 2, growx, wrap");

		ok = new JButton(Configuration.localization.getString("ok"));
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (parent == null)
					try {
						parent = DBConnection.getDatabase().getCollectionById(
								comboBox.getSelectedItem().toString());
					} catch (JSONException e) {
						e.printStackTrace();
						return;
					} catch (ParseException e) {
						e.printStackTrace();
						return;
					} catch (NullPointerException e) {
						JFrame f = new JFrame();
						JOptionPane.showMessageDialog(f, e.getMessage(),
								Configuration.localization.getString("error_title"),
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					} catch (GlodocsException e) {
						JFrame f = new JFrame();
						JOptionPane.showMessageDialog(f, e.getMessage(),
								Configuration.localization.getString("error_title"),
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				if (parent == null)
					return;
				try {

					document.editValue(tree.getUpdatedJSONObjectsCollection());

					Winda.getSingleton().loadDocuments(parent);
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (GlodocsException e) {
					JFrame f = new JFrame();
					JOptionPane.showMessageDialog(f, e.getMessage(),
							Configuration.localization.getString("error_title"),
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});
		add(ok);

		cancel = new JButton(Configuration.localization.getString("cancel"));
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int i = Winda.indexVisible;
				System.out.println("i=" + i);
				if (i != -1) {
					Winda.tabbedPane.remove(i);
				}
			}
		});
		add(cancel);

		comboBox.setEnabled(false);
		((DefaultComboBoxModel) comboBox.getModel()).setSelectedItem(document
				.getCollection().getId());
		textField.setText(document.getId());
		textField.setEnabled(false);
		parent = document.getCollection();
		self = this;
	}

	public void setCollection(String id) {
		comboBox.setEnabled(false);
		((DefaultComboBoxModel) comboBox.getModel()).setSelectedItem(id);
		try {
			parent = DBConnection.getDatabase().getCollectionById(id);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (GlodocsException e) {
			e.printStackTrace();
		}
	}

}
