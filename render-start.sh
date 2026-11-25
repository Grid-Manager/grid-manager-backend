#!/bin/sh
set -eu

if [ -n "${RENDER_DATABASE_URL:-}" ] && [ -z "${SPRING_DATASOURCE_URL:-}" ]; then
    case "$RENDER_DATABASE_URL" in
        postgresql://*@*/*)
            # Render injects the username and password separately. JDBC only needs host, port and database here.
            export SPRING_DATASOURCE_URL="jdbc:postgresql://${RENDER_DATABASE_URL##*@}"
            ;;
        *)
            echo "RENDER_DATABASE_URL must contain a Render PostgreSQL connection URL." >&2
            exit 1
            ;;
    esac
fi

export SERVER_PORT="${PORT:-10000}"
exec java -jar /app/grid-manager.jar
