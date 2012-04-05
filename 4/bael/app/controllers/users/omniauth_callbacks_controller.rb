class Users::OmniauthCallbacksController < Devise::OmniauthCallbacksController
  def twitter
    #render :text=>request.env["omniauth.auth"].to_json
    #return
    #raise request.env["omniauth.auth"].to_s
    #@user = User.find_for_twitter_oauth(request.env["omniauth.auth"]['uid'], current_user)
    
    #request.env["omniauth.auth"]
    
    twitter_id = request.env["omniauth.auth"]['uid']
    name = request.env["omniauth.auth"]['info']['name']
    nickname = request.env["omniauth.auth"]['info']['nickname']
    image_url = request.env["omniauth.auth"]['info']['image']
    
    if user = User.where(:twitter_id=>twitter_id).first
      flash[:notice] = "Successfully authorized"
    else
      user = User.create!(:twitter_id=>twitter_id, :name=>name, :nickname=>nickname, :image_url=>image_url, :email => "#{nickname}@gmail.com")
      flash[:notice] = "Successfully registered"
    end
    
    sign_in_and_redirect :user, user

  end
end
