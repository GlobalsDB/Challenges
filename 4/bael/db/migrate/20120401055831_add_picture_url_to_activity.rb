class AddPictureUrlToActivity < ActiveRecord::Migration
  def change
     add_column :activities, :picture_url, :string
  end
end
