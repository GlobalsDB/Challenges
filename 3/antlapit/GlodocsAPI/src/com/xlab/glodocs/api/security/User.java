package com.xlab.glodocs.api.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.db.Collection;
import com.xlab.glodocs.api.db.Document;
import com.xlab.glodocs.api.utils.Constants;
import com.xlab.glodocs.api.utils.GlodocsException;

/**
 * User of Glodocs database
 * 
 * @author Lapitskiy Anton
 * 
 */
public class User {

	/**
	 * User login
	 */
	private String login;

	/**
	 * User password
	 */
	private String password;

	/**
	 * System roles list
	 */
	protected ArrayList<Role> rolesList = new ArrayList<Role>();

	/**
	 * Object permissions list
	 */
	private HashMap<Document, ObjectAction> objectPrivilegesDocuments = new HashMap<Document, ObjectAction>();
	private HashMap<Collection, ObjectAction> objectPrivilegesCollections = new HashMap<Collection, ObjectAction>();

	private static DBConnection connection;
	private static NodeReference global;
	private static ValueList valueList;

	/**
	 * Creating new user
	 * 
	 * @param login
	 *            User's login
	 * @param password
	 *            User's password
	 * @throws NoSuchAlgorithmException
	 * @throws GlodocsException
	 */
	public User(String login, String password) throws GlodocsException {
		try {
			if (DBConnection.getLoggedUser() == null) {
				// installation
				this.login = login;
				this.password = password;
				connection = DBConnection.getInstance();
				global = connection.createNodeReference(Constants.GLOBAL_USERS);
				global.appendSubscript(login);
				valueList = connection.createList();

				MessageDigest md5 = MessageDigest.getInstance("MD5");
				md5.update(password.getBytes());
				// valueList.append(new String(md5.digest()));
				valueList.append(password);
				global.set(valueList);

			} else {
				if (AccessManager.checkPermissions(
						DBConnection.getLoggedUser(), Role.MANAGE_USERS)) {
					this.login = login;
					this.password = password;
					connection = DBConnection.getInstance();
					global = connection
							.createNodeReference(Constants.GLOBAL_USERS);
					global.appendSubscript(login);
					valueList = connection.createList();

					MessageDigest md5 = MessageDigest.getInstance("MD5");
					md5.update(password.getBytes());
					// valueList.append(new String(md5.digest()));
					valueList.append(password);
					global.set(valueList);

				}

			}
			global = connection.createNodeReference(Constants.GLOBAL_USERS);
			if (global.getList() == null)
				valueList = connection.createList();
			else
				valueList = global.getList();
			valueList.append(login);
			global.set(valueList);

		} catch (NoSuchAlgorithmException e) {
			throw new GlodocsException(e.getMessage());
		}
	}

	/**
	 * Getting existing user
	 * 
	 * @param username
	 * @throws GlodocsException
	 */
	public User(String username, String password, boolean plug)
			throws GlodocsException {
		this.login = username;
		this.password = password;

		loadParams();
	}

	/**
	 * Getting existing user for editing
	 * 
	 * @param username
	 * @throws GlodocsException
	 */
	public User(String username) throws GlodocsException {
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(),
				Role.MANAGE_USERS)) {
			this.login = username;
			loadParams();
		} else
			throw new GlodocsException(
					"Access Denied :: You are not granted MANAGE_USER role");
	}

	private void loadParams() throws GlodocsException {
		connection = DBConnection.getInstance();
		global = connection.createNodeReference(Constants.GLOBAL_USERS);
		global.appendSubscript(login);
		global.appendSubscript(Constants.USER_ROLES);
		valueList = global.getList();
		for (int i = 0; i < valueList.length(); ++i) {
			rolesList.add(Role.values()[valueList.getNextInt()]);
		}

		global = connection.createNodeReference(Constants.GLOBAL_USERS);
		global.appendSubscript(login);
		global.appendSubscript(Constants.USER_OBJECT_PRIV_COL);
		valueList = global.getList();
		for (int i = 0; i < valueList.length() / 2; ++i) {
			objectPrivilegesCollections.put(
					new Collection(valueList.getNextString()),
					ObjectAction.values()[valueList.getNextInt()]);
		}

		global = connection.createNodeReference(Constants.GLOBAL_USERS);
		global.appendSubscript(login);
		global.appendSubscript(Constants.USER_OBJECT_PRIV_DOC);
		valueList = global.getList();
		for (int i = 0; i < valueList.length() / 3; ++i) {
			objectPrivilegesDocuments.put(
					new Document(new Collection(valueList.getNextString()),
							valueList.getNextString()),
					ObjectAction.values()[valueList.getNextInt()]);
		}

	}

	public String getLogin() {
		return login;
	}

	/**
	 * Add permission to document to current user
	 * 
	 * @param document
	 *            Document to add permissions
	 * @param action
	 *            New object permission
	 * @throws GlodocsException
	 */
	public void addDocumentAction(Document document, ObjectAction action)
			throws GlodocsException {
		objectPrivilegesDocuments.put(document, action);
		save();
	}

	public void addCollectionAction(Collection collection, ObjectAction action)
			throws GlodocsException {
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(),
				Role.MANAGE_ACTIONS, collection)) {
			objectPrivilegesCollections.put(collection, action);
			save();
		}
	}

	public void addRole(Role role) throws GlodocsException {
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(),
				Role.MANAGE_ROLES)) {
			rolesList.add(role);
			save();
		}
	}

	/**
	 * Changing password of current user
	 * 
	 * @param oldPassword
	 *            Old password
	 * @param newPassword
	 *            New password
	 * @throws GlodocsException
	 */
	public void changePassword(String newPassword) throws GlodocsException {
		password = newPassword;
		save();
	}

	/**
	 * Saving new user data to database
	 * 
	 * @throws GlodocsException
	 */
	public void save() throws GlodocsException {
		connection = DBConnection.getInstance();
		global = connection.createNodeReference(Constants.GLOBAL_USERS);
		global.appendSubscript(login);

		valueList = connection.createList();
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(password.getBytes());
			// valueList.append(new String(md5.digest()));
			valueList.append(password);
			global.set(valueList);
		} catch (NoSuchAlgorithmException e) {
			throw new GlodocsException(e.getMessage());
		}

		global.appendSubscript(Constants.USER_ROLES);

		valueList = connection.createList();
		for (int i = 0; i < rolesList.size(); ++i) {
			valueList.append(rolesList.get(i).getIntegerCode());
		}

		global = connection.createNodeReference(Constants.GLOBAL_USERS);
		global.appendSubscript(login);
		global.appendSubscript(Constants.USER_OBJECT_PRIV_DOC);
		valueList = connection.createList();
		Iterator<Document> iterator = objectPrivilegesDocuments.keySet()
				.iterator();
		Document d = null;
		while (iterator.hasNext()) {
			d = iterator.next();
			valueList.append(d.getCollection().getId(), d.getId(),
					objectPrivilegesDocuments.get(d));
		}

		global = connection.createNodeReference(Constants.GLOBAL_USERS);
		global.appendSubscript(login);
		global.appendSubscript(Constants.USER_OBJECT_PRIV_COL);
		valueList = connection.createList();
		Iterator<Collection> iterator2 = objectPrivilegesCollections.keySet()
				.iterator();
		Collection c = null;
		while (iterator.hasNext()) {
			c = iterator2.next();
			valueList.append(c.getId(), objectPrivilegesCollections.get(c));
		}
	}

	/**
	 * Logging in
	 * 
	 * @param username
	 *            Login
	 * @param password
	 *            Password
	 * @return User instance if login information is correct, <b>null</b>
	 *         otherwise
	 * @throws GlodocsException
	 */
	public static User login(String username, String password)
			throws GlodocsException {
		connection = DBConnection.getInstance();
		// global.setSubscriptCount(0);
		global = connection.createNodeReference(Constants.GLOBAL_USERS);
		global.appendSubscript(username);

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(password.getBytes());
			System.out.println(username + " " + password);
			valueList = global.getList();
			if (valueList == null || valueList.length() == 0)
				throw new GlodocsException("User doesn't exist");
			String prePass = valueList.getNextString();
			System.out.println(prePass);

			// if (!prePass.equals(new String(md5.digest())))
			if (!prePass.equals(password))
				throw new GlodocsException("Wrong password");
			return new User(username, password, true);
		} catch (NoSuchAlgorithmException e) {
			throw new GlodocsException("Internal problems with password");

		}

	}

	public boolean has(Document document, ObjectAction action) {
		return objectPrivilegesDocuments.containsKey(document) ? (objectPrivilegesDocuments
				.get(document).equals(action)) : false;

	}

	public boolean has(Collection collection, ObjectAction action) {
		return objectPrivilegesCollections.containsKey(collection) ? (objectPrivilegesCollections
				.get(collection).equals(action)) : false;

	}

	public boolean has(Role role) {
		return rolesList.contains(role);
	}

	public ObjectAction getAction(Document document) {
		return objectPrivilegesDocuments.get(document);
	}

	public ObjectAction getAction(Collection collection) {
		return objectPrivilegesCollections.get(collection);
	}

	public ArrayList<Role> getRoles() {
		return rolesList;
	}

	public HashMap<Collection, ObjectAction> getColectionsPermissions() {
		return objectPrivilegesCollections;
	}

	public HashMap<Document, ObjectAction> getDocumentsPermissions() {
		return objectPrivilegesDocuments;
	}
}
