class Activity < ActiveRecord::Base
  attr_accessible :name, :parent_id, :duration, :picture_url
  belongs_to :parent, :class_name=>'Activity'
  has_many :children, :class_name=>'Activity', :foreign_key=>'parent_id'
end
