package com.github.fbascheper.dj.console.rest;

import com.github.fbascheper.dj.console.domain.library.Track;
import com.github.fbascheper.dj.console.domain.session.DiscJockey;
import com.github.fbascheper.dj.console.domain.session.MixSession;
import com.github.fbascheper.dj.console.domain.session.SessionTrack;
import com.github.fbascheper.dj.console.exception.MixSessionNotActiveException;
import com.github.fbascheper.dj.console.service.MixSessionService;
import com.github.fbascheper.dj.test.support.DiscJockeyTestObjects;
import com.github.fbascheper.dj.test.support.TrackTestObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CurrentMixSessionRestControllerTests {

    private MockMvc mockMvc;

    @Mock
    private MixSessionService mixSessionService;

    private final DiscJockey discJockey = DiscJockeyTestObjects.djVortex();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CurrentMixSessionRestController(mixSessionService))
                .setControllerAdvice(new RestApiExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnFatCurrentSessionJson() throws Exception {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var session = sampleSession(id, discJockey, TrackTestObjects.TRACK_AFTERBURN.getInstance());

        BDDMockito.given(mixSessionService.getCurrentSession()).willReturn(session);

        mockMvc.perform(get("/api/sessions/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(id.toString()))
                .andExpect(jsonPath("$.status").value("WARM_UP"))
                .andExpect(jsonPath("$.discJockeyName").value(discJockey.name()))
                .andExpect(jsonPath("$.discJockeyLibraryTrackCount").value(discJockey.trackLibrary().size()))
                .andExpect(jsonPath("$.playedTracks[0].song.title").value("Afterburn"))
                .andExpect(jsonPath("$.playedTracks[0].cuePoints").isArray())
                .andExpect(jsonPath("$.apiStyle", containsString("REST")));
    }

    @Test
    void shouldReturn404WhenNoActiveSession() throws Exception {
        BDDMockito.given(mixSessionService.getCurrentSession())
                .willThrow(new MixSessionNotActiveException("n/a"));

        mockMvc.perform(get("/api/sessions/current"))
                .andExpect(status().isNotFound());
    }

    private static MixSession sampleSession(UUID id, DiscJockey discJockey, Track... trackSamples) {
        List<SessionTrack> sessionTracks = Stream.of(trackSamples)
                .map(SessionTrack::fromLibrary)
                .toList();

        return MixSession.builder()
                .id(new MixSession.MixSessionId(id))
                .dj(discJockey)
                .tracks(sessionTracks)
                .build();
    }
}
