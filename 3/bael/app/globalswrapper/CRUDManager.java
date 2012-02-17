package globalswrapper;


import globalswrapper.*;
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
    	String globalName = SchemaManager.GetGlobalDataByTableNameAndProjectId(tableName, projectId);
        NodeReference node = null;
        try
        {
        	
        	node = connectionManager.getConnection().createNodeReference(globalName);
        	connectionManager.getConnection().startTransaction();
        	node.increment(1);
	        object.addProperty(ID, node.getString());
	        node.set(object.toString(), node.getLong(), "JSON");
	        
	        IndexManager manager = new IndexManager(tableName, projectId);
	        manager.AfterCreateRecord(object);
	        
	        connectionManager.getConnection().commit();
        }
        catch (Exception ex) 
        {
        	connectionManager.getConnection().rollback();
        	throw ex;
        }
        
         
        return object;
    }
    
    public JsonObject Read(Long projectId, String tableName, long id) throws Exception
    {
        String globalName = SchemaManager.GetGlobalDataByTableNameAndProjectId(tableName, projectId);
        NodeReference node = connectionManager.getConnection().createNodeReference(globalName);
        node.setSubscriptCount(0);
        String nodeValue = node.getObject(id, "JSON").toString(); 
        JsonObject response = new com.google.gson.JsonParser().parse(nodeValue).getAsJsonObject(); 
    	return response;
    }
    
    
    public JsonObject Update(Long projectId, String tableName, JsonObject object) throws Exception
    {
    
        String globalName = SchemaManager.GetGlobalDataByTableNameAndProjectId(tableName, projectId);
        
        if (!object.has(ID)){
        	return null;
        }
        
        String sid = object.get(ID).getAsString();
        Long id = Long.parseLong(sid);
        try
        {
        	connectionManager.getConnection().startTransaction();

            NodeReference node =   connectionManager.getConnection().createNodeReference(globalName);
            node.setSubscriptCount(0);
            JsonObject oldRecord = Read(projectId, tableName, id);
            
            IndexManager manager = new IndexManager(tableName, projectId);
	        manager.OnUpdateRecord(oldRecord, object);
	        node.set(object.toString(), id, "JSON"); 
            
            connectionManager.getConnection().commit();
        }
        catch (Exception ex) 
        {
        	connectionManager.getConnection().rollback();
        	throw ex;
        }
        
        return object;

    }
    
    
    public Boolean Delete(Long projectId, String tableName, long id) throws Exception
    {

        String globalName = SchemaManager.GetGlobalDataByTableNameAndProjectId(tableName, projectId);
        
        try
        {
        	connectionManager.getConnection().startTransaction();

            NodeReference node = connectionManager.getConnection().createNodeReference(globalName);

            JsonObject deletedRecord = Read(projectId, tableName, id);
            IndexManager manager = new IndexManager(tableName, projectId);
	        manager.BeforeDeletingRecord(deletedRecord);

            node.setSubscriptCount(0);
            node.appendSubscript(id);
            node.kill();       

        	connectionManager.getConnection().commit();
        }
        catch (Exception ex) 
        {
        	connectionManager.getConnection().rollback();
        	throw ex;
        }
        
        return true;
    }
}
