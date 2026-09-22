package com.github.fbascheper.dj.console.exception;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.stream.Collectors;

import static com.github.fbascheper.dj.console.exception.ErrorCode.DOMAIN_VALIDATION_ERROR;

/**
 * Exception thrown when a domain model validation fails.
 */
@Getter
@Slf4j
public class ModelValidationException extends DJConsoleException {

    private final Object instance;

    @SuppressWarnings("rawtypes")
    private final Set<ConstraintViolation> violations = new java.util.HashSet<>();

    public <T> ModelValidationException(T instance, Set<ConstraintViolation<T>> violations) {
        super("Validation failed for " + instance.getClass().getSimpleName(), DOMAIN_VALIDATION_ERROR);
        this.instance = instance;
        this.violations.addAll(violations);

        if (log.isInfoEnabled()) {
            log.info("Validation failed for {}: {}\n\t{}",
                    instance.getClass().getSimpleName(),
                    getDetailMessage(),
                    instance);
        }
    }

    public final String getDetailMessage() {
        return violations.stream().map(
                        cv -> "property '" + cv.getPropertyPath() + "' : " + cv.getMessage())
                .collect(Collectors.joining(", "));
    }
}
