package com.xlab.glodocs.api.test;

import java.util.ArrayList;
import java.util.HashMap;

import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.db.Collection;
import com.xlab.glodocs.api.db.Database;
import com.xlab.glodocs.api.db.Index;
import com.xlab.glodocs.api.security.ObjectAction;
import com.xlab.glodocs.api.security.Role;
import com.xlab.glodocs.api.security.User;

public class TestMain {

	/**
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		String home = System.getenv("GLOBALS_HOME");
		if (home == null) {
			System.out.println("GLOBALS_HOME is not set");
			return;
		}

		try {

			// try {
			/*
			 * Connection connection = ConnectionContext.getConnection(); if
			 * (!connection.isConnected()) connection.connect(); NodeReference
			 * ref = connection .createNodeReference(Constants.GLOBAL_SYSTEM);
			 * ref.kill();
			 */

			DBConnection conn = DBConnection.getInstance();

			conn.login("admin", "admin");

			Database db = DBConnection.getDatabase();
			Collection col = db.getCollectionById("test");
			Index i = col.getIndex();
			boolean b;
			short t = 0;
			// b = i.addIndex(t, "c", "200", "1");
			// b = i.addIndex(t, "c", "300", "2");
			// b = i.addIndex(t, "a", "bbb", "1");
			// b = i.addIndex(t, "b", "bbb", "1");
			// b = i.addIndex(t, "b", "ccc", "1");
			HashMap<String, String> m = new HashMap<String, String>();
			m.put("c", "100");
			// m.put("���", "2011");
			// ValueList list = i.exactQuery("�����", "Audi");
			// System.out.println(list.getNextString());

			HashMap<String, String> mr = new HashMap<String, String>();
			mr.put("c", "400");
			// mr.put("b", "bbb");
			ArrayList<String> ll = i.rangeManyQuery(m, mr);
			System.out.println(ll.size());
			// ll.close();
			// ll = i.rangeQuery("b", "bbb", "bbb");
			// System.out.println(ll.length());
			// ValueList l = i.exactManyQuery(m);
			/*
			 * ValueList l = i.rangeManyQuery(m, mr); if (l == null){
			 * System.out.println("null"); } else {
			 * System.out.println(l.length()); }
			 */
			// ValueList l = i.exactQuery("a", "hukhjlkhj");
			/*
			 * Collection collect = new Collection("Pupa", "admin", new
			 * Scheme(new JSONObject("{aaa:2,bbb:3}")));
			 * db.addCollection(collect);
			 */

			// Collection collect2 = db.getCollectionById("Auto");
			/*
			 * Document doc = new Document( collect, "bibika", "admin", new
			 * JSONObject( "{type:1, load:2, arr:[{int:i}, {str=\"ddd\"}]}"));
			 * collect.addDocument(doc);
			 */
			/*
			 * Collection collect = new Collection("Pupa");
			 * 
			 * Document doc2 = collect.getDocumentById("bibika");
			 * System.out.println("JSON = " + doc2.getJSONValue());
			 */
			// conn.close();

			/*
			 * } catch (GlodocsException e) { e.printStackTrace();
			 * 
			 * }
			 */

			/*
			 * Connection connection = ConnectionContext.getConnection(); if
			 * (!connection.isConnected()) connection.connect(); NodeReference
			 * ref = connection .createNodeReference("Product2");
			 * ref.killNode();
			 */
			/*
			 * ref.setSubscriptCount(0);
			 * ref.appendSubscript(Constants.GLOBAL_COLLECTIONLIST); ValueList
			 * val = ref.getList(); System.out.println(val.toString()); String
			 * key = val.getNextString(); System.out.println(key); ref.set(val);
			 * 
			 * ref = connection.createNodeReference("Pupa"); val =
			 * ref.getList(); for (int i = 0; i < val.length(); ++i) {
			 * System.out.println(val.getNextString()); }
			 * ref.appendSubscript("bibika"); val = ref.getList(); for (int i =
			 * 0; i < val.length(); ++i) {
			 * System.out.println(val.getNextString()); }
			 */

			String login = "admin";
			String pass = "admin";
			DBConnection dbconnection = DBConnection.getInstance(login, pass);
			User user = new User("user");
			user.addRole(Role.BROWSE_JOURNAL);
			user.addCollectionAction(dbconnection.getDatabase()
					.getCollectionById("Auto"), ObjectAction.WRITE);
			user.addDocumentAction(dbconnection.getDatabase()
					.getCollectionById("Auto").getDocumentById("First Auto"),
					ObjectAction.WRITE);
		} catch (Exception e) {
			System.out
					.println("Could not initialize GlobalsDB connection for Loader: "
							+ e);
			e.printStackTrace(System.err);
			return;
		}

	}
}
