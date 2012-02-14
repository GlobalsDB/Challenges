package globalswrapper;

public class FilterCondition {
	public FilterCondition AND;
	public FilterCondition OR;
	public boolean IsNegative;
	
	public String FieldName;
	public String TableName;
	public Long ProjectId;
	
	public ConditionType CondType;
	public Object FilterValue;

}
