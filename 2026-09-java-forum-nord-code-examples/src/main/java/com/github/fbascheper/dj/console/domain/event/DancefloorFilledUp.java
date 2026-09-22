package com.github.fbascheper.dj.console.domain.event;

import java.time.LocalDateTime;

/** Dancefloor fills up again — the DJ continues matching the current average energy level. */
public record DancefloorFilledUp(

        LocalDateTime occurredAt

) implements CrowdEvent { }
