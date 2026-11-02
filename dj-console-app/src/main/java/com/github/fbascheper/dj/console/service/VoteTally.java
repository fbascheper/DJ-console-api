package com.github.fbascheper.dj.console.service;

import java.util.List;

/**
 * Vote counts for a mix session (3 QR slots), used by queries and subscriptions.
 */
public record VoteTally(String sessionId, List<VoteChoice> choices, int totalVotes) {
}
