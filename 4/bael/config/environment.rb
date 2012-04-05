# Load the rails application
require File.expand_path('../application', __FILE__)

# Load heroku vars from local file
twitter_env = File.join(Rails.root, 'config', 'twitter_env.rb')
load(twitter_env) if File.exists?(twitter_env)

# Initialize the rails application
IDo::Application.initialize!
