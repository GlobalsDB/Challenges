package com.xlab.glodocs.api.db;

import java.text.ParseException;
import java.util.HashMap;

import org.json.JSONException;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.journal.HistoryEvent;
import com.xlab.glodocs.api.journal.JournalConstants;
import com.xlab.glodocs.api.security.AccessManager;
import com.xlab.glodocs.api.security.ObjectAction;
import com.xlab.glodocs.api.security.Role;
import com.xlab.glodocs.api.utils.Constants;
import com.xlab.glodocs.api.utils.GlodocsException;

/**
 * Class represents Database singleton Can be usev to work with collections
 * 
 * @author Lapitskiy Anton
 * 
 */
public class Database {
	/**
	 * Collections in database
	 */
	private HashMap<String, Collection> databaseCollections = new HashMap<String, Collection>();
	private NodeReference global;
	private ValueList valueList;
	private DBConnection connection;
	private boolean loadedCollections = false;

	public Database(DBConnection globalsDBConnection) {
		connection = globalsDBConnection;
		global = connection.createNodeReference();
		valueList = connection.createList();
		preload();
		// loadCollections();
	}

	/**
	 * Performing loading operations in Database
	 * 
	 * @see com.xlab.glodocs.api.db.Database#loadCollections()
	 */
	private void preload() {
		databaseCollections.clear();
		loadedCollections = false;
		global = connection.createNodeReference(Constants.GLOBAL_SYSTEM);
		global.setSubscriptCount(0);
		global.appendSubscript(Constants.GLOBAL_COLLECTIONLIST);
		valueList = global.getList();
		if (valueList == null)
			valueList = connection.createList();
		loadCollections();
	}

	/**
	 * Loading collections to map <br />
	 * Access managing is implemented in Collection constructor
	 */
	private void loadCollections() {
		for (int i = 0; i < valueList.length(); i++) {
			String key = valueList.getNextString();
			try {
				databaseCollections.put(key, new Collection(key));
			} catch (GlodocsException e) {
				System.out.println(e.getMessage());
			}
		}

		loadedCollections = true;

	}

	/**
	 * Adding new collection to database.<br />
	 * <b>Collection should be created before using this method</b>
	 * 
	 * @param Collection
	 *            Collection to add
	 * @throws GlodocsException
	 */
	public void addCollection(Collection collection) throws GlodocsException {
		global = connection.createNodeReference(Constants.GLOBAL_SYSTEM);
		global.setSubscriptCount(0);
		global.appendSubscript(Constants.GLOBAL_COLLECTIONLIST);
		valueList = global.getList();
		if (valueList == null) {
			valueList = connection.createList();
		}
		valueList.append(collection.getId());
		global.set(valueList);
		preload();
	}

	/**
	 * Getting collection by String id <br />
	 * Performing initialization of current Collection
	 * 
	 * @see com.xlab.glodocs.api.db.Collection#loadParameters()
	 * @param id
	 *            Collection's id to get
	 * @return Required collection
	 * @throws JSONException
	 * @throws ParseException
	 * @throws GlodocsException
	 */
	public Collection getCollectionById(String id) throws JSONException,
			ParseException, GlodocsException {
		if (!loadedCollections)
			loadCollections();
		final Collection out = databaseCollections.get(id);
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(), out,
				ObjectAction.READ)) {
			out.loadParameters();
			return out;
		} else {
			throw new GlodocsException(
					"Access Denied :: you have no read action permitted for collection");
		}
	}

	/**
	 * Deleting Collection by String id Refreshing Database state
	 * 
	 * @see Database#preload()
	 * @param id
	 *            Collection's id to remove
	 * @throws GlodocsException
	 */
	public void removeCollectionById(String id) throws GlodocsException {
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(),
				Role.DROP_COLLECTION, databaseCollections.get(id))) {
			databaseCollections.get(id).clear();
			global = DBConnection.getInstance().createNodeReference(id);
			global.killNode();
			global = connection.createNodeReference(Constants.GLOBAL_SYSTEM);
			global.setSubscriptCount(0);
			global.appendSubscript(Constants.GLOBAL_COLLECTIONLIST);

			valueList = global.getList();
			if (valueList != null) {
				valueList = connection.createList();
				databaseCollections.remove(id);
				Object[] arr = databaseCollections.keySet().toArray();
				for (int i = 0; i < arr.length; ++i) {
					valueList.append(arr[i].toString());
				}
				global.set(valueList);
			}

			preload();
			HistoryEvent.addEvent(DBConnection.getLoggedUser().getLogin(),
					JournalConstants.REMOVE_COLLECTION, id);
		} else {
			throw new GlodocsException(
					"Access Denied :: Can not drop collection");
		}
	}

	/**
	 * Loading collections
	 * 
	 * @return
	 */
	public HashMap<String, Collection> getCollections() {
		if (!loadedCollections)
			loadCollections();
		return databaseCollections;
	}

	/**
	 * Refreshing collections state in Database
	 * 
	 * @return refreshed collection's map
	 * @see Database#getCollections()
	 */
	public HashMap<String, Collection> refreshCollections() {
		loadedCollections = false;
		return getCollections();
	}

	/**
	 * Creating new Collection in Database from existing Scheme
	 * 
	 * @see Scheme
	 * @see Collection#Collection(String, String, Scheme)
	 * 
	 * @param key
	 *            Collection's key
	 * @param owner
	 *            Owner of new collection
	 * @param scheme
	 *            Colelction's schema
	 * @return new created collection
	 * @throws GlodocsException
	 */
	public Collection createCollection(String key, String owner, Scheme scheme)
			throws GlodocsException {
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(),
				Role.CREATE_COLLECTION)) {
			Collection collection = new Collection(key, owner, scheme);
			addCollection(collection);
			HistoryEvent.addEvent(DBConnection.getLoggedUser().getLogin(),
					JournalConstants.ADD_COLLECTION, key);
			return collection;
		} else {
			throw new GlodocsException(
					"Access denied :: you are not granted CREATE_COLLECTION role");
		}
	}

	public Object[] getUsersList() {
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(),
				Role.MANAGE_USERS)) {
			global = connection.createNodeReference(Constants.GLOBAL_USERS);
			valueList = global.getList();
			if (valueList == null)
				return null;
			Object[] out = new Object[valueList.length()];
			for (int i = 0; i < out.length; ++i) {
				out[i] = valueList.getNextString();
			}
			return out;
		}
		return null;
	}
}
