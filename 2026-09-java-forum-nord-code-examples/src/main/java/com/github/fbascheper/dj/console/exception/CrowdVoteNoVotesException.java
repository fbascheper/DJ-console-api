package com.github.fbascheper.dj.console.exception;

import java.util.UUID;

/**
 * Thrown when {@code applyCrowdVoteWinner} runs while the in-memory tally is empty.
 */
public class CrowdVoteNoVotesException extends DJConsoleException {

    public CrowdVoteNoVotesException(UUID sessionId) {
        super(
                "No crowd votes recorded for session %s — cannot pick a winner.".formatted(sessionId),
                ErrorCode.CROWD_VOTE_NO_VOTES);
    }
}
