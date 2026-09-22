package com.github.fbascheper.dj.console.rest.dto;

import java.util.UUID;

public record RestSongDto(
        UUID songId,
        String title,
        String audioFile,
        RestArtistDto artist
) {
}
