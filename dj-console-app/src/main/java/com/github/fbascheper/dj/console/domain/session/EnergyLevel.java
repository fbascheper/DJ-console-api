package com.github.fbascheper.dj.console.domain.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Domain value representing the crowd energy intensity of a track.
 *
 * <p>{@code EnergyLevel} drives track-selection logic in
 * {@link MixSession#applyEvent(com.github.fbascheper.dj.console.domain.event.CrowdEvent)}:
 * the DJ algorithm matches a desired energy level to the next track to be played.
 *
 * <p>The integer {@code intensity} is used for average calculations over a
 * session's played tracks (see {@link MixSession#getAverageEnergyLevel()}).
 */
@RequiredArgsConstructor
@Getter
public enum EnergyLevel {

    /** Low intensity (intensity 1) — used during warm-up phases. */
    LOW(1),

    /** Medium intensity (intensity 5) — transitional energy. */
    MEDIUM(5),

    /** Peak intensity (intensity 10) — full dancefloor energy. */
    HIGH(10);

    private final int intensity;

    /**
     * Returns the {@code EnergyLevel} that corresponds to the given rounded average
     * intensity value: 0–3 → {@code LOW}, 4–6 → {@code MEDIUM}, 7+ → {@code HIGH}.
     */
    public static EnergyLevel fromLevel(long round) {
        if (round <= 3) {
            return LOW;
        } else if (round <= 6) {
            return MEDIUM;
        } else {
            return HIGH;
        }
    }

    /**
     * Returns the next higher energy level: {@code LOW} escalates to {@code MEDIUM};
     * {@code MEDIUM} and {@code HIGH} both saturate at {@code HIGH}.
     */
    public EnergyLevel next() {
        return switch (this) {
            case LOW -> MEDIUM;
            case MEDIUM, HIGH -> HIGH;
        };
    }
}
