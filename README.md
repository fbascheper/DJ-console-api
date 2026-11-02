# From "Effective Java" to Java 25 and more — code examples

Source code for the talk **_From "Effective Java" to Java 25 and more — A Fresh Take on the Java Classic_**
by Frederieke Scheper, W-JAX, 3 November 2026.

*Effective Java* (3rd edition, 2018) has a Java 9 baseline. Since then Java has shipped 17 releases
and more than 250 JEPs. The talk goes through the book in eight acts and shows how modern Java
changes what its items tell you to do:

1. Creating & destroying objects: factories, builders, singletons, cleaners
2. Object methods & records
3. Classes & interfaces: sealed types, interface evolution
4. Generics & collections: gatherers, `SequencedCollection`
5. Enums & patterns: switch expressions, record patterns
6. Lambdas & modern idioms: `var`, `HttpClient`, module imports
7. Concurrency: virtual threads, scoped values
8. Putting it all together in the DJ Console

All eight acts use one running example, the **DJ Console API**: a Spring Boot 4 / Spring GraphQL
application.

## Repository layout

| Path | Contents |
|---|---|
| [`pom.xml`](pom.xml) | Parent POM: Java 27, Spring Boot 4.1, dependency and plugin versions, `pitest` profile |
| [`dj-console-app/`](dj-console-app/README.md) | The DJ Console API: overview, GraphQL examples, architecture |

For more detail, see the READMEs in the module:

- [Module README](dj-console-app/README.md): what the demo shows, the GraphQL endpoint, example queries
- [PostgreSQL setup](dj-console-app/README-pgsql.md): running Postgres with Podman or Docker, seed data, reset, troubleshooting
- Package documentation under `src/main/java/com/github/fbascheper/dj/console/`:
  - [`domain/`](dj-console-app/src/main/java/com/github/fbascheper/dj/console/domain/README.md): aggregate root rationale
  - [`domain/session/`](dj-console-app/src/main/java/com/github/fbascheper/dj/console/domain/session/README.md): the `MixSession` aggregate
  - [`domain/library/`](dj-console-app/src/main/java/com/github/fbascheper/dj/console/domain/library/README.md): music library reference catalog
  - [`domain/event/`](dj-console-app/src/main/java/com/github/fbascheper/dj/console/domain/event/README.md): the sealed `CrowdEvent` hierarchy
  - [`infrastructure/`](dj-console-app/src/main/java/com/github/fbascheper/dj/console/infrastructure/README.md): secondary adapters and JSONB persistence
  - [`service/`](dj-console-app/src/main/java/com/github/fbascheper/dj/console/service/README.md): application service and exceptions

## Quick start

Requires JDK 27, Maven, and Podman or Docker.

```bash
# 1. Start PostgreSQL (see README-pgsql.md for details)
podman run --name dj-console-postgres \
  -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=djconsole \
  -p 5432:5432 -d docker.io/library/postgres:18.6

# 2. Build and test everything
mvn clean verify

# 3. Run the API, then open http://localhost:8080/graphiql
cd dj-console-app && mvn spring-boot:run
```

Run mutation testing with `mvn -Ppitest test`.
