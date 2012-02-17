package controllers;

import java.net.InetAddress;

import jsGenerate.JSGenerator;
import globalswrapper.SchemaManager;
import globalswrapper.Utils;

import com.google.gson.JsonObject;
import com.sun.corba.se.spi.activation.Server;

import play.mvc.Controller;

public class Schema extends BaseController {
	public static void create(JsonObject body) {
		SchemaManager mr = new SchemaManager();
		Long projectId = body.get("project_id").getAsLong();
		
		try {			
			mr.InitSchema(body, projectId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
			Utils.writeToFile("", e.toString());
		}
	
		// generate JavaScripts
		String url = "";
	
		try {
					
			String serverUrl = getServerUrl();
			
			JSGenerator generator = new JSGenerator(body, projectId.toString(),serverUrl);
			
			
			String modelFileName = generator.GenerateModelJs();	
			url = serverUrl + modelFileName;
			
			String viewFileName = generator.GenerateViewJs();
			String htmlFileName = generator.GenerateHtml();
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		
		renderJSON(url);
	}

	public static void read(long project_id){
		SchemaManager mr = SchemaManager.Instance();
		JsonObject body = mr.ReadSchema(project_id);
		String serverUrl = getServerUrl();
		body.addProperty("models_url", "http://"+serverUrl+"/public/js/models/models" + project_id + ".js");
		body.addProperty("views_url", "http://"+serverUrl+"/public/js/views/view" + project_id + ".js");;
		body.addProperty("html_url", "http://"+serverUrl+"/public/htmls/html" + project_id + ".html");
		renderJSON(body.toString());	
	}
	
	public static void form(long project_id) {
		render(project_id);
	}
	
	private static String getServerUrl() {
		String result = "";
		try {
			result = request.host;
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
}
