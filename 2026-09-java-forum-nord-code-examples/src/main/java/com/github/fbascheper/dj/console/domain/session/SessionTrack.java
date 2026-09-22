package com.github.fbascheper.dj.console.domain.session;

import com.github.fbascheper.dj.console.domain.util.ValidatingBuilder;
import com.github.fbascheper.dj.console.domain.library.*;
import jakarta.validation.constraints.NotNull;
import lombok.Singular;
import java.time.Duration;
import java.util.List;

/** Session-local copy of a library Track with editable cue points. */
@lombok.Builder(toBuilder = true, buildMethodName = "internalBuild", builderClassName = "Builder")
public record SessionTrack(
        @NotNull Track.TrackId sourceTrackId,
        @NotNull Song song,
        @NotNull Duration length,
        @NotNull EnergyLevel energyLevel,
        @Singular List<CuePoint> cuePoints
) {
    public static class Builder implements ValidatingBuilder<SessionTrack> { }
    public static SessionTrack fromLibrary(Track t) {
        return SessionTrack.builder()
            .sourceTrackId(t.id())
            .song(t.song())
            .length(t.length())
            .energyLevel(t.energyLevel())
            .cuePoints(t.cuePoints())
            .build();
    }
}
