package com.github.fbascheper.dj.console.service;

/**
 * One crowd-vote option (QR slot) within a {@link VoteTally}.
 */
public record VoteChoice(int slot, String label, int votes) {
}
