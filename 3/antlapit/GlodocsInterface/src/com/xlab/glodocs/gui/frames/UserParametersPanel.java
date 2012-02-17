package com.xlab.glodocs.gui.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.db.Collection;
import com.xlab.glodocs.api.db.Document;
import com.xlab.glodocs.api.security.ObjectAction;
import com.xlab.glodocs.api.security.Role;
import com.xlab.glodocs.api.utils.GlodocsException;
import com.xlab.glodocs.gui.Configuration;

@SuppressWarnings("serial")
public class UserParametersPanel extends JPanel {

	JList rolesList;
	JList colPermList;
	JList docPermList;

	@SuppressWarnings("static-access")
	public UserParametersPanel() {
		MigLayout layout = new MigLayout(
				"flowx",
				"[]15[]",
				"[c,pref!]30[c,pref!]30[c,pref!]3[c,grow,fill]30[c,pref!]3[c,grow,fill]30[c,pref!]3[c,grow,fill]30[c,pref!]");

		setLayout(layout);

		try {
			JLabel label1 = new JLabel(
					Configuration.localization.getString("login"));
			JTextField textField = new JTextField(DBConnection.getInstance()
					.getLoggedUser().getLogin());
			textField.setEditable(false);

			add(label1);
			add(textField, "wrap");

			JLabel label2 = new JLabel(
					Configuration.localization.getString("password"));
			final JPasswordField passwordField = new JPasswordField(
					"                  ");

			add(label2);
			add(passwordField, "wrap");

			JLabel roleTitle = new JLabel(
					Configuration.localization.getString("roles"));
			add(roleTitle, "wrap");

			rolesList = new JList(new DefaultComboBoxModel());
			ArrayList<Role> roles = DBConnection.getLoggedUser().getRoles();
			for (int i = 0; i < roles.size(); ++i) {
				((DefaultComboBoxModel) rolesList.getModel()).addElement(roles
						.get(i).name());
			}

			JScrollPane rolesScroll = new JScrollPane(rolesList);
			add(rolesScroll, "wrap, span 2, growx");

			JLabel colPermTitle = new JLabel(
					Configuration.localization.getString("collection_perm"));
			add(colPermTitle, "wrap");

			colPermList = new JList(new DefaultComboBoxModel());
			HashMap<Collection, ObjectAction> collectionPermissions = DBConnection
					.getLoggedUser().getColectionsPermissions();
			Iterator<Collection> iter = collectionPermissions.keySet()
					.iterator();
			Collection col = null;
			while (iter.hasNext()) {
				col = iter.next();
				((DefaultComboBoxModel) colPermList.getModel())
						.addElement(new Object[] { col.getId(),
								collectionPermissions.get(col).name() });
			}

			JScrollPane colScroll = new JScrollPane(colPermList);
			add(colScroll, "wrap, span 2, growx");

			JLabel docTitle = new JLabel(
					Configuration.localization.getString("document_perm"));
			add(docTitle, "wrap");

			docPermList = new JList(new DefaultComboBoxModel());
			HashMap<Document, ObjectAction> documentPermissions = DBConnection
					.getLoggedUser().getDocumentsPermissions();
			Iterator<Document> iter2 = documentPermissions.keySet().iterator();
			Document doc = null;
			while (iter2.hasNext()) {
				doc = iter2.next();
				((DefaultComboBoxModel) docPermList.getModel())
						.addElement(new Object[] { doc.getId(),
								documentPermissions.get(doc).name() });
			}

			JScrollPane docScroll = new JScrollPane(docPermList);
			add(docScroll, "wrap, span 2, growx");

			JButton save = new JButton(
					Configuration.localization.getString("ok"));
			save.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					try {
						DBConnection.getLoggedUser().changePassword(
								passwordField.getText());
					} catch (GlodocsException e) {
						e.printStackTrace();
					}

				}
			});

			add(save, "wrap");

		} catch (GlodocsException e) {
			e.printStackTrace();
		}

	}
}
