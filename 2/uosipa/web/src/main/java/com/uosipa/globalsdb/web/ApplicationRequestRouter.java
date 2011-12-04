package com.uosipa.globalsdb.web;

import com.uosipa.globalsdb.web.page.logs.LogsPage;
import com.uosipa.globalsdb.web.page.login.LoginPage;
import com.uosipa.globalsdb.web.page.login.LogoutPage;
import com.uosipa.globalsdb.web.page.login.RegistrationPage;
import org.nocturne.link.Links;
import org.nocturne.main.LinkedRequestRouter;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
public class ApplicationRequestRouter extends LinkedRequestRouter {
    static {
        Links.add(LoginPage.class);
        Links.add(LogoutPage.class);
        Links.add(RegistrationPage.class);
        Links.add(LogsPage.class);
    }
}
