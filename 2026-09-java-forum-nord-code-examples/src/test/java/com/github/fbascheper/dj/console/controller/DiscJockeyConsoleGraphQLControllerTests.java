package com.github.fbascheper.dj.console.controller;

import com.github.fbascheper.dj.console.domain.event.CrowdCheered;
import com.github.fbascheper.dj.console.domain.event.CrowdEvent;
import com.github.fbascheper.dj.console.domain.library.Track;
import com.github.fbascheper.dj.console.domain.session.DiscJockey;
import com.github.fbascheper.dj.console.domain.session.MixSession;
import com.github.fbascheper.dj.console.domain.session.SessionTrack;
import com.github.fbascheper.dj.console.exception.CrowdVoteNoVotesException;
import com.github.fbascheper.dj.console.exception.MixSessionNotActiveException;
import com.github.fbascheper.dj.console.exception.MixSessionNotFoundException;
import com.github.fbascheper.dj.console.graphql.CrowdVoteTallyPublisher;
import com.github.fbascheper.dj.console.graphql.MixSessionUpdatePublisher;
import com.github.fbascheper.dj.console.service.VoteChoice;
import com.github.fbascheper.dj.console.service.VoteTally;
import com.github.fbascheper.dj.console.service.CrowdVoteService;
import com.github.fbascheper.dj.console.service.MixSessionService;
import com.github.fbascheper.dj.test.support.DiscJockeyTestObjects;
import com.github.fbascheper.dj.test.support.TrackTestObjects;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@GraphQlTest(DiscJockeyConsoleGraphQLController.class)
public class DiscJockeyConsoleGraphQLControllerTests {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private MixSessionService mixSessionService;

    @MockitoBean
    private MixSessionUpdatePublisher mixSessionUpdatePublisher;

    @MockitoBean
    private CrowdVoteService crowdVoteService;

    @MockitoBean
    private CrowdVoteTallyPublisher crowdVoteTallyPublisher;

    private DiscJockey discJockey = DiscJockeyTestObjects.djVortex();

    @Test
    void shouldGetCurrentMixSession() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var session = sampleSession(id
                , discJockey
                , TrackTestObjects.TRACK_AFTERBURN.getInstance()
        );

        BDDMockito.given(mixSessionService.getCurrentSession()).willReturn(session);

        this.graphQlTester
                .documentName("currentMixSession")
                .execute()
                .path("currentMixSession.id").entity(String.class).isEqualTo(id.toString())
                .path("currentMixSession.status").entity(String.class).isEqualTo("WARM_UP")
                .path("currentMixSession.tracks[0].song.title").entity(String.class).isEqualTo("Afterburn");
    }

    @Test
    void shouldGetMixSessionById() {
        var id = UUID.fromString("e53dab05-53b6-5054-98d8-9057ebe2397b");
        var session = sampleSession(id
                , discJockey
                , TrackTestObjects.TRACK_AFTERBURN.getInstance()
        );

        BDDMockito.given(mixSessionService.getSessionById(id)).willReturn(session);

        this.graphQlTester
                .documentName("mixSessionById")
                .variable("id", id.toString())
                .execute()
                .path("mixSession.id").entity(String.class).isEqualTo(id.toString())
                .path("mixSession.status").entity(String.class).isEqualTo("WARM_UP")
                .path("mixSession.tracks[0].song.title").entity(String.class).isEqualTo("Afterburn");
    }

    @Test
    void shouldApplyCrowdCheered() {
        UUID id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");

        var before = sampleSession(id
                , discJockey
                , TrackTestObjects.TRACK_AFTERBURN.getInstance()
        );
        var after = sampleSession(
                id,
                discJockey,
                List.of(new CrowdCheered(LocalDateTime.now())),
                TrackTestObjects.TRACK_AFTERBURN.getInstance(),
                TrackTestObjects.TRACK_NEON_REQUIEM.getInstance()
        );
        BDDMockito.given(mixSessionService.getSessionById(id)).willReturn(before);
        BDDMockito.given(mixSessionService.applyCrowdCheered(id)).willReturn(after);

        this.graphQlTester
                .documentName("crowdCheered")
                .variable("id", id.toString())
                .execute()
                .path("crowdCheered.id").entity(String.class).isEqualTo(id.toString())
                .path("crowdCheered.status").entity(String.class).isEqualTo("PEAK")
                .path("crowdCheered.tracks[0].id").entity(String.class).matches(s -> !s.isBlank())
                .path("crowdCheered.tracks[0].song.title").entity(String.class).isEqualTo("Neon Requiem");
    }

    @Test
    void shouldApplyCrowdEnergyDropped() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var after = sampleSession(id
                , discJockey
                , TrackTestObjects.TRACK_AFTERBURN.getInstance()
                , TrackTestObjects.TRACK_NEON_REQUIEM.getInstance()
        );

        BDDMockito.given(mixSessionService.applyCrowdEnergyDropped(id)).willReturn(after);

        this.graphQlTester
                .document("""
                        mutation CrowdEnergyDropped($id: ID!) {
                          crowdEnergyDropped(id: $id) {
                            id
                            tracks(last: 1) {
                              song { title }
                            }
                          }
                        }
                        """)
                .variable("id", id.toString())
                .execute()
                .path("crowdEnergyDropped.id").entity(String.class).isEqualTo(id.toString())
                .path("crowdEnergyDropped.tracks[0].song.title").entity(String.class).isEqualTo("Neon Requiem");
    }

    @Test
    void shouldApplyDancefloorEmptied() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var after = sampleSession(id, discJockey,
                TrackTestObjects.TRACK_CHROME_THUNDER.getInstance(),
                TrackTestObjects.TRACK_AFTERBURN.getInstance()
        );

        BDDMockito.given(mixSessionService.applyDancefloorEmptied(id)).willReturn(after);

        this.graphQlTester
                .document("""
                        mutation DancefloorEmptied($id: ID!) {
                          dancefloorEmptied(id: $id) {
                            id
                            tracks(last: 1) {
                              song { title }
                            }
                          }
                        }
                        """)
                .variable("id", id.toString())
                .execute()
                .path("dancefloorEmptied.id").entity(String.class).isEqualTo(id.toString())
                .path("dancefloorEmptied.tracks[0].song.title").entity(String.class).isEqualTo("Afterburn");
    }

    @Test
    void shouldApplyDancefloorFilledUp() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var after = sampleSession(id, discJockey,
                TrackTestObjects.TRACK_CHROME_THUNDER.getInstance(),
                TrackTestObjects.TRACK_NEON_REQUIEM.getInstance()
        );

        BDDMockito.given(mixSessionService.applyDancefloorFilledUp(id)).willReturn(after);

        this.graphQlTester
                .document("""
                        mutation DancefloorFilledUp($id: ID!) {
                          dancefloorFilledUp(id: $id) {
                            id
                            tracks(last: 1) {
                              song { title }
                            }
                          }
                        }
                        """)
                .variable("id", id.toString())
                .execute()
                .path("dancefloorFilledUp.id").entity(String.class).isEqualTo(id.toString())
                .path("dancefloorFilledUp.tracks[0].song.title").entity(String.class).isEqualTo("Neon Requiem");
    }

    @Test
    void shouldApplyRequestFromAudience() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var trackName = "Chrome Thunder";
        var after = sampleSession(id, discJockey,
                TrackTestObjects.TRACK_AFTERBURN.getInstance(),
                TrackTestObjects.TRACK_CHROME_THUNDER.getInstance()
        );

        BDDMockito.given(mixSessionService.applyRequestFromAudience(id, trackName)).willReturn(after);

        this.graphQlTester
                .document("""
                        mutation RequestFromAudience($id: ID!, $trackName: String!) {
                          requestFromAudience(id: $id, trackName: $trackName) {
                            id
                            tracks(last: 1) {
                              song { title }
                            }
                          }
                        }
                        """)
                .variable("id", id.toString())
                .variable("trackName", trackName)
                .execute()
                .path("requestFromAudience.id").entity(String.class).isEqualTo(id.toString())
                .path("requestFromAudience.tracks[0].song.title").entity(String.class).isEqualTo("Chrome Thunder");
    }

    @Test
    void shouldGetVoteTally() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var session = sampleSession(id, discJockey, TrackTestObjects.TRACK_AFTERBURN.getInstance());
        var tally = new VoteTally(
                id.toString(),
                List.of(
                        new VoteChoice(0, "Neon Requiem", 1),
                        new VoteChoice(1, "Velvet Surge", 0),
                        new VoteChoice(2, "Chrome Thunder", 0)),
                1);

        BDDMockito.given(mixSessionService.getSessionById(id)).willReturn(session);
        BDDMockito.given(crowdVoteService.getTally(id)).willReturn(tally);

        this.graphQlTester
                .documentName("voteTally")
                .variable("id", id.toString())
                .execute()
                .path("voteTally.totalVotes").entity(Integer.class).isEqualTo(1)
                .path("voteTally.choices[0].votes").entity(Integer.class).isEqualTo(1)
                .path("voteTally.choices[1].votes").entity(Integer.class).isEqualTo(0);
    }

     
    @Test
    void shouldCastCrowdVote() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var after = new VoteTally(
                id.toString(),
                List.of(
                        new VoteChoice(0, "Neon Requiem", 0),
                        new VoteChoice(1, "Velvet Surge", 1),
                        new VoteChoice(2, "Chrome Thunder", 0)),
                1);

        BDDMockito.given(crowdVoteService.castVote(id, 1)).willReturn(after);

        this.graphQlTester
                .documentName("castCrowdVote")
                .variable("id", id.toString())
                .variable("slot", 1)
                .execute()
                .path("castCrowdVote.totalVotes").entity(Integer.class).isEqualTo(1)
                .path("castCrowdVote.choices[1].votes").entity(Integer.class).isEqualTo(1);
    }

    @Test
    void shouldApplyCrowdVoteWinner() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var afterSession = sampleSession(id, discJockey,
                TrackTestObjects.TRACK_AFTERBURN.getInstance(),
                TrackTestObjects.TRACK_NEON_REQUIEM.getInstance());

        BDDMockito.given(crowdVoteService.applyCrowdVoteWinner(id)).willReturn(afterSession);

        this.graphQlTester
                .documentName("applyCrowdVoteWinner")
                .variable("id", id.toString())
                .execute()
                .path("applyCrowdVoteWinner.tracks[0].song.title")
                .entity(String.class)
                .isEqualTo("Neon Requiem");
    }

    @Test
    void shouldReturnBadRequestWhenApplyCrowdVoteWinnerWithNoVotes() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");

        BDDMockito.given(crowdVoteService.applyCrowdVoteWinner(id))
                .willThrow(new CrowdVoteNoVotesException(id));

        this.graphQlTester
                .documentName("applyCrowdVoteWinner")
                .variable("id", id.toString())
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.getFirst().getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
                    assertThat(errors.getFirst().getExtensions()).containsKey("errorCode");
                    assertThat(errors.getFirst().getMessage()).contains("No crowd votes");
                });
    }

    @Test
    void shouldStreamCrowdVoteTallyInitialSnapshot() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var tally = new VoteTally(
                id.toString(),
                List.of(
                        new VoteChoice(0, "Neon Requiem", 0),
                        new VoteChoice(1, "Velvet Surge", 0),
                        new VoteChoice(2, "Chrome Thunder", 0)),
                0);

        BDDMockito.given(mixSessionService.getSessionById(id)).willReturn(
                sampleSession(id, discJockey, TrackTestObjects.TRACK_AFTERBURN.getInstance()));
        BDDMockito.given(crowdVoteService.getTally(id)).willReturn(tally);
        BDDMockito.given(crowdVoteTallyPublisher.streamForSession(id)).willReturn(Flux.empty());

        this.graphQlTester
                .document("""
                        subscription CrowdVoteTallyUpdated($id: ID!) {
                          crowdVoteTallyUpdated(id: $id) {
                            sessionId
                            totalVotes
                          }
                        }
                        """)
                .variable("id", id.toString())
                .executeSubscription()
                .toFlux("crowdVoteTallyUpdated.sessionId", String.class)
                .as(StepVerifier::create)
                .expectNext(id.toString())
                .verifyComplete();
    }

    @Test
    void shouldStreamInitialSnapshotOnSubscription() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var session = sampleSession(id, discJockey, TrackTestObjects.TRACK_AFTERBURN.getInstance());

        BDDMockito.given(mixSessionService.getSessionById(id)).willReturn(session);
        BDDMockito.given(mixSessionUpdatePublisher.streamForSession(id)).willReturn(Flux.empty());

        this.graphQlTester
                .document("""
                        subscription MixSessionUpdated($id: ID!) {
                          mixSessionUpdated(id: $id) {
                            id
                            status
                          }
                        }
                        """)
                .variable("id", id.toString())
                .executeSubscription()
                .toFlux("mixSessionUpdated.id", String.class)
                .as(StepVerifier::create)
                .expectNext(id.toString())
                .verifyComplete();
    }

    @Test
    void shouldReturnNotFoundErrorWhenSessionNotFound() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        BDDMockito.given(mixSessionService.getSessionById(id))
                .willThrow(new MixSessionNotFoundException(id.toString()));

        this.graphQlTester
                .document("""
                        query MixSession($id: ID!) {
                          mixSession(id: $id) { id }
                        }
                        """)
                .variable("id", id.toString())
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.getFirst().getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
                    assertThat(errors.getFirst().getExtensions())
                            .containsKey("errorCode")
                            .containsEntry("sessionId", id.toString());
                });
    }

    @Test
    void shouldApplyRecovery() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var after = sampleSession(id, discJockey,
                TrackTestObjects.TRACK_AFTERBURN.getInstance(),
                TrackTestObjects.TRACK_NEON_REQUIEM.getInstance()
        );

        BDDMockito.given(mixSessionService.applyRecovery(id)).willReturn(after);

        this.graphQlTester
                .documentName("applyRecovery")
                .variable("id", id.toString())
                .execute()
                .path("applyRecovery.id").entity(String.class).isEqualTo(id.toString())
                .path("applyRecovery.tracks[0].song.title").entity(String.class).isEqualTo("Neon Requiem");
    }

    @Test
    void shouldReturnBadRequestErrorWhenSessionNotActive() {
        BDDMockito.given(mixSessionService.getCurrentSession())
                .willThrow(new MixSessionNotActiveException("active session"));

        this.graphQlTester
                .document("""
                        query CurrentMixSession {
                          currentMixSession { id }
                        }
                        """)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.getFirst().getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
                    assertThat(errors.getFirst().getExtensions()).containsKey("errorCode");
                });
    }

    private MixSession sampleSession(UUID id, DiscJockey discJockey, Track... trackSamples) {
        return sampleSession(id, discJockey, List.of(), trackSamples);
    }

    private MixSession sampleSession(
            UUID id, DiscJockey discJockey, List<CrowdEvent> crowdEvents, Track... trackSamples) {

        var builder = MixSession.builder()
                .id(new MixSession.MixSessionId(id))
                .dj(discJockey);
        crowdEvents.forEach(builder::crowdEvent);
        Stream.of(trackSamples).map(SessionTrack::fromLibrary).forEach(builder::track);
        return builder.build();
    }
}