package globalswrapper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intersys.globals.NodeReference;

public class ProjectManager {
	
	public static String PROJECT_NAME = "project_name";
	public static ProjectManager _manager;
	
	public static ProjectManager Instance()
	{
		if (_manager == null)
		{
			_manager = new ProjectManager();
		}
		
		return _manager;
		
	}
	
	public JsonObject CreateProject(JsonObject projectInfo)
	{
		if (projectInfo == null)
		{
			System.out.println("Null project info applied.");
			return null;
		}
		
		if (projectInfo.has(PROJECT_NAME))
		{
			String name = projectInfo.get(PROJECT_NAME).toString();
			
			NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(SchemaManager.Instance().GetProjectsStorageGlobalsName());
			node.setSubscriptCount(0);
			node.increment(1);
			Long nextId = node.getLong();
			node.set(name, nextId, PROJECT_NAME);
			node.close();
			projectInfo.addProperty("Id", nextId);
			node.close();
		}
		
		return projectInfo;
	}
	

	
	public JsonArray GetProjectsList()
	{
		JsonArray array = new JsonArray();
		NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(SchemaManager.Instance().GetProjectsStorageGlobalsName());
		Long key = (long)0;
		JsonParser parser = new JsonParser();
		
		while (true)
		{
			String strKey = node.nextSubscript(key);
			if (strKey.equals(""))
				break;
			key = Long.parseLong(strKey);
			String nodeValue = node.getString(key, "project_name");
			nodeValue = parser.parse(nodeValue).getAsString();
			
			JsonObject project = new JsonObject();
			project.addProperty("project_id", key);
			project.addProperty("project_name", nodeValue);
			Boolean isSchemaExists = SchemaManager.Instance().IsSchemaExist(key);
			project.addProperty("IsSchemaExists", isSchemaExists);
			array.add(project);
		}
		node.close();
		return array;
		
	}

}
