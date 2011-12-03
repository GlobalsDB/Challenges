package com.uosipa.globalsdb.web.page.frame;

import com.uosipa.globalsdb.web.page.login.LoginPage;
import com.uosipa.globalsdb.web.page.login.LogoutPage;
import com.uosipa.globalsdb.web.page.login.RegistrationPage;
import org.nocturne.link.Links;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Levshunov (dlevshunov@uosipa.com)
 */
public class TopMenuFrame extends ApplicationFrame {
    private List<Link> links;

    @Override
    public void action() {
        links = new ArrayList<Link>();

        if (getUser() == null) {
            setupLinksForAnonymousUser();
        } else {
            setupLinksForAuthorizedUser();
        }

        put("links", links);
        put("user", getUser());
    }

    private void setupLinksForAuthorizedUser() {
        links.add(new Link(Links.getLink(LogoutPage.class), $("Logout")));
    }

    private void setupLinksForAnonymousUser() {
        links.add(new Link(Links.getLink(LoginPage.class), $("Enter")));
        links.add(new Link(Links.getLink(RegistrationPage.class), $("Registration")));
    }
}
