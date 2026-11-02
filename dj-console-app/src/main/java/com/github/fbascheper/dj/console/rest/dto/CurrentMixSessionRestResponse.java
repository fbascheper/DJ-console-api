package com.github.fbascheper.dj.console.rest.dto;

import com.github.fbascheper.dj.console.domain.event.CrowdEvent;

import java.util.List;
import java.util.UUID;

/**
 * Fixed-shape "kitchen sink" JSON for {@code GET /api/sessions/current}.
 * <p>
 * Contrasts with GraphQL: every REST client receives the same fat payload
 * (played tracks with cue points, full crowd timeline, DJ library size, etc.)
 * even when a client only needs “title of the last track”.
 */
public record CurrentMixSessionRestResponse(
        String apiStyle,
        UUID sessionId,
        String status,
        String discJockeyName,
        int discJockeyLibraryTrackCount,
        List<RestPlayedTrackDto> playedTracks,
        List<CrowdEvent> crowdEventTimeline
) {
}
