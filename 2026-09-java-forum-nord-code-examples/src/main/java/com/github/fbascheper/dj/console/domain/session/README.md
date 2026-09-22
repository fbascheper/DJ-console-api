# `domain.session` - Aggregate Root `MixSession`

## Purpose

`MixSession` models one DJ set and is the behavioral consistency boundary for event-driven track decisions.

## Aggregate Root

- **Root:** `MixSession`
- **Boundary:** application of crowd events, next-track decisioning, and session status progression

## Main Model Elements

- `MixSession`: root entity with `id`, session tracks, and crowd events.
- `SessionTrack`: session-local track representation used while mixing.
- `EnergyLevel`: domain value used in track-selection rules.
- `DiscJockey`: performer context participating in sessions.
- `DanceEvent`: event container where a DJ can run one or more sessions.
- `MixSessionRepository`: persistence contract for aggregate access.

## Invariants

- A crowd event application updates session state atomically.
- Track progression remains consistent with energy-level rules.
- Session status (`WARM_UP`, `PEAK`, `COOL_DOWN`) is derived from the aggregate state.

## Relationships and Cardinalities

- A `DiscJockey` can perform at `0..n` `DanceEvent`s.
- Each `DanceEvent` has `1..n` `MixSession`s.
- Each `MixSession` uses a subset of the DJ personal library.

## Notes

`MixSession` is intentionally documented as an aggregate root because it owns the behavior and invariants that span events, tracks, and decision logic.

