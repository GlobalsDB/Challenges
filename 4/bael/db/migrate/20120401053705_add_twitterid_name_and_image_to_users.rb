class AddTwitteridNameAndImageToUsers < ActiveRecord::Migration
  def change
    add_column :users, :twitter_id, :string
    add_column :users, :name, :string
    add_column :users, :nickname, :string
    add_column :users, :image_url, :string
    add_index :users, :twitter_id
  end
end
