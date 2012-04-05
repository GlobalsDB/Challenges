class CreateBonusAchievements < ActiveRecord::Migration
  def change
    create_table :bonus_achievements do |t|
      t.string :name
      t.string :picture_url
      t.string :description

      t.timestamps
    end
  end
end
