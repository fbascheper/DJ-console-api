# PostgreSQL (Podman) setup for development

Run Postgres in Podman, matching `src/main/resources/application.yaml`:
`localhost:5432`, database `djconsole`, user/password `postgres`.

Docker works too — every `podman` command below is drop-in compatible. This project
defaults to Podman because it needs no daemon and no Docker Desktop licence.

## Prerequisites

```bash
brew install podman        # engine; Podman Desktop is an optional GUI on top
podman machine init        # once per machine
```

On macOS the `podman` CLI talks to a small Linux VM, so the VM must be running before
any container command works:

```bash
podman machine start
podman ps                  # empty table with headers = ready
```

`podman machine start` only needs to be repeated after a reboot.

## Start the database

```bash
podman rm -f dj-console-postgres 2>/dev/null

podman run --name dj-console-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=djconsole \
  -p 5432:5432 \
  -d docker.io/library/postgres:18.6
```

Confirm it is accepting connections before starting the API — the first run pulls the
image, so give it a few seconds:

```bash
podman exec dj-console-postgres pg_isready -U postgres -d djconsole
# /var/run/postgresql:5432 - accepting connections
```

Two details in that `run` command are Podman-specific:

- **The image is fully qualified** as `docker.io/library/postgres:18.6`. Docker silently
  assumes Docker Hub for a bare `postgres:18.6`; Podman does not, and depending on your
  `registries.conf` it will either prompt you to choose a registry or fail outright.
- **The tag is pinned to a patch version**, not the floating `18`. The demo should behave
  identically on the conference machine and on yours.

## Schema and seed data

The API uses `spring.jpa.hibernate.ddl-auto: create` (see `src/main/resources/application.yaml`).
On each application start, Hibernate recreates the schema and the `SmartLifecycle` loaders
repopulate the music library and mix sessions from `src/main/resources/seed/`.

**After changing domain JSON or seed files**, restart the API so tables are recreated and
seeds run again:

```bash
cd 2026-09-java-forum-nord-code-examples && mvn spring-boot:run
```

## Crowd cheer / DJ `trackLibrary`

Mix sessions are stored as JSONB. The app fills `DiscJockey.trackLibrary` from the music
catalog when loading sessions (see `MixSessionSmartLoader` and `MixSessionRepositoryImpl`).
If you ever see errors about a null track library when calling `crowdCheered`, restart the
API with the configuration above; with `ddl-auto: create`, a normal restart is enough for a
clean schema.

If you use a **persistent** Postgres where Hibernate does not recreate the schema (for
example you switched `ddl-auto` to `update` or `validate`), remove old data or reset the
container — see below.

## Reset Postgres (optional)

Remove the container, then start a fresh one:

```bash
podman stop dj-console-postgres 2>/dev/null || true
podman rm dj-console-postgres 2>/dev/null || true
# then podman run ... as in "Start the database"
```

## Connect with psql (optional)

```bash
podman exec -it dj-console-postgres psql -U postgres -d djconsole
```

## Troubleshooting

**`podman machine start` fails with `krunkit was terminated by signal: abort trap`**

The `libkrun` VM backend is missing its `virglrenderer` library, which comes from the
`libkrun/krun` tap rather than homebrew-core. Either repair it:

```bash
brew trust libkrun/krun
brew install libkrun/krun/virglrenderer
```

…or sidestep it by switching to Podman's built-in Apple Hypervisor backend, which has no
third-party dependencies. libkrun only buys you GPU passthrough, which this demo does not use:

```bash
sed -i '' 's/provider = "libkrun"/provider = "applehv"/' ~/.config/containers/containers.conf
podman machine rm -f podman-machine-default   # a machine keeps its original backend
podman machine init && podman machine start
```

Removing the machine also deletes its containers and images, so recreate the database
afterwards as described in [Start the database](#start-the-database).

**Port 5432 already in use**

Another Postgres is bound to the port. Find it with `podman ps -a` and `lsof -i :5432`,
then stop it or point the container at a different host port (`-p 5433:5432`) and update
`spring.datasource.url` in `application.yaml` to match.
