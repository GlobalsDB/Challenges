package globalswrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intersys.globals.NodeReference;

public class ListWorker {

	public Long ProjectId;
	public String TableName;
	
	public ListWorker(Long projectId, String tableName)
	{
		ProjectId = projectId;
		TableName = tableName;
	}
	
	public ArrayList<JsonObject> GetList(FilterExpression expression, SortCondition sort, PageInfo requiredPage)
	{
		ArrayList<JsonObject> list = ApplyFilter2(expression);
		list = SortItems(list, sort);
		list = PaginateItems(list, requiredPage);
		return list;
	}

	public ArrayList<JsonObject> ApplyFilter2(FilterExpression expression)
	{
		if (expression == null)
		{
			return FullScan(expression);
		}
		else
		{
			if (expression.IndexedCondtionsByFieldName.isEmpty())
			{
				return FullScan(expression);
			}
			
			ArrayList<Long> setToScan = ExtractSubSetByIndexedCondtions(expression);
			return ScanSet(expression, setToScan);
		}
	}
	
	// Apply on indexed fields conditions
	private ArrayList<Long> ExtractSubSetByIndexedCondtions(FilterExpression expression)
	{
		ArrayList<Long> setToScan = null;
		String globalName =  SchemaManager.GetGlobalIndexByTableNameAndProjectId(this.TableName, this.ProjectId);
		NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(globalName);
		
		for (Map.Entry<String, ArrayList<FilterCondition>> entry : expression.IndexedCondtionsByFieldName.entrySet()) 
		{ 
			setToScan = ExtractFromIndex(node, setToScan, expression, entry.getValue(), entry.getKey());
			// we supply a only AND filter at the moment, that's why if founded count = 0 - we can stop.
		    if (setToScan.size() == 0)
		    	return setToScan;
		} 
		return setToScan;
	}
	
	private ArrayList<FilterCondition> PrepareIndexRangeByConditions(ArrayList<FilterCondition> conditions)
	{
		ArrayList<FilterCondition> range = new ArrayList<FilterCondition>();
		FilterCondition left = null;
		FilterCondition right = null;
		
		for (int i=0; i<conditions.size(); i++)
		{
			FilterCondition condition = conditions.get(i);
			String conditionType = condition.CondType;
			// if equal => left = right, if left = right already the check values - if different - then range = null
			if (conditionType.equalsIgnoreCase(ConditionType.EQUAL))
			{
				// we've already meet 
				if (left != null && left.CondType.equalsIgnoreCase(ConditionType.EQUAL))
				{
					// we have a error 1 && 0 = 0
					if (!left.FilterValue.toString().equalsIgnoreCase(condition.FilterValue.toString()))
					{
						return null;
					}
				}
				else
				{
					left = condition;
					right = condition;
				}
				continue;
			}
			
			// if left = right, we have not to add a conditions
			if (left.equals(right))
			{
				continue;
			}
			
			if (conditionType.equalsIgnoreCase(ConditionType.GRETATEOREQUAL) || condition.CondType.equalsIgnoreCase(ConditionType.GREATER))
			{
				// if new condition value is greater, or values are equal and the condition is stronger - let's replace the condition
				int comparedValues = left.CompareValues(condition);
				if (comparedValues == -1 || (comparedValues == 0 && left.CompareConditionTypes(condition) == -1))
				{
					left = condition;
				}
			}

			if (conditionType.equalsIgnoreCase(ConditionType.LESOREQUAL) || condition.CondType.equalsIgnoreCase(ConditionType.LESS))
			{
				// if new condition value is greater, or values are equal and the condition is stronger - let's replace the condition
				int comparedValues = right.CompareValues(condition);
				if (comparedValues == 1 || (comparedValues == 0 && right.CompareConditionTypes(condition) == -1))
				{
					right = condition;
				}
			}
		}
		
		
		range.add(0, left);
		range.add(1, right);
		return range;
	}
	
	/// extract from index values
	private ArrayList<Long> ExtractFromIndex(NodeReference node, ArrayList<Long> previousSetToScan, FilterExpression expression, ArrayList<FilterCondition> conditions, String indexName)
	{
		ArrayList<Long> setToScan = new ArrayList<Long>();
		

		// prepare range
		ArrayList<FilterCondition> range = PrepareIndexRangeByConditions(conditions);
		if (range == null)
			return setToScan;
		
		
		FilterCondition left = range.get(0);
		FilterCondition right = range.get(1);
	
		String leftsubscript = "";
		if (left != null)
		{
			leftsubscript = left.FilterValue.toString();
		}
		
		
		String rightsubscript = "";
		if (right != null)
		{
			rightsubscript = right.FilterValue.toString();
		}
		
		rightsubscript = IndexManager.ConvertToIndex(rightsubscript);
		leftsubscript = IndexManager.ConvertToIndex(leftsubscript);
	
		String key = "";
		while (true)
		{
			key = node.nextSubscript(indexName, leftsubscript, key);
			if (key.equals(""))
			{
				leftsubscript = node.nextSubscript(indexName, leftsubscript);
				key = node.nextSubscript(indexName, leftsubscript, key);
			}
			
			if (key.equals(""))
			{
				break;
			}
			
			if (leftsubscript.equals(rightsubscript))
			{
				break;
			}
			
			
			Long parsedKey = Long.parseLong(key);
			if (previousSetToScan == null || (!previousSetToScan.contains(parsedKey)))
				continue;
			
			
			setToScan.add(parsedKey);
		}
		
		return setToScan;
	}
	
	private String ConvertFromIndex(String indexValue)
	{
		return indexValue.substring(1);
	}
	
	/// filter sub set
	private ArrayList<JsonObject> ScanSet(FilterExpression expression, ArrayList<Long> setToScan)
	{
		ArrayList<JsonObject> list = new ArrayList<JsonObject>();
		String globalName = Utils.TableNameToGlobalsName(TableName+SchemaManager.Instance().GetProjectPrefix(ProjectId));  
		NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(globalName);
		
		for (int i=0;i<setToScan.size(); i++)
		{
			String nodeValue = node.getObject(setToScan.get(i), "JSON").toString();
			JsonObject obj = new JsonParser().parse(nodeValue).getAsJsonObject();
			if (expression == null)
			{
				list.add(obj);
			}
			else
			{
				if (expression.IsValid(obj))
				{
					list.add(obj);
				}
			}
		}
		return list;
		
	}
	
	/// full scan
	private ArrayList<JsonObject> FullScan(FilterExpression expression)
	{
		ArrayList<JsonObject> list = new ArrayList<JsonObject>();
		
		String globalName = Utils.TableNameToGlobalsName(TableName+SchemaManager.Instance().GetProjectPrefix(ProjectId));  
		NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(globalName);
		
		Long key = (long)0;
		while (true)
		{
			String strKey = node.nextSubscript(key);
			if (strKey.equals(""))
				break;
			key = Long.parseLong(strKey);
			String nodeValue = node.getObject(key, "JSON").toString();
			JsonObject obj = new JsonParser().parse(nodeValue).getAsJsonObject();

			
			if (expression == null)
			{
				list.add(obj);
			}
			else
			{
				if (expression.IsValid(obj))
				{
					list.add(obj);
				}
			}
						
		}
		
		return list;
		
	}
	
	
	
	public ArrayList<JsonObject> SortItems(ArrayList<JsonObject> items, SortCondition condition)
	{
		if (condition != null)
			condition.sort(items);
		return items;
	}
	
	public ArrayList<JsonObject> PaginateItems(ArrayList<JsonObject> items, PageInfo requiredPage)
	{
		if (requiredPage == null)
			return items;
		
		int hiIndex = requiredPage.GetHiIndex();
		if (hiIndex == 0)
			return items;
		
		int lowIndex = requiredPage.GetLowIndex();
		
		hiIndex = Math.min(hiIndex, items.size());
		lowIndex = Math.min(lowIndex, items.size());
		return new ArrayList<JsonObject>(items.subList(lowIndex, hiIndex));
	
	}
	
	public void WriteList(ArrayList<JsonObject> items)
	{
		for (int i=0; i<items.size(); i++)
		{
			System.out.println(items.get(i));
		}
		
	}
}
