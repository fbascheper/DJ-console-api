# `domain.event` - Crowd Event Model

## Purpose

This package contains the event types that drive behavior inside the `MixSession` aggregate.

## Event Hierarchy

`CrowdEvent` variants include:

- `CrowdCheered`
- `CrowdEnergyDropped`
- `DancefloorEmptied`
- `DancefloorFilledUp`
- `RequestFromAudienceReceived`

## DDD Role

- Events are part of the `MixSession` behavioral flow.
- They are interpreted by `MixSession.applyEvent(...)`.
- They are not aggregate roots on their own in this model.

## Semantics

- Events represent observed audience signals.
- The aggregate translates those signals into next-track decisions while preserving session invariants.

