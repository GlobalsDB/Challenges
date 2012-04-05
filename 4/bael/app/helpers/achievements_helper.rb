module AchievementsHelper
  def achievements
    if current_user.nil?
      []
    else
      Achievement.get_by_user(current_user.id)
    end
  end
end
