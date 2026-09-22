package com.github.fbascheper.dj.console.bootstrap.dto;

import com.github.fbascheper.dj.console.domain.library.Track;

import java.util.List;

/**
 * Seed DTO for a {@link com.github.fbascheper.dj.console.domain.session.DiscJockey}'s
 * personal track library.
 *
 * <p>Loaded from {@code classpath:seed/discjockeys/*.json} at startup (phase 15, before
 * mix-session seeds at phase 20). Each DJ seed file declares the subset of tracks from
 * the global music library that this DJ has in their personal catalog.
 */
public record DiscJockeySeed(
        String name,
        List<Track.TrackId> trackIds
) {
}
