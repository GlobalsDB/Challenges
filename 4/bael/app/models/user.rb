class User < ActiveRecord::Base
  # Include default devise modules. Others available are:
  # :token_authenticatable, :encryptable, :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable, :omniauthable

  # Setup accessible (or protected) attributes for your model
  attr_accessible :email, :password, :password_confirmation, :remember_me, :twitter_id, :name, :nickname, :image_url
  # attr_accessible :title, :body
  
  has_many :gained_achievements, :class_name=>'UserAchievement', :foreign_key=>'user_id'
  has_many :user_bonuses, :class_name=>'UserBonus', :foreign_key=>'user_id'
  
  def self.find_for_twitter_oauth(uid, signed_in_resource=nil)

    if user = User.where(:email => "#{uid}@gmail.com").first
      user
    else # Create a user with a stub password. 
      User.create!(:email => "#{uid}@gmail.com", :password => Devise.friendly_token[0,20]) 
    end
  end
  
  
  def self.get_name_by_id(id)
    cached_key = "User " + id.to_s
    name = Globals.pop_from_cache(cached_key)
    return name unless name.blank?
    name = User.find(id).name
    Globals.push_in_cache(cached_key, name)
    name
  end
  
  def achievements
    Achievement.get_by_user(self.id)
  end
  
end
