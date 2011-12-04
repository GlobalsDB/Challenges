package com.xlab.apacheglobals;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.xlab.apacheglobals.utils.Configuration;

public class Installation {

	/**
	 * @param args
	 * @throws Exception
	 * 
	 */
	public static void main(String[] args) {
		try {
			install(args[0], args[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void install(String logFileName, String dateFormat)
			throws Exception {
		NodeReference node = GlobalsConnection.getInstance()
				.createNodeReference(Configuration.CONFIG_GLOBAL);
		ValueList list = GlobalsConnection.getInstance().createList();
		list.append(logFileName);
		list.append(dateFormat);
		node.set(list);

		logFileName = list.getNextString();
		dateFormat = list.getNextString();

		node = GlobalsConnection.getInstance().createNodeReference(
				Configuration.INDEX_GLOBAL);
		list = GlobalsConnection.getInstance().createList();
		node.set(list);

		node = GlobalsConnection.getInstance().createNodeReference(
				Configuration.DATA_GLOBAL);
		list = GlobalsConnection.getInstance().createList();
		node.set(list);
	}

}
