package com.github.fbascheper.dj.console.exception;


public abstract class MixSessionException extends DJConsoleException {

    protected MixSessionException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}