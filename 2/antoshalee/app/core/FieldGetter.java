package core;

import java.lang.reflect.Field;
import java.util.Date;

import com.intersys.globals.NodeReference;

public class FieldGetter {
    public static void GetFieldValue(Persistent obj, Field field, Object nodeValue, String subscript, NodeReference node)
    {
    	Object fieldAsObj = null;
    	try {
				fieldAsObj = field.get(obj);
				if (Persistent.class.isAssignableFrom(field.getType()))
				{
					Persistent fieldAsPersistentObject;
					try {
						fieldAsPersistentObject = (Persistent) field.getDeclaringClass().newInstance();
						fieldAsPersistentObject.Id = Long.parseLong(nodeValue.toString());
			    		fieldAsObj = DataWorker.Instance().LoadObjectById(fieldAsPersistentObject.Id, fieldAsPersistentObject);
			    		field.set(obj,  fieldAsObj);
						return;
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		
		    		
		    		
				}
	    	
			
			  if (nodeValue instanceof java.lang.Long)
	          {
	              Long nodeLongValue = node.getLong(subscript);
	              field.setLong(obj, nodeLongValue);
	          }
	          else
	          {
	              if(field.getType() == java.util.Date.class)
	              {
	                  Date dateValue = new Date(nodeValue.toString());
	                   
	                   field.set(obj, dateValue);
	              }
	              else
	               {
	            	  if (nodeValue instanceof java.lang.Integer)
	    	          {
	    	              field.setInt(obj, node.getInt(subscript));
	    	          }
	            	  else
	            	  {
	            		  field.set(obj, nodeValue);  
	            	  }
	              
	                   
	               }
	          }
			  
		
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    
   
}
