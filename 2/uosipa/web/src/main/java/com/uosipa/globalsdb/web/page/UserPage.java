package com.uosipa.globalsdb.web.page;

import com.uosipa.globalsdb.web.page.login.LoginPage;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
public abstract class UserPage extends ApplicationPage {
    protected boolean checkAccess() {
        return getUser() != null;
    }

    @Override
    public void initializeAction() {
        super.initializeAction();

        if (!checkAccess()) {
            abortWithRedirect(LoginPage.class);
        }
    }
}
