package com.github.fbascheper.dj.console.service;

import com.github.fbascheper.dj.console.config.CrowdVoteProperties;
import com.github.fbascheper.dj.console.domain.session.MixSession;
import com.github.fbascheper.dj.console.exception.CrowdVoteInvalidSlotException;
import com.github.fbascheper.dj.console.exception.CrowdVoteNoVotesException;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;

@Service
public class CrowdVoteService {

    private final CrowdVoteProperties properties;
    private final MixSessionService mixSessionService;
    private final CrowdVoteTallyPort crowdVoteTallyPort;
    private final ConcurrentHashMap<UUID, AtomicIntegerArray> tallies = new ConcurrentHashMap<>();

    public CrowdVoteService(
            CrowdVoteProperties properties,
            MixSessionService mixSessionService,
            CrowdVoteTallyPort crowdVoteTallyPort) {
        this.properties = properties;
        this.mixSessionService = mixSessionService;
        this.crowdVoteTallyPort = crowdVoteTallyPort;
    }

    public VoteTally getTally(UUID sessionId) {
        var counts = tallies.get(sessionId);
        var list = new ArrayList<VoteChoice>();
        int total = 0;
        for (var def : properties.choices()) {
            int v = counts == null ? 0 : counts.get(def.slot());
            total += v;
            list.add(new VoteChoice(def.slot(), def.label(), v));
        }
        return new VoteTally(sessionId.toString(), list, total);
    }

    public VoteTally castVote(UUID sessionId, int slot) {
        if (slot < 0 || slot > 2) {
            throw new CrowdVoteInvalidSlotException(slot);
        }
        mixSessionService.getSessionById(sessionId);
        tallies.computeIfAbsent(sessionId, k -> new AtomicIntegerArray(3)).incrementAndGet(slot);
        var tally = getTally(sessionId);
        crowdVoteTallyPort.publish(tally);
        return tally;
    }

    public VoteTally reset(UUID sessionId) {
        mixSessionService.getSessionById(sessionId);
        tallies.remove(sessionId);
        var tally = getTally(sessionId);
        crowdVoteTallyPort.publish(tally);
        return tally;
    }

    /**
     * Winning slot: highest vote count; ties broken by lowest slot index.
     */
    public int resolveWinningSlot(VoteTally tally) {
        int max = tally.choices().stream().mapToInt(VoteChoice::votes).max().orElse(0);
        return tally.choices().stream()
                .filter(c -> c.votes() == max)
                .mapToInt(VoteChoice::slot)
                .min()
                .orElse(0);
    }

    public String trackNameForSlot(int slot) {
        return properties.choices().stream()
                .filter(c -> c.slot() == slot)
                .findFirst()
                .map(CrowdVoteProperties.Choice::trackName)
                .orElseThrow(() -> new IllegalStateException("No choice for slot " + slot));
    }

    /**
     * Applies the crowd-vote winner as the next requested track, resets the tally,
     * and publishes the cleared tally to subscribers.
     *
     * @throws CrowdVoteNoVotesException if no votes have been cast for the session
     * @throws com.github.fbascheper.dj.console.exception.MixSessionNotFoundException
     *         if no session with that ID exists
     */
    public MixSession applyCrowdVoteWinner(UUID sessionId) {
        mixSessionService.getSessionById(sessionId);
        var tally = getTally(sessionId);
        if (tally.totalVotes() == 0) {
            throw new CrowdVoteNoVotesException(sessionId);
        }
        int winningSlot = resolveWinningSlot(tally);
        var trackName = trackNameForSlot(winningSlot);
        var saved = mixSessionService.applyRequestFromAudience(sessionId, trackName);
        reset(sessionId);
        return saved;
    }
}
