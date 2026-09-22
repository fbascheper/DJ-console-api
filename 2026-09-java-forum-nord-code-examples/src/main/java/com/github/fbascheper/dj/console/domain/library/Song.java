package com.github.fbascheper.dj.console.domain.library;

import com.github.fbascheper.dj.console.domain.util.ValidatingBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * A song performed by a specific {@link Artist}.
 * Immutable and identified by a SongId.
 */
@lombok.Builder(
        toBuilder = true,
        buildMethodName = "internalBuild",
        builderClassName = "Builder"
)
public record Song(

        @NotNull
        SongId id,

        @NotNull
        Artist artist,

        @NotBlank
        String title,

        String audioFile

) {

    /**
     * Lombok builder extended with domain validation through {@link ValidatingBuilder}.
     * The builder auto‑assigns a UUID id if not provided.
     */
    public static class Builder implements ValidatingBuilder<Song> {

        private SongId id;
        private Artist artist;
        private String title;
        private String audioFile;

        @Override
        public void onSetDefaultValues() {
            if (id == null) {
                id = new SongId(UUID.randomUUID());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Song other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Strongly‑typed identity for Song.
     */
    public record SongId(@NotNull UUID value) {
    }
}