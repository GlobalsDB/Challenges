
/**
 * Module dependencies.
 */

var express = require('express')
  , routes = require('./routes')
  , globals = require('cache')


var app = module.exports = express.createServer(
//		form({ keepExtensions: true })
	);

// Configuration
app.configure(function(){
  app.use(express.bodyParser());
  app.use(express.methodOverride());
  app.use(app.router);
  app.use(express.static(__dirname + '/'));
});

app.configure('development', function(){
  app.use(express.errorHandler({ dumpExceptions: true, showStack: true })); 
});

app.configure('production', function(){
  app.use(express.errorHandler()); 
});

app.post('/app/addPost', routes.addPost);
app.post('/app/editPost', routes.editPost);
app.post('/app/deletePost/:post', routes.deletePost);
app.get('/app/getPost/:post', routes.getPost);
app.get('/app/listPosts', routes.listPosts);
app.get('/app/listPosts/:page', routes.listPosts);

var webport = 3000;

app.listen(webport);

console.log("Express server listening on port %d in %s mode", app.address().port, app.settings.env);