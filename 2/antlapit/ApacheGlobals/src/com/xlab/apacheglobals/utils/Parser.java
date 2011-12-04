package com.xlab.apacheglobals.utils;

import com.intersys.globals.NodeReference;
import com.xlab.apacheglobals.GlobalsConnection;

public class Parser {
	public static int recordImported = 0;

	public static void load() {
		try {
			NodeReference node = GlobalsConnection.getInstance()
					.createNodeReference(Configuration.CONFIG_GLOBAL);
			recordImported = node.getInt("count");
		} catch (Exception e) {
			recordImported = 0;
		}

	}

	public static void saveImported() {
		try {
			NodeReference node = GlobalsConnection.getInstance()
					.createNodeReference(Configuration.CONFIG_GLOBAL);
			node.set(recordImported, "count");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
