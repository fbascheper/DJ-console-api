package com.github.fbascheper.dj.console.bootstrap.dto;

import com.github.fbascheper.dj.console.domain.library.Track;
import com.github.fbascheper.dj.console.domain.session.MixSession;
import com.github.fbascheper.dj.console.domain.event.CrowdEvent;

import java.util.List;

/**
 * Seed DTO for a {@link MixSession}.
 *
 * <p>The {@code dj} field carries only the DJ's name as a reference; the full
 * {@link com.github.fbascheper.dj.console.domain.session.DiscJockey} (including their
 * personal track library) is loaded separately from
 * {@code classpath:seed/discjockeys/*.json} by
 * {@link com.github.fbascheper.dj.console.bootstrap.MixSessionSmartLoader}.
 *
 * <p>The {@code trackIds} are the tracks pre-loaded into the session (the initial
 * played/queued subset of the DJ's library).
 */
public record MixSessionSeed(
        MixSession.MixSessionId id,
        DjRef dj,
        List<Track.TrackId> trackIds,
        List<CrowdEvent> crowdEvents
) {
    /**
     * Lightweight DJ reference used in session seed files.
     * The full DJ personal library is in {@code seed/discjockeys/<name>.json}.
     */
    public record DjRef(String name) {
    }
}
