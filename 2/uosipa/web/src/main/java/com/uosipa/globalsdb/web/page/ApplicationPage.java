package com.uosipa.globalsdb.web.page;

import com.uosipa.globalsdb.database.Database;
import com.uosipa.globalsdb.model.User;
import org.nocturne.main.Page;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
public abstract class ApplicationPage extends Page {
    protected static final String AUTHORIZED_USER_SESSION_KEY = "authorizedUser";

    private TopMenuFrame topMenuFrame = new TopMenuFrame();

    public abstract String getPageTitle();

    @Override
    public void initializeAction() {
        super.initializeAction();

        put("pageTitle", getPageTitle());
        put("globalsVersion", Database.getVersion());
    }

    protected final User getUser() {
        return getSession(AUTHORIZED_USER_SESSION_KEY, User.class);
    }

    @Override
    public void finalizeAction() {
        parse("topMenuFrame", topMenuFrame);

        super.finalizeAction();
    }
}
