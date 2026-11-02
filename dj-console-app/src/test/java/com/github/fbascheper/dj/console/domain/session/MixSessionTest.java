package com.github.fbascheper.dj.console.domain.session;

import com.github.fbascheper.dj.console.domain.event.CrowdCheered;
import com.github.fbascheper.dj.console.domain.event.CrowdEnergyDropped;
import com.github.fbascheper.dj.console.domain.event.DancefloorEmptied;
import com.github.fbascheper.dj.console.domain.event.DancefloorFilledUp;
import com.github.fbascheper.dj.console.domain.event.RequestFromAudienceReceived;
import com.github.fbascheper.dj.test.support.DiscJockeyTestObjects;
import com.github.fbascheper.dj.test.support.MixSessionTestObjects;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MixSessionTest {

    @Test
    void equalsAndHashCode_basedOnIdOnly() {
        EqualsVerifier.forClass(MixSession.class)
                .withOnlyTheseFields("id")
                .verify();
    }

    @Test
    void toString_shouldProduceReadableSummary() {
        var id = UUID.fromString("91af9cac-ce3a-55a9-85bf-a051d84d4a0d");
        var session = MixSession.builder()
                .id(new MixSession.MixSessionId(id))
                .dj(DiscJockeyTestObjects.djVortex())
                .build();

        assertThat(session.toString())
                .startsWith("MixSession[id=91af9cac")
                .contains("dj=DJ VORTEX")
                .contains("tracks=0")
                .contains("status=WARM_UP")
                .doesNotContain("trackLibrary");
    }

    @Test
    void status_afterCrowdCheered_isPeak() {
        var mixSession = MixSessionTestObjects.vortexEmptyMixSession();
        var updated = mixSession.applyEvent(new CrowdCheered(LocalDateTime.now()));
        assertThat(updated.getStatus()).isEqualTo(MixSession.Status.PEAK);
    }

    @Test
    void status_afterDancefloorFilledUp_isWarmUp() {
        var mixSession = MixSessionTestObjects.vortexEmptyMixSession();
        var updated = mixSession.applyEvent(new DancefloorFilledUp(LocalDateTime.now()));
        assertThat(updated.getStatus()).isEqualTo(MixSession.Status.WARM_UP);
    }

    @Test
    void status_afterDancefloorEmptied_isCoolDown() {
        var mixSession = MixSessionTestObjects.vortexEmptyMixSession();
        var updated = mixSession.applyEvent(new DancefloorEmptied(LocalDateTime.now()));
        assertThat(updated.getStatus()).isEqualTo(MixSession.Status.COOL_DOWN);
    }

    @Test
    void status_afterCrowdEnergyDropped_isCoolDown() {
        var mixSession = MixSessionTestObjects.vortexEmptyMixSession();
        var updated = mixSession.applyEvent(new CrowdEnergyDropped(LocalDateTime.now()));
        assertThat(updated.getStatus()).isEqualTo(MixSession.Status.COOL_DOWN);
    }

    @Test
    void applyEvent_requestFromAudienceReceived_chromeThunder() {
        // Arrange
        var mixSession = MixSessionTestObjects.vortexEmptyMixSession();
        var occurredAt = LocalDateTime.now().minusMinutes(1);
        var event = new RequestFromAudienceReceived("Chrome Thunder", occurredAt);

        // Act
        var updatedMixSession = mixSession.applyEvent(event);

        // Assert
        assertThat(updatedMixSession.tracks()).hasSize(1);
        assertThat(updatedMixSession.tracks().getFirst().song().title()).isEqualTo("Chrome Thunder");
    }

    @Test
    void applyEvent_requestFromAudienceReceived_chromeThunderAndWakeMeUp() {
        // Arrange
        var mixSession = MixSessionTestObjects.vortexEmptyMixSession();
        var firstEventTime = LocalDateTime.now().minusMinutes(1);
        var firstEvent = new RequestFromAudienceReceived("Chrome Thunder", firstEventTime);

        // Act - First event
        var afterFirstEvent = mixSession.applyEvent(firstEvent);

        var secondEventTime = firstEventTime.plusSeconds(3);
        var secondEvent = new RequestFromAudienceReceived("Afterburn", secondEventTime);
        var afterSecondEvent = afterFirstEvent.applyEvent(secondEvent);

        // Assert
        assertThat(afterSecondEvent.tracks()).hasSize(2);
        assertThat(afterSecondEvent.tracks().get(0).song().title()).isEqualTo("Chrome Thunder");
        assertThat(afterSecondEvent.tracks().get(1).song().title()).isEqualTo("Afterburn");
    }

    @Test
    void applyEvent_requestFromAudienceReceived_unknownTrackFallsBackToAverageEnergyLevel() {
        // Arrange
        var mixSession = MixSessionTestObjects.vortexEmptyMixSession();
        var firstEventTime = LocalDateTime.now().minusMinutes(1);
        var firstEvent = new RequestFromAudienceReceived("Chrome Thunder", firstEventTime);

        // Act - First event (LOW energy)
        var afterFirstEvent = mixSession.applyEvent(firstEvent);

        var secondEventTime = firstEventTime.plusSeconds(3);
        var secondEvent = new RequestFromAudienceReceived("Afterburn", secondEventTime);
        var afterSecondEvent = afterFirstEvent.applyEvent(secondEvent);

        // Store average energy level before requesting unknown track
        var averageLevelBeforeUnknownRequest = afterSecondEvent.getAverageEnergyLevel();

        var thirdEventTime = secondEventTime.plusSeconds(10);
        var thirdEvent = new RequestFromAudienceReceived("Something like suavemente ?!?", thirdEventTime);
        var afterThirdEvent = afterSecondEvent.applyEvent(thirdEvent);

        // Assert
        assertThat(afterThirdEvent.tracks()).hasSize(3);
        // The third track should be a MEDIUM energy track (matching the average level)
        assertThat(afterThirdEvent.tracks().get(2).energyLevel()).isEqualTo(EnergyLevel.MEDIUM);
        // The average energy level should remain unchanged
        assertThat(afterThirdEvent.getAverageEnergyLevel()).isEqualTo(averageLevelBeforeUnknownRequest);
    }

}