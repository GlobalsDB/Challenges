# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20120401092109) do

  create_table "achievements", :force => true do |t|
    t.string   "name"
    t.string   "description"
    t.string   "picture_url"
    t.datetime "created_at",  :null => false
    t.datetime "updated_at",  :null => false
    t.integer  "duration"
    t.integer  "activity_id"
    t.integer  "count"
  end

  create_table "activities", :force => true do |t|
    t.string   "name"
    t.integer  "parent_id"
    t.datetime "created_at",  :null => false
    t.datetime "updated_at",  :null => false
    t.integer  "duration"
    t.string   "picture_url"
  end

  create_table "bonus_achievements", :force => true do |t|
    t.string   "name"
    t.string   "picture_url"
    t.string   "description"
    t.datetime "created_at",  :null => false
    t.datetime "updated_at",  :null => false
  end

  create_table "user_achievements", :force => true do |t|
    t.integer  "user_id"
    t.integer  "achievement_id"
    t.datetime "created_at",     :null => false
    t.datetime "updated_at",     :null => false
  end

  create_table "user_bonus", :force => true do |t|
    t.integer  "user_id"
    t.integer  "bonus_id"
    t.datetime "created_at", :null => false
    t.datetime "updated_at", :null => false
  end

  create_table "users", :force => true do |t|
    t.string   "email",              :default => "",    :null => false
    t.string   "encrypted_password", :default => "",    :null => false
    t.datetime "created_at",                            :null => false
    t.datetime "updated_at",                            :null => false
    t.string   "twitter_id"
    t.string   "name"
    t.string   "nickname"
    t.string   "image_url"
    t.boolean  "is_admin",           :default => false
  end

  add_index "users", ["email"], :name => "index_users_on_email", :unique => true
  add_index "users", ["twitter_id"], :name => "index_users_on_twitter_id"

end
