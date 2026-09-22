# `domain.library` - Music Library Reference Catalog

## Purpose

This package contains the read-only reference catalog of all music available to DJs.
It is a supporting domain concept, not a behavioural aggregate root: it has no lifecycle
events and does not enforce cross-entity invariants.

## Key types

- **`MusicLibrary`** — the catalog aggregate; holds lists of `Artist`, `Song`, and `Track`.
  Stored as a single JSONB document in the `music_libraries` table.
- **`Track`** — a playable version of a `Song` annotated with DJ metadata: `duration`,
  `EnergyLevel`, and a list of `CuePoint`s. `Track.TrackId` is the strongly-typed identity
  used when building a `MixSession`.
- **`Song`** — a title and a reference to its `Artist`. Embedded inside `Track`.
- **`Artist`** — a name and a strongly-typed `ArtistId`. Embedded inside `Song`.
- **`CuePoint`** — a labelled timestamp within a track, used by DJ tooling (e.g. `intro`,
  `chorus`, `drop`).

## DDD role

`MusicLibrary` is a _reference aggregate_ accessed only through the `MusicLibraryLookup`
secondary port (defined in the parent `domain/` package). `MixSession` reads track data from
the library during session setup but never writes back to it. This strict unidirectional
read direction keeps the session aggregate self-contained and testable in isolation.

## Invariants

- A `Track` must reference a `Song` that is present in the library.
- A `Song` must reference an `Artist` that is present in the library.

These are enforced by `MusicLibrarySmartLoader` at startup and are structural constraints,
not runtime-enforced domain invariants.

## Persistence

`MusicLibraryEntity` stores the entire `MusicLibrary` as a `@JdbcTypeCode(SqlTypes.JSON)`
JSONB column. There is exactly one library row; the loader uses a fixed well-known UUID as
its primary key.

Seed data is loaded by `MusicLibrarySmartLoader` (SmartLifecycle phase 10) from:

```
src/main/resources/seed/library/artists.json
src/main/resources/seed/library/songs.json
src/main/resources/seed/library/tracks.json
```
