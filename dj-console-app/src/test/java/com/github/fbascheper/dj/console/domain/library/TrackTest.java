package com.github.fbascheper.dj.console.domain.library;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class TrackTest {

    @Test
    void equalsAndHashCode_basedOnIdOnly() {
        EqualsVerifier.forClass(Track.class)
                .withOnlyTheseFields("id")
                .verify();
    }
}
