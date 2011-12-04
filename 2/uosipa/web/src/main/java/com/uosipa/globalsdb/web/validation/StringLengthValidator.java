package com.uosipa.globalsdb.web.validation;

import org.nocturne.validation.ValidationException;
import org.nocturne.validation.Validator;

/**
 * @author Dmitry Levshunov (levshunov.d@gmail.com)
 */
public class StringLengthValidator extends Validator {
    private static final int DEFAULT_MIN_LENGTH = 0;
    private static final int DEFAULT_MAX_LENGTH = Integer.MAX_VALUE;

    private int minLength;
    private int maxLength;

    public StringLengthValidator(int minLength, int maxLength) {
        if (minLength < 0) {
            throw new IllegalArgumentException("Minimal length must be a non negative integer number.");
        }

        if (maxLength < 0) {
            throw new IllegalArgumentException("Maximal length must be a non negative integer number.");
        }

        if (maxLength < minLength) {
            throw new IllegalArgumentException("Maximal length must be not less than minimal length.");
        }

        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public void run(String value) throws ValidationException {
        if (value == null) {
            if (minLength > 0) {
                throw new ValidationException($("Length should be at least {0} symbols", minLength));
            } else {
                return;
            }
        }

        int length = value.length();

        if (length < minLength) {
            throw new ValidationException($("Length should be at least {0} symbols", minLength));
        }

        if (length > maxLength) {
            throw new ValidationException($("Length should be no more than {0} symbols", maxLength));
        }
    }
}
