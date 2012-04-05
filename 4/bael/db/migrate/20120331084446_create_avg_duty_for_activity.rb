class CreateAvgDutyForActivity < ActiveRecord::Migration
  def change
    add_column :activities, :duration, :integer
  end

end
