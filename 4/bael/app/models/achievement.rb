class Achievement < ActiveRecord::Base
  attr_accessible :description, :name, :picture_url, :duration, :count, :activity_id
  belongs_to :activity, :class_name=>'Activity'
  has_many :users, :class_name=>'UserAchievement', :foreign_key=>'achievement_id'
  
  def user_has_gained(user_id, finish_time)
    return false if activity_id.nil? 
    start_time = finish_time - (60 * 60 * 24 * duration)
    if duration == 0
      start_time = 0
      finish_time = 0
    end
    return Globals.get_activities_count_by_user_and_range(user_id, activity_id, start_time, finish_time) >= count
  end
  
  def self.create_achievements_by_user_id_and_activity_id(user_id, activity_id, finish_time)
    Achievement.find_all_by_activity_id(activity_id).each { |achievement| 
      if achievement.user_has_gained(user_id, finish_time)
        if UserAchievement.find_all_by_user_id_and_achievement_id(user_id, achievement.id).first.nil?  
          UserAchievement.create(:user_id => user_id,  :achievement_id => achievement.id)
          Achievement.create_user_bonuses(user_id)
        end  
      end
    }
  
  end

  def self.get_by_user(user_id)
    UserAchievement.find_all_by_user_id(user_id).to_a.collect{ |ua| ua.achievement}
  end
  
  # adding additional achievements
  def self.create_user_bonuses(user_id)
    user = User.find(user_id)
    return if user.nil?
    ids = []
    UserAchievement.find_all_by_user_id(user_id).each {|user_achievement | ids.push(user_achievement.achievement_id) }
    
    bonuses_ids = []
    Globals.calc_bonuses_ids(ids.sort, bonuses_ids)
    
    
    bonuses_ids.each { |id| 
      UserAchievement.create(:user_id => user_id, :achievement_id => id) if UserAchievement.find_all_by_user_id_and_achievement_id(user_id, id).first.nil? }
  end
  
  def self.init_bonus_achievements(bonus_id, ids)
    Globals.init_bonus_achievements(bonus_id, ids.sort)
  end
  
end
