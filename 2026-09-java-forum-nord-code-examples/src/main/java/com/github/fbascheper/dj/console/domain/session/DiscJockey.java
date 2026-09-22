package com.github.fbascheper.dj.console.domain.session;

import com.github.fbascheper.dj.console.domain.util.ValidatingBuilder;
import jakarta.validation.constraints.NotBlank;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The DJ who mixes tracks in {@link MixSession}-instances.
 */
@lombok.Builder(toBuilder = true, buildMethodName = "internalBuild", builderClassName = "Builder")
public record DiscJockey(

        @NotBlank
        String name,

        List<SessionTrack> trackLibrary
) {

    @SuppressWarnings("unused")
    public static class Builder implements ValidatingBuilder<DiscJockey> {
        // lombok will generate the constructor, setters, build method, etc.
    }

    public SessionTrack findNextNewTrack(EnergyLevel desiredEnergyLevel, List<SessionTrack> alreadyPlayed) {
        return trackLibrary
                .stream()
                .filter(track -> track.energyLevel() == desiredEnergyLevel)
                .filter(Predicate.not(alreadyPlayed::contains))
                .findAny()
                .orElseGet(() -> findNextNewTrackWithHighestEnergyLevel(alreadyPlayed));
    }

    public SessionTrack findNextNewTrackWithHighestEnergyLevel(List<SessionTrack> alreadyPlayed) {
        return trackLibrary
                .stream()
                .sorted(Comparator.comparingInt((SessionTrack track) -> track.energyLevel().getIntensity()).reversed())
                .filter(Predicate.not(alreadyPlayed::contains))
                .findFirst()
                .orElseGet(trackLibrary::getFirst);
    }

    public Optional<SessionTrack> findTrackByTitle(String title) {
        return trackLibrary
                .stream()
                .filter(track -> track.song().title().equalsIgnoreCase(title))
                .findFirst();
    }

}