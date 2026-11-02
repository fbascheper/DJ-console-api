
package com.github.fbascheper.dj.console.domain.session;

import java.util.Optional;
import java.util.UUID;

/**
 * Secondary port (outbound) for persisting and retrieving {@link MixSession} aggregates.
 *
 * <p>Only the aggregate root is accessed through this interface. Entities nested inside
 * {@code MixSession} (tracks, crowd events) are never persisted individually.
 *
 * <p>Implementations reside in the {@code infrastructure} package.
 */
public interface MixSessionRepository {

    /**
     * Returns the "current" active session according to the domain policy
     * (currently: the most recently stored session).
     * Returns {@link Optional#empty()} when no session exists.
     */
    Optional<MixSession> findCurrent();

    /**
     * Loads a session by its strongly-typed UUID identity.
     * Returns {@link Optional#empty()} when no session with that ID exists.
     */
    Optional<MixSession> findById(UUID id);

    /**
     * Persists the given session (insert or update) and returns the saved instance.
     */
    MixSession save(MixSession session);
}
