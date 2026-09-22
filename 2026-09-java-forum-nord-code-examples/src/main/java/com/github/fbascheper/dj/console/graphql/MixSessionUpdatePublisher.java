package com.github.fbascheper.dj.console.graphql;

import com.github.fbascheper.dj.console.domain.session.MixSession;
import com.github.fbascheper.dj.console.service.MixSessionUpdatePort;

import org.springframework.stereotype.Component;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Bridges domain updates to GraphQL subscriptions via a hot multicast stream.
 * Implements {@link MixSessionUpdatePort} so the service layer can publish without
 * depending on this GraphQL-layer class.
 */
@Component
public class MixSessionUpdatePublisher implements MixSessionUpdatePort {

    private final Sinks.Many<MixSession> sink =
            Sinks.many().multicast().onBackpressureBuffer();

    /**
     * Notify subscribers that a session was persisted (e.g. after {@code crowdCheered}).
     */
    public void publish(MixSession session) {
        sink.tryEmitNext(session);
    }

    /**
     * Stream of updates for a single session id (does not include an initial snapshot).
     */
    public Flux<MixSession> streamForSession(UUID id) {
        return sink.asFlux().filter(s -> s.id().value().equals(id));
    }
}
