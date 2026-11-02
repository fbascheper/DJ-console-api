package com.github.fbascheper.dj.console.rest;

import com.github.fbascheper.dj.console.domain.session.MixSession;
import com.github.fbascheper.dj.console.domain.session.SessionTrack;
import com.github.fbascheper.dj.console.rest.dto.CurrentMixSessionRestResponse;
import com.github.fbascheper.dj.console.rest.dto.RestArtistDto;
import com.github.fbascheper.dj.console.rest.dto.RestCuePointDto;
import com.github.fbascheper.dj.console.rest.dto.RestPlayedTrackDto;
import com.github.fbascheper.dj.console.rest.dto.RestSongDto;

final class MixSessionRestMapper {

    private MixSessionRestMapper() {
    }

    static CurrentMixSessionRestResponse toFatResponse(MixSession session) {
        var dj = session.dj();
        var played = session.tracks().stream()
                .map(MixSessionRestMapper::mapPlayedTrack)
                .toList();
        return new CurrentMixSessionRestResponse(
                "REST — single GET returns a fixed fat document (demo: contrast with GraphQL)",
                session.id().value(),
                session.getStatus().name(),
                dj.name(),
                dj.trackLibrary().size(),
                played,
                session.crowdEvents()
        );
    }

    private static RestPlayedTrackDto mapPlayedTrack(SessionTrack t) {
        var song = t.song();
        var artist = song.artist();
        return new RestPlayedTrackDto(
                t.sourceTrackId().value(),
                new RestSongDto(
                        song.id().value(),
                        song.title(),
                        song.audioFile(),
                        new RestArtistDto(artist.id().value(), artist.name())
                ),
                t.length().toString(),
                t.energyLevel().name(),
                t.cuePoints().stream()
                        .map(cp -> new RestCuePointDto(cp.label(), cp.elapsedTime().toString()))
                        .toList()
        );
    }
}
