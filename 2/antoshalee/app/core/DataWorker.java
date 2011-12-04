package core;
import core.ConnectionManager;
import core.Persistent;
import com.intersys.globals.Connection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field; 
import java.lang.reflect.Method; 
import com.intersys.globals.NodeReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import support.LogWriter;
public class DataWorker {

    private HashMap<String, Field> fieldsMap;
    private String SchemaClassName;
    
    private static DataWorker dataWorker;
    private Connection connection;
    public static DataWorker Instance()
    {
        if (dataWorker == null)
        {
            dataWorker = Init();
        }
        return dataWorker;
    }
    
    public static String GetIndexGlobalName(Class persistentClass)
    {
    	return persistentClass.getSimpleName().concat("I");
    }
    
    public static String GetDataGlobalName(Class persistentClass)
    {
    	return persistentClass.getSimpleName().concat("D");
    }
    
    public static NodeReference GetNodeReference(String nodeName)
    {
    	return Instance().connection.createNodeReference(nodeName);
    }
    
    
    public static DataWorker Init()
    {
    	DataWorker worker = new DataWorker();
    	worker.connection = ConnectionManager.Instance().getConnection();
    	return worker;
    }
    
    public void UpdateIndicesOnCreate(Persistent obj)
    {
    }
    
    private void UpdateIndexForField(Persistent obj, Field field, Persistent oldObj) 
    		throws IllegalAccessException
    {
    	if (field.getName() == "Id")
    		return;
    	
    	Index indexAnnotation = field.getAnnotation(Index.class);
    	
    	if (indexAnnotation == null)
    		return;
    	
    	String indexName = indexAnnotation.IndexName();
    	
    	NodeReference node = null;
        try
        {
            String globalName = obj.GetIndexGlobalName();
            node = connection.createNodeReference(globalName);
        }
        catch (Exception ex)
        {
           ex.printStackTrace();
        }
        
        if (oldObj != null)
        {
        	Object oldVal = field.get(oldObj);
        	if (oldVal != null)
        	{
        		Object val = ConvertValueForIndexing(oldVal);
	        	if (node.exists(indexName, val, oldObj.Id))
	        		node.kill(indexName, val, oldObj.Id);
        	}
	    }
        
        if (obj != null)
        {
        	Object newVal = field.get(obj);
        	if (newVal != null)
        	{
        		Object val = ConvertValueForIndexing(newVal);
        		if (!node.exists(indexName, val, obj.Id))
        			node.set("", indexName, val, obj.Id);
        	}
        }
    }
    
    public static Object ConvertValueForIndexing(Object value)
    {
    	if (value instanceof String)
    	{
    		//System.out.println(">"+value.toString().toUpperCase()+"<");
    		
    		String str = " ".concat(value.toString().toUpperCase());
    		//System.out.println(">"+str+"<");
    		if (str.length() > 500)
    			return str.substring(0, 500);
    		return str;
    	}
    	else if (value instanceof Date)
    	{
    		Date val = (Date) value;
    		return " ".concat(DateHelper.DateToString(val));
    	}
    	else
    	{
    		return value;
    	}
    }
    
    
    
    public void UpdateIndicesOnUpdate(Persistent oldObj, Persistent obj)
    {
    	
    }
    
    public void UpdateIndicesOnDelete(Persistent obj) throws IllegalArgumentException, IllegalAccessException
    {
    	String globalName = obj.GetIndexGlobalName();
        NodeReference node = connection.createNodeReference(globalName);
        
    	Field[] fields = obj.getClass().getFields();
    	for (int i =0; i< fields.length; i++)
    	{
    		Field field = fields[i];
    		Index annotation = field.getAnnotation(Index.class);
    		if (annotation != null)
    			node.kill(annotation.IndexName(), ConvertValueForIndexing(field.get(obj).toString()), obj.Id);
    	}
    }

    
    public Persistent SaveObject(Persistent obj) throws IllegalAccessException
    {
        NodeReference node = null;
        try
        {
            String globalName = obj.GetStorageGlobalName();
            node = connection.createNodeReference(globalName);
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        }
        
        Persistent oldObj = null;
        if (obj.Id == 0)
        {
        	node.increment(1);
            
        	obj.Id = node.getLong();
        }
        else
        {
        	oldObj = LoadObjectById(obj.Id, obj);
        }
        
        Class info = obj.getClass();
        Field[] fields = info.getFields();
        Field field;

        for (int i = 0; i < fields.length; i++)
        { 
        	field = fields[i];
            FieldSetter.SetFieldValue(obj, field, node);
            UpdateIndexForField(obj, field, oldObj);
        }
        return obj;
    }
    
    public void DeleteObjectById(Long id, String globalName)
    {
    	NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(globalName);
        node.kill(id);
    }
    
    public void DeleteObject(Persistent obj) throws IllegalArgumentException, IllegalAccessException
    {
    	String globalName = obj.GetStorageGlobalName();
    	NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(globalName);
        node.kill(obj.Id);
        UpdateIndicesOnDelete(obj);
    }
    
    public Persistent LoadObjectById(Long id, Persistent obj) throws IllegalAccessException
    {
        NodeReference node = connection.createNodeReference(obj.GetStorageGlobalName());
        
        InitSchema(obj);
        
        node.setSubscriptCount(0);
        node.appendSubscript(id);
        Field field;
        String subscript = "";
        Object nodeValue = null;
        do {
            subscript = node.nextSubscript(subscript);
            if (subscript.length() > 0) 
            {
               field = fieldsMap.get(subscript);
               nodeValue = node.getObject(subscript);
               FieldGetter.GetFieldValue(obj, field, nodeValue, subscript, node);
            }
         }while (subscript.length() > 0);
         
       
        return obj;
      
        
    }
    private void InitSchema(Object obj)
    {
        Class classInfo = obj.getClass();
        
        if (fieldsMap == null || SchemaClassName != classInfo.getName())
        {
            fieldsMap = new HashMap<String, Field>();
        }
        SchemaClassName = classInfo.getName();
        Field[] fields = classInfo.getFields();

        for (int i = 0; i < fields.length; i++)
        { 
            fieldsMap.put(fields[i].getName(), fields[i]);
        }
    }


}
