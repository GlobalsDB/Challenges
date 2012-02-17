package com.xlab.glodocs.gui.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.db.Collection;
import com.xlab.glodocs.api.db.Document;
import com.xlab.glodocs.api.security.ObjectAction;
import com.xlab.glodocs.api.security.Role;
import com.xlab.glodocs.api.security.User;
import com.xlab.glodocs.api.utils.GlodocsException;
import com.xlab.glodocs.gui.Configuration;

public class UserManagingPanel extends JPanel {

	private JComboBox comboBox;
	private JList rolesList;
	private JList colPermList;
	private JList docPermList;

	/**
	 * Create the frame.
	 */
	public UserManagingPanel() {
		MigLayout layout = new MigLayout(
				"flowx",
				"[]15[]5[]5[]",
				"[c,pref!]15[c,pref!]3[c,pref!]3[c,grow,fill]15[c,pref!]3[c,pref!]3[c,grow,fill]15[c,pref!]3[c,pref!]3[c,grow,fill]15[c,pref!]");

		setLayout(layout);
		JLabel labelUser = new JLabel(
				Configuration.localization.getString("user"));

		comboBox = new JComboBox();
		Object[] arr = DBConnection.getDatabase().getUsersList();
		if (arr != null)
			for (int i = 0; i < arr.length; ++i)
				comboBox.addItem(arr[i]);
		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					loadInfo(comboBox.getSelectedItem());
				} catch (GlodocsException e) {
					e.printStackTrace();
				}

			}
		});

		JLabel orLabel = new JLabel(Configuration.localization.getString("or"));
		JButton createButton = new JButton(
				Configuration.localization.getString("createUser"));

		add(labelUser);
		add(comboBox);
		add(orLabel);
		add(createButton, "wrap");

		JLabel roleTitle = new JLabel(
				Configuration.localization.getString("roles"));
		add(roleTitle, "wrap");

		rolesList = new JList(new DefaultComboBoxModel());

		JScrollPane rolesScroll = new JScrollPane(rolesList);
		add(rolesScroll, "wrap, span 4, growx");

		JButton addRole = new JButton(
				Configuration.localization.getString("addRole"));
		JButton deleteRole = new JButton(
				Configuration.localization.getString("deleteRole"));
		add(addRole);
		add(deleteRole, "wrap");

		JLabel colPermTitle = new JLabel(
				Configuration.localization.getString("collection_perm"));
		add(colPermTitle, "wrap");

		colPermList = new JList(new DefaultComboBoxModel());

		JScrollPane colScroll = new JScrollPane(colPermList);
		add(colScroll, "wrap, span 4, growx");

		JButton addCol = new JButton(
				Configuration.localization.getString("addCol"));
		JButton deleteCol = new JButton(
				Configuration.localization.getString("deleteCol"));
		add(addCol);
		add(deleteCol, "wrap");

		JLabel docTitle = new JLabel(
				Configuration.localization.getString("document_perm"));
		add(docTitle, "wrap");

		docPermList = new JList(new DefaultComboBoxModel());

		JScrollPane docScroll = new JScrollPane(docPermList);
		add(docScroll, "wrap, span 4, growx");

		JButton addDoc = new JButton(
				Configuration.localization.getString("addDoc"));
		JButton deleteDoc = new JButton(
				Configuration.localization.getString("deleteDoc"));
		add(addDoc);
		add(deleteDoc, "wrap");

		JButton save = new JButton(Configuration.localization.getString("ok"));
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

			}
		});

		add(save, "wrap");

	}

	protected void loadInfo(Object selectedItem) throws GlodocsException {
		User user = new User(selectedItem.toString());

		ArrayList<Role> roles = user.getRoles();
		for (int i = 0; i < roles.size(); ++i) {
			((DefaultComboBoxModel) rolesList.getModel()).addElement(roles.get(
					i).name());
		}

		HashMap<Collection, ObjectAction> collectionPermissions = user
				.getColectionsPermissions();
		Iterator<Collection> iter = collectionPermissions.keySet().iterator();
		Collection col = null;
		while (iter.hasNext()) {
			col = iter.next();
			((DefaultComboBoxModel) colPermList.getModel())
					.addElement(new Object[] { col.getId(),
							collectionPermissions.get(col).name() });
		}

		HashMap<Document, ObjectAction> documentPermissions = user
				.getDocumentsPermissions();
		Iterator<Document> iter2 = documentPermissions.keySet().iterator();
		Document doc = null;
		while (iter2.hasNext()) {
			doc = iter2.next();
			((DefaultComboBoxModel) docPermList.getModel())
					.addElement(new Object[] { doc.getId(),
							documentPermissions.get(doc).name() });
		}

	}
}
