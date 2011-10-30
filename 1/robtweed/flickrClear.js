var globals = require('cache');
var db = new globals.Cache();

var params = {
  path:'/home/rob/globals111/mgr',
  username: '_SYSTEM',
  password: 'SYS',
  namespace: 'USER'
};

db.open(params);
db.kill({global: 'flickr'});
