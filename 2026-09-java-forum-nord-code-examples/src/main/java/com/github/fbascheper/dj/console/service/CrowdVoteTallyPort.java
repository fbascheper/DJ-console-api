package com.github.fbascheper.dj.console.service;

/**
 * Output port (secondary/driven): notifies subscribers when the crowd-vote tally changes.
 *
 * <p>The service layer depends on this interface; the GraphQL adapter implements it
 * via a Reactor hot multicast sink.
 */
public interface CrowdVoteTallyPort {

    void publish(VoteTally tally);
}
