package com.github.fbascheper.dj.test.support;

import com.github.fbascheper.dj.console.domain.library.CuePoint;
import com.github.fbascheper.dj.console.domain.session.EnergyLevel;
import com.github.fbascheper.dj.console.domain.library.Track;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static com.github.fbascheper.dj.test.support.CuePointTestObjects.defaultCuePointList;
import static com.github.fbascheper.dj.test.support.SongTestObjects.*;

public enum TrackTestObjects {

    TRACK_CHROME_THUNDER(CHROME_THUNDER, Duration.ofSeconds(5), EnergyLevel.LOW, defaultCuePointList()),

    TRACK_AFTERBURN(AFTERBURN, Duration.ofSeconds(5), EnergyLevel.HIGH, defaultCuePointList()),

    TRACK_NEON_REQUIEM(NEON_REQUIEM, Duration.ofSeconds(90), EnergyLevel.HIGH, defaultCuePointList()),
    TRACK_SKYFALL_PROTOCOL(SKYFALL_PROTOCOL, Duration.ofSeconds(20), EnergyLevel.MEDIUM, defaultCuePointList()),
    TRACK_ECLIPSE_MODE(ECLIPSE_MODE, Duration.ofSeconds(10), EnergyLevel.MEDIUM, defaultCuePointList());


    private final Track instance;

    TrackTestObjects(SongTestObjects songTestObjects, Duration duration, EnergyLevel energyLevel, List<CuePoint> cuePoints) {

        this.instance = Track.builder()
                .song(songTestObjects.getInstance())
                .length(duration)
                .energyLevel(energyLevel)
                .cuePoints(cuePoints)
                .build();
    }

    public Track getInstance() {
        return instance;
    }


    public static List<Track> trackListWithEnergyLevel(int numberOfTracks, EnergyLevel energyLevel) {
        return Stream.iterate(0, i -> i < numberOfTracks, i -> i + 1)
                .map(_ ->
                        Track.builder()
                                .energyLevel(energyLevel)
                                .build())
                .toList();
    }
}
