package com.github.fbascheper.dj.console.exception;

public class MixSessionNotActiveException extends MixSessionException {

    public MixSessionNotActiveException(String mixSessionId) {
        super("No active session found", ErrorCode.MIX_SESSION_NOT_ACTIVE);
    }
}
