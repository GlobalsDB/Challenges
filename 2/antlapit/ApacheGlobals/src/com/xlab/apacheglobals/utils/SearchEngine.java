package com.xlab.apacheglobals.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.xlab.apacheglobals.GlobalsConnection;

public class SearchEngine {

	public static ArrayList<Integer> getRecordByOneField(String field,
			String value) throws Exception {

		NodeReference node = GlobalsConnection.getInstance()
				.createNodeReference(Configuration.INDEX_GLOBAL);
		node.appendSubscript(field);
		node.appendSubscript(value);
		ValueList list = node.getList();
		if (list == null)
			return null;

		ArrayList<Integer> idList = new ArrayList<Integer>();
		Object[] objs = list.getAll();
		for (int i = 0; i < objs.length; ++i) {
			idList.add((Integer) objs[i]);
		}

		return idList;
	}

	public static ArrayList<Integer> getRangeByOneField(String field,
			String valueMin, String valueMax) throws Exception {
		NodeReference node = GlobalsConnection.getInstance()
				.createNodeReference(Configuration.INDEX_GLOBAL);
		node.appendSubscript(field);
		String subscript = "";
		ValueList tempVal;
		String tempStr = "";
		ArrayList<Integer> ret = new ArrayList<Integer>();

		do {
			subscript = node.nextSubscript(field, subscript);
			// if (!valueMax.isEmpty())
			if (!subscript.isEmpty()
					&& (subscript.compareToIgnoreCase(valueMax) > 0))
				break;
			if (!subscript.isEmpty()
					&& (subscript.compareToIgnoreCase(valueMin) >= 0)) {
				tempVal = node.getList();
				tempVal.resetToFirst();
				do {
					tempStr = tempVal.getNextString();
					if (tempStr.length() > 0)
						ret.add(Integer.parseInt(tempStr));
				} while (tempVal.length() > 0);
			}

		} while (subscript.length() > 0);

		return ret;
	}

	public static ArrayList<Integer> getRecordByManyFields(
			HashMap<String, String> fieldValues) throws Exception {
		Iterator<String> iter = fieldValues.keySet().iterator();
		String key = "";
		ArrayList<Integer> result = new ArrayList<Integer>();
		ArrayList<Integer> temp;
		boolean flag = false;
		int id;

		while (iter.hasNext()) {
			key = iter.next();
			if (!flag) {
				result = getRecordByOneField(key, fieldValues.get(key));
				flag = true;
			} else {
				temp = getRecordByOneField(key, fieldValues.get(key));
				for (int i = 0; i < result.size(); ++i) {
					id = result.get(i);
					if (!temp.contains(id)) {
						result.remove((Object) id);
					}

				}
			}
		}
		return result;

	}

	public static ArrayList<Integer> getRangeByManyFields(
			HashMap<String, String> fieldValuesMin,
			HashMap<String, String> fieldValuesMax) throws Exception {
		Iterator<String> iter = fieldValuesMin.keySet().iterator();
		String key = "";
		ArrayList<Integer> result = new ArrayList<Integer>();
		ArrayList<Integer> temp;
		boolean flag = false;
		int id;

		while (iter.hasNext()) {
			key = iter.next();
			if (!flag) {
				result = getRangeByOneField(key, fieldValuesMin.get(key),
						fieldValuesMax.get(key));
				flag = true;
			} else {
				temp = getRangeByOneField(key, fieldValuesMin.get(key),
						fieldValuesMax.get(key));
				for (int i = 0; i < result.size(); ++i) {
					id = result.get(i);
					if (!temp.contains(id)) {
						result.remove((Object) id);
					}

				}
			}
		}
		return result;

	}
}
