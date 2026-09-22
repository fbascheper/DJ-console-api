package com.github.fbascheper.dj.console.exception;

import lombok.Getter;

@Getter
public abstract class DJConsoleException extends RuntimeException {

    private final ErrorCode errorCode;

    protected DJConsoleException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

