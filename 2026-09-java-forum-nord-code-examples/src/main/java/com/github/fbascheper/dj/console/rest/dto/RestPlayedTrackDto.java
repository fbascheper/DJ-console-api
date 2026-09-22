package com.github.fbascheper.dj.console.rest.dto;

import java.util.List;
import java.util.UUID;

public record RestPlayedTrackDto(
        UUID sourceTrackId,
        RestSongDto song,
        /** ISO-8601 duration, e.g. PT3M45S — often irrelevant for a projector vote UI but shipped with REST. */
        String lengthIso8601,
        String energyLevel,
        List<RestCuePointDto> cuePoints
) {
}
