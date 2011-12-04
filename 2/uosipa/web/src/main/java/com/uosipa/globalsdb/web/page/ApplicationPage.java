package com.uosipa.globalsdb.web.page;

import com.uosipa.globalsdb.database.Database;
import com.uosipa.globalsdb.model.User;
import com.uosipa.globalsdb.web.page.frame.TopMenuFrame;
import org.nocturne.main.Page;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
public abstract class ApplicationPage extends Page {
    protected static final String AUTHORIZED_USER_SESSION_KEY = "authorizedUser";

    private TopMenuFrame topMenuFrame = new TopMenuFrame();

    public abstract String getPageTitle();

    private long executionStartTime;

    @Override
    public void initializeAction() {
        executionStartTime = System.nanoTime();

        super.initializeAction();

        put("pageTitle", getPageTitle());
        put("globalsVersion", Database.getVersion());
        put("home", getRequest().getContextPath());
    }

    protected final User getUser() {
        return getSession(AUTHORIZED_USER_SESSION_KEY, User.class);
    }

    @Override
    public void finalizeAction() {
        parse("topMenuFrame", topMenuFrame);

        super.finalizeAction();

        long executionEndTime = System.nanoTime();

        put("executionTime", (executionEndTime - executionStartTime) / 1000000);
    }
}
