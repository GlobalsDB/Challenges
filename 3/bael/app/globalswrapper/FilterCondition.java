package globalswrapper;

import globalswrapper.DataTypesHelper.FieldType;
import globalswrapper.ConditionType;
import java.util.Date;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FilterCondition {
	
	public Boolean IsIndexed;
	public Boolean IsNegative = false;
	public String FieldName;
	public FieldType DataType;
	public String TableName;
	public String CondType;
	public Object FilterValue;

	public static FilterCondition getFromJsonObject(String tableName, JsonObject object)
	{
		FilterCondition result = new FilterCondition();
		result.TableName = tableName;
		result.FieldName = object.get("fieldName").getAsString();
		result.CondType = object.get("conditionType").getAsString();
		result.FilterValue = object.get("filterValue").getAsString();
			
		return result;
	}
	
	public Boolean ConditionTypeIsAppropriateForIndexing()
	{
		return false;
		/*
		if (CondType.equalsIgnoreCase(ConditionType.CONTAINS))
		{
			return false;
		}
		
		if (CondType.equalsIgnoreCase(ConditionType.NOTEQUAL))
		{
			return false;
		}
		
		return true;*/
	}
	
	public Boolean IsValid(JsonObject record)
	{
		JsonElement nodeValue = record.get(FieldName);
		switch (DataType)
		{
			case STRING_TYPE: 
					return ApplyStringFilter(nodeValue.getAsString());
			case DATE_TYPE: Date date = DataTypesHelper.StringToDate(nodeValue.getAsString());
					return ApplyDateFilter(date);
			case BOOLEAN_TYPE: 
					return ApplyBooleanFilter(nodeValue.getAsBoolean());
			default: 
					return true;
		}
	}
	

	// coming soon
	private Boolean ApplyDateFilter(Date date)
	{
		// 1 > 0 = -1 <
		Date filterValue = DataTypesHelper.StringToDate(FilterValue.toString());
		
		if (CondType.equalsIgnoreCase(ConditionType.GRETATEOREQUAL))
		{
			return (date.compareTo(filterValue) >= 0);
		}
		
		if (CondType.equalsIgnoreCase(ConditionType.GREATER))
		{
			return (date.compareTo(filterValue) > 0);
		}

		if (CondType.equalsIgnoreCase(ConditionType.LESOREQUAL))
		{
			return (date.compareTo(filterValue) >= 0);
		}
		
		if (CondType.equalsIgnoreCase(ConditionType.LESS))
		{
			return (date.compareTo(filterValue) > 0);
		}

		if (CondType.equalsIgnoreCase(ConditionType.EQUAL))
		{
			return (date.compareTo(filterValue) == 0);
		}
		
		return false;
	}
	
	private Boolean ApplyBooleanFilter(Boolean nodeValue)
	{
		Boolean filterValue = Boolean.parseBoolean(FilterValue.toString());
		
		if (CondType.equalsIgnoreCase(ConditionType.EQUAL))
		{
			return nodeValue == filterValue;
		}
		
		if (CondType.equalsIgnoreCase(ConditionType.NOTEQUAL))
		{
			return nodeValue != filterValue;
		}
		
	
		return false;
		
	}
	
	
	private Boolean ApplyStringFilter(String nodeValue)
	{
		if (CondType.equalsIgnoreCase(ConditionType.EQUAL))
		{
			return StringIsEqual(nodeValue);
		}
	
		if (CondType.equalsIgnoreCase(ConditionType.CONTAINS))
		{
			Boolean res = StringContains(nodeValue);
			return StringContains(nodeValue);
		}
		return false;
		
	}
	
	private Boolean StringContains(String nodeValue)
	{
		return nodeValue.toUpperCase().contains(FilterValue.toString().toUpperCase()); 
	}
	
	
	private Boolean StringIsEqual(String nodeValue)
	{
		Boolean result = nodeValue.equalsIgnoreCase(FilterValue.toString()); 
 		return  result;
	}
	
	// Which condition value is greater?
	public int CompareValues(FilterCondition comparedCondition)
	{
		switch (DataType)
		{
			case STRING_TYPE: 
				FilterValue.toString().compareTo((String) comparedCondition.FilterValue);
			case DATE_TYPE: 
				FilterValue.toString().compareTo((String) comparedCondition.FilterValue);
			case BOOLEAN_TYPE:
				Boolean thisValue = Boolean.parseBoolean(FilterValue.toString());
				return thisValue.compareTo(Boolean.parseBoolean(comparedCondition.FilterValue.toString()));
			default: 
				return 0;
		}
	}
	
	// Which condition type is stronger?
	public int CompareConditionTypes(FilterCondition comparedCondition)
	{
		if (CondType.equalsIgnoreCase(comparedCondition.CondType))
			return 0;
		
		if ((CondType.equalsIgnoreCase(ConditionType.GREATER) 
			|| CondType.equalsIgnoreCase(ConditionType.GRETATEOREQUAL))
			&&
			(comparedCondition.CondType.equalsIgnoreCase(ConditionType.GREATER) ||  
				comparedCondition.CondType.equalsIgnoreCase(ConditionType.GRETATEOREQUAL)))
		{
			if (CondType.equalsIgnoreCase(ConditionType.GREATER))
			{
				return 1;
			}
			else 
			{
				return -1; 
			}
		}
		
		
		if ((CondType.equalsIgnoreCase(ConditionType.LESS) 
			|| CondType.equalsIgnoreCase(ConditionType.LESOREQUAL))
			&&
			(comparedCondition.CondType.equalsIgnoreCase(ConditionType.LESS) ||  
				comparedCondition.CondType.equalsIgnoreCase(ConditionType.LESOREQUAL)))
		{
			if (CondType.equalsIgnoreCase(ConditionType.LESS))
			{
				return 1;
			}
			else 
			{
				return -1; 
			}
		}
		
		return -2;
		
	}
	
}
