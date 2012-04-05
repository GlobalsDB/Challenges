class ApplicationController < ActionController::Base
  protect_from_forgery
  
  before_filter :authenticate_user!  
  
  def is_admin?
    redirect_to home_path if !current_user || !current_user.is_admin
  end
  
  def index
    @user = current_user
  end
end
