package com.uosipa.globalsdb.web.validation;

import org.nocturne.validation.ValidationException;
import org.nocturne.validation.Validator;

import java.util.regex.Pattern;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
public class LoginValidator extends Validator {
    private static final Pattern pattern = Pattern.compile("[a-zA-Z0-9.]*");

    @Override
    public void run(String value) throws ValidationException {
        if (value == null) {
            throw new ValidationException($("Length should be at least {0} symbols", 4));
        }

        if (value.length() < 4) {
            throw new ValidationException($("Length should be at least {0} symbols", 4));
        }
        if (value.length() > 30) {
            throw new ValidationException($("Length should be no more than {0} symbols", 30));
        }

        if (!pattern.matcher(value).matches()) {
            throw new ValidationException("Only latin letters, digits and symbol '.' are allowed");
        }

        char firstCharacter = value.charAt(0);
        char lastCharacter = value.charAt(value.length() - 1);

        if (!Character.isLetter(firstCharacter)) {
            throw new ValidationException($("First character must be letter"));
        }

        if (!(Character.isLetterOrDigit(lastCharacter))) {
            throw new ValidationException($("Last character must be letter or digit"));
        }
    }
}
