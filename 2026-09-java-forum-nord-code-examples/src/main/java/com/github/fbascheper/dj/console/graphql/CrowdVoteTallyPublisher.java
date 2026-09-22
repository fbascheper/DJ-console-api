package com.github.fbascheper.dj.console.graphql;

import com.github.fbascheper.dj.console.service.CrowdVoteTallyPort;
import com.github.fbascheper.dj.console.service.VoteTally;

import org.springframework.stereotype.Component;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Multicast stream of {@link VoteTally} updates for GraphQL subscriptions (per session).
 * Implements {@link CrowdVoteTallyPort} so the service layer can publish without
 * depending on this GraphQL-layer class.
 */
@Component
public class CrowdVoteTallyPublisher implements CrowdVoteTallyPort {

    private final Sinks.Many<VoteTally> sink =
            Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public void publish(VoteTally tally) {
        sink.tryEmitNext(tally);
    }

    public Flux<VoteTally> streamForSession(UUID id) {
        var idStr = id.toString();
        return sink.asFlux().filter(t -> t.sessionId().equals(idStr));
    }
}
