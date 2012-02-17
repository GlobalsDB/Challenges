package com.xlab.glodocs.gui.frames;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.json.JSONException;

import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.db.Collection;
import com.xlab.glodocs.api.db.Index;
import com.xlab.glodocs.api.utils.GlodocsException;

public class DinamicFilterConditionPanel extends JPanel {
	private ArrayList<String> indexes;
	private Collection collection;
	private MigLayout layout;
	private JTextField[] left, right;
	private HashMap<String, String> leftMap = new HashMap<String, String>();
	private HashMap<String, String> rightMap = new HashMap<String, String>();

	public void updateFilter(Object selectedItem) throws JSONException,
			ParseException, GlodocsException {
		removeAll();
		collection = DBConnection.getDatabase().getCollectionById(
				selectedItem.toString());
		indexes = collection.getScheme().getIndexes();
		if (indexes.size() == 0)
			throw new GlodocsException("No indexes found");
		String vertical = "";
		for (int i = 0; i < indexes.size() - 1; ++i) {
			vertical += "[c,pref!]15";
		}
		vertical += "[c,pref!]";

		layout = new MigLayout("flowx", "[]15[]15[]15[]", vertical);
		setLayout(layout);

		left = new JTextField[indexes.size()];
		right = new JTextField[indexes.size()];

		for (int i = 0; i < indexes.size(); ++i) {
			add(new JLabel(indexes.get(i)));
			left[i] = new JTextField(30);
			add(left[i]);
			right[i] = new JTextField(30);
			add(new JLabel("---"));
			add(right[i], "wrap");
		}
	}

	public ArrayList<String> executeFilter() throws GlodocsException {
		leftMap.clear();
		rightMap.clear();

		for (int i = 0; i < indexes.size(); ++i) {
			{
				leftMap.put(indexes.get(i), left[i].getText());
				rightMap.put(indexes.get(i), right[i].getText());
			}
		}

		Index index = new Index(DBConnection.getInstance(), collection);

		ArrayList<String> out = index.rangeManyQuery(leftMap, rightMap);
		return out;
	}
}
