# eperusteet-amosaa-service: parannussuunnitelma

Päivitetty: 2026-09-23
Kohde: `eperusteet-amosaa-service`, Java-koodi (Spring Boot 4, Java 21, Hibernate 7). Noin 620 päätiedostoa (68 entiteettiä), 37 testitiedostoa.
Rajaus: `src/main/java` ja `src/test/java`. SQL-migraatioita on katsottu vain uniikkiehtojen osalta. Käyttöliittymä, CI, Docker-skriptit ja `tools/` eivät kuulu tähän läpikäyntiin.

Sama läpikäynti on tehty sisarpalveluille: `eperusteet/docs/improvement_plan.md` ja `eperusteet-ylops/improvement_plan.md`. Monet löydökset toistuvat kaikissa kolmessa, koska niissä on yhteinen pohja (tietoturvakonfiguraatio, PDF-takaisinkutsut, liitteet, julkaisuvirta, HTTP-asiakkaat).

## 1. Lukuohje

Kaikki polut ovat suhteessa hakemistoon `eperusteet-amosaa-service/src/main/java/fi/vm/sade/eperusteet/amosaa/`, ellei toisin mainita. Rivinumerot viittaavat commitin `4216e900` tilaan.

Vakavuusluokat:

- **Kriittinen**: hyödynnettävissä ilman kirjautumista tai kenen tahansa kirjautuneen toimesta, tai johtaa virheellisesti julkaistuun sisältöön.
- **Korkea**: todellinen bugi, tietovuoto kirjautuneille käyttäjille tai tuotantohäiriön riski.
- **Keskitaso**: virhetilanteet käsitellään väärin, piileviä vikoja tai suorituskykyongelma.
- **Matala**: ylläpidettävyys ja siisteys.

Työmäärä: S = alle päivä, M = 1–3 päivää, L = yli 3 päivää.

Merkintä **(V)** tarkoittaa, että löydös on tarkistettu käsin lähdekoodista tämän dokumentin kirjoittamisen yhteydessä. Muut löydökset ovat katselmoinnissa lähdekoodista vahvistettuja, mutta rivinumerot kannattaa tarkistaa ennen korjausta.

---

## 2. Yhteenveto ja korjausjärjestys

| # | Toimenpide | Vakavuus | Työ | Kohta |
|---|---|---|---|---|
| 1 | Rajaa PDF-datan ja -tilan päivitys PDF-palvelulle | Kriittinen | S | 3.1 |
| 2 | Lisää oikeustarkistukset `DokumenttiKuvaService`-palveluun, älä luo rivejä GET-pyynnössä | Kriittinen | S | 3.2 |
| 3 | Julkaisu: uniikkiehto revisiolle, KESKEN-esto, tila vasta onnistuneen julkaisun jälkeen, ei jaettua dataa | Kriittinen | M | 3.3, 5.1, 5.2 |
| 4 | Rajaa sisältöviitteiden kopiointi ja linkitys luettaviin lähteisiin | Korkea | S | 4.1 |
| 5 | Korjaa tilasiirtymäehto (julkaistun poisto ilman OPH-ylläpitäjää) | Korkea | S | 4.2 |
| 6 | Poista validoinnin tyhjät `catch (NullPointerException)` -lohkot ja switch-lauseen puuttuva `break` | Korkea | S | 4.3, 4.4 |
| 7 | Liitteet: `or isAuthenticated()`, IDOR ja rikkinäinen `delete`-annotaatio | Korkea | S | 7.2 |
| 8 | Ota CSRF ja tietoturvaotsakkeet käyttöön, rajaa CAS-proxyt, oletuksena kielletty suodatinketju | Korkea | M | 7.1 |
| 9 | Tilaa muuttavat GET-reitit POST-metodiin ja ylläpitäjän oikeudelle | Korkea | S | 7.3 |
| 10 | HTTP-asiakkaat: aikakatkaisut, tuotanto-oletusten poisto, virhetulosten välimuistituksen esto | Korkea | M | 8.1 |
| 11 | Ajastukset ja batch-ajot klusteriturvallisiksi, jumiutuneen KESKEN-tilan palautus | Korkea | M | 8.2 |
| 12 | Korjaa NPE-riskit | Keskitaso | M | 6 |
| 13 | Domain: equals/hashCode, kaskadit jaettuihin teksteihin, kopioinnin jaetut viitteet | Keskitaso | M | 9 |
| 14 | Rajaa kuvakoko, sivukoko ja `Content-Disposition` | Keskitaso | S | 7.4 |
| 15 | Testikattavuus: julkaisu (ohitettu testi), integraatiot, oikeudet | Keskitaso | L | 11 |
| 16 | Refaktoroi suurimmat luokat, siirry pois Orikasta | Matala | L | 10, 12 |

---

## 3. Kriittiset löydökset

### 3.1 PDF-datan ja -tilan ylikirjoitus ilman oikeustarkistusta (V)

`service/dokumentti/DokumenttiService.java:43-45`, `resource/ops/DokumenttiPdfContoller.java:29-42`, `service/dokumentti/impl/DokumenttiServiceImpl.java:230-243`

```java
void updateDokumenttiPdfData(byte[] pdfData, Long dokumenttiId);
void updateDokumenttiTila(DokumenttiTila tila, Long dokumenttiId);
```

Metodeilta puuttuu `@PreAuthorize`, ja suodatinketju vaatii POST-pyynnöille vain kirjautumisen. Kuka tahansa kirjautunut virkailija voi korvata minkä tahansa koulutustoimijan julkaistun opetussuunnitelman PDF:n. Paikallisessa profiilissa `config/WebSecurityConfigurationDev.java:39` sallii polun `/api/dokumentit/pdf/**` ilman kirjautumista.

**Korjaus:** rajaa kutsut PDF-palvelun palvelutunnukselle (oma rooli tai jaettu salaisuus otsakkeessa), ja tarkista, että dokumentti on generointijonossa. Toteuta yhdessä eperusteen ja ylopsin kanssa (esimerkiksi `eperusteet-backend-utils`).

### 3.2 Dokumenttikuvat ilman oikeustarkistusta, GET luo rivejä (V)

`service/dokumentti/DokumenttiKuvaService.java:10-20` ja `resource/ops/DokumenttiController.java:173-183`

```java
@RequestMapping(value = "/dokumenttikuva", method = RequestMethod.GET)
public ResponseEntity<DokumenttiKuvaDto> getDokumenttiKuva(...) {
    DokumenttiKuvaDto dto = dokumenttiKuvaService.getDto(opsId, Kieli.of(kieli));
    if (dto == null) {
        dto = mapper.map(dokumenttiKuvaService.createDtoFor(opsId, Kieli.of(kieli)), DokumenttiKuvaDto.class);
    }
```

- `DokumenttiKuvaService`-rajapinnassa ei ole yhtään `@PreAuthorize`-annotaatiota. Kuka tahansa kirjautunut voi lisätä tai poistaa minkä tahansa opetussuunnitelman PDF-kuvat (rivit 189-234).
- GET on `permitAll` suodatintasolla, ja se luo tietokantaan uuden rivin, jos kuvaa ei ole. Kirjautumaton käyttäjä voi siis luoda rivejä mielivaltaisille `opsId`-arvoille.

**Korjaus:** `hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')` kirjoittaville ja `ESITYS` lukeville metodeille. Palauta GET-pyynnössä tyhjä DTO luomatta riviä.

### 3.3 Julkaisun revisio ilman uniikkiehtoa ja ilman KESKEN-estoa (V)

`service/koulutustoimija/impl/JulkaisuServiceImpl.java:184-200, 256`, `service/batch/JulkaisuJobProcessor.java:61`

```java
julkaisu.setRevision(vanhatJulkaisut.stream().mapToInt(Julkaisu::getRevision).max().orElse(0) + 1);
```

- Revisio lasketaan max + 1 -periaatteella ilman lukitusta kolmessa paikassa: `teeJulkaisuAsync`, `aktivoiJulkaisu` (rivi 292) ja batch-ajo `JulkaisuJobProcessor`.
- Taulussa `julkaisu` ei ole uniikkiehtoa `(opetussuunnitelma_id, revision)` (`src/main/resources/db/migration/V2_20201127121000__julkaisu.sql`; myöhemmät migraatiot lisäävät vain indeksin). Ylopsissa vastaava ehto on olemassa.
- `teeJulkaisu` ei tarkista, onko julkaisu jo tilassa `KESKEN`.

Samanaikaiset julkaisut (kaksi käyttäjää, kaksi solmua, tai batch-ajo yhtä aikaa käyttäjän kanssa) voivat tuottaa saman revision. Julkaistun datan näkymä (`julkaistu_opetussuunnitelma_data_view`) ja `findFirstByOpetussuunnitelmaOrderByRevisionDesc` valitsevat silloin satunnaisen niistä.

**Korjaus:**

- Flyway-migraatio: `ALTER TABLE julkaisu ADD CONSTRAINT julkaisu_ops_revision_uk UNIQUE (opetussuunnitelma_id, revision)`. Tarkista ensin tuotantodata duplikaattien varalta.
- Hylkää uusi julkaisu, jos tila on `KESKEN` eikä aikakatkaisu ole ylittynyt.
- Laske revisio samassa lukitussa transaktiossa (`SELECT ... FOR UPDATE` opetussuunnitelman riville).

---

## 4. Logiikkavirheet

### 4.1 Sisältöviitteiden kopiointi ja linkitys mistä tahansa opetussuunnitelmasta (V), korkea

`service/ops/impl/SisaltoViiteServiceImpl.java:1118-1130, 1161-1172`, `service/ops/SisaltoViiteService.java:89-90, 119-120`

```java
@PreAuthorize("hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')")
List<SisaltoViiteDto> copySisaltoViiteet(@P("ktId") Long ktId, @P("opsId") Long opsId, List<Long> viitteet);
...
List<SisaltoViite> kopiot = viitteet.stream()
        .map(viiteId -> repository.findOne(viiteId))
```

Oikeus tarkistetaan vain kohdeopetussuunnitelmaan. Lähdeviitteet haetaan pelkällä id:llä ilman tarkistusta siitä, saako käyttäjä lukea niiden opetussuunnitelmaa. Käyttäjä, jolla on muokkausoikeus omaan opetussuunnitelmaansa, voi kopioida tai linkittää siihen minkä tahansa toisen koulutustoimijan keskeneräistä sisältöä id:n perusteella. `kloonaaTekstiKappale` (rivi 637) sisältää saman puutteen `TODO`-kommenttina.

`linkSisaltoViiteet` kaatuu lisäksi `findFirst().get()`-kutsuun (rivit 1121-1124), jos juuresta puuttuu `TUTKINNONOSAT`-lapsi.

**Korjaus:** tarkista jokaiselle lähdeviitteelle `permissionManager.hasPermission(..., lähde-ops, ESITYS)` tai rajaa lähteet pohjaan ja julkaistuihin. Käytä `orElseThrow(BusinessRuleViolationException)`.

### 4.2 Julkaistun opetussuunnitelman poistosuoja ei koske ammatillisia (V), korkea

`service/koulutustoimija/impl/OpetussuunnitelmaServiceImpl.java:996-1019` (`updateTila`)

```java
if (nykyinen.mahdollisetSiirtymat().contains(tila)
        && (ops.getOpsKoulutustyyppi() == null || ops.getOpsKoulutustyyppi().isAmmatillinen()
        || (!(tila.equals(Tila.POISTETTU) && CollectionUtils.isNotEmpty(ops.getJulkaisut())) || permissionManager.hasOphAdminPermission()))) {
```

Ehdon tarkoitus on, että julkaisuja sisältävän opetussuunnitelman voi poistaa vain OPH:n ylläpitäjä. Ensimmäiset kaksi haaraa (`opsKoulutustyyppi == null` tai ammatillinen) ohittavat suojan kokonaan. Ammatillisissa opetussuunnitelmissa, jotka ovat amosaan pääkäyttötapaus, kuka tahansa muokkausoikeuden omaava voi poistaa julkaistun opetussuunnitelman. Varmista, onko poikkeus tarkoituksellinen. Jos on, kirjaa syy koodiin.

Lisäksi `mahdollisetSiirtymat()` kutsutaan ilman `isPohja`-tietoa (`domain/Tila.java:14-18`), joten pohjalle sallitaan siirtymä `VALMIS -> JULKAISTU`, vaikka `mahdollisetSiirtymat(true)` estäisi sen.

**Korjaus:** erota ehdot omiksi, nimetyiksi tarkistuksiksi (esimerkiksi `boolean poistoVaatiiAdminin = tila == POISTETTU && hasJulkaisut`), ja käytä `mahdollisetSiirtymat(ops.getTyyppi() == POHJA)`.

### 4.3 Validointi nielee NullPointerExceptionin (V), korkea

`service/ops/impl/ValidointiServiceImpl.java:262-278`

```java
try {
    Integer minimi = moduuli.getMuodostumisSaanto().getKoko().getMinimi();
    if (sisallonKokoJaLaajuus.getFirst() < minimi) { ctx.validointi.virhe(...); }
} catch (NullPointerException ex) {
}
```

NPE:tä käytetään kontrollirakenteena puuttuvan koon tai laajuuden käsittelyyn. Sama `catch` nielee myös todelliset virheet (esimerkiksi `moduuli.getNimi()` tai `sisallonKokoJaLaajuus.getFirst()` on `null`), jolloin rakenteen virhe jää raportoimatta ja julkaisu voi mennä läpi.

**Korjaus:** korvaa eksplisiittisillä `null`-tarkistuksilla: `Optional.ofNullable(saanto.getKoko()).map(Koko::getMinimi)`.

### 4.4 Switch-lauseesta puuttuu `break` (V), keskitaso

`service/ops/impl/ValidointiServiceImpl.java:335-342`

```java
switch (viite.getTyyppi()) {
    case TUTKINNONOSA:
        validoiTutkinnonOsa(validointi, viite, ops);
    //case OSASUORITUSPOLKU: Osasuorituspolkua ei validoida
    case SUORITUSPOLKU:
        validoiSuorituspolku(validointi, viite, ops);
    default:
        break;
}
```

Tutkinnon osalle ajetaan myös suorituspolun validointi. Nyt se on käytännössä tyhjä operaatio, koska tutkinnon osalla ei ole suorituspolkua, mutta rakenne on hauras. Sama malli toistuu `SisaltoViiteServiceImpl.java:510-513` (`OSAAMISMERKKI` -> `default`).

**Korjaus:** lisää `break` jokaiseen haaraan, tai käytä Java 21:n `switch`-lauseketta nuolisyntaksilla.

### 4.5 Toimimattomat ja keskeneräiset toiminnot, matala

- `service/ops/impl/SisaltoViiteServiceImpl.java:924-925` (`revertToVersion`): tyhjä metodi. Toteuta tai poista endpoint.
- `service/koulutustoimija/impl/OpetussuunnitelmaServiceImpl.java:911-913` (`getPoistetut`): palauttaa aina tyhjän listan.
- (V) `service/external/impl/KayttajanTietoServiceImpl.java:322-329` (`getKayttaja`): koulutustoimijaan kuulumisen tarkistus on kommentoitu pois `// FIXME`-merkinnällä. Minkä tahansa käyttäjän tiedot voi hakea minkä tahansa koulutustoimijan kautta.
- `SisaltoViiteServiceImpl.java:231` (vanhemman tyyppiä ei tarkisteta) ja `ValidointiServiceImpl.java:166` (oman tutkinnon osan sisältöä ei tarkisteta): TODO-merkinnät, joilla on toiminnallinen vaikutus. Kirjaa tiketeiksi.

---

## 5. Julkaisuvirta

### 5.1 Tila asetetaan julkaistuksi ennen kuin julkaisu on onnistunut (V), kriittinen

`service/koulutustoimija/impl/OpetussuunnitelmaServiceImpl.java:1003-1015`

```java
ops.setTila(tila);
if (tila.equals(Tila.JULKAISTU)) {
    validoi(ktId, opsId).forEach(Validointi::tuomitse);
    julkaisuService.teeJulkaisu(ktId, opsId, JulkaisuBaseDto.builder()...build());
}
```

`teeJulkaisu` käynnistää `teeJulkaisuAsync`-metodin toisessa säikeessä (`JulkaisuServiceImpl.java:198-200`) ennen kuin `updateTila`-metodin transaktio on commitoitu. Seuraukset:

- Opetussuunnitelma tallentuu tilaan `JULKAISTU`, vaikka asynkroninen julkaisu epäonnistuisi (esimerkiksi PDF- tai tallennusvirhe). Silloin `JulkaisuTila` on `VIRHE`, eikä `julkaisu`-riviä ole.
- Asynkroninen säie voi lukea commitoimatonta tilaa.
- `teeJulkaisuAsync` asettaa tilan `JULKAISTU` myös itse onnistuneen julkaisun jälkeen (rivi 271), joten `updateTila`-metodin asetus on tarpeeton.

**Korjaus:** älä aseta tilaa `JULKAISTU` `updateTila`-metodissa. Käynnistä asynkroninen työ vasta commitin jälkeen (`TransactionSynchronizationManager.registerSynchronization(... afterCommit ...)` tai `@TransactionalEventListener(phase = AFTER_COMMIT)`).

### 5.2 Julkaisu jakaa datan edellisen revision kanssa (V), korkea

`service/koulutustoimija/impl/JulkaisuServiceImpl.java:286-297` (`aktivoiJulkaisu`)

```java
julkaisu.setData(vanhaJulkaisu.getData());
```

Uusi `Julkaisu`-rivi viittaa samaan `JulkaisuData`-entiteettiin kuin vanha. Liitos on `domain/koulutustoimija/Julkaisu.java:51-52` määritelty `@OneToOne(cascade = ALL, orphanRemoval = true)`, joten entiteetti olettaa yksinomaisen omistajuuden. Kun toinen jakavista julkaisuista poistetaan (esimerkiksi `MaintenanceServiceImpl.poistaJulkaisut`, rivi 179), Hibernate poistaa myös datan, jolloin toinen julkaisu joko menettää datansa tai poisto kaatuu viite-eheyteen. `vanhaJulkaisu` voi lisäksi olla `null`, jos revisiota ei ole. Eperusteessa `aktivoiJulkaisu` jakaa datan samalla tavalla.

**Korjaus:** `assertExists(vanhaJulkaisu)` ja datan kopiointi (`new JulkaisuData(vanhaJulkaisu.getData().getData())`). Vaihtoehtoisesti muuta liitos `@ManyToOne`-muotoon ilman kaskadia ja `orphanRemoval`-asetusta, jos datan jakaminen on tarkoituksellista.

### 5.3 Julkaisun virheiden käsittely, keskitaso

- `JulkaisuServiceImpl.java:243-251` (V): `DokumenttiException` lokitetaan ja julkaisu jatkuu. PDF:n generointi on lisäksi asynkroninen (`generateWithDto`), joten julkaisuun tallennetaan dokumentti-id:t, joiden generointi voi vielä epäonnistua. Merkitse puuttuvat dokumentit näkyviin.
- `JulkaisuServiceImpl.java:264-268` (V): kaikki poikkeukset, myös `opetussuunnitelma-ei-validi`, kääritään geneeriseksi `julkaisun-tallennus-epaonnistui`-virheeksi. Tallenna virhekoodi tilaan, jotta käyttöliittymä voi näyttää syyn.
- `JulkaisuServiceImpl.java:330-338` (`viimeisinJulkaisuTila`): lukumetodi päivittää tilan arvoon `VIRHE` aikakatkaisun jälkeen. Siirrä siivous ajastettuun tehtävään.
- Kaatuneen JVM:n jälkeen tila jää arvoon `KESKEN`, kunnes joku kutsuu `viimeisinJulkaisuTila`-metodia. Lisää käynnistyksen yhteydessä ajettava palautus.

### 5.4 Read-only-transaktio kirjoittaa, keskitaso

`service/dokumentti/impl/DokumenttiServiceImpl.java:120-129` (`getLatestDokumentti`): `@Transactional(readOnly = true)`, mutta aikakatkaisun ylittänyt dokumentti merkitään tilaan `EPAONNISTUI` ja tallennetaan. Kirjoitus voi jäädä tekemättä. **Korjaus:** erillinen `REQUIRES_NEW`-metodi tai ajastettu siivous.

---

## 6. NullPointerException-riskit

| Sijainti | Ongelma | Korjaus |
|---|---|---|
| `service/koulutustoimija/impl/JulkaisuServiceImpl.java:286-292` (V) | `aktivoiJulkaisu`: `vanhaJulkaisu` voi olla `null` | `assertExists` |
| `service/koulutustoimija/impl/JulkaisuServiceImpl.java:193, 359-366` | `isValidTiedote(null)` | `null`-tarkistus |
| `service/ops/impl/SisaltoViiteServiceImpl.java:793-796` | `validateRakenne`: `vanhatViitteetMap.get(...)` tuntemattomalla id:llä tai `uusiParent == null` | `containsKey` ja `BusinessRuleViolationException` |
| `service/ops/impl/SisaltoViiteServiceImpl.java:1121-1124` (V) | `findFirst().get()` puuttuvalle `TUTKINNONOSAT`-juurelle | `orElseThrow` |
| `service/ops/impl/SisaltoViiteServiceImpl.java:909-910` | `getRevisions`: `findOne` voi palauttaa `null` | `assertExists` |
| `service/koulutustoimija/impl/OpetussuunnitelmaServiceImpl.java:537-542, 852-853` | `findOps` ja `getOpetussuunnitelma`: `findOne` | `assertExists` |
| `service/ops/impl/AmmatillinenOpetussuunnitelmaCreateService.java:90-91, 131, 183-184` | `findFirst().get()` puuttuvalle juurelle tai suoritustavalle | `orElseThrow` selkeällä viestillä |
| `service/peruste/impl/AmmatillinenOpetussuunnitelmaPerustePaivitysService.java:66` | `findFirst().get()` | `orElseThrow` |
| `service/ohje/impl/OhjeServiceImpl.java:44-45`, `service/ops/impl/TermistoServiceImpl.java:83-84` | `findOne` ja sen jälkeen suora käyttö | `assertExists` |
| `service/util/impl/KoodistoClientImpl.java:118-120` | `getNimi`: `getByUri` voi palauttaa `null` | `null`-tarkistus |
| `repository/CustomJpaRepository.java:11-13` | `findOne` palauttaa `null`, mikä on edellisten päälähde | `findByIdOrThrow`-apumetodi (kohta 12) |

---

## 7. Tietoturva

### 7.1 Suodatinketjun asetukset, korkea

`config/WebSecurityConfiguration.java:96-99, 138-147`

- `.headers(AbstractHttpConfigurer::disable)` ja `.csrf(AbstractHttpConfigurer::disable)`: ilman CSRF-suojausta toinen sivusto voi tehdä kirjautuneen käyttäjän nimissä tilaa muuttavia pyyntöjä. Myös `X-Frame-Options`, `X-Content-Type-Options` ja HSTS kytkeytyvät pois.
- `ticketValidator.setAcceptAnyProxy(true)`: mikä tahansa CAS-asiakas voi hankkia proxy-tiketin tähän palveluun.
- `GET /api/**` on `permitAll()`: GET-reittien suojaus nojaa kokonaan metoditason annotaatioihin (ks. 3.2).
- `src/main/resources/application.properties:42`: `spring_security_default_access=permitAll` ei ole Java-koodin käytössä (vain vanhoissa XML-testikonteksteissa). Poista, jotta se ei anna väärää kuvaa.

**Korjaus:** CSRF käyttöön (`CookieCsrfTokenRepository.withHttpOnlyFalse()`), otsakkeiden oletukset takaisin, `setAllowedProxyChains`, oletuksena kielletty suodatinketju ja eksplisiittinen lista julkisista reiteistä.

### 7.2 Liitteet (V), korkea

`service/ops/LiiteService.java:14-32` ja `service/ops/impl/LiiteServiceImpl.java:48-96`

```java
@PreAuthorize("hasPermission(#opsId, 'opetussuunnitelma', 'ESITYS') or isAuthenticated()")
LiiteDto get(@P("opsId") Long opsId, UUID id);
...
@PreAuthorize("hasPermission(#ktId, 'koulutustoimija', 'MUOKKAUS')")
void delete(@P("opsId") Long ktId, @P("opsId") Long opsId, UUID id);
```

- `get`, `getAll`, `export` ja `exportLiitePerusteelta` hyväksyvät `or isAuthenticated()`. Kuka tahansa kirjautunut voi ladata minkä tahansa opetussuunnitelman liitteet.
- `get` ja `export` hakevat liitteen pelkällä UUID:llä (`liiteRepository.findOne(id)`) ilman tarkistusta siitä, kuuluuko se polun `opsId`-opetussuunnitelmaan (IDOR). Repositoriossa on jo `findOne(opsId, id)`, mutta sitä ei käytetä.
- `delete`: molemmat parametrit on nimetty `@P("opsId")`, joten SpEL-lausekkeen `#ktId` ei viittaa mihinkään parametriin ja evaluoituu arvoon `null`. Oikeustarkistus tehdään siis `hasPermission(null, 'koulutustoimija', 'MUOKKAUS')`-kutsuna, jonka tulos riippuu `PermissionManagerImpl`-toteutuksen `null`-käsittelystä. Kohteena on lisäksi koulutustoimija eikä opetussuunnitelma.

**Korjaus:** poista `or isAuthenticated()`, käytä `findOne(opsId, id)`, ja korjaa `delete`: `@P("ktId") Long ktId, @P("opsId") Long opsId` ja `hasPermission({#ktId, #opsId}, 'opetussuunnitelma', 'MUOKKAUS')`.

### 7.3 Tilaa muuttavat GET-reitit ja liian heikot tarkistukset, korkea/keskitaso

- (V) `resource/koulutustoimija/MigrationController.java:20-23`: `GET` kutsuu `mapKoulutustyyppi`-metodia, joka vaatii vain kirjautumisen (`service/koulutustoimija/OpetussuunnitelmaService.java:45-46`). Kuka tahansa kirjautunut voi ajaa migraation. Vaadi OPH:n ylläpitäjän oikeus ja POST, tai poista reitti, jos migraatio on jo ajettu.
- `resource/hallinta/MaintenanceController.java:34-64`: `clearCache` vaatii vain kirjautumisen. Muut reitit vaativat OPH HALLINTA -oikeuden, mutta ovat GET-reittejä, joten CSRF:n puuttuessa ne voi laukaista ylläpitäjän istunnolla.
- `resource/hallinta/HallintaController.java:18-21` ja `resource/batch/BatchController.java:43-61`: GET-reitit, joilla on sivuvaikutuksia.
- `service/koulutustoimija/OpetussuunnitelmaService.java:148-149`: tilastot `isAuthenticated() or @profileService.isDevProfileActive()`, ja `resource/koulutustoimija/TilastotController.java:39-41` ilman sivukoon ylärajaa.
- `service/ops/TermistoService.java:11-18`: `ESITYS or isAuthenticated()`.
- `service/koulutustoimija/impl/OpetussuunnitelmaServiceImpl.java:851-854` (`getOpetussuunnitelma`) hakee opetussuunnitelman pelkällä `opsId`-arvolla tarkistamatta, kuuluuko se polun `ktId`-koulutustoimijaan. Muut polut käyttävät `findOps(ktId, opsId)`-metodia.
- `service/dokumentti/impl/DokumenttiServiceImpl.java:221-226`: dokumentti haetaan id:llä tarkistamatta, kuuluuko se polun opetussuunnitelmaan. ESITYS-oikeudella opetussuunnitelmaan A voi hakea opetussuunnitelman B dokumentin.

**Korjaus:** kaikki tilaa muuttavat reitit POST-metodiin ja vähintään OPH HALLINTA -oikeudelle. Poista `or isAuthenticated()`-ehdot. Sido alihaut aina vanhempaan (`ktId -> opsId -> dokumentti tai liite`).

### 7.4 Palvelunestoriskit ja syötteet, keskitaso

- **Kuvien skaalaus:** `resource/ops/LiitetiedostoController.java:77-78, 95-116`. `new BufferedImage(width, height, ...)` asiakkaan antamilla mitoilla ilman ylärajaa. Aseta yläraja (esimerkiksi 4000 px).
- **Sivukoko:** tilastot (ks. 7.3). Julkiset koulutustoimijahaut rajaavat 1000:een, mikä on suuri. Julkinen opetussuunnitelmahaku rajaa 100:aan ja `OpetussuunnitelmaJulkaistuQueryDto` 50:een; käytä yhtenäistä rajaa.
- **`Content-Disposition`:** `resource/ops/DokumenttiController.java:120-126` ja `resource/julkinen/JulkinenController.java:250-256` liittävät opetussuunnitelman nimen otsakkeeseen sellaisenaan. Käytä `ContentDisposition.attachment().filename(nimi, UTF_8)`.
- **Jsonpath:** `service/koulutustoimija/impl/JulkaisuServiceImpl.java:145-146, 444-473` rakentaa jsonpath-lausekkeen merkkijonona, mutta polun osat validoidaan säännöllisellä lausekkeella. Riski on pieni. Harkitse jsonpath-muuttujia, kuten eperusteen suunnitelman kohdassa 7.2.

### 7.5 Muut, matala

- **HTML-sanitointi:** `domain/validation/ValidHtml.java:46-60`. `href`- ja `src`-attribuuteille ei rajata protokollia. `ValidHtmlValidatorBase.isValidUrls` on merkitty vanhentuneeksi eikä ole käytössä. Lisää `addProtocols("a", "href", "http", "https", "mailto")` ja vastaava `img`-elementille.
- **`PermissionManagerLocal`:** `service/security/PermissionManagerLocal.java:8-14` sallii kaiken. Kaada käynnistys, jos se on aktiivinen muussa kuin `local`-profiilissa.
- **Dev-konfiguraatio:** `config/WebSecurityConfigurationDev.java:55-123` käyttää muistinvaraisia käyttäjiä ja `withDefaultPasswordEncoder`-toteutusta. Hyväksyttävää vain paikallisesti.

---

## 8. Integraatiot ja taustatyöt

### 8.1 HTTP-asiakkaat ja välimuisti, korkea

- **Tuotanto-URL:t oletuksina:** `service/util/impl/KoodistoClientImpl.java:56`, `service/external/impl/KoodistoServiceImpl.java:27` ja `service/dokumentti/impl/LokalisointiServiceImpl.java:22`. Väärin konfiguroitu ympäristö kutsuu hiljaa tuotantoa. Poista oletukset.
- **Aikakatkaisut puuttuvat:** `new RestTemplate()` kohdissa `KoodistoClientImpl.java:82, 93, 157, 167`, `KoodistoServiceImpl.java:41`, `LokalisointiServiceImpl.java:34`, `ArviointiasteikkoServiceImpl.java:49, 70`, `EperusteetClientImpl.java:85` ja `EperusteetServiceImpl.java:99`. `RestClientFactoryImpl` asettaa `OphHttpClient`-asiakkaalle 60 sekunnin aikakatkaisun; käytä samaa kaikkialla.
- **Heittomerkit oletusarvoissa:** `EperusteetClientImpl.java:56`, `OrganisaatioServiceImpl.java:52`, `KayttajanTietoServiceImpl.java:91, 148`, `ExternalPdfServiceImpl.java:36` ja `RestClientFactoryImpl.java:23-30`. Spring tulkitsee `''`-oletuksen kirjaimellisesti, joten puuttuva asetus ei kaada käynnistystä. Käytä `${prop:}`-muotoa ja validoi pakolliset asetukset.
- **401/403 hiljaa tyhjäksi ja välimuistiin:** `KayttajanTietoServiceImpl.java:163-175` palauttaa `new KayttajanTietoDto(oid)`, joka välimuistitetaan (`kayttajat`, TTL 1 h, ilman `unless`-ehtoa). `KayttooikeusServiceImpl.java:78-88` palauttaa 401-vastauksessa tyhjän listan. Autentikointivirhe näyttää käyttäjälle tuntemattomalta henkilöltä tai tyhjältä virkailijalistalta tunnin ajan.
- **Virhetulokset välimuistiin:** `KoodistoClientImpl.java:91-100` (`koodistokoodit`), `LokalisointiServiceImpl.java:32-52` (`lokalisoinnit`) ja `OrganisaatioServiceImpl.java:57-75` (`organisaatiot`). Lisää `unless = "#result == null || #result.isEmpty()"`.
- **CAS-tilan epäjohdonmukaisuus:** `EperusteetClientImpl.java:79` käyttää useimmille kutsuille `get(url, false)`, mutta `getTiedotteetHaku` (rivi 258) `true`. Koska `RestClientFactoryImpl` välimuistittaa asiakkaan pelkän URL:n perusteella (kuten ylopsissa), ensimmäinen kutsu määrää, käyttääkö saman URL:n asiakas CAS-tunnistautumista. Käytä avaimena `service + requireCas`.
- **N+1-HTTP-kutsut:** `KayttajanTietoServiceImpl.java:290-294` (yksi kutsu organisaatiota kohden) ja `service/tilastot/impl/TilastotServiceImpl.java:46-53` (`findAll` ja kolme `count`-kyselyä toimijaa kohden).
- **Näennäinen sivutus:** `EperusteetClientImpl.java:189, 211` käyttää `sivukoko=9999`. Toteuta oikea sivutus.
- **URL-koodaus puuttuu:** `LokalisointiServiceImpl.java:35`, `OrganisaatioServiceImpl.java:60, 80`, `KoodistoClientImpl.java:83, 94, 158` ja `EperusteetClientImpl.java:170` (diaarinumero). Käytä `UriComponentsBuilder`-luokkaa, kuten `TiedoteQueryDto.toRequestParams` jo tekee.

### 8.2 Ajastukset ja batch-ajot, korkea

- `service/util/ScheduledService.java:32-42`: päivittäinen ajo klo 04 tyhjentää välimuistit ja lataa kaikki koulutustoimijat jokaisella solmulla.
- `config/ScheduledConfiguration.java:18-20`: tunneittainen istuntojen siivous jokaisella solmulla.
- `service/batch/JulkaisuJobReader.java:59-75`: batch-ajo lataa kaikki opetussuunnitelmat muistiin (`findByTyyppi`) ja suodattaa ne vasta sitten. Batch-julkaisu laskee revision samalla max + 1 -periaatteella (kohta 3.3).
- `service/koulutustoimija/impl/OpetussuunnitelmaServiceImpl.java:1079-1095` (`updateOpetussuunnitelmaSisaltoviitePiilotukset`): `repository.findAll()` ja nielaistu `BusinessRuleViolationException`.

**Korjaus:** ShedLock (JDBC) ajastettuihin tehtäviin, sivutettu lukija batch-ajoon, ja virheet lokiin vähintään tasolla `warn`.

---

## 9. Domain-malli ja persistointi

### 9.1 equals/hashCode, korkea

| Luokka | Ongelma |
|---|---|
| `domain/ammattitaitovaatimukset/AmmattitaitovaatimuksenKohdealue.java:50-63` | `equals` vertaa `id`-kenttää, `hashCode`-metodia ei ole. Kaikki tallentamattomat oliot (`id == null`) ovat keskenään yhtä suuria |
| `domain/arviointi/Arviointi.java:82-103` | Syvä sisältövertailu ja kokoelmiin perustuva `hashCode` muuttuvalle entiteettiverkolle |
| `domain/OsaamistasonKriteeri.java:76-94` | Sama kuin edellinen |

Samat luokat ovat rikki myös eperusteessa, joten korjaus kannattaa tehdä molempiin samalla mallilla: `id != null && id.equals(o.id)` ja luokkakohtainen vakio-`hashCode`.

### 9.2 Kaskadit, keskitaso

- `domain/Osaamistaso.java:20-21`: `CascadeType.ALL` ja EAGER `@ManyToOne`-liitoksella muuttumattomaan ja välimuistitettuun `LokalisoituTeksti`-entiteettiin. Poiston kaskadi voi poistaa jaetun tekstin. Käytä korkeintaan `PERSIST`-kaskadia.
- `CascadeType.ALL` ilman `orphanRemoval`-asetusta: `domain/tutkinnonosa/`-hakemiston `OmaTutkinnonosa.java:62`, `OsaAlue.java:46`, `Osaamistavoite.java:77` ja `Tutkinnonosa.java:66, 71` sekä `domain/arviointi/Arviointiasteikko.java:22`.
- EAGER-hakuja on 24. Pahimmat: `domain/tutkinnonosa/Suorituspolku.java:31` (`@OneToMany` rivit), `Tutkinnonosa.java:66` (`omatutkinnonosa`), `Arviointiasteikko.java:22` (`osaamistasot`) sekä `TutkinnonosaToteutus`- ja `OmaOsaAlueToteutus`-luokkien `@OneToOne`-liitokset.

### 9.3 Kopiointi jakaa viitteitä, keskitaso

- `domain/OsaamistasonKriteeri.java:97-103`: kopio jakaa `osaamistaso`-viitteen ja `kriteerit`-listan.
- `domain/tutkinnonosa/Tutkinnonosa.java:95`: kopio jakaa `osaamisenOsoittaminen`-viitteen.
- `domain/tutkinnonosa/OsaamismerkkiKappale.java:49`: kopio jakaa `kuvaus`-viitteen.
- `domain/teksti/SisaltoViite.java:269, 279, 283`: kopio jakaa `ohjeteksti`-, `CachedPeruste`- ja `pohjanTekstikappale`-viitteet. `CachedPeruste` jaetaan tarkoituksella, ja `LokalisoituTeksti` on muuttumaton, mutta `pohjanTekstikappale` pitää tarkistaa.

Jos jaettu olio on muuttuva, muokkaus näkyy molemmissa kopioissa. Tee syväkopio tai varmista, että tyyppi on muuttumaton.

### 9.4 Repository ja mappaus, keskitaso/matala

- `repository/liite/impl/LiiteRepositoryImpl.java:21-24`: `IOUtils.toByteArray(is)` lukee koko tiedoston muistiin. Käytä `BlobProxy.generateProxy(is, length)` ja kokorajaa.
- `getOne()`-kutsuja 3: `OpetussuunnitelmaServiceImpl.java:706` ja `OpetussuunnitelmaAikatauluServiceImpl.java:28, 42`. Korvaa `findById`-kutsulla.
- `service/mapping/LokalisoituTekstiConverter.java:29-45`: asiakkaan antama `dto.getId()` aiheuttaa tietokantahaun. Rajoita tai ohita tuntemattomat id:t.
- `service/mapping/ReferenceableEntityConverter.java:44-45`: `em.getReference` palauttaa laiskan proxyn, joten puuttuva viite huomataan myöhään.
- `service/mapping/OptionalSupport.java:31`: TODO. `Optional`-arvojen merge-semantiikka voi tyhjentää kenttiä yllättäen.
- `domain/teksti/SisaltoViite.java:222-227` (`getRoot`) ja `SisaltoViiteServiceImpl.java:700-718` (`kopioiHierarkia`): rekursio ilman syklisuojaa. `CollectionUtil.treeToStream` suojaa jo; käytä samaa mallia.

---

## 10. Koodin laatu

Suurimmat luokat (riviä):

| Luokka | Rivit |
|---|---:|
| `service/ops/impl/SisaltoViiteServiceImpl` | 1252 |
| `service/koulutustoimija/impl/OpetussuunnitelmaServiceImpl` | 1116 |
| `domain/teksti/SisaltoViite` | 631 |
| `service/koulutustoimija/impl/JulkaisuServiceImpl` | 522 |
| `service/external/impl/EperusteetServiceImpl` | 465 |
| `resource/koulutustoimija/OpetussuunnitelmaController` | 450 |

Muut mittarit:

- TODO- ja FIXME-merkintöjä 15.
- `printStackTrace`-kutsuja 2: `MaintenanceServiceImpl.java:162` ja `OpetussuunnitelmaServiceImpl.java:1056`. `System.out`-kutsuja ei ole.
- Tyhjiä `catch`-lohkoja 3: `ValidointiServiceImpl.java:268, 276` ja `KayttajanTietoServiceImpl.java:270`.
- `java.util.Date`-kenttiä domain-luokissa 23.
- `service/dokumentti/impl/ExternalPdfServiceImpl.java:70-77`: HTTP-virheet muunnetaan merkkijonoksi `"error"` ja sen jälkeen geneeriseksi poikkeukseksi, jolloin tilakoodi katoaa.

---

## 11. Testaus

Nykytila: 37 testitiedostoa, joista noin 27 on testiluokkia. `AmosaaAppRolesIT` testaa roolien rajoja.

Puutteet ja toimenpiteet:

1. **Julkaisun testi on ohitettu.** `JulkaisuServiceIt` on merkitty `@Ignore`-annotaatiolla, joten julkaisuvirralla (kohdat 3.3 ja 5) ei ole testejä. Korjaa ja ota käyttöön. Lisää testit validointivirheelle, PDF-virheelle ja samanaikaiselle julkaisulle.
2. **Oikeustestit:** laajenna `AmosaaAppRolesIT` testaamaan IDOR-tapaukset: toisen koulutustoimijan `opsId`, toisen opetussuunnitelman liite tai dokumentti, sekä sisältöviitteiden kopiointi toisesta opetussuunnitelmasta (kohdat 4.1, 7.2 ja 7.3).
3. **Validointi:** yksikkötestit `ValidointiServiceImpl`-luokan rakennetarkistuksille puuttuvalla koolla ja laajuudella (kohta 4.3).
4. **Integraatiot:** `EperusteetClientImpl`, `KoodistoClientImpl`, `LokalisointiServiceImpl`, `ArviointiasteikkoServiceImpl`, `ExternalPdfServiceImpl` ja `KayttooikeusServiceImpl` ilman testejä. Lisää WireMock-testit virhevastauksille ja välimuistikäytökselle.
5. **Muut ohitetut testit:** `SisaltoViiteServiceIT` ja `HtmlValidationTest`. Korjaa tai poista.

---

## 12. Parannusideat

- **Yhteiset korjaukset sisarpalveluiden kanssa.** PDF-takaisinkutsujen palvelutunnistus, suodatinketjun oletuskielto, CSRF, `RestClientFactoryImpl`-välimuistin avain, jaettu HTTP-asiakas aikakatkaisuilla, ShedLock ja julkaisun uniikkiehto kannattaa toteuttaa kerran ja ottaa käyttöön kaikissa kolmessa palvelussa.
- **Polkuhierarkian sidonta.** Amosaan reitit ovat muotoa `/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/...`. Tee yhteinen apumetodi (esimerkiksi `OpsContext.resolve(ktId, opsId)`), joka hakee opetussuunnitelman ja varmistaa sen kuuluvan koulutustoimijaan, ja käytä sitä kaikissa palvelumetodeissa. Liitteet, dokumentit ja sisältöviitteet haetaan aina tämän kontekstin kautta.
- **`findByIdOrThrow`** `CustomJpaRepository`-rajapintaan, ja vaiheittainen `findOne`-kutsujen korvaus.
- **Julkaisun tilakone** yhteen paikkaan: tilat `JULKAISEMATON`, `KESKEN`, `JULKAISTU` ja `VIRHE`, asynkroninen työ commitin jälkeen, KESKEN-esto, virhekoodi käyttöliittymälle ja palautus käynnistyksessä.
- **Staattinen analyysi** (Error Prone tai SpotBugs) CI:hin: tyhjät `catch`-lohkot, switch-lauseen fall-through, `printStackTrace` ja `Optional.get()`.
- **Mappaus:** vaiheittainen siirtymä Orikasta MapStructiin.
- **Suurten luokkien jako:** `SisaltoViiteServiceImpl` (rakenne, kopiointi ja linkitys, tyyppikohtaiset päivitykset) ja `OpetussuunnitelmaServiceImpl` (luonti, tilat, haku).

---

## 13. Tarkistetut väitteet, jotka eivät ole ongelmia

- **Lukon avaus ilman omistajatarkistusta.** `AbstractLockService.unlock` vaatii vain kirjautumisen, mutta `service/locking/LockManagerImpl.java:106-116` tarkistaa, että lukko on kirjautuneen käyttäjän, ja heittää muuten `LockingException`-poikkeuksen.
- **Kommentit.** Toisin kuin eperusteessa ja ylopsissa, kommentit on rajattu opetussuunnitelman LUKU- tai KOMMENTOINTI-oikeuteen.
- **Sisältöviitteiden omistajuus muokkauksessa.** `SisaltoViiteServiceImpl.findViite` käyttää `findOneByOwnerIdAndId`-kyselyä, joten muokattava viite kuuluu aina polun opetussuunnitelmaan. Ongelma koskee vain kopioinnin ja linkityksen lähdeviitteitä (kohta 4.1).
- **Puun uudelleenjärjestely.** `reorderSubTree` (`SisaltoViiteServiceImpl.java:853`) hakee juuren `findViite(opsId, ...)`-kutsulla ja validoi uuden rakenteen vanhoja viitteitä vasten, joten viitettä ei voi siirtää toisesta opetussuunnitelmasta.
- **`CollectionUtil.treeToStream`** (`service/util/CollectionUtil.java:21-58`) ohittaa jo käsitellyt solmut, joten sitä käyttävät puun läpikäynnit eivät jää silmukkaan.
- **Julkaisun tila-tallennus.** `saveJulkaistuOpetussuunnitelmaTila` kutsutaan `self`-proxyn kautta, joten `REQUIRES_NEW` toteutuu (toisin kuin ylopsissa).
- **Async-säikeen tietoturvakonteksti.** `AsyncConfig` ja `DefaultConfigs` käyttävät `DelegatingSecurityContext*`-luokkia.
- **SQL-injektio.** `OpetussuunnitelmaRepositoryImpl` käyttää Criteria API:a, ja `JulkaisuRepository`-luokan natiivikyselyt nimettyjä parametreja.
- **`SisaltoViiteServiceImpl`-transaktiot.** Luokka on `readOnly = true`, mutta kirjoittavilla metodeilla on oma `@Transactional(readOnly = false)`.
- **`SisaltoViite.copy`** (`domain/teksti/SisaltoViite.java:258-`) kopioi tyyppikohtaiset lapsientiteetit (`Tutkinnonosa`, `Suorituspolku`, `TekstiKappale` ym.) omilla `copy`-metodeillaan. Jaetut viitteet on listattu kohdassa 9.3.
- **`Liite.data`** on laiskasti ladattava `Blob`.
- **S3 ja tiedostonimet:** palvelu ei käytä S3:a.
