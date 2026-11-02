package com.github.fbascheper.dj.console.domain.session;

import com.github.fbascheper.dj.console.domain.library.Artist;
import com.github.fbascheper.dj.console.exception.ModelValidationException;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

    @Test
    void artistWithValidNameIsCreatedSuccessfully() {
        Artist artist = Artist.builder()
                .name("Valid Artist Name")
                .build();

        assertEquals("Valid Artist Name", artist.name());
    }

    @Test
    void artistWithBlankNameThrowsException() {
        assertThrows(ModelValidationException.class, () ->
                Artist.builder()
                        .name("")
                        .build()
        );
    }

    @Test
    void artistWithNullNameThrowsException() {
        assertThrows(ModelValidationException.class, () ->
                Artist.builder()
                        .name(null)
                        .build()
        );
    }

    @Test
    void equalsAndHashCode_basedOnIdOnly() {
        EqualsVerifier.forClass(Artist.class)
                .withOnlyTheseFields("id")
                .verify();
    }

}