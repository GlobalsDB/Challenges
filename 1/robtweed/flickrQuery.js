var globals = require('cache');
var db = new globals.Cache();

var params = {
  path:'/home/rob/globals111/mgr',
  username: '_SYSTEM',
  password: 'SYS',
  namespace: 'USER'
};

db.open(params);

var listByAuthorName = function(name) {
  var len = name.length;
  var names = [];
  var gnode = {global: 'flickr', subscripts: ['index', 'byAuthorName', name]};
  var pname = db.previous(gnode).result;
  var done = false;
  while (!done) {
    gnode = {global: 'flickr', subscripts: ['index', 'byAuthorName', pname]};
    var pname = db.order(gnode).result;
    if ((pname === '') || (pname.substr(0,len) !== name)) {
      done = true;
    }
    else {
      names.push(pname);
    }
  }
  return names;
};

var name;
var id;

var prefix = process.argv[2];

var names = listByAuthorName(prefix);

console.log("-------------------------------");
for (var i = 0; i < names.length; i++) {
  name = names[i];
  var gnode = {global: 'flickr', subscripts: ['index', 'byAuthorName', name]};
  var ids = db.retrieve(gnode, 'list');
  for (var j = 0; j < ids.length; j++) {
    id = ids[j];
    console.log("id = " + id);
    gnode = {global: 'flickr', subscripts: ['data', id, 'title']};
    console.log("title: " + db.get(gnode).data);
    gnode = {global: 'flickr', subscripts: ['data', id, 'author', 'name']};
    var name = db.get(gnode).data;
    gnode = {global: 'flickr', subscripts: ['data', id, 'author', 'uri']};
    var uri = db.get(gnode).data;
    gnode = {global: 'flickr', subscripts: ['data', id, 'author', 'nsid']};
    var nsid = db.get(gnode).data;
    gnode = {global: 'flickr', subscripts: ['data', id, 'author', 'buddyIcon']};
    var buddyIcon = db.get(gnode).data;

    console.log("Author details:");
    console.log("  name: " + name);
    console.log("  uri: " + uri);
    console.log("  nsid: " + nsid);
    console.log("  buddy icon: " + buddyIcon);
  }
    gnode = {global: 'flickr', subscripts: ['data', id, 'taken']};
    console.log("taken: " + db.get(gnode).data);

    gnode = {global: 'flickr', subscripts: ['data', id, 'published']};
    console.log("published: " + db.get(gnode).data);

    gnode = {global: 'flickr', subscripts: ['data', id, 'updated']};
    console.log("updated: " + db.get(gnode).data);
  console.log("-------------------------------------");
}

