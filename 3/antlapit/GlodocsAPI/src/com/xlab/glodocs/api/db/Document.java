package com.xlab.glodocs.api.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.journal.HistoryEvent;
import com.xlab.glodocs.api.journal.JournalConstants;
import com.xlab.glodocs.api.security.AccessManager;
import com.xlab.glodocs.api.security.Role;
import com.xlab.glodocs.api.security.User;
import com.xlab.glodocs.api.utils.Constants;
import com.xlab.glodocs.api.utils.GlodocsException;

/**
 * General class to work with document in database <br />
 * Contains all document's parameters such as {@link Document#owner},
 * {@link Document#creationDate} and others <bt /> Document's data is stored in
 * JSONObject {@link Document#value}
 * 
 * @author Lapitskiy Anton
 * 
 */
public class Document {
	/**
	 * String identifier
	 */
	private String id;

	/**
	 * Parent collection
	 */
	private Collection collection;

	/**
	 * Owner's name
	 */
	private String owner;

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) throws GlodocsException {
		this.owner = owner;
		saveData();
	}

	/**
	 * JSON value of current Document
	 * 
	 * @see org.json.JSONObject
	 */
	private JSONObject value;
	private final DBConnection connection;
	private NodeReference global;
	private ValueList valueList;

	/**
	 * Creation date
	 */
	private Date creationDate;

	/**
	 * Required fields that are also indexed
	 */
	private HashMap<String, String> indexedFields = new HashMap<String, String>();

	/**
	 * Public constructor<br />
	 * Used to get <b>existing</b> Document from Database by {@link Document#id} <br />
	 * Automatically loads document data
	 * 
	 * @see Document#loadData()
	 * @param collection
	 *            parent colelction
	 * @param id
	 *            document's name
	 * @throws GlodocsException
	 */
	public Document(Collection collection, String id) throws GlodocsException {
		this.id = id;
		this.collection = collection;
		this.connection = DBConnection.getInstance();
		try {
			loadData();
		} catch (ParseException e) {
			throw new GlodocsException("Unable to parse date");
		} catch (JSONException e) {
			throw new GlodocsException("Unable to parse JSONObject");
		}
	}

	/**
	 * Public constructor <br />
	 * Used to create <b>new</b> Document in Collection
	 * 
	 * @param collection
	 *            parent collection
	 * @param id
	 *            document's name
	 * @param object
	 *            document's value
	 * @throws GlodocsException
	 */
	public Document(Collection collection, String id, JSONObject object)
			throws GlodocsException {
		this.collection = collection;
		if (AccessManager.checkPermissions(DBConnection.getLoggedUser(),
				Role.CREATE_COLLECTION, collection)) {
			this.connection = DBConnection.getInstance();
			this.id = id;
			this.owner = DBConnection.getLoggedUser().getLogin();
			this.value = object;
			this.creationDate = Calendar.getInstance().getTime();
			saveData();
		} else
			throw new GlodocsException(
					"Access Denied :: can not create document");
	}

	/**
	 * Loading document data such as {@link Document#owner} and
	 * {@link Document#creationDate}
	 * 
	 * @throws ParseException
	 * @throws JSONException
	 */
	private void loadData() throws ParseException, JSONException {
		global = connection.createNodeReference(collection.getId());
		global.setSubscriptCount(0);
		global.appendSubscript(id);
		valueList = global.getList();
		owner = valueList.getNextString();
		creationDate = (new SimpleDateFormat(Constants.DATE_FORMAT)
				.parse(valueList.getNextString()));
		value = new JSONObject(valueList.getNextString());

		global.appendSubscript(Constants.GLOBAL_DOCUMENT_INDEXES);
		valueList = global.getList();

		indexedFields.clear();
		if (valueList == null) {
			valueList = connection.createList();
			global.set(valueList);
		} else {
			for (int i = 0; i < valueList.length() / 2; ++i) {
				String temp = valueList.getNextString();
				indexedFields.put(temp, valueList.getNextString());
			}
		}

	}

	/**
	 * Method clears document data
	 */
	public void clear() {
		global = connection.createNodeReference(collection.getId());
		global.setSubscriptCount(0);
		global.appendSubscript(id);
		valueList = connection.createList();
		valueList.clear();
		global.set(valueList);
	}

	/**
	 * Method saves new document data in database
	 * 
	 * @throws GlodocsException
	 * @see {@link Document#deleteHandler(Index)}
	 * @see {@link Document#insertHandler(Index)}
	 */
	public void saveData() throws GlodocsException {
		checkFields();

		global = connection.createNodeReference(collection.getId());
		global.setSubscriptCount(0);
		global.appendSubscript(id);
		valueList = connection.createList();
		valueList.append(owner);
		valueList.append(new SimpleDateFormat(Constants.DATE_FORMAT)
				.format(creationDate));
		valueList.append(value.toString());
		global.set(valueList);

		deleteHandler(collection.getIndex());
		insertHandler(collection.getIndex());

		global.appendSubscript(Constants.GLOBAL_DOCUMENT_INDEXES);
		valueList = connection.createList();

		fillIndexedFields();

		Iterator<String> iter = indexedFields.keySet().iterator();
		while (iter.hasNext()) {
			String temp = iter.next();
			valueList.append(temp);
			valueList.append(indexedFields.get(temp));
		}
		global.set(valueList);
		HistoryEvent.addEvent(DBConnection.getLoggedUser().getLogin(),
				JournalConstants.MODIFY_RECORD, collection.getId(), id);
	}

	/**
	 * Add values of indexed values to {@link Index} object
	 */
	private void fillIndexedFields() {
		ArrayList<String> indexes = collection.getScheme().getIndexes();
		for (int i = 0; i < indexes.size(); ++i) {
			addIndexHandler(indexes.get(i), collection.getIndex());
		}

	}

	/**
	 * Method checks new fields' values <br />
	 * Checking performed before saving data
	 * 
	 * @see Document#saveData()
	 * @throws GlodocsException
	 */
	private void checkFields() throws GlodocsException {
		ArrayList<String> list = collection.getScheme().getFields();
		if (value == null)
			throw new GlodocsException("Document is empty");
		for (int i = 0; i < list.size(); ++i) {
			if (!value.has(list.get(i)))
				throw new GlodocsException("Require field:" + list.get(i));
		}

	}

	/**
	 * Checking indexed fields in {@link Index} object.<bt /> This method
	 * deletes outdated values.
	 */
	private void validateIndexedFields() {
		String tempK;
		String tempV;

		final Index index = collection.getIndex();
		Iterator<String> iter = indexedFields.keySet().iterator();
		while (iter.hasNext()) {
			tempK = iter.next();
			tempV = indexedFields.get(tempK);
			if (!index.containsIndex(tempK, tempV, id)) {
				index.deleteIndex(tempK, tempV, id);
			}
		}
	}

	@Override
	public String toString() {
		return "Document [id=" + id + ", owner=" + owner + ", value=" + value
				+ ", creationDate=" + creationDate + ", indexedFields="
				+ indexedFields + "]";
	}

	/**
	 * Adding values, that should be indexed, to {@link Index} object <br />
	 * Index type is <b>Simple Index</b>
	 * 
	 * @see Index
	 * @param index
	 *            Index handler to add
	 */
	public void insertHandler(Index index) {
		insertHandler(index, Constants.INDEX_TYPE_INDEX);
	}

	/**
	 * Adding values, that should be indexed, to {@link Index} object <br />
	 * Index type can be <b>Simple Index</b> or <b>Unique</b>
	 * 
	 * @param index
	 *            Index handler to add
	 * @param type
	 *            type of new index
	 */
	private void insertHandler(Index index, short type) {
		String tempK;
		String tempV;

		validateIndexedFields();
		Iterator<String> iter = indexedFields.keySet().iterator();
		while (iter.hasNext()) {
			tempK = iter.next();
			tempV = indexedFields.get(tempK);
			index.addIndex(type, tempK, tempV, id);
		}
	}

	/**
	 * Deleting indexed values of document from {@link Index} object
	 * 
	 * @param index
	 *            index handler to remove
	 */
	public void deleteHandler(Index index) {
		String tempK;
		String tempV;

		validateIndexedFields();
		Iterator<String> iter = indexedFields.keySet().iterator();
		while (iter.hasNext()) {
			tempK = iter.next();
			tempV = indexedFields.get(tempK);
			index.deleteIndex(tempK, tempV, id);
		}
	}

	/**
	 * Method performs indexing document's value after adding new {@link Index}
	 * to {@link Collection} <br />
	 * Index type is <b>Simple Index</b>
	 * 
	 * @param field
	 *            new indexed field
	 * @param index
	 *            index handler that is edited
	 */
	public void addIndexHandler(String field, final Index index) {
		addIndexHandler(field, index, Constants.INDEX_TYPE_INDEX);
	}

	/**
	 * Method performs indexing document's value after adding new {@link Index}
	 * to {@link Collection} <br />
	 * Index type can be <b>Simple Index</b> or <b>Unique</b>
	 * 
	 * @param field
	 *            new indexed field
	 * @param index
	 *            index handler that is edited
	 * @param type
	 *            type of new index
	 */
	public void addIndexHandler(String field, final Index index, short type) {
		try {
			String temp = getValue(field);
			if (!temp.isEmpty()) {
				indexedFields.put(field, temp);
				index.addIndex(type, field, getIndexedValue(field), id);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getting value from JSONObject
	 * 
	 * @param field
	 *            name of field
	 * @return value of field in document
	 * @throws JSONException
	 */
	private String getValue(String field) throws JSONException {
		return getValueLevelDown(field, value);
	}

	/**
	 * Performing recursive scanning of JSONArray
	 * 
	 * @param field
	 *            name of field
	 * @param arr
	 *            JSONArray to search
	 * @return value of field in document
	 * @throws JSONException
	 */
	private String getValueLevelDown(String field, JSONArray arr)
			throws JSONException {
		String out = "";
		for (int i = 0; i < arr.length(); ++i) {
			Object o = arr.get(i);
			if (o instanceof JSONObject)
				out = getValueLevelDown(field, (JSONObject) o);
			if (o instanceof JSONArray)
				out = getValueLevelDown(field, (JSONArray) o);

			if (!out.isEmpty())
				return out;
		}
		return out;
	}

	/**
	 * Performing recursive scanning of JSONObject
	 * 
	 * @param field
	 *            name of field
	 * @param obj
	 *            JSONObject to search
	 * @return value of field in document
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private String getValueLevelDown(String field, final JSONObject obj)
			throws JSONException {
		if (obj.has(field))
			return obj.get(field).toString();

		String out = "";
		Iterator<String> iter = obj.keys();
		while (iter.hasNext()) {
			String temp = iter.next();
			Object o = obj.get(temp);
			if (o instanceof JSONObject)
				out = getValueLevelDown(field, (JSONObject) o);
			if (o instanceof JSONArray)
				out = getValueLevelDown(field, (JSONArray) o);

			if (!out.isEmpty())
				return out;
		}

		return out;
	}

	/**
	 * Deleting index in <b>field</b> from <b>index</b>
	 * 
	 * @param field
	 *            name of field
	 * @param index
	 *            Index handler
	 */
	public void deleteIndexHandler(String field, final Index index) {
		indexedFields.remove(field);
		index.deleteIndex(field, getIndexedValue(field), id);
	}

	public void exportToExcel() {

	}

	public void exportToXML() {

	}

	public void addRules(User user) {

	}

	/**
	 * Getting indexed value by <b>name</b>
	 * 
	 * @param name
	 *            field's name
	 * @return required value
	 */
	public String getIndexedValue(String name) {
		return indexedFields.get(name);
	}

	public String getId() {
		return id;
	}

	public Collection getCollection() {
		return collection;
	}

	public JSONObject getJSONValue() {
		return value;
	}

	/**
	 * Update document data in database
	 * 
	 * @param updatedJSONObjectsCollection
	 *            new document's value
	 * @throws GlodocsException
	 */
	public void editValue(JSONObject updatedJSONObjectsCollection)
			throws GlodocsException {
		this.value = updatedJSONObjectsCollection;
		saveData();

	}
}
