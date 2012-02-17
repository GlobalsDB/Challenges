$(function() {
	

	window.aboutPage = Backbone.View.extend({
		el: '#maincont',
		template: _.template($('#aboutPage').html()),
		render: function() {
			$(this.el).html(this.template({}));
		}
	});
	
	var menu = Backbone.View.extend({
		el: '#topmenu',
		initialize: function() {
			Menu.bind('add', this.addMenuItem, this);

		},
		addMenuItem: function(item) {
			var menuItem= _.template('<li id="mi_<%= link %>"><a href="#<%= link %>"><%= name %></a></li>');
			var menuHeader= _.template('<li class="nav-header"><%= name %></li>');
			if(item.get('link')) {
				$(this.el).append(menuItem(item.toJSON()));
			} else {
				$(this.el).append(menuHeader(item.toJSON()));
			}
			return this;
		}
	})
	window.menuList = Backbone.Collection.extend({
		model: Backbone.Model.extend({
			idAttribute: 'link'
		})
	});

	window.Menu = new window.menuList;

	window.menu = new menu;
	window.Menu.add([
		{name: 'Menu'},
		{name: 'Main', link: 'main', view: window.mainPage},
		{name: 'About', link: 'about', view: window.aboutPage},
		{name: 'Additional'},
		{name: 'Add post', link: 'addpost', view: window.editPost}
	]);
	
	var Controller = Backbone.Router.extend({
			routes: {
					"post:post": "post",
					"": "goPage",
					":page": "goPage",
					"edit/:post": "editPost",
					"delete/:post": "deletePost"
			},

			goPage: function (page) {
				if((page === undefined)||(!window.Menu._byId[page])) { 
					this.navigate('main', {trigger: true, replace: true});
					return;
				}
				$('#topmenu li').attr('class','');
				$('#mi_'+page).attr('class','active');
				var view = window.Menu._byId[page].get('view');
				window.page = new view;
				window.page.render();
			},
			post: function(post) {
				var view = new window.viewPost();
				view.render(post);
			},
			
			editPost: function(postId) {
				var view = new window.editPost();
				view.render(postId);
			},
			
			deletePost: function(postId) {
				console.log(postId);
				$('#deleteConfirm'+postId).modal('hide');
				$.ajax({
					url: '/app/deletePost/'+postId,
					type: 'post',
					async: false
				})
				this.navigate('/#main');
			}

	});

	var controller = new Controller();

	Backbone.history.start();
	
});
