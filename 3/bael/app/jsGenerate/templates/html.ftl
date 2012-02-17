<!DOCTYPE html>
<html>
    <head>
        <title>Test HTML</title>
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js"></script>
        <!--[if lt IE 9]><script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]-->   
        <script type="text/javascript" src='http://${server_url}/public/js/tundra.js'></script>
        <script type="text/javascript" src='http://${server_url}/public/js/models/models${project_id}.js'></script>
        <script type="text/javascript" src='http://${server_url}/public/js/views/view${project_id}.js'></script>
        
              <script>
 
      $(document).ready(function(){
<#list tables as t>
        Refresh${t.table_name}Objects = function(){
          ${t.table_name}.order("Id","desc").getObjects(function(objects){
            $("#content").empty();
            $('#content').html(${t.table_name}View.renderList(objects));
          });
        };
        
        /* ${t.table_name} list */
        $('.${t.table_name}_list_btn').click(function(){
          Refresh${t.table_name}Objects();
        });
        
        /* New ${t.table_name} */
        $(document).delegate(".${t.table_name}_new_btn", "click", function(){
            $("#content").empty();
            $('#content').html(${t.table_name}View.renderNew());
        });
        
        /* Create ${t.table_name} */
        $(document).delegate("#${t.table_name}_create_btn", "click", function(){
            object = new ${t.table_name}();
          <#list t.columns as c>
            object.${c.column_name} = $('#${t.table_name}_${c.column_name}_input').val();
          </#list>     
            object.save(function(){
              Refresh${t.table_name}Objects();
            })
        });
        
        /* Delete ${t.table_name} */
        $(document).delegate(".${t.table_name}_delete_btn", "click", function(){
          var objectId = $(this).attr("object_id");
          ${t.table_name}.deleteId(objectId, function(){
            alert('${t.table_name} was successfully deleted');
            Refresh${t.table_name}Objects();
          });
        });
        
        /* Edit ${t.table_name} */
        $(document).delegate(".${t.table_name}_edit_btn", "click", function(){
          var objectId = $(this).attr("object_id");
          ${t.table_name}.open(objectId,function(object){
            $('#content').empty();
            $('#content').html(${t.table_name}View.renderEdit(object));
          });
        });
        
        /* Update ${t.table_name} */
        $(document).delegate(".${t.table_name}_update_btn", "click", function(){
          var objectId = $(this).attr("object_id");
          ${t.table_name}.open(objectId,function(object){
          
          <#list t.columns as c>
            object.${c.column_name} = $('#${t.table_name}_${c.column_name}_input').val();
          </#list>
            object.save(function(){
              Refresh${t.table_name}Objects();
            })
          });
        });
  </#list>
 
      });
    	</script>
        
    </head>
    <body>
      
    <#list tables as t>
      <a href='#' class='${t.table_name}_list_btn'>${t.table_name} list</a>
    </#list>
    
      <div id="content">
      </div>     

    </body>
</html>
