package com.xlab.glodocs.api.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

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
 * Class represents single Collection in Database Can be used to work with
 * Documents in Collection
 * 
 * @author Lapitskiy Anton
 * 
 */
public class Collection {
	/**
	 * String identifier
	 */
	private String id;

	/**
	 * Schema of the Collection<br />
	 * Containg metadata of internal structure of the Collection
	 * 
	 * @see Scheme
	 * 
	 */
	private Scheme scheme;

	/**
	 * Index managing object
	 * 
	 * @see Index
	 */
	private Index index;
	private String owner;

	public void setOwner(String owner) throws GlodocsException {
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(),
				Role.MANAGE_USERS)) {
			this.owner = owner;
			saveParameters();
		} else {
			throw new GlodocsException("Access Denied :: Can not set owner");
		}
	}

	public String getOwner() {
		return owner;
	}

	private Date creationDate;
	private int documentsCount;

	/**
	 * Document's list
	 */
	private HashMap<String, Document> documents = new HashMap<String, Document>();
	private NodeReference global;
	private ValueList valueList;
	private final DBConnection connection;
	private boolean loaded = false;

	/**
	 * Public constructor that creates new Collection in Database, but doesn't
	 * modify data in Database object
	 * 
	 * @see Database#createCollection(String, String, Scheme)
	 * @param key
	 *            Collection's name
	 * @param owner
	 *            Username
	 * @param scheme2
	 *            Collection's scheme
	 * @throws GlodocsException
	 */
	public Collection(String key, String owner, Scheme scheme2)
			throws GlodocsException {

		this.connection = DBConnection.getInstance();

		global = connection.createNodeReference(key);
		global.setSubscriptCount(0);

		if (global.getList() != null)
			throw new GlodocsException("Collection exists");
		valueList = connection.createList();

		this.id = key;
		this.scheme = scheme2;

		this.owner = owner;
		this.creationDate = Calendar.getInstance().getTime();
		this.documentsCount = 0;

		valueList.append(owner);
		creationDate = Calendar.getInstance().getTime();
		valueList.append((new SimpleDateFormat(Constants.DATE_FORMAT))
				.format(creationDate));
		valueList.append(documentsCount);
		valueList.append(scheme2.toString());
		global.set(valueList);

		global = connection.createNodeReference(Constants.GLOBAL_SYSTEM);
		global.setSubscriptCount(0);
		global.appendSubscript(Constants.GLOBAL_COLLECTIONS + "_" + id);
		valueList = connection.createList();
		global.set(valueList);

		index = new Index(connection, this);

	}

	/**
	 * Method saves edited Scheme in current Collection
	 * 
	 * @param scheme2
	 *            new Collection's scheme
	 * @throws GlodocsException
	 */
	public void editScheme(Scheme scheme2) throws GlodocsException {
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(), this,
				ObjectAction.WRITE)) {
			ArrayList<String> indexes = scheme.getIndexes();

			ArrayList<String> indexes2 = scheme2.getIndexes();

			for (int i = 0; i < indexes.size(); ++i) {
				if (!indexes2.contains(indexes.get(i)))
					removeIndexById(indexes.get(i));
			}

			for (int i = 0; i < indexes2.size(); ++i) {
				if (!indexes.contains(indexes2.get(i)))
					addIndex(indexes2.get(i));
			}

			this.scheme = scheme2;
			saveParameters();
			HistoryEvent.addEvent(DBConnection.getLoggedUser().getLogin(),
					JournalConstants.MODIFY_RECORD, id);
		} else
			throw new GlodocsException(
					"Access Denied :: Can not edit collection");

	}

	/**
	 * Constructor gets existing Collection from Database by
	 * {@link Collection#id}
	 * 
	 * @param key
	 *            Collection's name
	 * @throws GlodocsException
	 */
	public Collection(String key) throws GlodocsException {
		this.id = key;
		this.connection = DBConnection.getInstance();
		if (!AccessManager.checkPermissions(DBConnection.getLoggedUser(), this,
				ObjectAction.READ))
			throw new GlodocsException(
					"Access Denied :: Can not read collection");
		index = new Index(connection, this);
		// preload();
	}

	/**
	 * Performing initialization of current Collection
	 * 
	 * @see Collection#loadParameters()
	 * @throws GlodocsException
	 */
	private void preload() throws GlodocsException {
		global = connection.createNodeReference(Constants.GLOBAL_SYSTEM);
		global.setSubscriptCount(0);
		global.appendSubscript(Constants.GLOBAL_COLLECTIONS + "_" + id);
		valueList = global.getList();
		try {
			if (valueList != null) {
				loadParameters();
			} else {
				throw new GlodocsException("Collection doesn't exist");
			}
		} catch (ParseException e) {
			throw new GlodocsException("Unsupported date format");
		} catch (JSONException e) {
			throw new GlodocsException("Unsupported date format");
		}
	}

	/**
	 * Loading parameters while initializing Collection<br />
	 * Also loading Documents into {@link Collection#documents}
	 * 
	 * @throws JSONException
	 * @throws ParseException
	 */
	public void loadParameters() throws JSONException, ParseException {
		global = connection.createNodeReference(this.id);
		global.setSubscriptCount(0);
		valueList = global.getList();

		if (valueList == null) {
			valueList = connection.createList();
			global.set(valueList);
		}
		if (valueList.length() != 0) {
			owner = valueList.getNextString();
			creationDate = (new SimpleDateFormat(Constants.DATE_FORMAT))
					.parse(valueList.getNextString());
			documentsCount = valueList.getNextInt();
			scheme = new Scheme(new JSONObject(valueList.getNextString()));
			for (int i = 0; i < documentsCount; i++) {
				String key = valueList.getNextString();
				try {
					documents.put(key, new Document(this, key));
				} catch (GlodocsException e) {
					e.printStackTrace();
				}
			}
		}
		loaded = true;

		System.out.println(toString());

	}

	@Override
	public String toString() {
		return "Collection [id=" + id + ", scheme=" + scheme + ", owner="
				+ owner + ", creationDate=" + creationDate
				+ ", documentsCount=" + documentsCount + ", loaded=" + loaded
				+ "]";
	}

	/**
	 * Getting document's list that belongs to current collection
	 * 
	 * @return Map of documents
	 * @throws JSONException
	 * @throws ParseException
	 */
	public HashMap<String, Document> getDocuments() throws JSONException,
			ParseException {
		if (!loaded)
			loadParameters();
		return documents;
	}

	public HashMap<String, Document> refresh() {
		loaded = false;
		return documents;
	}

	public void clear() throws GlodocsException {
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(), this,
				ObjectAction.FULL)) {
			Iterator<String> iterator = documents.keySet().iterator();
			while (iterator.hasNext()) {
				removeDocumentById(iterator.next());
			}
			HistoryEvent.addEvent(DBConnection.getLoggedUser().getLogin(),
					JournalConstants.CLEAR_COLECTION, id);
		} else
			throw new GlodocsException(
					"Access Denied :: Can not clear collection");
	}

	public Scheme getScheme() {
		return scheme;
	}

	/**
	 * Method adds new Document to Collection <b>Document should be created
	 * before running this method</b>
	 * 
	 * @see Document
	 * @param document
	 *            Docment to add
	 * @throws JSONException
	 * @throws ParseException
	 * @throws GlodocsException
	 */
	public void addDocument(Document document) throws JSONException,
			ParseException, GlodocsException {
		documents.put(document.getId(), document);
		++documentsCount;
		saveParameters();
		preload();
		HistoryEvent.addEvent(DBConnection.getLoggedUser().getLogin(),
				JournalConstants.ADD_RECORD, id, document.getId());
	}

	/**
	 * Saving new parameters of current collection
	 * 
	 * @see Collection#loadParameters()
	 */
	private void saveParameters() {
		global = connection.createNodeReference(this.id);
		global.setSubscriptCount(0);
		valueList = global.getList();
		valueList.clear();

		valueList.append(owner);
		valueList.append((new SimpleDateFormat(Constants.DATE_FORMAT))
				.format(creationDate));
		valueList.append(documentsCount);
		valueList.append(scheme.toString());

		Iterator<String> iter = documents.keySet().iterator();
		while (iter.hasNext()) {
			valueList.append(iter.next());
		}

		global.set(valueList);
		loaded = true;

		System.out.println(toString());

	}

	/**
	 * Getting Document by {@link Document#id}
	 * 
	 * @param id
	 *            Document's id to get
	 * @return Required document
	 */
	public Document getDocumentById(String id) {
		return documents.get(id);
	}

	/**
	 * Removing Document by {@link Document#id} <br />
	 * Also refreshing Collection
	 * 
	 * @param id
	 * @throws GlodocsException
	 */
	public void removeDocumentById(String id) throws GlodocsException {
		Document d = getDocumentById(id);
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(), d,
				ObjectAction.FULL)) {
			d.deleteHandler(index);

			global = connection.createNodeReference(this.id);
			global.setSubscriptCount(0);
			global.appendSubscript(id);
			global.killNode();
			documents.remove(id);
			--documentsCount;
			saveParameters();
			preload();
			HistoryEvent.addEvent(DBConnection.getLoggedUser().getLogin(),
					JournalConstants.REMOVE_RECORD, this.id, id);
		} else
			throw new GlodocsException("Access Denied :: can not drop document");
	}

	/**
	 * Adding index on field
	 * 
	 * @param field
	 *            Field that should be indexed
	 */
	public void addIndex(String field) {
		Iterator<String> iter = documents.keySet().iterator();
		while (iter.hasNext()) {
			documents.get(iter.next()).addIndexHandler(id, index);
		}
	}

	/**
	 * Removing index on field
	 * 
	 * @param id
	 *            Indexed field that should be deleted
	 */
	public void removeIndexById(String id) {
		Iterator<String> iter = documents.keySet().iterator();
		while (iter.hasNext()) {
			documents.get(iter.next()).deleteIndexHandler(id, index);
		}

	}

	public String getId() {
		return id;
	}

	public Iterator<Document> iterator() {
		return null;
	}

	public Index getIndex() {
		return index;
	}

}
