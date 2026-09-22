package com.github.fbascheper.dj.console.domain.event;

import java.time.LocalDateTime;

/** Dancefloor empties — the DJ immediately selects the highest-energy track available to recapture the crowd. */
public record DancefloorEmptied(

        LocalDateTime occurredAt

) implements CrowdEvent { }
