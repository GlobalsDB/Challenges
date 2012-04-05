require 'test_helper'

class BonusAchievementsControllerTest < ActionController::TestCase
  setup do
    @bonus_achievement = bonus_achievements(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:bonus_achievements)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create bonus_achievement" do
    assert_difference('BonusAchievement.count') do
      post :create, :bonus_achievement => { :description => @bonus_achievement.description, :name => @bonus_achievement.name, :picture_url => @bonus_achievement.picture_url }
    end

    assert_redirected_to bonus_achievement_path(assigns(:bonus_achievement))
  end

  test "should show bonus_achievement" do
    get :show, :id => @bonus_achievement
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => @bonus_achievement
    assert_response :success
  end

  test "should update bonus_achievement" do
    put :update, :id => @bonus_achievement, :bonus_achievement => { :description => @bonus_achievement.description, :name => @bonus_achievement.name, :picture_url => @bonus_achievement.picture_url }
    assert_redirected_to bonus_achievement_path(assigns(:bonus_achievement))
  end

  test "should destroy bonus_achievement" do
    assert_difference('BonusAchievement.count', -1) do
      delete :destroy, :id => @bonus_achievement
    end

    assert_redirected_to bonus_achievements_path
  end
end
