package globalswrapper;

import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intersys.globals.NodeReference;

public class ListWorker {

	public ArrayList<JsonObject> GetList(FilterCondition condition, SortCondition sort, PageInfo requiredPage)
	{
		ArrayList<JsonObject> list = ApplyFilter(condition);
		list = SortItems(list, sort);
		list = PaginateItems(list, requiredPage);
		return list;
	}
	
	public ArrayList<JsonObject> ApplyFilter(FilterCondition condition)
	{
		ArrayList<JsonObject> list = new ArrayList<JsonObject>();
		
		String globalName = Utils.TableNameToGlobalsName(condition.TableName+SchemaManager.Instance().GetProjectPrefix(condition.ProjectId));  
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
			list.add(obj);
		}
		
		return list;
		
	}
	
	public ArrayList<JsonObject> SortItems(ArrayList<JsonObject> items, SortCondition condition)
	{
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
