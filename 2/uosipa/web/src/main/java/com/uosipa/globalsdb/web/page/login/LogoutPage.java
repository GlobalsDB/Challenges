package com.uosipa.globalsdb.web.page.login;

import com.uosipa.globalsdb.web.page.UserPage;
import org.nocturne.link.Link;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
@Link("logout")
public class LogoutPage extends UserPage {
    @Override
    public String getPageTitle() {
        return "";
    }

    @Override
    public void initializeAction() {
        super.initializeAction();

        skipTemplate();
    }

    @Override
    public void action() {
        removeSession(AUTHORIZED_USER_SESSION_KEY);

        abortWithRedirect(LoginPage.class);
    }
}
