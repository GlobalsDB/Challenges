module ApplicationHelper
  def authorisation_html
    logged_in = true
    username = "Kvitunov"

    if user_signed_in?
      "<img src='#{current_user.image_url}' width='30px' height='30px' style='margin-bottom: 4px;' /> <b>#{current_user.name}</b>  #{link_to 'Log Out', destroy_user_session_path, :method=>:delete}".html_safe
    end
  end

  def is_active_link(path)
    if current_page?(path)
      "class='active'".html_safe
    else
      ""
    end
  end
end
