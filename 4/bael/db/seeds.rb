Activity.destroy_all
Achievement.destroy_all
User.destroy_all

activity_eating = Activity.create({:name => 'Eating', :picture_url => 'eating.png'})
fatman = Achievement.create({:name => 'Fatman', :description => "I'm lovin' it", :duration => '1', :count => '7', :activity_id => activity_eating.id, :picture_url => 'fatman.png' })

#activity_coding = Activity.create({:name => 'Coding'})
activity_working = Activity.create({:name => 'Working', :picture_url => 'working.png'})
Achievement.create({:name => 'hard worker', :description => 'no pain no gain!', :duration => '1', :count => '10',
    :activity_id => activity_working.id, :picture_url => 'hardworker.png' })

activity_cleaning = Activity.create({:name => 'Cleaning', :picture_url => 'cleaning.png'})
Achievement.create({:name => 'super cleaner', :description => 'wash it!', :duration => '1', :count => '4',
    :activity_id => activity_cleaning.id, :picture_url => 'supercleaner.png' })

activity_shopping = Activity.create({:name => 'Shopping', :picture_url => 'shopping.png'})

Achievement.create({:name => 'shopoholic', :description => 'you can buy more!', :duration => '10', :count => '20',
    :activity_id => activity_shopping.id, :picture_url => 'shopoholic.png' })

activity_running = Activity.create({:name => 'Running', :picture_url => 'running.png'})
runner_achievement = Achievement.create({:name => 'Runner lvl 1', :description => 'Run, Forest, run!', :duration => '100', :count => '25', :activity_id => activity_running.id, :picture_url => 'running.png' })

#activity_pissing = Activity.create({:name => 'Pissing', :picture_url => ''})
#activity_sleeping = Activity.create({:name => 'Sleeping', :picture_url => ''})
activity_studying = Activity.create({:name => 'Studying', :picture_url => 'studying.png'})

Achievement.create({:name => 'Nerd', :description => 'Wisdom is power', :duration => '10', :count => '20', :activity_id => activity_studying.id , :picture_url => 'nerd.png'})

activity_walking = Activity.create({:name => 'Walking with dog', :picture_url => 'walking_with_dog.png'})
walker_achievement = Achievement.create({:name => 'Dog walker', :description => 'dog is your best friend!', :duration => '10', :count => '5',
    :activity_id => activity_walking.id, :picture_url => 'dog_walker.png' })

activity_drinking = Activity.create({:name => 'Drinking', :picture_url => 'drinking.png'})

Achievement.create({:name => 'Drinker', :description => 'to see a snake in the boot!', :duration => '5', :count => '10',
    :activity_id => activity_drinking.id, :picture_url => 'drinker.png' })

activity_playing_balalaika = Activity.create({:name => 'Playing balalaika with my bear', :picture_url => 'playing_balalaika_with_my_bear.png'})
balalaika_achievement = Achievement.create({:name => 'Balalaika performance', :description => 'Play balalaika with your bear', :duration => '0', :count => '1', :activity_id => activity_playing_balalaika.id, :picture_url => 'balalaika.png' })


bonus = Achievement.create({:name => 'Bonus', :description => 'Secret bonus for playing balalaika and eating',  :duration => '0', :count => '1', :picture_url => 'bonus.png' })
ids = []
ids.push(balalaika_achievement.id)
ids.push(fatman.id)
Achievement.init_bonus_achievements(bonus.id, ids)

bonus2 = Achievement.create({:name => 'Secret Bonus 2', :description => 'Secret bonus for runningand walking with dog
',  :duration => '0', :count => '1', :picture_url => 'runner_dog_walker.png' })

ids = []
ids.push(runner_achievement.id)
ids.push(walker_achievement.id)
Achievement.init_bonus_achievements(bonus2.id, ids)


UserAchievement.destroy_all
Globals.clean
