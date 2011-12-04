package com.uosipa.globalsdb.web.page.login;

import com.uosipa.globalsdb.dao.UserDao;
import com.uosipa.globalsdb.model.Service;
import com.uosipa.globalsdb.model.User;
import com.uosipa.globalsdb.web.page.ApplicationPage;
import com.uosipa.globalsdb.web.page.logstable.LogsPage;
import com.uosipa.globalsdb.web.validation.StringLengthValidator;
import org.nocturne.annotation.Action;
import org.nocturne.annotation.Parameter;
import org.nocturne.annotation.Validate;
import org.nocturne.link.Link;
import org.nocturne.validation.ValidationException;
import org.nocturne.validation.Validator;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
@Link("")
public class LoginPage extends ApplicationPage {
    @Override
    public String getPageTitle() {
        return $("Login");
    }

    @Override
    public void initializeAction() {
        super.initializeAction();

        if (getUser() != null) {
            abortWithRedirect(LogsPage.class, "service", Service.TOMCAT);
        }

        addCss("css/login-form.css");
    }

    @Override
    public void action() {
        // No operations.
    }

    @Parameter(stripMode = Parameter.StripMode.NONE)
    private String login;
    @Parameter(stripMode = Parameter.StripMode.NONE)
    private String password;

    private User databaseUser;

    @Validate("login")
    public boolean validateLogin() {
        addValidator("login", new StringLengthValidator(4, 50));
        addValidator("password", new StringLengthValidator(4, 50));
        addValidator("password", new Validator() {
            @Override
            public void run(String value) throws ValidationException {
                databaseUser = UserDao.getInstance().authenticate(login, password);
                if (databaseUser == null) {
                    throw new ValidationException($("Invalid login or password"));
                }
            }
        });

        return runValidation();
    }

    @Action("login")
    public void login() {
        putSession(AUTHORIZED_USER_SESSION_KEY, databaseUser);

        abortWithRedirect(LogsPage.class, "service", Service.TOMCAT);
    }
}
