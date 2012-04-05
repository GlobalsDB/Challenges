class AddActivityToAchievement < ActiveRecord::Migration
  def change
      add_column :achievements, :duration, :integer
      add_column :achievements, :activity_id, :integer
      add_column :achievements, :count, :integer
  end
end
