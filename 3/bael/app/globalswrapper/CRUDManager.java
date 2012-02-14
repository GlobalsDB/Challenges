package globalswrapper;

import java.lang.reflect.Field;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intersys.globals.*;



public class CRUDManager {
	
	   
    private static CRUDManager manager;
    private static ConnectionManager connectionManager;
    public static CRUDManager Instance()
    {
        if (manager == null)
        {
            manager = Init();
        }
        return manager;
    }
    
    private static CRUDManager Init()
    {
        CRUDManager _manager = new CRUDManager();
        _manager.connectionManager = ConnectionManager.Instance();
        return _manager;
    }
    
    public static String ID = "Id";
    public JsonObject Create(Long projectId, String tableName, JsonObject object) throws Exception
    {
    	tableName = tableName + SchemaManager.Instance().GetProjectPrefix(projectId);
        String globalName = Utils.TableNameToGlobalsName(tableName);
        
        /*if (object.has(ID))
        {
        	
        }*/
        NodeReference node = null;
        node = connectionManager.getConnection().createNodeReference(globalName);
        node.increment(1);
        object.addProperty(ID, node.getString());
        node.set(object.toString(), node.getLong(), "JSON");     
         
        return object;
    }
    
    public JsonObject Read(Long projectId, String tableName, long id) throws Exception
    {
    	tableName = tableName + SchemaManager.Instance().GetProjectPrefix(projectId);
        String globalName = Utils.TableNameToGlobalsName(tableName);
        
        
        NodeReference node =   connectionManager.getConnection().createNodeReference(globalName);
        node.setSubscriptCount(0);
        node.appendSubscript(id);
        String nodeValue = node.getObject("JSON").toString();
        JsonObject response = new com.google.gson.JsonParser().parse(nodeValue).getAsJsonObject(); 
    	return response;
    }
    
    
    public JsonObject Update(Long projectId, String tableName, JsonObject object) throws Exception
    {
    	tableName = tableName + SchemaManager.Instance().GetProjectPrefix(projectId);
        String globalName = Utils.TableNameToGlobalsName(tableName);
        
        if (!object.has(ID))
        {
        	
        	return null;
        }
        
        String sid = object.get(ID).toString();
        Long id = Long.parseLong(sid);
        
        
        NodeReference node =   connectionManager.getConnection().createNodeReference(globalName);
        node.setSubscriptCount(0);
        node.appendSubscript(id);
        
        node.set(object.toString(), node.getLong(), "JSON"); 
        return object;

    }
    
    
    public Boolean Delete(Long projectId, String tableName, long id) throws Exception
    {
    	tableName = tableName + SchemaManager.Instance().GetProjectPrefix(projectId);
        String globalName = Utils.TableNameToGlobalsName(tableName);
        NodeReference node =   connectionManager.getConnection().createNodeReference(globalName);
        node.setSubscriptCount(0);
        node.appendSubscript(id);
        node.kill(); 
        return true;
    }
    
    
    

}
