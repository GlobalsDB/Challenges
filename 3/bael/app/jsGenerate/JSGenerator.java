package jsGenerate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.corba.se.spi.activation.Server;


import freemarker.template.*;


public class JSGenerator {
	
	private JsonObject schema;
	private String projectId;
	private Map<String, Object> map;
	private Configuration templateConfig;
	private String serverUrl;
	
	public JSGenerator(JsonObject _schema, String _projectId, String _serverUrl) throws Exception
	{
		schema = _schema;
		projectId = _projectId;
		serverUrl = _serverUrl;
		map = convertJsonToModel(schema);
		map.put("project_id", _projectId);
		map.put("server_url", _serverUrl);
		
		/* init freemarker configuration */
		templateConfig = new Configuration();	
		templateConfig.setDirectoryForTemplateLoading(new File(System.getProperty("application.path"), "/app/jsGenerate/templates"));	
		templateConfig.setObjectWrapper(new DefaultObjectWrapper());
	}
	
	
	public String GenerateModelJs() throws IOException, TemplateException
	{
		Template template = templateConfig.getTemplate("model.ftl");
	
		
		String path = System.getProperty("application.path");
		String fileName =  "/public/js/models/models"+projectId+".js";
		
		Writer out = new FileWriter(new File(path, fileName));		
	    template.process(map, out);
	    out.flush();
	    
	    return fileName;
			
	}
	
	public String GenerateViewJs() throws IOException, TemplateException
	{	
		Template template = templateConfig.getTemplate("view.ftl");
			
		String path = System.getProperty("application.path");
		String fileName =  "/public/js/views/view"+projectId+".js";
		
		Writer out = new FileWriter(new File(path, fileName));		
	    template.process(map, out);
	    out.flush();
	    
	    return fileName;
			
	}
	
	public String GenerateHtml() throws Exception
	{
		Template template = templateConfig.getTemplate("html.ftl");
		
		String path = System.getProperty("application.path");
		String fileName = "/public/htmls/html"+projectId+".html";
		
		Writer out = new FileWriter(new File(path, fileName));		
	    template.process(map, out);
	    out.flush();
		
		return fileName;
		
	}
	

	private Map<String,Object> convertJsonToModel(JsonObject schema)
	{
		Map<String,Object> model = new HashMap<String,Object>();
		Map<String,Object> tableModel;
		Map<String,Object> columnModel;
		List<Object> tables = new ArrayList<Object>();
		List<Object> columns; 
		
		JsonArray jsonTables = schema.getAsJsonArray("tables");
		
		JsonObject jsonTable;
		JsonArray jsonColumns;
		JsonObject jsonColumn;
		
		for (int i=0; i < jsonTables.size(); i++)
		{
			jsonTable = jsonTables.get(i).getAsJsonObject();
			
			tableModel = new HashMap<String,Object>();	
			tableModel.put("table_name", jsonTable.get("table_name").getAsString());
			tables.add(tableModel);
			
			jsonColumns = jsonTable.getAsJsonArray("columns");
			
			columns= new ArrayList<Object>();
			for (int j=0; j < jsonColumns.size(); j++)
			{
				jsonColumn = jsonColumns.get(j).getAsJsonObject();
				columnModel = new HashMap<String,Object>();
				columnModel.put("column_name", jsonColumn.get("column_name").getAsString());
				columns.add(columnModel);
			}
			tableModel.put("columns", columns);
	
		}
		
		model.put("tables", tables);
		return model;
	}
	



}
