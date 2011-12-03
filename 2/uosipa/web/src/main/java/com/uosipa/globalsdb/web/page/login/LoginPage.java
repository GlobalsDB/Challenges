package com.uosipa.globalsdb.web.page.login;

import com.uosipa.globalsdb.database.Database;
import com.uosipa.globalsdb.model.User;
import com.uosipa.globalsdb.web.page.ApplicationPage;
import com.uosipa.globalsdb.web.page.general.PersonalPage;
import com.uosipa.globalsdb.web.validation.StringLengthValidator;
import org.apache.commons.codec.digest.DigestUtils;
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
            abortWithRedirect(PersonalPage.class);
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

    @Validate("login")
    public boolean validateLogin() {
        addValidator("login", new StringLengthValidator(4, 50));
        addValidator("password", new StringLengthValidator(4, 50));
        addValidator("password", new Validator() {
            @Override
            public void run(String value) throws ValidationException {
                String databasePassword = Database.read(login);
                if (password == null || password.isEmpty() || !databasePassword.equals(DigestUtils.shaHex(password))) {
                    throw new ValidationException($("Invalid login or password"));
                }
            }
        });

        return runValidation();
    }

    @Action("login")
    public void login() {
        User user = new User();
        user.setLogin(login);

        putSession(AUTHORIZED_USER_SESSION_KEY, user);

        abortWithRedirect(PersonalPage.class);
    }
}
