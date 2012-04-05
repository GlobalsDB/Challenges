class UserAchievement < ActiveRecord::Base
  attr_accessible :achievement_id, :user_id
  
  belongs_to :achievement, :class_name=>'Achievement'
  belongs_to :user, :class_name=>'User'
  
  
end