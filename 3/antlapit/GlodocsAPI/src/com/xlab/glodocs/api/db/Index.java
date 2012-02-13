package com.xlab.glodocs.api.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.utils.Constants;

/**
 * Index implementation in Glodocs <br />
 * Also contains methods for querying
 * 
 * @author Minenok Alexander, Lapitskiy Anton
 * 
 */
public class Index {
	private DBConnection connection;
	private NodeReference container;

	public Index(DBConnection globalsDBConnection, Collection collection) {
		connection = globalsDBConnection;
		container = connection.createNodeReference(collection.getId()
				+ Constants.GLOBALS_COLLECTION_INDEX);
	}

	private ValueList getExisting(String field, String value) {
		ValueList values = null;
		value = value.toLowerCase();
		container.appendSubscript(field);
		if (container.hasSubnodes()) {
			container.appendSubscript(value);
			values = container.getList();
		}
		container.setSubscriptCount(0);

		return values;
	}

	/**
	 * Adding to existing index new values
	 * 
	 * @param type
	 *            index type
	 * @param field
	 *            name of indexed field
	 * @param value
	 *            value of field
	 * @param identifier
	 *            document identifier
	 * @return result of adding index
	 */
	public boolean addIndex(short type, String field, String value,
			String identifier) {
		ValueList values = getExisting(field, value);
		if (values == null)
			values = connection.createList();
		if ((type == Constants.INDEX_TYPE_UNIQUE) && (values.length() > 0)) {
			// можно кидать эксепшн конечно, но не очень-то и нужно
			return false;
		}
		// наверно надо проверять, если ли уже там такой элемент, но это
		// замедлит выполнение
		values.append(identifier);
		container.set(values, field, value);
		values.close();
		return true;
	}

	/**
	 * Checking existing indexed value
	 * 
	 * @param field
	 *            name of indexed field
	 * @param value
	 *            value of field
	 * @param identifier
	 *            document identifier
	 * @return result of adding index
	 */
	public boolean containsIndex(String field, String value, String identifier) {
		ValueList values = getExisting(field, value);

		String tempStr = "";

		if (values != null) {
			values.resetToFirst();
			for (int i = 0; i < values.length(); ++i) {
				tempStr = values.getNextString();
				if (!tempStr.isEmpty() && tempStr.equals(identifier))
					return true;
			}
			values.close();
		}
		return false;
	}

	/**
	 * Removing index from handler
	 * 
	 * @param field
	 *            name of indexed field
	 * @param value
	 *            value of field
	 * @param identifier
	 *            document identifier
	 */
	public void deleteIndex(String field, String value, String identifier) {
		ValueList values = getExisting(field, value);

		ValueList newValues = connection.createList();
		String tempStr = "";

		value = value.toLowerCase();

		if (values != null) {
			values.resetToFirst();
			for (int i = 0; i < values.length(); ++i) {
				tempStr = values.getNextString();
				if (!tempStr.equals(identifier))
					newValues.append(tempStr);
			}
			values.clear();
			values.close();
		}
		container.set(newValues, field, value);
		newValues.close();
	}

	/**
	 * Search documents for exact pair <b>(field, value)</b>
	 * 
	 * @param field
	 *            name of indexed field
	 * @param value
	 *            value of field
	 * @return List of document, that suit criteria
	 */
	public ArrayList<String> exactQuery(String field, String value) {
		ArrayList<String> ret = new ArrayList<String>();
		ValueList l = getExisting(field, value);
		if (l != null) {
			for (int i = 0; i < l.length(); ++i)
				ret.add(l.getNextString());
			l.close();
		}
		return ret;
	}

	/**
	 * Search documents from <b>(field, min)</b> to <b>(field, max)</b>
	 * 
	 * @param field
	 *            name of indexed field
	 * @param min
	 *            minimum field value
	 * @param max
	 *            maximum field value
	 * @return List of document, that suit criteria
	 */
	public ArrayList<String> rangeQuery(String field, String min, String max) {
		ArrayList<String> ret = new ArrayList<String>();
		ValueList tempVal;
		String subscript = "";
		String tempStr = "";
		container.appendSubscript(field);
		if (!container.hasSubnodes())
			return ret;

		do {
			subscript = container.nextSubscript(subscript);
			if ((subscript.length() > 0) && (max.length() > 0)
					&& (subscript.compareToIgnoreCase(max) > 0))
				break;
			if ((subscript.length() > 0)
					&& (subscript.compareToIgnoreCase(min) >= 0)) {
				container.appendSubscript(subscript);
				tempVal = container.getList();
				tempVal.resetToFirst();
				for (int i = 0; i < tempVal.length(); ++i) {
					tempStr = tempVal.getNextString();
					if (tempStr.length() > 0)
						ret.add(tempStr);
				}
			}
			container.setSubscriptCount(1);
		} while (subscript.length() > 0);
		container.setSubscriptCount(0);
		return ret;
	}

	/**
	 * Search for list of exact pairs <b>(field, value)</b>
	 * 
	 * @param fields
	 *            map if field's values
	 * @return List of document, that suit criteria
	 */
	public ArrayList<String> exactManyQuery(HashMap<String, String> fields) {
		ArrayList<String> tret = new ArrayList<String>();
		ArrayList<String> tlist = new ArrayList<String>();
		Iterator<Entry<String, String>> it = fields.entrySet().iterator();
		Entry<String, String> e;
		short i = 0;

		while (it.hasNext()) {
			e = it.next();
			tlist = exactQuery(e.getKey(), e.getValue());
			if ((tlist == null) || (tlist.size() == 0))
				return new ArrayList<String>();
			if (i == 0) {
				for (int j = 0; j < tlist.size(); ++j)
					tret.add(tlist.get(j));
				i = 1;
			} else {
				tlist.clear();
				for (int j = 0; j < tret.size(); ++j) {
					if (!tlist.contains(tret.get(j)))
						tret.remove(j);
				}
			}
			if (tret.size() == 0)
				return new ArrayList<String>();
		}
		return tret;
	}

	/**
	 * Search documents for list of ranges from <b>(field, left)</b> to
	 * <b>(field, right)</b>
	 * 
	 * @param fieldsLeft
	 *            map of field's minimum values
	 * @param fieldsRight
	 *            map of field's maximum values
	 * @return List of document, that suit criteria
	 */
	public ArrayList<String> rangeManyQuery(HashMap<String, String> fieldsLeft,
			HashMap<String, String> fieldsRight) {
		if (fieldsLeft.size() != fieldsRight.size())
			return new ArrayList<String>();
		ArrayList<String> tret = new ArrayList<String>();
		ArrayList<String> tlist = new ArrayList<String>();
		Iterator<Entry<String, String>> itl = fieldsLeft.entrySet().iterator();
		Iterator<Entry<String, String>> itr = fieldsRight.entrySet().iterator();
		Entry<String, String> el, er;
		short i = 0;

		while (itl.hasNext() && itr.hasNext()) {
			el = itl.next();
			er = itr.next();
			tlist = rangeQuery(el.getKey(), el.getValue(), er.getValue());
			if ((tlist == null) || (tlist.size() == 0))
				return new ArrayList<String>();
			if (i == 0) {
				for (int j = 0; j < tlist.size(); ++j)
					tret.add(tlist.get(j));
				i = 1;
			} else {
				tlist.clear();
				for (int j = 0; j < tret.size(); ++j) {
					if (!tlist.contains(tret.get(j)))
						tret.remove(j);
				}
			}
			if (tret.size() == 0)
				return new ArrayList<String>();
		}
		return tret;
	}

}
