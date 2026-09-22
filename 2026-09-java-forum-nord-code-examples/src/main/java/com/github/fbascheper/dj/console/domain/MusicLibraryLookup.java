package com.github.fbascheper.dj.console.domain;

import com.github.fbascheper.dj.console.domain.library.Track;
import com.github.fbascheper.dj.console.domain.session.SessionTrack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Secondary port (outbound) for reading tracks from the music library.
 *
 * <p>This interface is the domain-side contract for the music catalog read path.
 * Implementations live in the {@code infrastructure} package and are invisible
 * to the domain model.
 *
 * <p>The library is treated as a read-only reference catalog: the domain never
 * writes back to it.
 */
public interface MusicLibraryLookup {

    /**
     * Returns the track with the given ID, or {@link java.util.Optional#empty()} if it
     * does not exist in the library.
     */
    Optional<Track> findTrackById(UUID trackId);

    /**
     * Returns {@code true} if every ID in the supplied list corresponds to a
     * known track in the library.
     */
    boolean existAllTrackIds(List<UUID> trackIds);

    /**
     * Full catalog as {@link SessionTrack}s — used to populate {@link com.github.fbascheper.dj.console.domain.session.DiscJockey#trackLibrary()}.
     */
    List<SessionTrack> allSessionTracks();
}
