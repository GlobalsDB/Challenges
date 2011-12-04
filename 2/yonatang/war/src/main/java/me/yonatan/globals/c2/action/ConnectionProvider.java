package me.yonatan.globals.c2.action;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.intersys.globals.Connection;
import com.intersys.globals.impl.ConnectionImpl;

@ApplicationScoped
public class ConnectionProvider {

	private Connection connection;

	@Produces
	Connection connectionProvider() {
		if (connection == null) {
			try {
				connection = new ConnectionImpl();
				connection.connect("me.yonatan.globals.c2", "_SYS", "SYSTEM");
			} catch (UnsatisfiedLinkError e) {
				// NO GLOBALS_HOME set!
			}

		}
		return connection;
	}

}
