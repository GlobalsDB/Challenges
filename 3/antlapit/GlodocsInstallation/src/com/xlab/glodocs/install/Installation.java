package com.xlab.glodocs.install;

import java.security.NoSuchAlgorithmException;

import com.intersys.globals.NodeReference;
import com.intersys.globals.ValueList;
import com.xlab.glodocs.api.DBConnection;
import com.xlab.glodocs.api.security.Role;
import com.xlab.glodocs.api.security.User;
import com.xlab.glodocs.api.utils.Constants;
import com.xlab.glodocs.api.utils.GlodocsException;

public class Installation {

	/**
	 * @param args
	 * @throws GlodocsException
	 * @throws NoSuchAlgorithmException
	 */
	public static void main(String[] args) throws GlodocsException,
			NoSuchAlgorithmException {
		System.out.println("Installation starts");
		DBConnection conn = DBConnection.getInstance();

		String username = "";
		String password = "";
		if (args.length < 2) {
			username = "admin";
			password = "admin";
		} else {
			username = args[0];
			password = args[1];
		}

		System.out.println("Creating admin user = " + username);
		User user = new User(username, password);
		NodeReference global = conn.createNodeReference(Constants.GLOBAL_USERS);
		global.appendSubscript(user.getLogin());
		global.appendSubscript(Constants.USER_ROLES);
		Role[] arr = Role.values();
		ValueList list = conn.getConnection().createList();
		for (int i = 0; i < arr.length; ++i) {
			System.out.println("Granting " + arr[i]);
			list.append(arr[i].getIntegerCode());
		}
		global.set(list);

		global = conn.createNodeReference(Constants.GLOBAL_USERS);
		global.appendSubscript(user.getLogin());
		global.appendSubscript(Constants.USER_OBJECT_PRIV_COL);
		list = conn.getConnection().createList();
		global.set(list);

		global = conn.createNodeReference(Constants.GLOBAL_USERS);
		global.appendSubscript(user.getLogin());
		global.appendSubscript(Constants.USER_OBJECT_PRIV_DOC);
		list = conn.getConnection().createList();
		global.set(list);

		System.out.println("Granting succeeded");
		System.out.println("Installation complete");

	}

}
