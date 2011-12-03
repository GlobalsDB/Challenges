var globals = require('cache');
var flr = require('./fileReader');

var db = new globals.Cache();

exports.listLogs = function() {
	
	var rootOfGlobalsInstall = '/opt/globals'
	var dbParams = {
		path: rootOfGlobalsInstall + '/mgr',
		username: '_SYSTEM',
		password: 'SYS',
		namespace: 'USER',
	}
	
	var resOpen = db.open(dbParams);
	if(resOpen.result === '0'){
		this.ok = '0';
		console.log('error open DB: ' + resOpen.ErrorMessage);
		return this;		
	}
	this.ok = '1';
	
	var global = 'listLogs';

	var req = function(obj){
		obj = obj || {};
		var req = {};
		req.global = global;
		req.subscripts = [].concat(obj.subscripts || []);
		if(obj.object) req.object = obj.object;
		return req;
	}

	this.get = function(id) {
		if(!db.data(new req({subscripts: [id]})).defined) return;		
		var log = db.retrieve(new req({subscripts: [id]}), 'object').object
		log.id = id;
		return new Log(log);
	}
	
	this.fetch = function() {
		var list = db.retrieve(new req(), 'list' );
		
		var result = [];
		list.forEach(function(el){
			var obj = db.retrieve(new req({subscripts: [el]}), 'object').object;
			obj.id = el; 
			result.push(new Log(obj));
		}, this);
		return result;
	};
	
	this.clear = function() {
		db.kill('listLogs')
		db.kill('logs')
	};
	
	this.add = function(fileLog, canRefresh, fileName){
		var id = db.increment(new req()).data;
		var obj = new req({
					subscripts: [id],
					object: {
						fileLog: fileLog ,
						fileName: fileName || '',
						canRefresh: canRefresh
					}
			});
		db.update(obj, 'object');
		var log = this.get(id);
		if(log) {
			log.refresh();
			return log;
		} 
		return; 
	};
	
	this.delete = function(id){
		var log = this.get(id);
		log.clear();
		db.kill(new req({subscripts: [id]}));
	}
	
	this.startAutoRefresh = function(){
		var spawn = require('child_process').spawn;
		var logs = this.fetch();
		logs.forEach(function(el){
			if(el.canRefresh) {
				var fileLog = el.fileLog;
				console.log('autoRefresh from: ' + fileLog);
				el.clear();
				var tail = spawn("tail", ["-f", fileLog]);
				tail.stdout.setEncoding('utf8');
				tail.stdout.on('data', function (data) {
					var list = data.split('\n')
					list.forEach(function(str){
						el.add(str);
					})
				});
			}
		})
		return
		
	}	

	var Log = function(log) {
		var id = log.id;
		var global = 'logs';
		var fileLog = log.fileLog;
		var canRefresh = log.canRefresh;
		this.id = id;
		this.fileLog = log.fileName || fileLog;
		this.canRefresh = (canRefresh === 'true');

		var req = function(obj){
			obj = obj || {};
			var req = {};
			req.global = global;
			req.subscripts = [id].concat(obj.subscripts || []);
			if(obj.lo) req.lo = obj.lo;
			if(obj.max) req.max = obj.max;
			if(obj.hi) req.hi = obj.hi;
			if(obj.data) req.data = obj.data;
			return req;
		}
		 
		this.fetch = function(hi, max){
			max = max || this.count;			
			var result = [];
			var ind = hi || '';
			for(
				var i=0,ind = db.previous( new req( { subscripts:[ind] } ) ).result; 
				i<max && ind != '' ; 
				ind = db.previous( new req( { subscripts:[ind] } ) ).result,i++ ) {
					
				result.push(db.get(new req( { subscripts: [ind] } )).data );
			}
			return result;
		}
		
		this.clear = function(){
			db.kill(new req());
		}
				
		this.refresh = function(){
			if(!canRefresh) return false;
			this.clear();
			console.log('refresh from: ' + fileLog);
			var reader = new flr.FileLineReader(fileLog);
			while (reader.hasNextLine()){
				var str = reader.nextLine();
				var inc = db.increment(new req()).data;	
				var data = new req({
					subscripts: [inc],
					data: str
				});
				db.set(data);
			}
		}
		this.add = function(str){
			var inc = db.increment(new req()).data;	
			var data = new req({
				subscripts: [inc],
				data: str
			});
			db.set(data);
		}		
		this.count = function(){
			var list = db.retrieve(new req(), 'list');
			return list.length;
		}();
	}

	return this;	
}
