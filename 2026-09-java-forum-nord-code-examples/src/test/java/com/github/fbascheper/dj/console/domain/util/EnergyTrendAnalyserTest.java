package com.github.fbascheper.dj.console.domain.util;

import com.github.fbascheper.dj.console.domain.session.SessionTrack;
import com.github.fbascheper.dj.test.support.TrackTestObjects;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class EnergyTrendAnalyserTest {

    private static final SessionTrack LOW_TRACK =
            SessionTrack.fromLibrary(TrackTestObjects.TRACK_CHROME_THUNDER.getInstance());
    private static final SessionTrack MEDIUM_TRACK =
            SessionTrack.fromLibrary(TrackTestObjects.TRACK_SKYFALL_PROTOCOL.getInstance());
    private static final SessionTrack HIGH_TRACK =
            SessionTrack.fromLibrary(TrackTestObjects.TRACK_AFTERBURN.getInstance());

    // --- slidingWindow ---

    @Test
    void slidingWindow_emitsCorrectNumberOfWindows() {
        var result = List.of(1, 2, 3).stream()
                .gather(EnergyTrendAnalyser.slidingWindow(2))
                .toList();

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).containsExactly(1, 2);
        assertThat(result.get(1)).containsExactly(2, 3);
    }

    @Test
    void slidingWindow_windowLargerThanInput_emitsNothing() {
        var result = List.of(1).stream()
                .gather(EnergyTrendAnalyser.slidingWindow(2))
                .toList();

        assertThat(result).isEmpty();
    }

    @Test
    void slidingWindow_emptyInput_emitsNothing() {
        var result = List.<Integer>of().stream()
                .gather(EnergyTrendAnalyser.slidingWindow(2))
                .toList();

        assertThat(result).isEmpty();
    }

    @Test
    void slidingWindow_windowSizeOne_emitsSingleElementWindows() {
        var result = List.of("a", "b", "c").stream()
                .gather(EnergyTrendAnalyser.slidingWindow(1))
                .toList();

        assertThat(result).hasSize(3);
        assertThat(result.get(0)).containsExactly("a");
        assertThat(result.get(2)).containsExactly("c");
    }

    @Test
    void slidingWindow_windowEqualsInputSize_emitsOneWindow() {
        var result = IntStream.rangeClosed(1, 5).boxed()
                .gather(EnergyTrendAnalyser.slidingWindow(5))
                .toList();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).containsExactly(1, 2, 3, 4, 5);
    }

    // --- isEnergyRising ---

    @Test
    void isEnergyRising_lowThenHigh_returnsTrue() {
        var tracks = List.of(LOW_TRACK, HIGH_TRACK);
        assertThat(EnergyTrendAnalyser.isEnergyRising(tracks, 2)).isTrue();
    }

    @Test
    void isEnergyRising_lowLowThenHigh_returnsTrue() {
        var tracks = List.of(LOW_TRACK, LOW_TRACK, HIGH_TRACK);
        assertThat(EnergyTrendAnalyser.isEnergyRising(tracks, 2)).isTrue();
    }

    @Test
    void isEnergyRising_highThenLow_returnsFalse() {
        var tracks = List.of(HIGH_TRACK, HIGH_TRACK, LOW_TRACK);
        assertThat(EnergyTrendAnalyser.isEnergyRising(tracks, 2)).isFalse();
    }

    @Test
    void isEnergyRising_allSameEnergy_returnsFalse() {
        var tracks = List.of(MEDIUM_TRACK, MEDIUM_TRACK, MEDIUM_TRACK);
        assertThat(EnergyTrendAnalyser.isEnergyRising(tracks, 2)).isFalse();
    }

    @Test
    void isEnergyRising_singleTrack_returnsFalse() {
        assertThat(EnergyTrendAnalyser.isEnergyRising(List.of(LOW_TRACK), 2)).isFalse();
    }

    @Test
    void isEnergyRising_emptyTracks_returnsFalse() {
        assertThat(EnergyTrendAnalyser.isEnergyRising(List.of(), 2)).isFalse();
    }
}
