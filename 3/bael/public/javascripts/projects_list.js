$(document).ready(function() {

	$.get("/project/get_projects_list", function (data) {
		if (data.length != 0) {
			$.each(data, function () {
				var elements = '<li>';
				
				if (this.IsSchemaExists == true) {
					elements = elements + '<a href="/schema/' + this.project_id +'"><button class="small create_scheme_button"><span class="icon"><span aria-hidden="true">F</span></span>show or edit schema</button></a>';
				} else {
					elements = elements + '<a href="/schema/' + this.project_id +'"><button class="small create_scheme_button"><span class="icon"><span aria-hidden="true">f</span></span>create schema</button></a>';
				}
				elements = elements + '<span>' + this.project_name + '</span></li>';
				
				$("#projects_list").append(elements);
			});
		}
		else {
			$("#projects_list").append("<h2>No projects created yet...</h2>");
		}
	});
	
});