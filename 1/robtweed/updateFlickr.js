var http = require("http");
var ewdDOM = require('ewdDOM');
var fs = require('fs');

var params = {
  path:'/home/rob/globals111/mgr'
};

ewdDOM.start(params,function(db) {
  console.log("Updating Flickr database with latest feed");

  var loadNewRecords = function(document) {
    var entries = document.getElementsByTagName('entry');
    var entry;
    var child;
    var flickr;
    var id;
    var achild;
    var gnode;
    for (var i = 0; i < entries.length; i++) {
      entry = entries[i];
      console.log("entry: " + JSON.stringify(entry));
      child = '';
      flickr = {};
      flickr.link = {};
      flickr.author = {};
      id = '';
      do {
        child = entry.getNextChild(child);
        if (child) {
          if (child.tagName === 'title') {
            flickr.title = child.text;
          }
          if (child.tagName === 'link') {
            flickr.link.rel = child.getAttribute('rel');
            flickr.link.type = child.getAttribute('type');
            flickr.link.href = child.getAttribute('href');
          }
          if (child.tagName === 'id') {
            id = child.text;
          }
          if (child.tagName === 'published') {
            flickr.published = child.text;
          }
          if (child.tagName === 'updated') {
            flickr.updated = child.text;
          }
          if (child.tagName === 'dc:date.taken') {
            flickr.taken = child.text;
          }
          if (child.tagName === 'author') {
            achild = '';
            do {
            achild = child.getNextChild(achild);
              if (achild) {
                if (achild.tagName === 'name') {
                  flickr.author.name = achild.text;
                }
                if (achild.tagName === 'uri') {
                  flickr.author.uri = achild.text;
                }
                if (achild.tagName === 'flickr:nsid') {
                  flickr.author.nsid = achild.text;
                }
                if (achild.tagName === 'flickr:buddyicon') {
                  flickr.author.buddyIcon = achild.text;
                }
              }
            }
            while (achild);
          }
        }
      }
      while (child);
      console.log("id: " + id);
      console.log(JSON.stringify(flickr));
      console.log('------------------------');
      gnode = {global: "flickr", subscripts: ['data', id]};
      db.update({node: gnode, object: flickr}, 'object');
      gnode = {global: 'flickr', subscripts: ['index', 'byAuthorName', flickr.author.name, id], data: ''};
      db.set(gnode);
      gnode = {global: 'flickr', subscripts: ['index', 'byTitle', flickr.title, id], data: ''};
      db.set(gnode);
    }
};

  var options = {
    host: 'api.flickr.com',
    port: 80,
    path: '/services/feeds/photos_public.gne',
    method: 'GET'
  };

  var req = http.request(options, function(res) {
    res.setEncoding('utf8');
    var content = '';
    res.on('data', function (chunk) {
      content = content + chunk;
    });
    res.on('end', function() {
      //console.log("content: " + content);

      var xmlPath = "/home/rob/gdbwork/flickr.txt";

      fs.writeFile(xmlPath, content, function(err) {
        if(err) {
          console.log(err);
        } 
        else {
          console.log("Procesing received feed...");
          var document = ewdDOM.parse(xmlPath, 'flickr');
          loadNewRecords(document);
          console.log("Update complete!");
        }
      }); 


    });
  });

  req.end();

});

