package com.github.fbascheper.dj.console.controller;

import com.github.fbascheper.dj.console.domain.session.MixSession;
import com.github.fbascheper.dj.console.domain.session.SessionTrack;
import com.github.fbascheper.dj.console.domain.library.Song;
import com.github.fbascheper.dj.console.domain.library.Artist;
import com.github.fbascheper.dj.console.graphql.CrowdVoteTallyPublisher;
import com.github.fbascheper.dj.console.graphql.MixSessionUpdatePublisher;
import com.github.fbascheper.dj.console.service.VoteTally;
import com.github.fbascheper.dj.console.service.CrowdVoteService;
import com.github.fbascheper.dj.console.service.MixSessionService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class DiscJockeyConsoleGraphQLController {

    private final MixSessionService mixSessionService;
    private final MixSessionUpdatePublisher mixSessionUpdatePublisher;
    private final CrowdVoteService crowdVoteService;
    private final CrowdVoteTallyPublisher crowdVoteTallyPublisher;

    public DiscJockeyConsoleGraphQLController(
            MixSessionService mixSessionService,
            MixSessionUpdatePublisher mixSessionUpdatePublisher,
            CrowdVoteService crowdVoteService,
            CrowdVoteTallyPublisher crowdVoteTallyPublisher) {
        this.mixSessionService = mixSessionService;
        this.mixSessionUpdatePublisher = mixSessionUpdatePublisher;
        this.crowdVoteService = crowdVoteService;
        this.crowdVoteTallyPublisher = crowdVoteTallyPublisher;
    }

    // ---------------------------------------------------------
    // QUERY: Convenience method for "the active session"
    // ---------------------------------------------------------
    @QueryMapping
    public MixSession currentMixSession() {
        log.info("GraphQL query: currentMixSession");
        var session = mixSessionService.getCurrentSession();
        log.debug("GraphQL query: currentMixSession -> session id={}, status={}, tracks={}",
                session.id().value(), session.getStatus(), session.tracks().size());
        return session;
    }

    // ---------------------------------------------------------
    // QUERY: Load session by ID
    // ---------------------------------------------------------
    @QueryMapping
    public MixSession mixSession(@Argument UUID id) {
        log.info("GraphQL query: mixSession [id={}]", id);
        var session = mixSessionService.getSessionById(id);
        log.debug("GraphQL query: mixSession [id={}] -> status={}, tracks={}",
                id, session.getStatus(), session.tracks().size());
        return session;
    }

    @QueryMapping
    public VoteTally voteTally(@Argument UUID id) {
        log.info("GraphQL query: voteTally [id={}]", id);
        mixSessionService.getSessionById(id);
        var tally = crowdVoteService.getTally(id);
        log.debug("GraphQL query: voteTally [id={}] -> tally={}", id, tally);
        return tally;
    }

    // ---------------------------------------------------------
    // MUTATION: Apply crowd event to a session by ID
    // ---------------------------------------------------------
    @MutationMapping
    public MixSession crowdCheered(@Argument UUID id) {
        log.info("GraphQL mutation: crowdCheered [id={}]", id);
        var session = mixSessionService.applyCrowdCheered(id);
        log.debug("GraphQL mutation: crowdCheered [id={}] -> status={}, tracks={}",
                id, session.getStatus(), session.tracks().size());
        return session;
    }

    @MutationMapping
    public MixSession crowdEnergyDropped(@Argument UUID id) {
        log.info("GraphQL mutation: crowdEnergyDropped [id={}]", id);
        var session = mixSessionService.applyCrowdEnergyDropped(id);
        log.debug("GraphQL mutation: crowdEnergyDropped [id={}] -> status={}, tracks={}",
                id, session.getStatus(), session.tracks().size());
        return session;
    }

    @MutationMapping
    public MixSession dancefloorEmptied(@Argument UUID id) {
        log.info("GraphQL mutation: dancefloorEmptied [id={}]", id);
        var session = mixSessionService.applyDancefloorEmptied(id);
        log.debug("GraphQL mutation: dancefloorEmptied [id={}] -> status={}, tracks={}",
                id, session.getStatus(), session.tracks().size());
        return session;
    }

    @MutationMapping
    public MixSession dancefloorFilledUp(@Argument UUID id) {
        log.info("GraphQL mutation: dancefloorFilledUp [id={}]", id);
        var session = mixSessionService.applyDancefloorFilledUp(id);
        log.debug("GraphQL mutation: dancefloorFilledUp [id={}] -> status={}, tracks={}",
                id, session.getStatus(), session.tracks().size());
        return session;
    }

    @MutationMapping
    public MixSession requestFromAudience(@Argument UUID id, @Argument String trackName) {
        log.info("GraphQL mutation: requestFromAudience [id={}, trackName={}]", id, trackName);
        var session = mixSessionService.applyRequestFromAudience(id, trackName);
        log.debug("GraphQL mutation: requestFromAudience [id={}, trackName={}] -> status={}, tracks={}",
                id, trackName, session.getStatus(), session.tracks().size());
        return session;
    }

    @MutationMapping
    public MixSession applyRecovery(@Argument UUID id) {
        log.info("GraphQL mutation: applyRecovery [id={}]", id);
        var session = mixSessionService.applyRecovery(id);
        log.debug("GraphQL mutation: applyRecovery [id={}] -> status={}, tracks={}",
                id, session.getStatus(), session.tracks().size());
        return session;
    }

    @MutationMapping
    public VoteTally castCrowdVote(@Argument UUID id, @Argument int slot) {
        log.info("GraphQL mutation: castCrowdVote [id={}, slot={}]", id, slot);
        var tally = crowdVoteService.castVote(id, slot);
        log.debug("GraphQL mutation: castCrowdVote [id={}, slot={}] -> tally={}", id, slot, tally);
        return tally;
    }

    @MutationMapping
    public VoteTally resetCrowdVote(@Argument UUID id) {
        log.info("GraphQL mutation: resetCrowdVote [id={}]", id);
        var tally = crowdVoteService.reset(id);
        log.debug("GraphQL mutation: resetCrowdVote [id={}] -> tally={}", id, tally);
        return tally;
    }

    @MutationMapping
    public MixSession applyCrowdVoteWinner(@Argument UUID id) {
        log.info("GraphQL mutation: applyCrowdVoteWinner [id={}]", id);
        var session = crowdVoteService.applyCrowdVoteWinner(id);
        log.debug("GraphQL mutation: applyCrowdVoteWinner [id={}] -> status={}, tracks={}",
                id, session.getStatus(), session.tracks().size());
        return session;
    }

    // ---------------------------------------------------------
    // SUBSCRIPTION: live session updates over WebSocket
    // ---------------------------------------------------------

    @SubscriptionMapping
    public Flux<MixSession> mixSessionUpdated(@Argument UUID id) {
        log.info("GraphQL subscription: mixSessionUpdated [id={}]", id);
        return Flux.concat(
                Mono.fromCallable(() -> {
                    var session = mixSessionService.getSessionById(id);
                    log.debug("GraphQL subscription: mixSessionUpdated [id={}] initial snapshot -> status={}, tracks={}",
                            id, session.getStatus(), session.tracks().size());
                    return session;
                }),
                mixSessionUpdatePublisher.streamForSession(id)
                        .doOnNext(s -> log.debug("GraphQL subscription: mixSessionUpdated [id={}] push -> status={}, tracks={}",
                                id, s.getStatus(), s.tracks().size())));
    }

    @SubscriptionMapping
    public Flux<VoteTally> crowdVoteTallyUpdated(@Argument UUID id) {
        log.info("GraphQL subscription: crowdVoteTallyUpdated [id={}]", id);
        return Flux.concat(
                Mono.fromCallable(() -> {
                    mixSessionService.getSessionById(id);
                    var tally = crowdVoteService.getTally(id);
                    log.debug("GraphQL subscription: crowdVoteTallyUpdated [id={}] initial snapshot -> tally={}", id, tally);
                    return tally;
                }),
                crowdVoteTallyPublisher.streamForSession(id)
                        .doOnNext(t -> log.debug("GraphQL subscription: crowdVoteTallyUpdated [id={}] push -> tally={}", id, t)));
    }

    // ---------------------------------------------------------
    // Nested resolvers
    // ---------------------------------------------------------

    /**
     * Supports GraphQL: tracks(last: 1)
     */
    @SchemaMapping
    public List<SessionTrack> tracks(
            MixSession mixSession,
            @Argument Integer last
    ) {
        List<SessionTrack> all = mixSession.tracks();
        if (last == null || last >= all.size()) {
            return all;
        }
        return all.subList(all.size() - last, all.size());
    }

    @SchemaMapping
    public Song song(SessionTrack track) {
        return track.song();
    }

    @SchemaMapping
    public Artist artist(Song song) {
        return song.artist();
    }

    @SchemaMapping
    public String id(Artist artist) {
        return artist.id().value().toString();
    }

    @SchemaMapping
    public int energyLevel(SessionTrack track) {
        return track.energyLevel().getIntensity();
    }

    @SchemaMapping
    public String id(MixSession mixSession) {
        return mixSession.id().value().toString();
    }

    @SchemaMapping
    public String id(SessionTrack track) {
        return track.sourceTrackId().value().toString();
    }
}