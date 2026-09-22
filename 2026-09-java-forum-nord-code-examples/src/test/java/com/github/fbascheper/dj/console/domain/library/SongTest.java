package com.github.fbascheper.dj.console.domain.library;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class SongTest {

    @Test
    void equalsAndHashCode_basedOnIdOnly() {
        EqualsVerifier.forClass(Song.class)
                .withOnlyTheseFields("id")
                .verify();
    }
}
