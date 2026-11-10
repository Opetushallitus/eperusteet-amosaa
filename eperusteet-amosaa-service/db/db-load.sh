#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

DUMP="$ROOT/amosaa.zip"

if ! docker info >/dev/null 2>&1; then
  echo "Docker ei ole käynnissä. Käynnistä Docker Desktop ja yritä uudelleen."
  exit 1
fi

if [ ! -f "$DUMP" ]; then
  echo "Tiedostoa db/amosaa.zip ei ole. Aja ensin ./db-save.sh."
  exit 1
fi

if [ -z "$(docker compose ps -q --status running eperusteet-amosaa)" ]; then
  echo "Käynnistetään tietokanta."
  docker compose up -d --wait
fi

psql_value() {
  docker compose exec -T eperusteet-amosaa psql \
    --username=oph \
    --dbname=postgres \
    --quiet \
    --tuples-only \
    --no-align \
    --command="$1"
}

drop_database_if_exists() {
  local name="$1"
  local exists
  exists="$(psql_value "SELECT 1 FROM pg_database WHERE datname = '${name}'")"
  exists="${exists//[[:space:]]/}"
  if [ "$exists" = "1" ]; then
    docker compose exec -T eperusteet-amosaa dropdb --username=oph --force "$name"
  fi
}

echo "Palautetaan dump tietokantaan amosaa_load."
drop_database_if_exists amosaa_load
docker compose exec -T eperusteet-amosaa createdb --username=oph amosaa_load
unzip -p "$DUMP" amosaa.sql | docker compose exec -T eperusteet-amosaa psql \
  --username=oph \
  --dbname=amosaa_load \
  --quiet \
  --set=ON_ERROR_STOP=1 \
  >/dev/null

others="$(psql_value "SELECT count(*) FROM pg_stat_activity WHERE datname = 'amosaa' AND backend_type = 'client backend'")"
others="${others//[[:space:]]/}"
if [ "$others" != "0" ]; then
  echo "Tietokanta amosaa on käytössä (${others} yhteyttä). Pysäytä sovellus ja aja ./db-load.sh uudelleen."
  exit 1
fi

echo "Vaihdetaan palautettu kanta käyttöön."
docker compose exec -T eperusteet-amosaa psql \
  --username=oph \
  --dbname=postgres \
  --quiet \
  --set=ON_ERROR_STOP=1 \
  --command="SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'amosaa' AND pid <> pg_backend_pid();" \
  --command="ALTER DATABASE amosaa RENAME TO amosaa_old;" \
  --command="ALTER DATABASE amosaa_load RENAME TO amosaa;" \
  >/dev/null
drop_database_if_exists amosaa_old

echo "Kanta ladattu dumpista db/amosaa.zip."
