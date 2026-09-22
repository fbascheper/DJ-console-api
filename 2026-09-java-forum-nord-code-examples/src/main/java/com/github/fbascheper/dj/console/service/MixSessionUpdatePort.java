package com.github.fbascheper.dj.console.service;

import com.github.fbascheper.dj.console.domain.session.MixSession;

/**
 * Output port (secondary/driven): notifies subscribers when a {@link MixSession} is updated.
 *
 * <p>The service layer depends on this interface; the GraphQL adapter implements it
 * via a Reactor hot multicast sink.
 */
public interface MixSessionUpdatePort {

    void publish(MixSession session);
}
