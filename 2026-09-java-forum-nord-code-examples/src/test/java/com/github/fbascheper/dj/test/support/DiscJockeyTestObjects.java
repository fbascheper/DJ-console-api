package com.github.fbascheper.dj.test.support;

import com.github.fbascheper.dj.console.domain.session.DiscJockey;
import com.github.fbascheper.dj.console.domain.session.SessionTrack;

import java.util.EnumSet;

/**
 * Test objects for {@link DiscJockey}-instance related tests.
 */
public class DiscJockeyTestObjects {

    private DiscJockeyTestObjects() {
        // prevent instantiation
    }

    public static DiscJockey djVortex() {
        var trackList = EnumSet.allOf(TrackTestObjects.class).stream()
                .map(TrackTestObjects::getInstance)
                .map(SessionTrack::fromLibrary) // Convert to domain Track objects
                .toList(
                );

        return new DiscJockey("DJ VORTEX", trackList);
    }

}
