package com.github.fbascheper.dj.console.domain.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;

/**
 * Sealed domain event hierarchy representing an observable audience signal during a DJ set.
 *
 * <p>Events are applied to the {@link com.github.fbascheper.dj.console.domain.session.MixSession}
 * aggregate via {@code MixSession.applyEvent(CrowdEvent)}, which uses a Java sealed-type
 * {@code switch} expression to decide the next track.
 *
 * <p>Events are value objects — they carry only the {@code occurredAt} timestamp and any
 * additional context needed by the track-selection algorithm.
 *
 * <p>Permitted subtypes and their track-selection semantics:
 * <ul>
 *   <li>{@link CrowdCheered} — audience approves; continue at current average energy level</li>
 *   <li>{@link CrowdEnergyDropped} — energy dip detected; escalate one energy level via {@code EnergyLevel.next()}</li>
 *   <li>{@link DancefloorEmptied} — floor cleared; immediately select the highest-energy available track</li>
 *   <li>{@link DancefloorFilledUp} — floor full again; match current average energy level</li>
 *   <li>{@link RequestFromAudienceReceived} — audience requests a track by title; falls back
 *       to average-energy selection when the requested title is unknown</li>
 * </ul>
 *
 * <p>JSON persistence (JSONB) requires a type discriminator — see {@link JsonTypeInfo} /
 * {@link JsonSubTypes}. Records without {@code type} in legacy rows deserialize as
 * {@link CrowdCheered} via {@link JsonTypeInfo#defaultImpl()}.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        defaultImpl = CrowdCheered.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CrowdCheered.class, name = "CROWD_CHEERED"),
        @JsonSubTypes.Type(value = CrowdEnergyDropped.class, name = "CROWD_ENERGY_DROPPED"),
        @JsonSubTypes.Type(value = DancefloorEmptied.class, name = "DANCEFLOOR_EMPTIED"),
        @JsonSubTypes.Type(value = DancefloorFilledUp.class, name = "DANCEFLOOR_FILLED_UP"),
        @JsonSubTypes.Type(value = RequestFromAudienceReceived.class, name = "REQUEST_FROM_AUDIENCE_RECEIVED")

})
public sealed interface CrowdEvent
        permits CrowdEnergyDropped, DancefloorFilledUp, DancefloorEmptied, CrowdCheered, RequestFromAudienceReceived {

    LocalDateTime occurredAt();
}

