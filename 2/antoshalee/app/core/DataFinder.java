package core;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import models.ElementCount;

import org.eclipse.jdt.internal.core.ElementCache;

import com.intersys.globals.NodeReference;

public class DataFinder {
	
	Class searchClass = null;
	
	String indexGlobal = "";
	
	ArrayList<Long> ids = null;
	
	OrderById order = OrderById.Asc;
	
	int currentPosition = 0;
	
	int top = -1;
	
	ArrayList<ConditionItem> conditions = new ArrayList<ConditionItem>();
	
	public enum	ConditionTypes {
		Equals,
		GreaterThen,
		LessThen,
		GreaterOrEqual,
		LessOrEqual;
	}
	
	public enum OrderById {
		Asc,
		Desc;
	}
	
	private class ConditionItem
	{
		String field;
		ConditionTypes condition;
		Object value;
		
		public ConditionItem(String field, ConditionTypes condition, Object value) {
			this.field = field;
			this.condition = condition;
			this.value = value;
		}
	}
	
	public DataFinder OrderByIdDesc()
	{
		order = OrderById.Desc;
		return this;
	}
	
	public DataFinder(Class searchClass) throws IllegalArgumentException {
		if (searchClass.getGenericSuperclass() != Persistent.class)
			throw new IllegalArgumentException();
		this.searchClass = searchClass;
		
		this.indexGlobal = DataWorker.GetIndexGlobalName(searchClass);
	}
	
	public Persistent Next() throws InstantiationException, IllegalAccessException
	{
		if (ids == null)
			initSearch();
		
		if (currentPosition >= ids.size())
			return null;
		
		if (top > 0 && currentPosition >= top)
			return null;
		
		Persistent obj = (Persistent) (searchClass.newInstance());
		obj.Id = ids.get(currentPosition);
		obj.LoadData();
		
		currentPosition++;
		return obj;
	}
	
	void addCondition(String field, ConditionTypes condition, Object value) throws NoSuchFieldException, SecurityException
	{
		Field searchField = searchClass.getField(field);
		Index annotation = searchField.getAnnotation(Index.class);
		if (annotation == null)
			throw new IllegalArgumentException(field);
		
		conditions.add(new ConditionItem(annotation.IndexName(), condition, DataWorker.ConvertValueForIndexing(value)));
	}
	
	public DataFinder Where(String field, Object value) throws NoSuchFieldException, SecurityException
	{ 
		return Where(field, ConditionTypes.Equals, value);
	}
	
	public DataFinder Where(String field, ConditionTypes condition, Object value) throws NoSuchFieldException, SecurityException
	{
		addCondition(field, condition, value);
		return this;
	}
	
	public int Count()
	{
		if (ids == null)
			initSearch();
		return ids.size();
	}
	
	public DataFinder Top(int top)
	{
		this.top = top;
		return this;
	}
	
	private void initSearch()
	{
		if (conditions.size() == 0)
		{
			ids = generateFullIds();
			//return;
		}
		else
		{
			for (ConditionItem item : conditions)
			{
				ArrayList<Long> conditionIds = generateIdsForCondition(item);
				intersect(conditionIds);
			}
		}
		if (order == OrderById.Asc)
		{
			Collections.sort(ids, new Comparator<Long>() {
				public int compare(Long o1, Long o2) {
					return o1.compareTo(o2);
				}
			} );
		}
		else
		{
			Collections.sort(ids, new Comparator<Long>() {
				public int compare(Long o1, Long o2) {
					return o2.compareTo(o1);
				}
			} );
		}
		
	}
	
	private ArrayList<Long> generateFullIds()
	{
		ArrayList<Long> results = new ArrayList<Long>();
		NodeReference node = DataWorker.GetNodeReference(DataWorker.GetDataGlobalName(searchClass));
		Long key = (long)0;
		while (true)
		{
			String strKey = node.nextSubscript(key);
			if (strKey.equals(""))
				break;
			key = Long.parseLong(strKey);
			results.add(key);
		}
		
		return results;
	}
	
	private ArrayList<Long> generateIdsForCondition(ConditionItem conditionItem)
	{
		NodeReference node = DataWorker.GetNodeReference(indexGlobal);
		
		switch (conditionItem.condition) {
		case Equals:
			return generateIdsForEquals(node, conditionItem.field, conditionItem.value);
		case LessOrEqual:
		case LessThen:
		case GreaterThen:
		case GreaterOrEqual:
			return generateIdsForCompare(node, conditionItem.field, conditionItem.value, conditionItem.condition);
		default:
			return new ArrayList<Long>();
		}
	}
	
	private ArrayList<Long> generateIdsForEquals(NodeReference node, String indexName, Object value)
	{
		ArrayList<Long> results = new ArrayList<Long>();
		String key = "";
		while (true)
		{
			key = node.nextSubscript(indexName, value, key);
			if (key.equals(""))
				break;
			
			Long parsedKey = Long.parseLong(key);
			if (ids != null && !ids.contains(parsedKey))
				continue;
				
			results.add(parsedKey);
		}
		return results;
	}
	
	private ArrayList<Long> generateIdsForCompare(NodeReference node, String indexName, Object value, ConditionTypes condition)
	{
		ArrayList<Long> results = new ArrayList<Long>();
		
		Object fieldKey = value;
		while (true)
		{
			switch (condition)
			{
			case GreaterThen:
			case LessThen:
				if (fieldKey.equals(value))
					if (condition == ConditionTypes.LessThen)
						fieldKey = node.previousSubscript(indexName, fieldKey);
					else
						fieldKey = node.nextSubscript(indexName, fieldKey);
			}
			
			if (fieldKey.equals(""))
				break;
			
			String key = "";
			while (true)
			{
				key = node.nextSubscript(indexName, fieldKey, key);
				if (key.equals(""))
					break;
				
				Long parsedKey = Long.parseLong(key);
				if (ids != null && !ids.contains(parsedKey))
					continue;
					
				results.add(parsedKey);
			}
			
			if (condition == ConditionTypes.LessThen || condition == ConditionTypes.LessOrEqual)
				fieldKey = node.previousSubscript(indexName, fieldKey);
			else
				fieldKey = node.nextSubscript(indexName, fieldKey);
		}
		
		return results;
	}
	
	private void intersect(ArrayList<Long> filterList)
	{
		if (ids == null)
		{
			ids = filterList;
			return;
		}
		
		for (int i = ids.size()-1; i>=0; i--)
		{
			if (!filterList.contains(ids.get(i)))
			{
				ids.remove(i);
			}
		}
	}
	
	
	public DataFinder getAddedIdsSinceId(long Id)
	{
		ids = new ArrayList<Long>();
		NodeReference node = DataWorker.GetNodeReference(DataWorker.GetDataGlobalName(searchClass));
		Long key = Id;
		while (true)
		{
			String strKey = node.nextSubscript(key);
			if (strKey.equals(""))
				break;
			key = Long.parseLong(strKey);
			ids.add(key);
		}
		
		return this;
	}
	
	public List<ElementCount> getIndexValueCounts(String IndexName, int top)
	{
		
		NodeReference node = DataWorker.GetNodeReference(indexGlobal);
		ArrayList<ElementCount> idsCount = new ArrayList<ElementCount>();
	
		String key = "";
		while (true)
		{
			key = node.nextSubscript(IndexName, key);
			if (key.equals(""))
				break;
			
			String key2 = "";
			int count = 0;
			while(true)
			{
				key2 = node.nextSubscript(IndexName, key, key2);
				if (key2.equals(""))
					break;
				count ++;
			}
			ElementCount elementCount = new ElementCount();
			elementCount.count = (long) count;
			elementCount.elementId = key;
			idsCount.add(elementCount);
		}
		
		Collections.sort(idsCount, new Comparator<ElementCount>() {
			public int compare(ElementCount o1, ElementCount o2) {
				return o2.count.compareTo(o1.count);
			}
		} );
		
		int maxCount = Math.min(idsCount.size(), top);
		List<ElementCount> list = idsCount.subList(0, maxCount);
		for (ElementCount elem : list)
		{
			elem.elementId = elem.elementId.substring(1);
		}
		return list;
	}

	/*
	public ArrayList<Long> getIndexValueCounts2(String IndexName) throws NoSuchFieldException, SecurityException, IllegalArgumentException
	{
		NodeReference node = DataWorker.GetNodeReference(indexGlobal);
		ArrayList<Long> results = new ArrayList<Long>();
		String key = "";
		while (true)
		{
			key = node.nextSubscript(IndexName, key);
			if (key.equals(""))
				break;
			System.out.println("key = '" + key + "' convert '" +  DataWorker.ConvertValueForIndexing(key)+"'");
			System.out.println(new DataFinder(searchClass).Where("elementId", key).Count());
		}
		return results;
	}
	*/
	
	

}
