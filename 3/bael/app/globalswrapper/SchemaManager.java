package globalswrapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intersys.globals.NodeReference;

public class SchemaManager {
	public static String TABLES_NAME = "tables";
	public static String COLUMNS_NAME = "columns";
	public static String DATA_TYPE = "type";
	public static String TABLE_NAME = "table_name";
	public static String COLUMN_TYPE = "ColumnType";
	public static String REQUIRED = "Required";
	
	
	public static SchemaManager _manager;
	
	public static SchemaManager Instance()
	{
		if (_manager == null)
		{
			_manager = Init();
		}
		return _manager;
	}
	
	private static SchemaManager Init()
	{
		return new SchemaManager();
		
	}
	
	
	/// Создает схему данных
	public void InitSchema(JsonObject schemaObject, Long projectId) throws Exception
	{
		 JsonArray tables = schemaObject.get(TABLES_NAME).getAsJsonArray();
		 for (int i=0; i<tables.size(); i++)
		 {
			JsonObject table = tables.get(i).getAsJsonObject();
			 CreateTable(table, projectId);
		 }
	}
	
	public Boolean ClearScheme()
	{
		return true;
	}
	

	public String GetTableStorageGlobalsName()
	{
		return "UserTables";
		
	}
	
	public String GetProjectsStorageGlobalsName()
	{
		return "Projects";
		
	}
	
	public String GetProjectPrefix(Long projectId)
	{
		return "P"+projectId.toString();
	}
	
	
	/*
	// Создаем информацию о схеме данных через типовую структуру глобалов
	public void CreateTableOld(JsonObject table)
	{
		NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(GetTableStorageGlobalsName());
	    String tableName = table.get(TABLE_NAME).getAsString();
	    JsonArray columns = table.get(COLUMNS_NAME).getAsJsonArray();
	    node.set(columns.size(), tableName, "ColumnsCount");
	   
	    for (int i=0; i<columns.size(); i++)
	    {
	    	JsonObject column = columns.get(i).getAsJsonObject();
	    	String columnName = column.get("column_name").getAsString();
	    	node.set("", tableName, COLUMNS_NAME, columnName);
	    	node.set(column.get(DATA_TYPE).getAsString(), tableName,   COLUMNS_NAME,  columnName, COLUMN_TYPE);
	    	node.set(0, tableName,  COLUMNS_NAME,  columnName, REQUIRED);
	    }
	    node.close();
	}*/
	
	
	// Создаем информацию о схеме данных через типовую структуру глобалов
	public void CreateTable(JsonObject table, Long projectId)
	{
		String SchemaGlobalName = GetTableStorageGlobalsName() + GetProjectPrefix(projectId);
		
		NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(SchemaGlobalName);
	    String tableName = table.get(TABLE_NAME).getAsString();
	    JsonArray columns = table.get(COLUMNS_NAME).getAsJsonArray();
	    node.set(columns.toString(), tableName, COLUMNS_NAME);
	    node.close();
	}
	
	public JsonObject ReadSchema(Long projectId)
	{
		
		JsonObject schema = new JsonObject();
		return schema;
		
	}
	// Считываем схему
	public JsonObject ReadTable(String tableName, Long projectId)
	{
		String SchemaGlobalName = GetTableStorageGlobalsName() + GetProjectPrefix(projectId);
		JsonObject object = new JsonObject();
		object.addProperty(TABLE_NAME, tableName);
		NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(SchemaGlobalName);
        node.setSubscriptCount(0);
        node.appendSubscript(tableName);

        String nodeValue = "";
        String subscript = "";
        do {
            subscript = node.nextSubscript(subscript);
            if (subscript.length() > 0) 
            {
            	nodeValue = node.getObject(subscript).toString();
            	JsonElement o = new com.google.gson.JsonParser().parse(nodeValue); 
            	object.add(subscript, o);
            }
         }while (subscript.length() > 0);
      
        //System.out.println(object.toString());
       
	    node.close();
	    return object;
	 
	}
}
