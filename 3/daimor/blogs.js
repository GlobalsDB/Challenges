
var globals = require('cache');

var db = new globals.Cache();

exports.blogs = function() {
	var rootOfGlobalsInstall = '/opt/globals'
	var dbParams = {
		path: rootOfGlobalsInstall + '/mgr',
		username: '_SYSTEM',
		password: 'SYS',
		namespace: 'USER'
	}
	
	var resOpen = db.open(dbParams);
	if(resOpen.result === '0'){
		this.ok = '0';
		console.log('error open DB: ' + resOpen.ErrorMessage);
		return this;		
	}
	this.ok = '1';
	
	var globalData = 'blogsD';
	var globalIndex = 'blogsI';
	
	var req = function(obj){
		obj = obj || {};
		var req = {};
		req.global = globalData;
		req.subscripts = [].concat(obj.subscripts || []);
		if(obj.object) req.object = obj.object;
		return req;
	}
	
	this.add = function(title, text, author) {
		var id = db.increment(new req()).data;
		var post = {
				id: id,
				title: title ,
				text: text,
				author: author
		};
		this.edit(post);
		return id; 
	};
	
	this.edit = function(post) {
		post = post || {};
		var texts = [];
		var cnt = 0;
		console.log(post);
		for(var i = 0; i < post.text.length; i += 120, cnt++) {
			texts.push(post.text.slice(i, i+120));
		}
		texts['cnt']= cnt;
		var obj = new req({
					subscripts: [post.id],
					object: {
						date: new Date().toUTCString(),
						title: post.title ,
						text: texts,
						author: post.author
					}
			});
		db.update(obj, 'object');
	};
	
	this.delete = function(id) {
		db.kill(
			new req({
				subscripts: [id]
			})
		);
	};
	
	var convertHtmlToText = function(inputText) {
			var returnText = "" + inputText;

			//-- remove BR tags and replace them with line break
			returnText=returnText.replace(/<br>/gi, "\n");
			returnText=returnText.replace(/<br\s\/>/gi, "\n");
			returnText=returnText.replace(/<br\/>/gi, "\n");

			//-- remove P and A tags but preserve what's inside of them
			returnText=returnText.replace(/<p.*>/gi, "\n"); 
			returnText=returnText.replace(/<a.*href="(.*?)".*>(.*?)<\/a>/gi, " $2 ($1)");

			//-- remove all inside SCRIPT and STYLE tags
			returnText=returnText.replace(/<script.*>[\w\W]{1,}(.*?)[\w\W]{1,}<\/script>/gi, "");
			returnText=returnText.replace(/<style.*>[\w\W]{1,}(.*?)[\w\W]{1,}<\/style>/gi, "");
			//-- remove all else
			returnText=returnText.replace(/<(?:.|\s)*?>/g, "");

			//-- get rid of more than 2 multiple line breaks:
			returnText=returnText.replace(/(?:(?:\r\n|\r|\n)\s*){2,}/gim, "\n\n");

			//-- get rid of more than 2 spaces:
			returnText=returnText.replace(/ +(?= )/g,'');

			//-- get rid of html-encoded characters:
			returnText=returnText.replace(/&nbsp;/gi," ");
			returnText=returnText.replace(/&amp;/gi,"&");
			returnText=returnText.replace(/&quot;/gi,'"');
			returnText=returnText.replace(/&lt;/gi,'<');
			returnText=returnText.replace(/&gt;/gi,'>');

			//-- return
			return returnText;
	};
	
	this.get = function(id, full) {
		full = full || false;
		var obj = db.retrieve(new req({subscripts: [id]}), 'object').object;
		var text = '';
		for(var i = 0; i < obj.text.cnt ; i++) {
			text += obj.text[i];
		}
		return {
			author: obj.author,
			text: text,
			title: obj.title,
			id: id,
			date: obj.date
		};
	};
	
	this.fetch = function() {
		var list = db.retrieve(new req(), 'list' );
		
		var result = [];
		for(var i = list.length-1; i >= 0; i--){
			result.push(this.get(list[i]));
		};
		return result;
	};
	
	return this;
}