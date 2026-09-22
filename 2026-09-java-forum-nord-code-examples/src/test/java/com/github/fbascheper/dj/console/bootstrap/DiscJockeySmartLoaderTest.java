package com.github.fbascheper.dj.console.bootstrap;

import com.github.fbascheper.dj.console.domain.MusicLibraryLookup;
import com.github.fbascheper.dj.test.support.TrackTestObjects;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Regression test for the NPE in {@code DiscJockey.findNextNewTrack}:
 * a {@code DiscJockey} deserialized from JSON without a seed file had
 * {@code trackLibrary == null}, causing a NullPointerException at runtime
 * when the {@code crowdCheered} mutation was applied.
 *
 * <p>This test verifies that {@link DiscJockeySmartLoader} builds a
 * {@code DiscJockey} whose {@code trackLibrary} is fully populated, so
 * that the {@code crowdCheered} mutation can safely call
 * {@code DiscJockey.findNextNewTrack}.
 */
class DiscJockeySmartLoaderTest {

    @Test
    void start_registersDiscJockeyWithPopulatedTrackLibrary() {
        // Arrange
        var lookup = mock(MusicLibraryLookup.class);
        var registry = new DiscJockeyRegistry();
        var mapper = JsonMapper.builder().build();

        // Return a valid track for any UUID — the test cares that the library
        // is non-null and non-empty, not which specific tracks are included.
        var sampleTrack = TrackTestObjects.TRACK_CHROME_THUNDER.getInstance();
        when(lookup.findTrackById(any())).thenReturn(Optional.of(sampleTrack));

        var loader = new DiscJockeySmartLoader(lookup, registry, mapper);

        // Act
        loader.start();

        // Assert — DJ must be in the registry with a non-null, non-empty library.
        // Before the fix, MixSessionSmartLoader deserialized DiscJockey from JSON,
        // leaving trackLibrary = null and causing a NullPointerException on
        // DiscJockey.findNextNewTrack when the crowdCheered mutation was applied.
        var djOpt = registry.findByName("DJ VORTEX");
        assertThat(djOpt).as("DJ VORTEX must be registered").isPresent();

        var trackLibrary = djOpt.get().trackLibrary();
        assertThat(trackLibrary)
                .as("trackLibrary must not be null (would cause NPE in findNextNewTrack)")
                .isNotNull()
                .isNotEmpty();
    }

}
