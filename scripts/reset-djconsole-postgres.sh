#!/usr/bin/env bash
# Removes the local dj-console Postgres container so the next `podman run` starts
# with an empty data directory. Use when you want a completely fresh database
# without relying on Hibernate ddl-auto (e.g. after switching ddl-auto away from create).
set -euo pipefail

CONTAINER_NAME="${DJ_CONSOLE_PG_CONTAINER:-dj-console-postgres}"
POSTGRES_IMAGE="${DJ_CONSOLE_PG_IMAGE:-docker.io/library/postgres:18.6}"

# Resolve the container engine. Podman is the default for this project; Docker still
# works if that is what you have. Note that a `docker` shell *alias* pointing at podman
# is invisible here — scripts do not inherit aliases — so we look for real binaries.
if [ -n "${DJ_CONSOLE_CONTAINER_ENGINE:-}" ]; then
  ENGINE="$DJ_CONSOLE_CONTAINER_ENGINE"
elif command -v podman >/dev/null 2>&1; then
  ENGINE="podman"
elif command -v docker >/dev/null 2>&1; then
  ENGINE="docker"
else
  echo "Error: neither podman nor docker found on PATH." >&2
  echo "Install Podman (brew install podman) or set DJ_CONSOLE_CONTAINER_ENGINE." >&2
  exit 1
fi

if ! command -v "$ENGINE" >/dev/null 2>&1; then
  echo "Error: container engine '$ENGINE' not found on PATH." >&2
  exit 1
fi

# With Podman on macOS the engine talks to a Linux VM that has to be running.
if ! "$ENGINE" info >/dev/null 2>&1; then
  echo "Error: '$ENGINE' is installed but not reachable." >&2
  if [ "$ENGINE" = "podman" ]; then
    echo "Start the Podman machine first:  podman machine start" >&2
  fi
  exit 1
fi

echo "Using container engine: $ENGINE"

if "$ENGINE" ps -a --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
  echo "Stopping and removing container: $CONTAINER_NAME"
  "$ENGINE" stop "$CONTAINER_NAME" >/dev/null
  "$ENGINE" rm "$CONTAINER_NAME" >/dev/null
  echo "Done."
else
  echo "No container named $CONTAINER_NAME found (nothing to remove)."
fi

echo ""
echo "Start Postgres again, for example:"
echo "  $ENGINE run --name $CONTAINER_NAME \\"
echo "    -e POSTGRES_PASSWORD=postgres \\"
echo "    -e POSTGRES_USER=postgres \\"
echo "    -e POSTGRES_DB=djconsole \\"
echo "    -p 5432:5432 \\"
echo "    -d $POSTGRES_IMAGE"
echo ""
echo "Then: cd dj-console-app && mvn spring-boot:run"
