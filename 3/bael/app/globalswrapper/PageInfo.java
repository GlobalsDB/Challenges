package globalswrapper;

import com.google.gson.JsonObject;

public class PageInfo {
	
	public int RecordsCountOnPage = 0;
	public int PageIndex = 0;
	
	public int GetLowIndex()
	{
		return RecordsCountOnPage * PageIndex;
	}
	
	public int GetHiIndex()
	{
		return RecordsCountOnPage * (1+PageIndex);
	}
	
	public static PageInfo getFromJsonObject(JsonObject object)
	{
		PageInfo result = new PageInfo();
		result.RecordsCountOnPage = object.get("recordsOnPage").getAsInt();
		result.PageIndex = object.get("pageIndex").getAsInt();
		
		return result;
	}
}
