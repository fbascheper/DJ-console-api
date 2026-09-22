package com.github.fbascheper.dj.console.graphql;

import com.github.fbascheper.dj.console.domain.library.Track;
import com.github.fbascheper.dj.console.domain.session.DiscJockey;
import com.github.fbascheper.dj.console.domain.session.MixSession;
import com.github.fbascheper.dj.console.domain.session.SessionTrack;
import com.github.fbascheper.dj.test.support.DiscJockeyTestObjects;
import com.github.fbascheper.dj.test.support.TrackTestObjects;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

class MixSessionUpdatePublisherTest {

    @Test
    void shouldStreamUpdatesForMatchingSessionId() {
        var publisher = new MixSessionUpdatePublisher();
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var session = sampleSession(id, TrackTestObjects.TRACK_AFTERBURN.getInstance());

        StepVerifier.create(publisher.streamForSession(id))
                .then(() -> publisher.publish(session))
                .expectNext(session)
                .thenCancel()
                .verify();
    }

    @Test
    void shouldNotStreamUpdatesForDifferentSessionId() {
        var publisher = new MixSessionUpdatePublisher();
        var subscribedId = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var otherId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var otherSession = sampleSession(otherId, TrackTestObjects.TRACK_AFTERBURN.getInstance());

        StepVerifier.create(publisher.streamForSession(subscribedId))
                .then(() -> publisher.publish(otherSession))
                .expectNoEvent(java.time.Duration.ofMillis(50))
                .thenCancel()
                .verify();
    }

    private MixSession sampleSession(UUID id, Track track) {
        List<SessionTrack> tracks = List.of(SessionTrack.fromLibrary(track));
        return MixSession.builder()
                .id(new MixSession.MixSessionId(id))
                .dj(DiscJockeyTestObjects.djVortex())
                .tracks(tracks)
                .build();
    }
}
