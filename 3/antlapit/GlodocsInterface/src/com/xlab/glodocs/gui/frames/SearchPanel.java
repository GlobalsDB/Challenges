package com.xlab.glodocs.gui.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import net.miginfocom.swing.MigLayout;

import org.json.JSONException;

import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.db.Collection;
import com.xlab.glodocs.api.utils.GlodocsException;
import com.xlab.glodocs.gui.Configuration;
import com.xlab.glodocs.gui.Window.Winda;

public class SearchPanel extends JPanel {

	private JComboBox comboBox;
	private final DinamicFilterConditionPanel filterCondition;
	private JButton search;
	private JList docsList;
	private ArrayList<String> documents;

	/**
	 * Create the panel.
	 */
	public SearchPanel() {
		MigLayout layout = new MigLayout("flowx", "[]15[]5[]5[]",
				"[c,pref!]15[c, pref!]15[c,pref!]15[c, grow 90, fill]15[c,pref!]");

		setLayout(layout);
		JLabel collectTitle = new JLabel(
				Configuration.localization.getString("collection"));
		add(collectTitle);

		comboBox = new JComboBox();
		Object[] arr = Winda.getSingleton().getCollectList();
		for (int i = 0; i < arr.length; ++i)
			comboBox.addItem(arr[i]);
		comboBox.setSelectedItem(null);
		add(comboBox, "wrap");
		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (comboBox.getSelectedItem() != null)
					search.setEnabled(true);
				updateFilter(comboBox.getSelectedItem());
			}
		});

		filterCondition = new DinamicFilterConditionPanel();
		add(filterCondition, "span 4, grow, wrap");

		search = new JButton("Search");
		search.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					((DefaultComboBoxModel) docsList.getModel())
							.removeAllElements();
					documents = filterCondition.executeFilter();
					for (int i = 0; i < documents.size(); ++i) {
						((DefaultComboBoxModel) docsList.getModel())
								.addElement(documents.get(i));
					}
				} catch (GlodocsException e) {
					JFrame f = new JFrame();
					JOptionPane.showMessageDialog(f, e.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}

			}
		});
		search.setEnabled(false);

		add(search, "wrap");

		docsList = new JList(new DefaultComboBoxModel());
		docsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int index = docsList.locationToIndex(e.getPoint());
					ListModel dlm = docsList.getModel();
					Object item = dlm.getElementAt(index);
					docsList.ensureIndexIsVisible(index);
					Collection collection;
					try {
						collection = DBConnection.getDatabase()
								.getCollectionById(
										comboBox.getSelectedItem().toString());
						DocumentPanel pan = new DocumentPanel(collection
								.getDocumentById(item.toString()));
						Winda.addTab(pan, item.toString());
					} catch (JSONException e1) {
						JFrame f = new JFrame();
						JOptionPane.showMessageDialog(f, e1.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					} catch (ParseException e1) {
						JFrame f = new JFrame();
						JOptionPane.showMessageDialog(f, e1.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					} catch (GlodocsException e1) {
						JFrame f = new JFrame();
						JOptionPane.showMessageDialog(f, e1.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}

				}
			}
		});
		JScrollPane docsScroll = new JScrollPane(docsList);
		add(docsScroll, "span2, grow, wrap");
	}

	protected void updateFilter(Object selectedItem) {
		try {
			filterCondition.updateFilter(selectedItem);
		} catch (JSONException e) {
			JFrame f = new JFrame();
			JOptionPane.showMessageDialog(f, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (ParseException e) {
			JFrame f = new JFrame();
			JOptionPane.showMessageDialog(f, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		} catch (GlodocsException e) {
			JFrame f = new JFrame();
			JOptionPane.showMessageDialog(f, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}

	}

}
