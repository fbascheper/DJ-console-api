package com.github.fbascheper.dj.console.domain.event;

import java.time.LocalDateTime;

/** Audience cheers — the DJ continues at the current average energy level. */
public record CrowdCheered(

        LocalDateTime occurredAt

) implements CrowdEvent { }
