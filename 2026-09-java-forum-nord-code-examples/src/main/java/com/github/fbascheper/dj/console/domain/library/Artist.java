package com.github.fbascheper.dj.console.domain.library;

import com.github.fbascheper.dj.console.domain.util.ValidatingBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * The performing artist of one or more songs.
 * Immutable and identified by an ArtistId.
 */
@lombok.Builder(toBuilder = true, buildMethodName = "internalBuild", builderClassName = "Builder")
public record Artist(

        @NotNull
        ArtistId id,

        @NotBlank
        String name
) {
    public static class Builder implements ValidatingBuilder<Artist> {
        private ArtistId id;     // Lombok will populate these
        private String name;

        // Lombok generates setters; we override only the hook:
        @Override
        public void onSetDefaultValues() {
            if (id == null) {
                id = new ArtistId(UUID.randomUUID());
            }
        }

        // Lombok generates internalBuild(); no need to write it manually.
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Artist other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public record ArtistId(@NotNull UUID value) {
    }
}