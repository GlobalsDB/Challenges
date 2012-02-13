package com.xlab.glodocs.api;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.intersys.globals.Connection;
import com.intersys.globals.ConnectionContext;
import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.xlab.glodocs.api.db.Database;
import com.xlab.glodocs.api.security.User;
import com.xlab.glodocs.api.utils.Constants;
import com.xlab.glodocs.api.utils.GlodocsException;

/**
 * Glodocs Connection class. <br />
 * Contains static method {@link DBConnection#getInstance()} that returns
 * DBConnection instance <br />
 * This method establish connection if there is no existing connection to
 * GlobalsDB
 * 
 * @author Lapitskiy Anton
 * 
 */
public class DBConnection {

	/**
	 * Instance of DBConnection
	 */
	private static DBConnection instance;
	private Connection connection;

	/**
	 * Instance of Database associated with DBConnection instance
	 */
	private static Database database;
	private static NodeReference global;
	private static ValueList valueList;
	private static User loggedUser;

	/**
	 * String name of Logged user
	 * 
	 * @return String identifier
	 */
	public static User getLoggedUser() {
		return loggedUser;
	}

	/**
	 * Getting Instance of DBConnection. <br />
	 * Establish connection if {@link DBConnection#instance} is <b>null</b>
	 * 
	 * @return DBConnection instance
	 * @throws GlodocsException
	 */
	public static DBConnection getInstance() throws GlodocsException {
		if (instance == null)
			instance = new DBConnection();
		return instance;
	}

	/**
	 * Private constructor with establishing connection to GlobalsDB
	 * 
	 * @throws GlodocsException
	 */
	private DBConnection() throws GlodocsException {
		String home = System.getenv("GLOBALS_HOME");
		if (home == null) {
			System.out.println("GLOBALS_HOME is not set");
			throw new GlodocsException("GLOBALS_HOME is not set");
		}

		try {
			ConnectionContext.setOption(ConnectionContext.THREADING_MODEL,
					ConnectionContext.THREADING_MODEL_NON_THREAD_SPECIFIC);

			connection = (Connection) ConnectionContext.getConnection();

			if (!connection.isConnected())
				connect();

			global = connection.createNodeReference();
			valueList = connection.createList();

		} catch (Exception e) {
			System.out
					.println("Could not initialize GlobalsDB connection for Loader: "
							+ e);
			e.printStackTrace(System.err);
			return;
		}
	}

	/**
	 * Performing user login operation with <b>username</b> and <b>password</b>
	 * 
	 * @param username
	 *            User's login
	 * @param password
	 *            User's password
	 * @throws GlodocsException
	 */
	public void login(String username, String password) throws GlodocsException {
		loggedUser = User.login(username, password);
		if (loggedUser == null)
			throw new GlodocsException("Login failed");
	}

	/**
	 * Getting instance of {@link Database} associated with DBConnection
	 * 
	 * @return Database instance
	 */
	public static Database getDatabase() {
		if (database == null) {
			try {
				getInstance();
			} catch (GlodocsException e) {
				e.printStackTrace();
				return null;
			}
			database = new Database(instance);
		}
		return database;
	}

	/**
	 * Connecting to GlobalsDB without authorizing
	 */
	public void connect() {
		connection.connect();
		//database = new Database(this);
	}

	/**
	 * Wrapper for GlobalsDB's {@link Connection#createNodeReference(String)}
	 * 
	 * @param globalSystem
	 *            Name of global
	 * @return NodeReference on global
	 */
	public NodeReference createNodeReference(String globalSystem) {
		return connection.createNodeReference(globalSystem);
	}

	/**
	 * Wrapper for GlobalsDB's {@link Connection#createNodeReference()}
	 * 
	 * @return NodeReference on global
	 */
	public NodeReference createNodeReference() {
		return connection.createNodeReference();
	}

	/**
	 * Wrapper for GlobalsDB's {@link Connection#createList()}
	 * 
	 * @return created empty ValueList
	 */
	public ValueList createList() {
		return connection.createList();
	}

	/**
	 * Closing connection to GlobalsDB
	 */
	public void close() {
		connection.close();

	}

	public Connection getConnection() {
		return connection;
	}

	public static DBConnection getInstance(String login, String pass)
			throws GlodocsException {
		if (instance == null)
			instance = new DBConnection(login, pass);
		return instance;
	}

	/**
	 * Private constructor with establishing connection to GlobalsDB
	 * 
	 * @throws GlodocsException
	 */
	private DBConnection(String login, String pass) throws GlodocsException {
		String home = System.getenv("GLOBALS_HOME");
		if (home == null) {
			System.out.println("GLOBALS_HOME is not set");
			throw new GlodocsException("GLOBALS_HOME is not set");
		}

		try {
			ConnectionContext.setOption(ConnectionContext.THREADING_MODEL,
					ConnectionContext.THREADING_MODEL_NON_THREAD_SPECIFIC);

			connection = (Connection) ConnectionContext.getConnection();

			if (!connection.isConnected())
				connect(login, pass);

			global = connection.createNodeReference();
			valueList = connection.createList();

		} catch (Exception e) {
			System.out
					.println("Could not initialize GlobalsDB connection for Loader: "
							+ e);
			e.printStackTrace(System.err);
			return;
		}
	}

	private void connect(String login, String pass) throws GlodocsException {
		connection.connect();
		loggedUser = User.login(login, pass);
		if (loggedUser == null)
			throw new GlodocsException("Login failed");
		database = new Database(this);
	}

}
