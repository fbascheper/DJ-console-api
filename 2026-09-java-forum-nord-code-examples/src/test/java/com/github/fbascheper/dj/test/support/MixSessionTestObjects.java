package com.github.fbascheper.dj.test.support;

import com.github.fbascheper.dj.console.domain.session.MixSession;

public final class MixSessionTestObjects {

    private MixSessionTestObjects() {
        // prevent instantiation
    }

    public static MixSession vortexEmptyMixSession() {
        var djVortex = DiscJockeyTestObjects.djVortex();

        return MixSession.builder()
                .dj(djVortex)
                .build();
    }

}
