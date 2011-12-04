
/**
 * Module dependencies.
 */

var express = require('express')
  , routes = require('./routes')
  , globals = require('cache')
 

var debug = function(obj){
	console.log(
		JSON.stringify(obj, null, '\t')
	)
}

var startWeb = false;

if(process.argv.length){
	var cmd = process.argv[2] || '';
	var arg = process.argv[3] || '';
	var listLogs = new require('./logs');
	listLogs = new listLogs.listLogs()
	if ( !listLogs.ok ) {
		console.log('errors');
		return;
	}	
	var showLogs = function() {
		var logs = listLogs.fetch();
		console.log('List logs:');
		logs.forEach(function(el){
			console.log('  ' + el.id + ': ' + el.fileLog + ' (count lines: ' + el.count+ ')');
		})		
	};
	if ((cmd === 'addlog')&&(arg != '')) {		
		console.log('Add log file: '+ arg)
		listLogs.add(arg, true);
		showLogs();
	} else if ((cmd === 'remove')&&(arg != '')) {
		listLogs.delete(arg);
		showLogs();				
	} else if ((cmd === 'refresh')&&(arg != '')) {
		var log = listLogs.get(arg);
		if(log){
			log.refresh();
		} else {
			console.log('Log file id: ' + arg + ' not found.');
		}
	} else if (cmd === 'list') {
		showLogs();
	} else if (cmd === 'viewlog') {
		var log = listLogs.get(arg);
		if(log){
			var logData = log.fetch();
			debug(logData);
		} else {
			console.log('Log file id: ' + arg + ' not found.');
		}		
	} else if (cmd === 'start') {
		startWeb = true;
	} else {
		console.log('Usage: node ./app.js [commands] [arguments]\n');
		console.log('Commands:')
		console.log('   start - start webserver')
		console.log('   addlog - Add log file')
		console.log('   viewlog - view log')
		console.log('   refresh - refresh from file')
		console.log('   list - show list logs')
		console.log('   remove - remove log')
	}
	console.log('');
}

if(!startWeb) {
	return;	
}

var app = module.exports = express.createServer(
//		form({ keepExtensions: true })
	);

// Configuration
app.configure(function(){
  app.set('views', __dirname + '/views');
  app.set('view engine', 'jade');
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(app.router);
  app.use(express.static(__dirname + '/public'));
});

app.configure('development', function(){
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true })); 
});

app.configure('production', function(){
  app.use(express.errorHandler()); 
});

// Routes

app.get('/', routes.index);
app.get('/logs/remove/:id', routes.logsRemove);
app.get('/logs/refresh/:id', routes.logRefresh);
app.get('/logs/:id', routes.logs);
app.get('/logs/:id/:max', routes.logs);
app.get('/logs', routes.logs);
app.get('/import', routes.importForm);
app.post('/import/new', routes.importFile);

app.listen(3000);

console.log("Express server listening on port %d in %s mode", app.address().port, app.settings.env);

listLogs.startAutoRefresh();

