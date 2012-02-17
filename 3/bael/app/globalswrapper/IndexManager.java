package globalswrapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import globalswrapper.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intersys.globals.NodeReference;


public class IndexManager {
	
	private JsonObject TableInfo;
	private String TableName;
	private ArrayList<String> IndexedColumnNames;
	private Long ProjectId;
	
	public IndexManager(String tableName, Long projectId)
	{
		
		TableInfo = SchemaManager.Instance().ReadTable(tableName, projectId);
		TableName = TableInfo.get(SchemaManager.TABLE_NAME).getAsString();
		this.ProjectId = projectId;
		
		
		FillIndexedColumnNamesFromTableInfo();
	}

	
	private void FillIndexedColumnNamesFromTableInfo()
	{
		IndexedColumnNames = new ArrayList<String>();
		JsonArray columns = TableInfo.getAsJsonArray(SchemaManager.COLUMNS_NAME);
		
		
		for (int i=0; i<columns.size(); i++)
		{
			JsonObject column = columns.get(i).getAsJsonObject();
			if (SchemaManager.IsColumnIndexed(column))
			{
				if (column.has(SchemaManager.COLUMN_NAME))
				{
					IndexedColumnNames.add(column.get(SchemaManager.COLUMN_NAME).getAsString());
				}
			}
		}
	}
	
	
	
	public void AfterCreateRecord(JsonObject newRecord)
	{
		NodeReference node = GetNode();
		WriteObjectValues(node, newRecord);
		node.close();
	}
	
	private void WriteObjectValues(NodeReference node, JsonObject record)
	{
		UpdateIndexForObject(node, record, true);
	}
	
	private void KillObjectValues(NodeReference node, JsonObject record)
	{
		UpdateIndexForObject(node, record, false);
	}
	
	private void UpdateIndexForObject(NodeReference node, JsonObject record, Boolean insertMode)
	{
		
		String fieldName;
		String fieldValue;
		if (!record.has("Id"))
		{
			return;
		}
		
		Long Id = record.get("Id").getAsLong();
		for (int i=0; i<IndexedColumnNames.size(); i++)
		{
			fieldName = IndexedColumnNames.get(i);
			fieldValue = record.get(fieldName).getAsString();
			
			if (insertMode)
			{
				SetIndexValue(node, Id, fieldName, fieldValue);
			}
			else
			{
				KillIndexValue(node, Id, fieldName, fieldValue);
			}
		}
	}
	
	
	
	static String ConvertToIndex(String fieldValue)
	{
		return " ".concat(fieldValue);
	}
	

	
	public void OnUpdateRecord(JsonObject oldRecord, JsonObject newRecord)
	{
		NodeReference node = GetNode();
		RemoveUnchangedIndexValues(oldRecord, newRecord);
		KillObjectValues(node, oldRecord);
		WriteObjectValues(node, newRecord);
		node.close();
		
	}

	// some kind of optimization
	private void RemoveUnchangedIndexValues(JsonObject oldRecord, JsonObject newRecord)
	{
		String fieldName;
		String oldFieldValue;
		String newFieldValue;
		
		int size = IndexedColumnNames.size();
		for (int i=size-1; i>=0; i--)
		{
			fieldName = IndexedColumnNames.get(i);
			oldFieldValue = oldRecord.get(fieldName).getAsString();
			newFieldValue = newRecord.get(fieldName).getAsString();
			
			if (oldFieldValue.equalsIgnoreCase(newFieldValue))
			{
				IndexedColumnNames.remove(i);
			}
		}
	}
	
	public void BeforeDeletingRecord(JsonObject deletedRecord)
	{
		NodeReference node = GetNode();
		KillObjectValues(node, deletedRecord);
		node.close();
	}
	
	public NodeReference GetNode()
	{
		String globalName = GetIndexGlobalName();
		NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(globalName);
		return node;
	}
	
	public String GetIndexGlobalName()
	{
		return SchemaManager.GetGlobalIndexByTableNameAndProjectId(this.TableName, this.ProjectId);
	}
	
	public void SetIndexValue(NodeReference indexNode, Long objectId, String indexFieldName, String fieldValue)
	{
		indexNode.set("", indexFieldName, ConvertToIndex(fieldValue), objectId);
	}
	
	public void KillIndexValue(NodeReference indexNode, Long objectId, String indexFieldName, String fieldValue)
	{
		indexNode.kill(indexFieldName, ConvertToIndex(fieldValue), objectId);
	}
	
	
	
		
		
	
	
	
	
}
