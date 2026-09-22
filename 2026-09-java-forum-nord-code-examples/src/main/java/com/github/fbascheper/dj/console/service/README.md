# `service` - Application Service Layer

## Role in the architecture

This package is the **application layer** in the hexagonal architecture. It sits between
the primary adapter (`DiscJockeyConsoleGraphQLController`) and the domain model, and it
owns the transaction boundary.

```
  Primary adapter                Application layer             Domain
  ──────────────────             ─────────────────             ──────
  GraphQLController  ──────────► MixSessionService  ─────────► MixSession.applyEvent()
  (Spring GraphQL)    calls       (transaction mgmt)  delegates  (aggregate root)
```

## `MixSessionService` / `MixSessionServiceImpl`

The single application service. Responsibilities:
- Defines `@Transactional` boundaries (read-only for queries, write for commands).
- Delegates to `MixSessionRepository` (secondary port) for retrieval and persistence.
- Creates domain event objects and delegates all business logic to `MixSession`.
- Throws typed exceptions that `GraphQLExceptionResolver` maps to GraphQL error responses.

**Design rule:** the service must not contain domain logic. If you find yourself writing
an `if` that touches domain state inside `MixSessionServiceImpl`, that logic belongs in
`MixSession.applyEvent()` or another domain method instead.

## Exception hierarchy

All application exceptions extend `DJConsoleException` and carry a machine-readable
`ErrorCode`. They are translated to GraphQL errors by `GraphQLExceptionResolver` in the
controller layer.

```
RuntimeException
└── DJConsoleException (abstract)  — carries ErrorCode
    ├── ModelValidationException   — ErrorCode.DOMAIN_VALIDATION_ERROR
    │     thrown by ValidatingBuilder when Jakarta Bean Validation fails
    └── MixSessionException (abstract)
        ├── MixSessionNotFoundException    — ErrorCode.MIX_SESSION_NOT_FOUND
        │     thrown when querying a session that does not exist
        └── MixSessionNotActiveException   — ErrorCode.MIX_SESSION_NOT_ACTIVE
              thrown when no active/current session is found
```

`ErrorCode` values are included in the GraphQL error response `extensions` map, allowing
clients to distinguish error types programmatically without parsing error messages.
