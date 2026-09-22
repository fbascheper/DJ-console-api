package com.github.fbascheper.dj.console.domain.util;

import com.github.fbascheper.dj.console.domain.session.SessionTrack;

import java.util.ArrayDeque;
import java.util.List;
import java.util.stream.Gatherer;

/**
 * Utility demonstrating {@link java.util.stream.Gatherer} (JEP 485, Java 24).
 *
 * <p>{@code slidingWindow} is a custom stateful intermediate stream operation —
 * something that was impossible to express cleanly before Java 24.
 * Effective Java Item 45: use streams judiciously; know when a custom gatherer
 * is clearer than a chain of {@code collect} + {@code reduce} hacks.
 */
public final class EnergyTrendAnalyser {

    private EnergyTrendAnalyser() {}

    /**
     * Returns a {@link Gatherer} that emits overlapping sliding windows of size {@code n}.
     * Each emitted window is an immutable {@code List<T>} snapshot.
     * Windows with fewer than {@code n} elements are not emitted.
     */
    public static <T> Gatherer<T, ?, List<T>> slidingWindow(int n) {
        return Gatherer.ofSequential(
                () -> new ArrayDeque<T>(n),
                (window, element, downstream) -> {
                    window.addLast(element);
                    if (window.size() == n) {
                        var shouldContinue = downstream.push(List.copyOf(window));
                        window.pollFirst();
                        return shouldContinue;
                    }
                    return true;
                }
        );
    }

    /**
     * Returns {@code true} if any sliding window of size {@code window} in the given
     * track list shows a strictly rising energy trend (last track higher than first).
     */
    public static boolean isEnergyRising(List<SessionTrack> tracks, int window) {
        return tracks.stream()
                .gather(slidingWindow(window))
                .anyMatch(w -> w.getLast().energyLevel().getIntensity()
                        > w.getFirst().energyLevel().getIntensity());
    }
}
