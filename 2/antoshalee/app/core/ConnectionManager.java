package core;
	
import support.LogWriter;

import com.intersys.globals.*;
	
public class ConnectionManager {

  	private static ConnectionManager manager;
  	private LogWriter log;
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
    	manager.log = new LogWriter();
    	manager.log.FileName = "c:\\temp\\javaLog.txt";
    	return manager;
    	
    }
    private Connection _connection;

    /**
     * @return the _connection
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
	            catch (GlobalsException ex)
	            {
	               log.WriteToFile(ex.getMessage(), true);
	               throw ex;
	            }
	            catch (Exception ex)
	            {
	            	log.WriteToFile(ex.getMessage(), true);
	   	        }
	        }
	        return _connection;
	    }
	    
	    public void CloseConnection()
	    {
	        getConnection().close();
	    }

}
