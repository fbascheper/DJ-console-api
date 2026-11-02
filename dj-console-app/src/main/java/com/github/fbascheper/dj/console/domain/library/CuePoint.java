package com.github.fbascheper.dj.console.domain.library;

import com.github.fbascheper.dj.console.domain.util.ValidatingBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;

/**
 * Represents a cue point in a track.
 * A cue point is a specific moment in the track that can be labeled for quick reference.
 */
@lombok.Builder(toBuilder = true, buildMethodName = "internalBuild", builderClassName = "Builder")
public record CuePoint(
        @NotBlank
        String label,

        @NotNull
        Duration elapsedTime
) {

        @SuppressWarnings("unused")
        public static class Builder implements ValidatingBuilder<CuePoint> {
                // lombok will generate the constructor, setters, build method, etc.
        }

}