package globalswrapper;

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
	

}
