/* Auto-generated file. You should not to modify it. */

tundraProjectId = ${project_id};

tundraServerUrl = "http://${server_url}/";

<#list tables as table>

	${table.table_name} = function(fields) {
	    TActiveRecord.call(this, fields);
	}
	${table.table_name}.tableName = "${table.table_name}";
	    
	${table.table_name}.prototype = new TActiveRecord();
	${table.table_name}.prototype.constructor = ${table.table_name};    
	
	${table.table_name}.getAll = function(successCallback) {
	  TActiveRecord.getAll(this, successCallback);
	}	
	
	${table.table_name}.open = function(id, successCallback) {
	    return TActiveRecord.open(this, id, successCallback);
	}
	
	${table.table_name}.method = function(methodName, methodBody) {
    	TActiveRecord.method(this, methodName, methodBody);
  	}
  	
  	${table.table_name}.deleteId = function(id, successCallback) {
    	TActiveRecord.deleteId(this, id, successCallback);
   	}
   	
   	${table.table_name}.where = function(field, predicate, value) {
    	return TActiveRecord.where(this, field, predicate, value);
  	}
  	
  	${table.table_name}.order = function(field, order) {
      return TActiveRecord.order(this, field, order);
    }
  
  	
</#list>
