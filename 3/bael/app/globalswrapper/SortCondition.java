package globalswrapper;

import globalswrapper.DataTypesHelper.FieldType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.hibernate.type.descriptor.java.DataHelper;

import com.google.gson.JsonObject;
import com.ning.http.util.DateUtil;

public class SortCondition {

	public String FieldName;
	private String _fieldType;
	
	Order order = Order.asc;

	public enum Order {
		asc(1), desc(-1);
		private int orderValue;
		
	    private Order(int value) {
	        orderValue = value;
	    }
	    
	    public int getTypeValue() {
	        return orderValue;
	    }
	}

	public static SortCondition getFromJsonObject(Long projectId, String tableName, JsonObject object)
	{
		SortCondition result = new SortCondition();
		result.FieldName = object.get("fieldName").getAsString();
		result.order = Order.valueOf(object.get("order").getAsString());
		result._fieldType =  SchemaManager.Instance().GetFieldType(projectId, tableName, result.FieldName);
		return result;
	}
	
	public void sort(ArrayList<JsonObject> items) {
		
		Collections.sort(items, getComparator());
	}

	public Comparator<JsonObject> getComparator() {
		FieldType type = FieldType.getType(_fieldType);
		switch (type) {
			case STRING_TYPE: return new StringComparator(); 
			case INT_TYPE: return new IntComparator();
			case DATE_TYPE: return new DateComparator();
			case BOOLEAN_TYPE: return new BooleanComparator();
			default: throw new RuntimeException("unknown type: "+_fieldType);		
		}
	}

	class StringComparator implements Comparator<JsonObject> {
		public int compare(JsonObject o1, JsonObject o2) {
			String field1 = DataTypesHelper.GetStringFromJSONObject(FieldName,o1);
			String field2 = DataTypesHelper.GetStringFromJSONObject(FieldName,o2);
			return order.orderValue*field1.compareToIgnoreCase(field2);
		}
	}

	class IntComparator implements Comparator<JsonObject> {
		public int compare(JsonObject o1, JsonObject o2) {
			Integer field1 = DataTypesHelper
					.GetIntFromJSONObject(FieldName, o1);
			Integer field2 = DataTypesHelper
					.GetIntFromJSONObject(FieldName, o2);
			return order.orderValue*field1.compareTo(field2);
		}
	}

	class DateComparator implements Comparator<JsonObject> {
		public int compare(JsonObject o1, JsonObject o2) {
			Date field1 = DataTypesHelper.GetDateFromJSONObject(FieldName, o1);
			Date field2 = DataTypesHelper.GetDateFromJSONObject(FieldName, o1);
			return order.orderValue*field1.compareTo(field2);
		}
	}
	
	class BooleanComparator implements Comparator<JsonObject> {
		public int compare(JsonObject o1, JsonObject o2) {
			Boolean field1 = DataTypesHelper.GetBooleanFromJSONObject(FieldName, o1);
			Boolean field2 = DataTypesHelper.GetBooleanFromJSONObject(FieldName, o1);
			return order.orderValue*field1.compareTo(field2);
		}
	}
}
