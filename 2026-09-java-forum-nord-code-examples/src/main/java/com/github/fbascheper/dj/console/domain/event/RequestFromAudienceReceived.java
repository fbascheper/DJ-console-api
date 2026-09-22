package com.github.fbascheper.dj.console.domain.event;

import java.time.LocalDateTime;

/** Audience requests a specific track by title — played if found in the DJ's library, otherwise falls back to average-energy selection. */
public record RequestFromAudienceReceived(

        String trackName,
        LocalDateTime occurredAt

) implements CrowdEvent {
}
