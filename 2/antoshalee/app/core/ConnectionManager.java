package core;

import support.LogWriter;

import com.intersys.globals.*;

public class ConnectionManager {

	private static ConnectionManager manager;
	public static ConnectionManager Instance()
	{
		if (ConnectionManager.manager == null)
		{
			ConnectionManager.manager = init();            
		}

		return ConnectionManager.manager;
	}

	private static ConnectionManager  init()
	{
		manager = new ConnectionManager();
		return manager;
	}
	
	private Connection _connection;

	/**
	 * @return connection to Globals DB
	 */
	public Connection getConnection() 
	{
		if (_connection == null)
		{
			try
			{
				_connection =  ConnectionContext.getConnection(); 
				if (!_connection.isConnected())
				{
					_connection.connect("USER","_SYSTEM","DATA");
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return _connection;
	}

	public void CloseConnection()
	{
		getConnection().close();
	}

}
