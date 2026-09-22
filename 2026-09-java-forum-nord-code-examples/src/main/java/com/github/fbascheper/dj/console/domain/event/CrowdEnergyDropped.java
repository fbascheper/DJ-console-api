package com.github.fbascheper.dj.console.domain.event;

import java.time.LocalDateTime;

/** Crowd energy drops — the DJ responds by escalating one energy level via {@link com.github.fbascheper.dj.console.domain.session.EnergyLevel#next()}. */
public record CrowdEnergyDropped(

        LocalDateTime occurredAt

) implements CrowdEvent { }
