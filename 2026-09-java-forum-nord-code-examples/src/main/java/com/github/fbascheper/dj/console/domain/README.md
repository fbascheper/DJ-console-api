# Domain-Driven Design: Why `MixSession` Is the Aggregate Root

This document explains the DDD rules and reasoning behind choosing **MixSession** as the **Aggregate Root** in the Disc Jockey Console domain model.

---

## ✔ 1. Ownership of Invariants
`MixSession` enforces aggregate‑wide rules that cross multiple domain objects:

- deciding the next track
- calculating the average energy level
- applying crowd events
- deriving session status (`WARM_UP`, `PEAK`, `COOL_DOWN`)

These rules depend on **multiple entities working together**, not on any single `Track`, `Song`, or `Artist`.
Therefore, the entity responsible for these invariants **must be the Aggregate Root**.

---

## ✔ 2. Transaction Boundary
A single domain action — for example:

```java
mixSession.applyEvent(new CrowdEvent(...));
```

This action modifies:

- the list of events
- the list of played tracks
- the session’s derived status
- potentially requires decision logic from `DiscJockey`

All of this must happen **atomically**.

In DDD, a transactional operation must occur **within one aggregate**, so `MixSession` naturally becomes the **transaction boundary** and therefore the **Aggregate Root**.

---

## ✔ 3. Consistency Boundary
The model must NOT allow inconsistencies such as:

- tracks updated without corresponding crowd events
- energy level changes without consistent next‑track logic

Only `MixSession` sees the *full picture*:

- events
- tracks
- energy progression
- decisions made by the DJ

Thus, `MixSession` is the **consistency boundary**, which is the defining property of an aggregate.

---

## ✔ 4. Entity Lifecycle
`MixSession` has a natural lifecycle:

- **start** (session begins)
- **evolution** (tracks & events accumulate)
- **end** (session concludes)

Other entities such as:

- `Artist`
- `Song`
- `Track`

…do not have such bounded lifecycles.

Anything with a meaningful lifecycle is a strong Aggregate Root candidate — here, that is **MixSession**.

---

## ✔ 5. Persistence Boundary
From a repository perspective, the **only correct repository** for the aggregate is:

```
MixSessionRepository
```

All persistence for:

- Tracks
- CuePoints
- CrowdEvents
- DiscJockey decisions

…flows **through the MixSession root**, not separate repositories.

This ensures:

- atomic updates
- transactional correctness
- no partial writes of cross‑entity invariants

---

# ✅ Conclusion
Following DDD rules, **MixSession is the only correct Aggregate Root** because it:

- owns cross‑entity invariants
- defines the transactional boundary
- maintains consistency
- has a meaningful domain lifecycle
- forms the persistence boundary

Other domain objects (`Artist`, `Song`, `Track`) exist *inside* the aggregate, but do **not** coordinate behavior.

`MixSession` is therefore the **center of behavioral gravity** in the domain.
