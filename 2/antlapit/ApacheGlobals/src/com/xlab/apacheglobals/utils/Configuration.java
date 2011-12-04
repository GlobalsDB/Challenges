package com.xlab.apacheglobals.utils;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.xlab.apacheglobals.GlobalsConnection;

public class Configuration {
	public static String dateFormat = "dd/MMM/yyyy:kk:mm:ss Z";
	public static String logFileName = "M:/wamp/logs/access.log";
	public static final String CONFIG_GLOBAL = "APACHEGLOBALSCONFIG";
	public static final String INDEX_GLOBAL = "APACHEGLOBALSINDEX";
	public static final String DATA_GLOBAL = "APACHEGLOBALSDATA";

	public static void loadConfig() throws Exception {
		NodeReference node = GlobalsConnection.getInstance()
				.createNodeReference(CONFIG_GLOBAL);
		ValueList list = node.getList();
		logFileName = list.getNextString();
		dateFormat = list.getNextString();
	}

	public static void saveConfig() throws Exception {
		NodeReference node = GlobalsConnection.getInstance()
				.createNodeReference(CONFIG_GLOBAL);
		ValueList list = GlobalsConnection.getInstance().createList();
		list.append(logFileName);
		list.append(dateFormat);
		node.set(list);
	}
}
