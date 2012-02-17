
var blogs = new require('./blogs').blogs();

var debug = function(obj){
	console.log(
		JSON.stringify(obj, null, '\t')
	)
}

exports.addPost = function(req, res) {
	var id = blogs.add(req.body.title, req.body.textPost, 1);
	console.log('added post id: '+id);
	res.redirect('/');
};

exports.editPost = function(req, res) {
	console.log('edited post id: ');
	var id = blogs.edit({
		id: req.body.id,
		title: req.body.title,
		text: req.body.textPost
	});
	res.redirect('/');
};

exports.listPosts = function(req, res) {
	console.log('get list posts');
	var posts = blogs.fetch();
	res.contentType('application/json');
	res.send(
		JSON.stringify({
			count: posts.length,
			posts: posts
		})
	);
};

exports.getPost = function(req, res) {
	console.log('get post');
	var post = blogs.get(req.params.post, true);
	res.contentType('application/json');
	res.send(
		JSON.stringify(
			post
		)
	);
};

exports.deletePost = function(req, res) {
	var id = req.params.post;
	console.log('delete post '+id);
	blogs.delete(id);
	res.contentType('application/json');
	res.send(
		JSON.stringify({})
	);
};
