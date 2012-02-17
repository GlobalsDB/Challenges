$(document).ready(function () {
	
	var project_id = $('#project_id').text();
	
	var is_creating = (project_id == "0");

	// Gets scheme from server, then parse it
	var getScheme = function () {
		if (project_id != "") {
			$.get("/schema/read/" + project_id, function (data) {
				parseScheme(data);
			});
		} else {
			// do nothing
		}
	};
	
	// Generate scheme from elements
	var generateScheme = function () {
		var scheme = {};
		if (project_id == "0") {
			scheme.project_id = "";
		} else {
			scheme.project_id = project_id;
		}
		scheme.user_id = "kvitunov";
		scheme.tables = [];
		
		$.each($('.table_wrapper'), function () {
			var table = {};
			table.table_name = $(this).children("h3").text();
			table.columns = [];
			
			$.each($('.column_row_' + table.table_name), function () {
				var hasIndex = 
				table.columns.push({column_name: $(this).children('.table_column_name').text(),
					                type: $(this).children('.table_column_type').text(),
					                has_index: ($(this).children('.has_index').text() == "yes")});
			});
			
			scheme.tables.push(table);
		});
		
		//alert(JSON.stringify(scheme));
		return scheme;
	};
	
	// Parse scheme and create elements
	var parseScheme = function(scheme) {
		project_id = scheme.project_id;
		
		if (scheme.tables.length > 0){
			$('#links_wrapper').show(100);
		}	
		
		$('#views_url').text(scheme.views_url);
		$('#views_url').attr('href', scheme.views_url);
		
		$('#models_url').text(scheme.models_url);
		$('#models_url').attr('href', scheme.models_url);
		
		$('#html_url').text(scheme.html_url);
		$('#html_url').attr('href', scheme.html_url);
		
		
		$.each(scheme.tables, function () {
			addTable(this);
		});
	};
	
	// Refreshes actions buttons
	var refreshActions = function () {
		$("#actions_wrapper").empty();
		
		if (project_id == "1") { return; }
		
		
		var button_html = '<br/><hr />';
		button_html += '<input id="table_name" type="text" class="col_3" placeholder="Table name" />';
		button_html += '<button id="create_table" class="medium blue"><span class="icon"><span aria-hidden="true">+</span></span>Create table</button>';
		button_html += '<button class="medium green" id="generate_scheme"><span class="icon"><span aria-hidden="true">F</span></span>';
		if (is_creating == true) {
			button_html += 'Generate schema</button>';
		} else {
			button_html += 'Modify schema</button>';
		}
		
		$("#actions_wrapper").append(button_html);
	};
	
	// Create table button click handler
	$('#create_table').live('click', function () {
		var table = {};
		table.table_name = $('#table_name').val().replace(/ /gi, "_");
		if (table.table_name == "") {
			$('#table_name').addClass("error");
		} else {
			$('#table_name').removeClass("error");
			$('#table_name').val('');
			table.columns = [];
			
			addTable(table);
		}
	});
	
	// Add column button click handler
	$('button.add_column').live('click', function () {
		var table_name = this.id.replace('add_column_', '');
		var columnNameInput = $('#column_name_tb_' + table_name);
		var columnName = columnNameInput.val().replace(/ /gi, "_");
		
		if (columnName == "") {
			columnNameInput.addClass("error");
		}
		else {
			columnNameInput.removeClass("error");
			columnNameInput.val('');
			
			var column = {};
			column.column_name = columnName;
			column.type = $('#column_type_select_' + table_name + ' option:selected').val();
			column.has_index = $('#index_flag_' + table_name).is(':checked');
			
			addColumn(table_name, column);
		}
	});
	
	// Remove column click handler
	$('.remove_column').live('click', function () {
		$(this).parent().parent().remove();
	});
	
	// Remove table click handler
	$('.remove_table').live('click', function () {
		$(this).parent().remove();
	});
	
	// Action button click handler
	$('#generate_scheme').live('click', function () {
		$.post('/schema/create', JSON.stringify(generateScheme()), function (data) {
			// do nothing
		}, "json");
		
		location.reload(true);
	});
	
	// Add table
	var addTable = function(table) {
		var table_name = table.table_name;
		var table_html = "";
		
		table_html += '<div class="table_wrapper" id="table_wrapper_' + table_name +'"><hr />';
		table_html += '<h3 class="table_header">' + table_name + '</h3>';
		if (project_id != "1") {
			table_html += '<span class="icon gray remove_table"><span aria-hidden="true">X</span></span>';
		}
		table_html += '<table class="table_table" id="table_table_' + table_name + '"><thead>';
		table_html += '<tr><th>Column name</th><th>Data type</th><th>Has index?</th><th></th></tr>';
		table_html += '</thead></table>';
		
		if (project_id != "1") {
			table_html += '<input id="column_name_tb_' + table_name + '" class="col_3 column_name_tb" type="text" placeholder="Column name"></input>';
			table_html += '<select class="column_type_select" id="column_type_select_' + table_name + '">' +
		                  '<option value="string">String</option>' +
		                  '<option value="integer">Integer</option>' +
		                  '<option value="boolean">Boolean</option>' +
		                  '<option value="date">Date</option>' +
		                  '</select>';
			table_html += '<input type="checkbox" class="index_flag" id="index_flag_' + table_name + '" style="margin: 16px 0 0px 0;" /> <label for="index_flag_' + table_name + '" class="inline">Has index? </label>';
			table_html += '<button class="small add_column" id="add_column_' + table_name + '"><span class="icon"><span aria-hidden="true">+</span></span>Add column</button>';
		}
		
		
		table_html += '<br /></div>';
		
		$("#tables_wrapper").append(table_html);
		
		if (table.columns.length != 0) {
			$.each(table.columns, function () {
				addColumn(table_name, this);
			});
		}
	};
	
	// Appends column to table
	var addColumn = function(table_name, column) {
		var column_name = column.column_name;
		var column_datatype = column.type;
		var column_html = '<tr class="column_row_' + table_name + '">';
		column_html += '<td class="table_column_name">' + column_name + '</td><td class="table_column_type">' + column_datatype + '</td>';
		column_html += '<td class="has_index">';
		if (column.has_index == true) {
			column_html += "yes";
		}
		column_html += '</td>';
		column_html += '<td>';
		if (project_id != "1") {
			column_html += '<span class="icon gray remove_column"><span aria-hidden="true">X</span></span>';
		}
		column_html += '</td>';
		column_html += '</tr>';
		
		$('#table_table_' + table_name).append(column_html);
	};
		
	
	if (is_creating == true) {
		
	} else {
		getScheme();
	}
	refreshActions();
});