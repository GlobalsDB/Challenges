package com.uosipa.globalsdb.web.page.general;

import com.uosipa.globalsdb.web.page.UserPage;
import org.nocturne.link.Link;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
@Link("personal")
public class PersonalPage extends UserPage {
    @Override
    public String getPageTitle() {
        return $("Personal page");
    }

    @Override
    public void action() {
        put("userName", getUser().getLogin());
    }
}
