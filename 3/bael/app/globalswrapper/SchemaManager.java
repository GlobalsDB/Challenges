package globalswrapper;

import globalswrapper.DataTypesHelper.FieldType;

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
	public static String COLUMN_NAME = "column_name";
	public static String REQUIRED = "Required";
	public static String IS_INDEXED_FIELD = "has_index"; 

	private static SchemaManager _manager;

	public static SchemaManager Instance() {
		if (_manager == null) {
			_manager = Init();
		}
		return _manager;
	}

	private static SchemaManager Init() {
		return new SchemaManager();

	}

	// / Создает схему данных
	public void InitSchema(JsonObject schemaObject, Long projectId)
			throws Exception {
		ClearScheme(projectId);
		JsonArray tables = schemaObject.get(TABLES_NAME).getAsJsonArray();
		for (int i = 0; i < tables.size(); i++) {
			JsonObject table = tables.get(i).getAsJsonObject();
			CreateTable(table, projectId);
		}
	}

	public void ClearScheme(Long projectId) {
		String globalStorageName = GetGlobalSchemaName(projectId);
		NodeReference node = ConnectionManager.Instance().getConnection()
				.createNodeReference(globalStorageName);
		node.kill();
		node.close();
	}

	public String GetTableStorageGlobalsName() {
		return "UserTables";

	}

	public String GetProjectsStorageGlobalsName() {
		return "Projects";

	}

	public String GetProjectPrefix(Long projectId) {
		return "P" + projectId.toString();
	}

	public String GetFieldType(Long projectId, String tableName,
			String fieldName) {
		JsonObject tableInfo = ReadTable(tableName, projectId);
		JsonObject column = GetColumnInfo(tableInfo, fieldName);
		if (column != null) {
			return column.get(DATA_TYPE).getAsString();
		}

		return FieldType.STRING_TYPE.getTypeValue();
	}
	
	public JsonObject GetColumnByProjectIdAndTableName(Long projectId, String tableName, String fieldName) 
	{
		JsonObject tableInfo = ReadTable(tableName, projectId);
		return GetColumnInfo(tableInfo, fieldName);
	}
	
	public FieldType GetFieldTypeAsEnum(Long projectId, String tableName,
			String fieldName) {
		
		return FieldType.getType(GetFieldType(projectId, tableName, fieldName));
	}

	private JsonObject GetColumnInfo(JsonObject table, String columnName) {
		JsonArray columns = GetColumns(table);
		for (int i = 0; i < columns.size(); i++) {
			JsonObject column = columns.get(i).getAsJsonObject();
			if (column.get(COLUMN_NAME).getAsString().equalsIgnoreCase(columnName)) {
				return column;
			}

		}
		return null;
	}

	private JsonArray GetColumns(JsonObject table) {
		return table.get(COLUMNS_NAME).getAsJsonArray();
	}
	
	public static Boolean IsColumnIndexed(JsonObject column)
	{
		return column.has(IS_INDEXED_FIELD) && (column.get(IS_INDEXED_FIELD).getAsBoolean() == true);
	}

	/*
	 * // Создаем информацию о схеме данных через типовую структуру глобалов
	 * public void CreateTableOld(JsonObject table) { NodeReference node =
	 * ConnectionManager
	 * .Instance().getConnection().createNodeReference(GetTableStorageGlobalsName
	 * ()); String tableName = table.get(TABLE_NAME).getAsString(); JsonArray
	 * columns = table.get(COLUMNS_NAME).getAsJsonArray();
	 * node.set(columns.size(), tableName, "ColumnsCount");
	 * 
	 * for (int i=0; i<columns.size(); i++) { JsonObject column =
	 * columns.get(i).getAsJsonObject(); String columnName =
	 * column.get("column_name").getAsString(); node.set("", tableName,
	 * COLUMNS_NAME, columnName); node.set(column.get(DATA_TYPE).getAsString(),
	 * tableName, COLUMNS_NAME, columnName, COLUMN_TYPE); node.set(0, tableName,
	 * COLUMNS_NAME, columnName, REQUIRED); } node.close(); }
	 */

	// Создаем информацию о схеме данных через типовую структуру глобалов
	public void CreateTable(JsonObject table, Long projectId) {
		String SchemaGlobalName = GetGlobalSchemaName(projectId);

		NodeReference node = ConnectionManager.Instance().getConnection()
				.createNodeReference(SchemaGlobalName);
		String tableName = table.get(TABLE_NAME).getAsString();
		JsonArray columns = GetColumns(table);
		node.set(columns.toString(), tableName, COLUMNS_NAME);
		node.close();
	}

	public JsonObject ReadSchema(Long projectId) {
		String SchemaGlobalName = GetGlobalSchemaName(projectId);
		NodeReference node = ConnectionManager.Instance().getConnection()
				.createNodeReference(SchemaGlobalName);

		JsonObject schema = new JsonObject();
		schema.addProperty("project_id", projectId);

		JsonArray tables = new JsonArray();
		String subscript = "";
		while (true) {
			subscript = node.nextSubscript(subscript);
			if (subscript.length() == 0)
				break;

			JsonObject tableInfo = ReadTable(subscript, projectId);
			tables.add(tableInfo);
		}
		schema.add("tables", tables);

		return schema;

	}

	public Boolean IsSchemaExist(Long projectId) {
		Boolean isExist = false;
		String SchemaGlobalName = GetGlobalSchemaName(projectId);
		NodeReference node = ConnectionManager.Instance().getConnection()
				.createNodeReference(SchemaGlobalName);
		isExist = node.hasSubnodes();
		node.close();
		return isExist;

	}

	public String GetGlobalSchemaName(Long projectId) {
		return GetTableStorageGlobalsName() + GetProjectPrefix(projectId);
	}

	// Считываем схему
	public JsonObject ReadTable(String tableName, Long projectId) {
		String SchemaGlobalName = GetGlobalSchemaName(projectId);
		JsonObject object = new JsonObject();
		object.addProperty(TABLE_NAME, tableName);
		NodeReference node = ConnectionManager.Instance().getConnection()
				.createNodeReference(SchemaGlobalName);
		node.setSubscriptCount(0);
		node.appendSubscript(tableName);

		String nodeValue = "";
		String subscript = "";
		do {
			subscript = node.nextSubscript(subscript);
			if (subscript.length() > 0) {
				nodeValue = node.getObject(subscript).toString();
				JsonElement o = new com.google.gson.JsonParser()
						.parse(nodeValue);
				object.add(subscript, o);
			}
		} while (subscript.length() > 0);

		node.close();
		return object;

	}
	
	public static String GetGlobalDataByTableNameAndProjectId(String tableName, Long projectId)
	{
		tableName = tableName + SchemaManager.Instance().GetProjectPrefix(projectId);
		String globalName = Utils.TableNameToGlobalsName(tableName);
		return globalName;
	}
	
	public static String GetGlobalIndexByTableNameAndProjectId(String tableName, Long projectId)
	{
		tableName = tableName + SchemaManager.Instance().GetProjectPrefix(projectId);
		String globalName = Utils.TableNameToGlobalsIndexName(tableName);
		return globalName;
	}
    
    
}
