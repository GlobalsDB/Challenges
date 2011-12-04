package com.uosipa.globalsdb.web.page.login;

import com.uosipa.globalsdb.dao.UserDao;
import com.uosipa.globalsdb.model.User;
import com.uosipa.globalsdb.web.page.ApplicationPage;
import com.uosipa.globalsdb.web.page.logs.LogsPage;
import com.uosipa.globalsdb.web.validation.StringLengthValidator;
import org.nocturne.annotation.Action;
import org.nocturne.annotation.Parameter;
import org.nocturne.annotation.Validate;
import org.nocturne.link.Link;
import org.nocturne.validation.RequiredValidator;
import org.nocturne.validation.ValidationException;
import org.nocturne.validation.Validator;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
@Link("registration")
public class RegistrationPage extends ApplicationPage {
    @Override
    public String getPageTitle() {
        return $("Registration");
    }

    @Override
    public void initializeAction() {
        super.initializeAction();

        if (getUser() != null) {
            abortWithRedirect(LogsPage.class);
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
    @Parameter(stripMode = Parameter.StripMode.NONE)
    private String passwordConfirmation;

    @Validate("register")
    public boolean validateRegister() {
        addValidator("login", new StringLengthValidator(4, 50));
        addValidator("password", new StringLengthValidator(4, 50));
        addValidator("passwordConfirmation", new RequiredValidator());
        addValidator("passwordConfirmation", new Validator() {
            @Override
            public void run(String value) throws ValidationException {
                if (!value.equals(password)) {
                    throw new ValidationException($("Password and confirmation must be the same"));
                }
            }
        });

        addValidator("login", new Validator() {
            @Override
            public void run(String value) throws ValidationException {
                if (UserDao.getInstance().isUserExist(login)) {
                    throw new ValidationException($("User already exist"));
                }
            }
        });

        return runValidation();
    }

    @Action("register")
    public void register() {
        UserDao.getInstance().addUser(new User(login, password));

        abortWithRedirect(LoginPage.class);
    }
}
