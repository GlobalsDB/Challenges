class ActivitiesController < ApplicationController
  
  before_filter :is_admin?, :except=>[:submit]
  
  def select
  end

  def submit
    user_id = current_user.id
    activity_id = params[:activity_id]

    duration = params[:duration]
    unless activity_id.to_i.to_s == activity_id
      duration = 5
    end

    finish_time = Time.new.to_i+(duration.to_i*60)
    Globals.save_activity(user_id, activity_id, finish_time)
    Achievement.create_achievements_by_user_id_and_activity_id(user_id, activity_id, finish_time)
    
    redirect_to home_url
  end

  def index
    @activities = Activity.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render :json => @activities }
    end
  end

  def show
    @activity = Activity.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render :json => @activity }
    end
  end

  def new
    @activity = Activity.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render :json => @activity }
    end
  end

  def edit
    @activity = Activity.find(params[:id])
  end

  def create
    @activity = Activity.new(params[:activity])

    respond_to do |format|
      if @activity.save
        format.html { redirect_to @activity, :notice => 'Activity was successfully created.' }
        format.json { render :json => @activity, :status => :created, :location => @activity }
      else
        format.html { render :action => "new" }
        format.json { render :json => @activity.errors, :status => :unprocessable_entity }
      end
    end
  end

  def update
    @activity = Activity.find(params[:id])

    respond_to do |format|
      if @activity.update_attributes(params[:activity])
        format.html { redirect_to @activity, :notice => 'Activity was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render :action => "edit" }
        format.json { render :json => @activity.errors, :status => :unprocessable_entity }
      end
    end
  end

  def destroy
    @activity = Activity.find(params[:id])
    @activity.destroy

    respond_to do |format|
      format.html { redirect_to activities_url }
      format.json { head :no_content }
    end
  end
end
