package com.github.fbascheper.dj.console.exception;

import lombok.Getter;

@Getter
public class MixSessionNotFoundException extends MixSessionException {

    private final String sessionId;

    public MixSessionNotFoundException(String mixSessionId) {
        super("Mix session with id " + mixSessionId + " not found", ErrorCode.MIX_SESSION_NOT_FOUND);
        this.sessionId = mixSessionId;
    }
}
