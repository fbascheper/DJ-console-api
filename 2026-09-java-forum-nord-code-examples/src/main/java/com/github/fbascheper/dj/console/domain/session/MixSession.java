package com.github.fbascheper.dj.console.domain.session;

import com.github.fbascheper.dj.console.domain.util.ValidatingBuilder;
import com.github.fbascheper.dj.console.domain.event.CrowdCheered;
import com.github.fbascheper.dj.console.domain.event.CrowdEnergyDropped;
import com.github.fbascheper.dj.console.domain.event.CrowdEvent;
import com.github.fbascheper.dj.console.domain.event.DancefloorEmptied;
import com.github.fbascheper.dj.console.domain.event.DancefloorFilledUp;
import com.github.fbascheper.dj.console.domain.event.RequestFromAudienceReceived;
import jakarta.validation.constraints.NotNull;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The session in which a DJ mixes tracks.
 * AKA the "DJ Set".
 * <p>
 * This acts as the aggregate root for the domain model.
 */
@lombok.Builder(toBuilder = true, buildMethodName = "internalBuild", builderClassName = "Builder")
public record MixSession(

        @NotNull
        MixSessionId id,

        @NotNull
        DiscJockey dj,

        @Singular
        List<SessionTrack> tracks,

        @Singular
        List<CrowdEvent> crowdEvents
) {

    @SuppressWarnings("unused")
    public static class Builder implements ValidatingBuilder<MixSession> {
        // lombok will generate the constructor, setters, build method, etc.

        private MixSessionId id;

        @Override
        public void onSetDefaultValues() {
            if (id == null) {
                id = new MixSessionId(UUID.randomUUID());
            }
        }
    }

    public MixSession.Status getStatus() {
        return MixSession.Status.of(this);
    }

    @Override
    public String toString() {
        return "MixSession[id=%s, dj=%s, tracks=%d, status=%s]"
                .formatted(id.value(), dj.name(), tracks.size(), getStatus());
    }

    public MixSession applyEvent(CrowdEvent event) {
        var newEvents = copyWithNewElement(crowdEvents, event);
        var newTracks = copyWithNewElement(tracks, decideNextTrack(event));

        return new MixSession(id, dj, newTracks, newEvents);
    }

    private <T> List<T> copyWithNewElement(List<T> list, T newElement) {
        var newList = new ArrayList<>(list);
        newList.add(newElement);
        return newList;
    }

    private SessionTrack decideNextTrack(CrowdEvent event) {
        var averageLevel = getAverageEnergyLevel();

        return switch (event) {
            case CrowdCheered _ -> dj.findNextNewTrack(averageLevel, tracks);
            case CrowdEnergyDropped _ -> dj.findNextNewTrack(averageLevel.next(), tracks);
            case DancefloorEmptied _ -> dj.findNextNewTrackWithHighestEnergyLevel(tracks);
            case DancefloorFilledUp _ -> dj.findNextNewTrack(averageLevel, tracks);
            case RequestFromAudienceReceived e -> dj.findTrackByTitle(e.trackName())
                    .orElseGet(() -> dj.findNextNewTrack(averageLevel, tracks)
                    );
        };
    }

    EnergyLevel getAverageEnergyLevel() {
        return EnergyLevel.fromLevel(
                Math.round(
                        tracks.stream()
                                .mapToInt(track -> track.energyLevel().getIntensity())
                                .average()
                                .orElse(0)
                )
        );
    }

    public enum Status {
        PEAK(null, EnergyLevel.HIGH),
        WARM_UP(5, EnergyLevel.MEDIUM),
        COOL_DOWN(null, EnergyLevel.LOW);

        private final Number tracksPlayed;
        private final EnergyLevel energyLevel;

        Status(Number tracksPlayed, EnergyLevel energyLevel) {
            this.tracksPlayed = tracksPlayed;
            this.energyLevel = energyLevel;
        }

        /**
         * Status follows the latest crowd signal when present; otherwise it reflects set progress
         * (warm-up before enough tracks, then peak vs cool-down from average deck energy).
         */
        public static Status of(MixSession mixSession) {
            var events = mixSession.crowdEvents();
            if (!events.isEmpty()) {
                var last = events.getLast();
                return switch (last) {
                    case CrowdCheered _ -> PEAK;
                    case DancefloorFilledUp _ -> WARM_UP;
                    case DancefloorEmptied _ -> COOL_DOWN;
                    case CrowdEnergyDropped _ -> COOL_DOWN;
                    case RequestFromAudienceReceived _ -> COOL_DOWN;
                };
            }
            if (mixSession.tracks().size() < WARM_UP.tracksPlayed.intValue()) {
                return WARM_UP;
            }
            if (EnergyLevel.HIGH == mixSession.getAverageEnergyLevel()) {
                return PEAK;
            }
            return COOL_DOWN;
        }
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof MixSession other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /** Strongly-typed identity for MixSession */
    public record MixSessionId(@NotNull UUID value) {}

}
