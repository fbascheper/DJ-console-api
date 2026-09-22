
package com.github.fbascheper.dj.console.service;

import com.github.fbascheper.dj.console.domain.session.MixSession;

import java.util.UUID;

/**
 * Application service that coordinates use-cases for {@link MixSession}.
 *
 * <p>This is the primary inbound port for all session-related operations.
 * All methods are transactional; write operations acquire a write transaction,
 * read operations use a read-only transaction.
 *
 * <p>Domain invariants are enforced by {@link MixSession} itself — this service
 * only orchestrates retrieval, event application, and persistence.
 */
public interface MixSessionService {

    /**
     * Returns the currently active mix session.
     *
     * @throws com.github.fbascheper.dj.console.exception.MixSessionNotActiveException
     *         if no active session exists
     */
    MixSession getCurrentSession();

    /**
     * Returns the session identified by {@code id}.
     *
     * @throws com.github.fbascheper.dj.console.exception.MixSessionNotFoundException
     *         if no session with that ID exists
     */
    MixSession getSessionById(UUID id);

    /**
     * Applies a {@link com.github.fbascheper.dj.console.domain.event.CrowdCheered}
     * event to the session identified by {@code id}, persists the result, and
     * returns the updated session.
     *
     * @throws com.github.fbascheper.dj.console.exception.MixSessionNotFoundException
     *         if no session with that ID exists
     */
    MixSession applyCrowdCheered(UUID id);

    MixSession applyCrowdEnergyDropped(UUID id);

    MixSession applyDancefloorEmptied(UUID id);

    MixSession applyDancefloorFilledUp(UUID id);

    MixSession applyRequestFromAudience(UUID id, String trackName);

    /**
     * Applies the demo recovery action. Behavior intentionally mirrors crowd-cheered:
     * one high-energy recovery step, then publish to subscribers.
     */
    MixSession applyRecovery(UUID id);
}
