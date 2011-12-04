package com.xlab.apacheglobals;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;

public class GlobalsConnection {
	private static GlobalsConnection instance;
	private Connection connection;

	public static GlobalsConnection getInstance() throws Exception {
		if (instance == null)
			instance = new GlobalsConnection();
		return instance;
	}

	private GlobalsConnection() throws Exception {
		String home = System.getenv("GLOBALS_HOME");
		if (home == null) {
			System.out.println("GLOBALS_HOME is not set");
			throw new Exception("GLOBALS_HOME is not set");
		}

		try {
			ConnectionContext.setOption(ConnectionContext.THREADING_MODEL,
					ConnectionContext.THREADING_MODEL_NON_THREAD_SPECIFIC);

			connection = (Connection) ConnectionContext.getConnection();

			if (!connection.isConnected())
				connection.connect();

		} catch (Exception e) {
			System.out
					.println("Could not initialize GlobalsDB connection for Loader: "
							+ e);
			e.printStackTrace(System.err);
			return;
		}
	}

	public NodeReference createNodeReference(String globalSystem) {
		return connection.createNodeReference(globalSystem);
	}

	public NodeReference createNodeReference() {
		return connection.createNodeReference();
	}

	public ValueList createList() {
		return connection.createList();
	}

	public void close() {
		connection.close();

	}

	public Connection getConnection() {
		return connection;
	}

}
