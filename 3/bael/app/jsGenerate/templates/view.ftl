<#list tables as table>
/* ${table.table_name} rendering methods */

${table.table_name}View = new Object();

${table.table_name}View.renderList = function(objects){
  var result = "<table>";  
  result = result + "<tr>";
  <#list table.columns as column>
  	result = result + "<th>${column.column_name}</th>";
  </#list>
  result = result + "<th></th>"; // column for control buttons
  result = result + "</tr>";
  for (var key in objects) {
    var object = objects[key];
    result = result + "<tr>";
    <#list table.columns as column>
	    result = result + "<td>"+object.${column.column_name}+"</td>";
    </#list>
    result = result + "<td>";
    result = result + "<input type='button' class='${table.table_name}_delete_btn' object_id='"+object.Id+"' value='Delete' />";
    result = result + "<input type='button' class='${table.table_name}_edit_btn' object_id='"+object.Id+"' value='Edit' /></td>";
    result = result + "</tr>";
  }  
  result = result + "</table>"
  
  result = result + "<input type='button' class='${table.table_name}_new_btn' value='New' /></td>";
  
  return result;
}

${table.table_name}View.renderEdit = function(object){
	var result = "";
<#list table.columns as column>
	var result = result + "<label>${column.column_name}</label>";
	result = result + "<input type='text' id='${table.table_name}_${column.column_name}_input' value='" + object.${column.column_name} +"' /><br />";
	
</#list>
	result = result + "<input type='button' class='${table.table_name}_update_btn' object_id='"+object.Id+"' value='Update' />";
	return result;
}

${table.table_name}View.renderNew = function(){
	var result = "";
<#list table.columns as column>
	var result = result + "<label>${column.column_name}</label>";
	result = result + "<input type='text' id='${table.table_name}_${column.column_name}_input'  /><br />";
</#list>
	result = result + "<input type='button' id='${table.table_name}_create_btn' value='Create' />";
	return result;
}

  
  	
</#list>

