package com.github.fbascheper.dj.console.bootstrap.hibernate;

import com.github.fbascheper.dj.console.domain.event.CrowdCheered;
import com.github.fbascheper.dj.console.domain.event.CrowdEvent;
import com.github.fbascheper.dj.console.domain.event.RequestFromAudienceReceived;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression test for the Jackson deserialization failure on the second
 * {@code crowdCheered} mutation call.
 *
 * <p><b>Root cause:</b> {@link CrowdEvent} is a sealed interface. When the
 * first mutation stored the updated {@code MixSession} as JSONB, Jackson wrote
 * the concrete event (e.g. {@code CrowdCheered}) with <em>no type discriminator</em>
 * — just {@code {"occurredAt":"..."}}. On the next load from the JSONB column,
 * Hibernate's {@link CustomJacksonJsonFormatMapper} tried to deserialize that
 * JSON back as {@code List<CrowdEvent>} and threw
 * {@code InvalidDefinitionException: Cannot construct instance of CrowdEvent}.
 *
 * <p><b>Fix:</b> Annotate {@code CrowdEvent} with {@code @JsonTypeInfo} and
 * {@code @JsonSubTypes} so Jackson writes and reads a {@code "@type"} field
 * alongside every event.
 */
class CustomJacksonJsonFormatMapperTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Test
    void crowdEvent_roundTripsAsInterfaceType_preservingConcreteSubtype() throws Exception {
        // Arrange
        CrowdEvent original = new CrowdCheered(LocalDateTime.of(2026, 3, 19, 20, 0));

        // Act — serialize using the *interface* type, as Hibernate does when
        // writing MixSession.crowdEvents: List<CrowdEvent> to the JSONB column
        String json = mapper.writerFor(CrowdEvent.class).writeValueAsString(original);
        CrowdEvent restored = mapper.readerFor(CrowdEvent.class).readValue(json);

        // Assert — the concrete type must survive the round-trip; without
        // @JsonTypeInfo this throws InvalidDefinitionException on readValue
        assertThat(restored)
                .as("concrete type must be preserved through JSON round-trip")
                .isInstanceOf(CrowdCheered.class);
        assertThat(((CrowdCheered) restored).occurredAt())
                .isEqualTo(((CrowdCheered) original).occurredAt());
    }

    @Test
    void crowdEventList_roundTripsPreservingAllSubtypes() throws Exception {
        // Arrange — multiple subtypes in one list, matching MixSession.crowdEvents
        List<CrowdEvent> original = List.of(
                new CrowdCheered(LocalDateTime.of(2026, 3, 19, 20, 0)),
                new RequestFromAudienceReceived("Chrome Thunder", LocalDateTime.of(2026, 3, 19, 20, 5))
        );

        var listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, CrowdEvent.class);

        // Act
        String json = mapper.writerFor(listType).writeValueAsString(original);
        List<CrowdEvent> restored = mapper.readerFor(listType).readValue(json);

        // Assert
        assertThat(restored).hasSize(2);
        assertThat(restored.get(0)).isInstanceOf(CrowdCheered.class);
        assertThat(restored.get(1)).isInstanceOf(RequestFromAudienceReceived.class);
    }

}
