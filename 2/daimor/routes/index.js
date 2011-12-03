
/*
 * GET home page.
 */
var listLogs = require('../logs');

var debug = function(obj){
	console.log(
		JSON.stringify(obj, null, '\t')
	)
}

listLogs = new listLogs.listLogs()


exports.index = function(req, res){
	res.redirect('/logs');	
};

exports.importForm = function(req, res){
	res.render('import', { title: 'Import log file' })
};

exports.importFile = function(req, res){

	var file = req.body.upload
	if(file){
		var filePath = file.path
		var filename = file.filename
		console.log('load uploaded file: ' + filename)
		listLogs.add(filePath, false, filename);		
	  	res.redirect('/logs');
	} else {
		res.render('import', { title: 'Import log' })
	}
};

exports.logs = function(req, res){
	var id = req.params.id;
	var id = req.params.id;
	var log = listLogs.get(id);		
	if(log) {
		var max = req.params.max;
		if((max === 'all')||(max === '0')) max = log.count;
		else max = max || 50;
		max = parseInt(max);
		var logData = log.fetch('',max);
		max += 50;
		if( max >= log.count) max = 0;
		res.render('logfile', { title: 'View log file', log: log, logData: logData, max: max, canRefresh: log.canRefresh })
	} else {
		var logs = listLogs.fetch();
		res.render('logs', { title: 'Show logs', logs : logs })
	}
};

exports.logRefresh = function(req, res){
	if(req.params.id) {
		var id = req.params.id;
		var log = listLogs.get(id);
		log.refresh();
		res.redirect('/logs/'+id);
	} else {
		var logs = listLogs.fetch();
		res.render('logs', { title: 'Show logs', logs : logs })
	}
	
}

exports.logsRemove = function(req, res){
	var id = req.params.id;
	console.log('remove id: '+id);
	listLogs.delete(id);
	res.redirect('/logs');
}