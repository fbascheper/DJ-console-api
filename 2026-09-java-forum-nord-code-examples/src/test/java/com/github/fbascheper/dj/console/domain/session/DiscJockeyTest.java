package com.github.fbascheper.dj.console.domain.session;

import com.github.fbascheper.dj.test.support.DiscJockeyTestObjects;
import com.github.fbascheper.dj.test.support.TrackTestObjects;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DiscJockeyTest {

    @Test
    void findNextNewTrackWithHighestEnergyLevel_excludesChromeThunderAndAfterburn() {
        // Arrange
        var djVortex = DiscJockeyTestObjects.djVortex();
        var alreadyPlayed = List.of(
                SessionTrack.fromLibrary(TrackTestObjects.TRACK_CHROME_THUNDER.getInstance()),
                SessionTrack.fromLibrary(TrackTestObjects.TRACK_AFTERBURN.getInstance())
        );

        // Act
        var nextTrack = djVortex.findNextNewTrackWithHighestEnergyLevel(alreadyPlayed);

        // Assert
        assertThat(nextTrack).isNotIn(alreadyPlayed);
        assertThat(nextTrack.energyLevel()).isEqualTo(EnergyLevel.HIGH);
    }

    @Test
    void findNextNewTrack_fallsBackToHighestEnergyTrackNotAlreadyPlayed() {
        // Arrange - both MEDIUM tracks are already played, so the desired level cannot be served
        var djVortex = DiscJockeyTestObjects.djVortex();
        var alreadyPlayed = List.of(
                SessionTrack.fromLibrary(TrackTestObjects.TRACK_SKYFALL_PROTOCOL.getInstance()),
                SessionTrack.fromLibrary(TrackTestObjects.TRACK_ECLIPSE_MODE.getInstance())
        );

        // Act - no unplayed MEDIUM track exists, so the orElseGet fallback decides
        var nextTrack = djVortex.findNextNewTrack(EnergyLevel.MEDIUM, alreadyPlayed);

        // Assert - the fallback must pick the highest-energy track that has NOT been played,
        // not simply the first track in the library
        assertThat(nextTrack).isNotIn(alreadyPlayed);
        assertThat(nextTrack.energyLevel()).isEqualTo(EnergyLevel.HIGH);
    }

    @Test
    void findNextNewTrack_returnsUnplayedTrackAtDesiredEnergyLevel() {
        // Arrange - a LOW and a HIGH track are played; both MEDIUM tracks remain available
        var djVortex = DiscJockeyTestObjects.djVortex();
        var alreadyPlayed = List.of(
                SessionTrack.fromLibrary(TrackTestObjects.TRACK_CHROME_THUNDER.getInstance()),
                SessionTrack.fromLibrary(TrackTestObjects.TRACK_AFTERBURN.getInstance())
        );

        // Act - a MEDIUM track is still available, so the fallback is NOT reached
        var nextTrack = djVortex.findNextNewTrack(EnergyLevel.MEDIUM, alreadyPlayed);

        // Assert
        assertThat(nextTrack).isNotIn(alreadyPlayed);
        assertThat(nextTrack.energyLevel()).isEqualTo(EnergyLevel.MEDIUM);
    }

}

