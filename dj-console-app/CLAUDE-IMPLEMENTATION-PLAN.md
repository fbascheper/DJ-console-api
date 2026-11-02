# Claude Code — Implementation Plan for DJ Console Demo Code
# Companion to: 01-presentation/slides/00-authoring-plan.md in the private presentation repo
#
# This document is written and owned by Claude Code.
# It records outstanding work only. Completed work is deleted, not archived —
# the code and git history are the record of what was done.
#
# All changes are in: dj-console-app/
# Source root shorthand: src/main/java/com/github/fbascheper/dj/console/
# Test root shorthand:   src/test/java/com/github/fbascheper/dj/

---

## 1. Outstanding Work

None. Every item from the previous round of the authoring plan is implemented
and verified — `MixSession.toString()`, `EnergyTrendAnalyser`, `DjSessionContext`,
`DjConsoleClientDemo`, `spring.threads.virtual.enabled`, `events.getLast()` in
`MixSession.Status.of()`, and the seed-loader stream refactors.

Add new items here as the authoring plan calls for them.

---

## 2. Standing Notes — Slide Snippets That Deliberately Differ From The Code

These are **not** defects. Do not "fix" the production code to match the slides.

### Record pattern destructuring — Act 6 slide 04, Act 9 slide 02

Slides may show the record-pattern form:

```java
case RequestFromAudienceReceived(String name, _) -> dj.findTrackByTitle(name)
```

Production code uses the named binding, which is arguably more readable:

```java
case RequestFromAudienceReceived e -> dj.findTrackByTitle(e.trackName())
```

Both compile on Java 27. The slide author (Copilot) may show either form.
**No code change required in the app.**

### `getFirst()` / `getLast()` — Act 5 slide 04

`DiscJockey.java` uses `orElseGet(trackLibrary::getFirst)` and
`MixSession.Status.of()` uses `events.getLast()`, so slides can show both
SequencedCollection accessors (Java 21) from the same `domain/session/` package.

---

## 3. Standing Requirements

- TDD is mandatory: failing test first, then implementation.
- New code needs ≥ 75% line coverage (project rule).
- After changing domain classes, run the `presentation-reviewer` agent per
  `.claude/rules/presentation-review.md`.
- After changing GraphQL schema, controllers, or publishers, run the
  `spring-graphql-reviewer` agent per `.claude/rules/dj-console-api/graphql-review.md`.
