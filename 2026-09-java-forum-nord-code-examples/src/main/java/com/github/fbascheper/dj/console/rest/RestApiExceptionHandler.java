package com.github.fbascheper.dj.console.rest;

import com.github.fbascheper.dj.console.exception.ErrorCode;
import com.github.fbascheper.dj.console.exception.MixSessionNotActiveException;
import com.github.fbascheper.dj.console.exception.MixSessionNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = CurrentMixSessionRestController.class)
public class RestApiExceptionHandler {

    @ExceptionHandler(MixSessionNotActiveException.class)
    ProblemDetail notActive(MixSessionNotActiveException ex) {
        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("No active session");
        pd.setProperty("errorCode", ErrorCode.MIX_SESSION_NOT_ACTIVE.name());
        return pd;
    }

    @ExceptionHandler(MixSessionNotFoundException.class)
    ProblemDetail notFound(MixSessionNotFoundException ex) {
        var pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Session not found");
        pd.setProperty("errorCode", ErrorCode.MIX_SESSION_NOT_FOUND.name());
        return pd;
    }
}
