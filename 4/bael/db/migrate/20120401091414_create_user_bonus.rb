class CreateUserBonus < ActiveRecord::Migration
  def change
    create_table :user_bonus do |t|
      t.integer :user_id
      t.integer :bonus_id

      t.timestamps
    end
  end
end
