package com.github.fbascheper.dj.console.domain.library;

import com.github.fbascheper.dj.console.domain.util.ValidatingBuilder;
import com.github.fbascheper.dj.console.domain.session.EnergyLevel;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * A track is a playable version of a {@link Song}, including length, energy level,
 * and DJ‑relevant cue points used when preparing or mixing {@link com.github.fbascheper.dj.console.domain.session.MixSession}.
 */
@lombok.Builder(
        toBuilder = true,
        buildMethodName = "internalBuild",
        builderClassName = "Builder"
)
public record Track(

        @NotNull
        TrackId id,

        @NotNull
        Song song,

        @NotNull
        Duration length,

        @NotNull
        EnergyLevel energyLevel,

        List<CuePoint> cuePoints

) {

    public static class Builder implements ValidatingBuilder<Track> {

        private TrackId id;
        private Song song;
        private Duration length;
        private EnergyLevel energyLevel;
        private List<CuePoint> cuePoints;

        @Override
        public void onSetDefaultValues() {
            if (id == null) {
                id = new TrackId(UUID.randomUUID());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Track other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public record TrackId(@NotNull UUID value) {}
}