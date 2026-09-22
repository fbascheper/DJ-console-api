package com.github.fbascheper.dj.console.domain.util;

import com.github.fbascheper.dj.console.exception.ModelValidationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public interface ValidatingBuilder<T> {

    // implicitly public static final
    Validator VALIDATOR = getValidator();

    default T build() {
        onSetDefaultValues();
        var result = this.internalBuild();
        var violations = VALIDATOR.validate(result);

        if (!violations.isEmpty()) {
            throw new ModelValidationException(result, violations);
        }
        return result;
    }

    T internalBuild();

    /**
     * This method provides a hook for subclasses to set their own default values.
     * It is called after the object is built but before validation.
     */
    default void onSetDefaultValues() {
        // no default implementation
    }

    private static Validator getValidator() {
        Validator validator;

        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }

        return validator;
    }
}
