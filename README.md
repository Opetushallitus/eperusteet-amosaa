# ePerusteet-amosaa

[![Build Status](https://github.com/Opetushallitus/eperusteet-amosaa/actions/workflows/build.yml/badge.svg)](https://github.com/Opetushallitus/eperusteet-amosaa/actions)
[![Build Status](https://github.com/Opetushallitus/eperusteet-amosaa-ui/actions/workflows/build.yml/badge.svg)](https://github.com/Opetushallitus/eperusteet-amosaa-ui/actions)

## 1. Palvelun tehtävä

Ammatillisen koulutuksen paikallisten opetussuunnitelmien laadintatyökalu.

## 2. Ohjeet

Palvelu toimii identtisesti [eperusteet](https://github.com/Opetushallitus/eperusteet) palvelun
kanssa, joten ylläpidetään ohjeita vain yhdessä paikassa. Korvaa ainoastaan
[eperusteet](https://github.com/Opetushallitus/eperusteet) ohjeissa `eperusteet` sanat
`eperusteet-amosaa` sanalla.

Esim. `eperusteet-service` -> `eperusteet-amosaa-service`

### Lokaali tietokanta

`local`-profiili käynnistää amosaa-kannan Docker Composella, jos se ei ole jo päällä. Compose-tiedosto on [`eperusteet-amosaa-service/db/compose.yaml`](eperusteet-amosaa-service/db/compose.yaml) (PostgreSQL 15, portti 5433). Docker Desktopin (tai vastaavan Docker-daemonin) on oltava käynnissä.

Kannan tyhjennys ja uudelleenluonti:

```bash
./eperusteet-amosaa-service/db/db-reset.sh
```

Flyway-migraatiot ajetaan seuraavalla palvelun käynnistyksellä.
