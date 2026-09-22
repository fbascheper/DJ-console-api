package com.github.fbascheper.dj.console.domain.library;

import com.github.fbascheper.dj.console.domain.util.ValidatingBuilder;
import lombok.Singular;

import java.util.List;

/**
 * A music library represents the set of all tracks available
 * to DJs and MixSessions. Each Track contains its Song and Artist,
 * so the library structure is rooted in Tracks.
 */
@lombok.Builder(toBuilder = true, buildMethodName = "internalBuild", builderClassName = "Builder")
public record MusicLibrary(

        @Singular
        List<Artist> artists,

        @Singular
        List<Song> songs,

        @Singular
        List<Track> tracks
) {

    public static class Builder implements ValidatingBuilder<MusicLibrary> {
        // Lombok generates everything else
    }
}