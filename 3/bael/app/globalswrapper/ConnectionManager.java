package globalswrapper;


import java.util.logging.Level;
import java.util.logging.Logger;

import com.intersys.globals.*;
public class ConnectionManager {
	
   private static ConnectionManager manager;
    public static ConnectionManager Instance()
    {
        if (ConnectionManager.manager == null)
        {
            ConnectionManager.manager = new ConnectionManager();            
        }
        
        return ConnectionManager.manager;
    }
    
    
    private com.intersys.globals.Connection _connection;

    public com.intersys.globals.Connection getConnection() 
    {
        if (_connection == null)
        {
            try
            {
                 _connection =  (com.intersys.globals.Connection) ConnectionContext.getConnection();
                if (!_connection.isConnected())
                {
                    _connection.connect("USER","_SYSTEM","DATA");
                }
            }
            catch (GlobalsException ex)
            {
            	System.out.println(ex.toString());
            }
            catch (Exception ex2)
            {
            	System.out.println(ex2.toString());
            }
        }
        return _connection;
    }
    
    public void CloseConnection()
    {
        try {
            getConnection().close();
        } catch (Exception ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
