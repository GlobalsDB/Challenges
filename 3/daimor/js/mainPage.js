window.mainPage = Backbone.View.extend({
		el: '#maincont',
		template: _.template($('#mainPage').html()),
		render: function(page) {
			var list = {};
			$.ajax({
				url: "/app/listPosts",
				dataType: "json",
				async: false,
				success: function(data) {
					list = data;
				}
			});
			$(this.el).html(this.template(list));
		}

});

window.viewPost = Backbone.View.extend({
		el: '#maincont',
		template: _.template($('#mainPage').html()),
		render: function(postId) {
			var post = {};
			$.ajax({
				url: '/app/getPost/'+postId,
				dataType: 'json',
				async: false,
				success: function(data) {
					post = data;
				}
			})
			$(this.el).html(this.template({posts: [post]}));
		}

});

window.addPost = Backbone.View.extend({
	el: '#maincont',
	template: _.template($('#editPost').html()),
	render: function() {
		$(this.el).html(this.template({
			action: 'addPost',
			title: '',
			text: ''
		}));
	}
});

window.editPost = Backbone.View.extend({
	el: '#maincont',
	template: _.template($('#editPost').html()),
	render: function(postId) {
		var post = { };
		if(postId) {
			$.ajax({
				url: '/app/getPost/'+postId,
				dataType: 'json',
				async: false,
				success: function(data) {
					post = data;
				}
			})
			post.action = 'editPost';
		} else {
			post.id = '';
			post.action = 'addPost';
			post.title = '';
			post.text = '';
		}
		$(this.el).html(this.template(post));
	}
});
