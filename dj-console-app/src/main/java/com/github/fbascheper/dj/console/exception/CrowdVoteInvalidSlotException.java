package com.github.fbascheper.dj.console.exception;

/**
 * Thrown when {@code castCrowdVote} receives a slot outside 0..2.
 */
public class CrowdVoteInvalidSlotException extends DJConsoleException {

    public CrowdVoteInvalidSlotException(int slot) {
        super(
                "Crowd vote slot must be 0, 1, or 2 — got %d.".formatted(slot),
                ErrorCode.CROWD_VOTE_INVALID_SLOT);
    }
}
