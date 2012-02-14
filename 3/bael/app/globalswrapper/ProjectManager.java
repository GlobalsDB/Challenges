package globalswrapper;

import com.google.gson.JsonObject;
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
		System.out.println("projectInfo");
		System.out.println(projectInfo);
		if (projectInfo == null)
			return null;
		
		if (projectInfo.has(PROJECT_NAME))
		{
			String name = projectInfo.get(PROJECT_NAME).toString();
			
			//System.out.println("projectsGlobal"+SchemaManager.Instance().GetProjectsStorageGlobalsName());
			
			NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(SchemaManager.Instance().GetProjectsStorageGlobalsName());
			node.setSubscriptCount(0);
			node.increment(1);
			Long nextId = node.getLong();
			node.set(name, nextId, PROJECT_NAME);
			node.close();
			projectInfo.addProperty("Id", nextId);
		}
		
		return projectInfo;
	}

}
