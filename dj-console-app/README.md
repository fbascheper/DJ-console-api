# DJ Console API

Java 27 + Spring Boot 4 + Spring GraphQL demo for **W-JAX 2026** (3 November 2026).
Talk: _Effective Java, revisited for Java 25 +_.

## What this demo shows

The running example that makes each Effective Java item concrete in an enterprise
application, rather than in an isolated snippet:

- How modern Java language features — records, sealed interfaces, pattern matching for
  `switch`, sequenced collections, unnamed patterns — replace the boilerplate that
  *Effective Java* had to prescribe by hand.
- How domain mutations (crowd events) are expressed as GraphQL mutations
  that trigger DDD aggregate behaviour inside `MixSession`.
- How hexagonal architecture separates the domain model from Spring infrastructure.
- How a REST-style multi-call flow (`GET /mixSession`, `GET /tracks`, `GET /artists/{id}`)
  collapses into a single GraphQL query that returns exactly the fields the client needs.

## Quick start

1. Start PostgreSQL as described in [`README-pgsql.md`](README-pgsql.md).
2. From this directory, run `mvn spring-boot:run` (or `mvn verify` to build and test).
3. Open GraphiQL at `http://localhost:8080/graphiql`.

The [repository README](../README.md) lists the build and test commands, including mutation testing.

## Architecture overview

The application follows a hexagonal architecture: the domain model has no Spring
dependencies, and Spring lives in the infrastructure adapters and the application service.
Each package is documented in a README next to its code:

| Package | README |
|---|---|
| `domain/` | [Aggregate root rationale](src/main/java/com/github/fbascheper/dj/console/domain/README.md) |
| `domain/session/` | [MixSession aggregate](src/main/java/com/github/fbascheper/dj/console/domain/session/README.md) |
| `domain/library/` | [Music library reference catalog](src/main/java/com/github/fbascheper/dj/console/domain/library/README.md) |
| `domain/event/` | [Crowd event hierarchy](src/main/java/com/github/fbascheper/dj/console/domain/event/README.md) |
| `infrastructure/` | [Secondary adapters & JSONB persistence](src/main/java/com/github/fbascheper/dj/console/infrastructure/README.md) |
| `service/` | [Application service & exceptions](src/main/java/com/github/fbascheper/dj/console/service/README.md) |

## GraphQL endpoint

- **GraphiQL playground:** `http://localhost:8080/graphiql` (requires the app to be running)
- **Schema:** `src/main/resources/graphql/schema.graphqls`

Example queries to try in the demo:

```graphql
# Fetch the current session with all tracks
query CurrentMixSession {
  currentMixSession {
    id
    status
    tracks {
      id
      energyLevel
      song { title artist { name } }
    }
  }
}

# Fetch only the last track (avoids overfetching)
query CurrentTrack {
  currentMixSession {
    tracks(last: 1) {
      energyLevel
      song { title artist { name } }
    }
  }
}

# Trigger a crowd-cheered event
mutation CrowdCheered($id: ID!) {
  crowdCheered(id: $id) {
    status
    tracks(last: 1) {
      song { title }
      energyLevel
    }
  }
}
```

## Database

See `README-pgsql.md` for PostgreSQL setup with Podman, schema/seed behaviour, and optional DB reset.

- **Clean dev state:** with default `ddl-auto: create`, restarting the API recreates tables and reloads seeds.
- **Reset the container only:** follow [Reset Postgres](README-pgsql.md#reset-postgres-optional) in `README-pgsql.md`.
