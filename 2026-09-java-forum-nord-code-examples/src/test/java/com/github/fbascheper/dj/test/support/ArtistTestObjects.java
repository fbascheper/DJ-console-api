package com.github.fbascheper.dj.test.support;

import com.github.fbascheper.dj.console.domain.library.Artist;

public enum ArtistTestObjects {

    ZEPHAR("ZEPH∆R"),
    NOVA_BLISS("NØVA BLISS"),
    AXIOM("AXIOM"),
    LUX_FADER("LUX FADER"),
    KROM("KRØM");

    private final Artist instance;

    ArtistTestObjects(String name) {
        this.instance = Artist.builder()
                .name(name)
                .build();
    }

    public Artist getInstance() {
        return instance;
    }
}
