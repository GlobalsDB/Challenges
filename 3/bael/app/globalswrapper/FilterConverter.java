package globalswrapper;

import com.google.gson.JsonObject;

public class FilterConverter {

	public FilterExpression Filter = null;
	public SortCondition Sort = null;
	public PageInfo Page = null;
	
	public static String Filter_Type = "filter";
	public static String Sort_Type = "sort";
	public static String PageInfo_Type = "pageInfo";
	
	public static FilterConverter Instance(Long projectId, String tableName, JsonObject object)
	{
		FilterConverter manager = new FilterConverter();
		
		if (object.has(Filter_Type)){
			manager.Filter = FilterExpression.getFromJsonObject(projectId, tableName, object.get(Filter_Type).getAsJsonArray());
		}
		if (object.has(Sort_Type)){
			manager.Sort = SortCondition.getFromJsonObject(projectId, tableName, object.get(Sort_Type).getAsJsonObject());
		}
		if (object.has(PageInfo_Type)){
			manager.Page = PageInfo.getFromJsonObject(object.get(PageInfo_Type).getAsJsonObject());
		}
		
		return manager; 
	}
	
}
