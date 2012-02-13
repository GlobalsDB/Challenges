package com.xlab.glodocs.api.db;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xlab.glodocs.api.utils.Validator;

/**
 * Class represents collection's schema <br />
 * Schema is a JSONObject and contains 2 lists of fields: <b>required fields</b>
 * and <b>indexed fields<b/> <br />
 * <i>Only required fields could be indexed</i>
 * 
 * @author Lapitskiy Anton
 * 
 */
public class Scheme {
	/**
	 * JSONObject source of current schema
	 */
	private JSONObject source;
	private ArrayList<String> indexes = new ArrayList<String>();
	private ArrayList<String> fields = new ArrayList<String>();

	/**
	 * Constructing new schema from input JSONObject <br />
	 * Parsing <b><i>source</i></b> to find lists of required and indexed
	 * fields.
	 * 
	 * @param source
	 *            JSONObject source of schema
	 */
	public Scheme(JSONObject source) {
		this.source = source;
		try {
			JSONArray jArr = null;
			if (source.has("indexes")) {
				jArr = source.getJSONArray("indexes");
				for (int i = 0; i < jArr.length(); ++i) {
					indexes.add(jArr.get(i).toString());
				}
			}
			if (source.has("fields")) {
				jArr = source.getJSONArray("fields");
				for (int i = 0; i < jArr.length(); ++i) {
					fields.add(jArr.get(i).toString());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getting list of required fields
	 * 
	 * @return list of required fields
	 */
	public ArrayList<String> getFields() {
		return fields;
	}

	public void addValidator(Validator validator) {

	}

	/**
	 * Getting list of indexed fields
	 * 
	 * @return list of indexed fields
	 */
	public ArrayList<String> getIndexes() {
		return indexes;
	}

	/**
	 * toString() method returns String value of JSONObject
	 * {@link Scheme#source}
	 */
	@Override
	public String toString() {
		return source.toString();
	}

}
