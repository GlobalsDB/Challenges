package globalswrapper;

import globalswrapper.DataTypesHelper.FieldType;
import globalswrapper.SortCondition.Order;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FilterExpression {
	
	public ArrayList<FilterCondition> conditions;
	public ArrayList<FilterCondition> notIndexedConditions;
	// for apply on index. (if field is indexed and condtion type is effective for indexing (<,>,<=,>=,=)
	public HashMap<String, ArrayList<FilterCondition>> IndexedCondtionsByFieldName;
	
	public FilterExpression(ArrayList<FilterCondition> conditions, Long projectId)
	{
		if (conditions != null)
		{
			this.conditions = conditions;
		}	
		FillDataTypesInfo(projectId);
	}
	
	
	
	// prepare filter expression - split on index and not indexed part
	private void FillDataTypesInfo(Long projectId)
	{
		IndexedCondtionsByFieldName = new HashMap<String, ArrayList<FilterCondition>>();
		
		if (conditions == null)
			return;
		notIndexedConditions = new ArrayList<FilterCondition>();
		for (int i=0;i<conditions.size(); i++)
		{
			FilterCondition condition = conditions.get(i);
			JsonObject column = SchemaManager.Instance().GetColumnByProjectIdAndTableName(projectId, condition.TableName, condition.FieldName);

			if (column != null)
			{
				condition.DataType =  FieldType.getType(column.get(SchemaManager.DATA_TYPE).getAsString());
				condition.IsIndexed =  SchemaManager.IsColumnIndexed(column);
				if (condition.IsIndexed && condition.ConditionTypeIsAppropriateForIndexing())
				{
					AddToIndexExpression(condition);
				}
				else
				{
					AddToNotIndexedExpression(condition);
				}
			}
		}
		
	}
	
	
	
	private void AddToNotIndexedExpression(FilterCondition condition)
	{
		notIndexedConditions.add(condition);
	}
	
	private void AddToIndexExpression(FilterCondition condition)
	{
		ArrayList<FilterCondition> byname = IndexedCondtionsByFieldName.get(condition.FieldName);
		if (byname == null)
		{
			byname = new ArrayList<FilterCondition>();
		}
		byname.add(condition);
		IndexedCondtionsByFieldName.put(condition.FieldName, byname);
	}
	
	
	
	public Boolean IsValid(JsonObject record)
	{
		if (conditions == null)
			return true;

		Boolean conditionIsValid;
		for (int i=0;i<conditions.size(); i++)
		{
			FilterCondition condition = conditions.get(i);
			if (condition != null)
			{
				conditionIsValid = condition.IsValid(record);
				if (condition.IsNegative)
				{
					conditionIsValid = !conditionIsValid;
				}
			
				if (!conditionIsValid)
				{
					return false;
				}
			}
		}
		
		return true;
		
	}
	
	public static FilterExpression getFromJsonObject(Long projectId, String tableName, JsonArray jsonList)
	{
		ArrayList conditions = new ArrayList<FilterCondition>();
	
		for (int i=0; i<jsonList.size(); i++)
		{
			FilterCondition condition = FilterCondition.getFromJsonObject(tableName, jsonList.get(i).getAsJsonObject());
			conditions.add(condition);
		}
		
		FilterExpression result = new FilterExpression(conditions, projectId);
	
		return result;
	}	
}
