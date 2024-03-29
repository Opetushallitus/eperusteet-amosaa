angular.module("app").config($stateProvider =>
    $stateProvider.state("root.koulutustoimija", {
        abstract: true,
        url: "/koulutustoimija/:ktId",
        resolve: {
            koulutustoimijat: Api => Api.all("koulutustoimijat"),
            koulutustoimija: ($stateParams, koulutustoimijat) => koulutustoimijat.get($stateParams.ktId),
            nimiLataaja: ($q, koulutustoimija) => kayttajaOid =>
                $q(resolve =>
                    koulutustoimija
                        .one("kayttajat", kayttajaOid)
                        .get()
                        .then(res => resolve(Kayttajatiedot.parsiEsitysnimi(res)))
                        .catch(() => resolve(KaannaService.kaanna("muokkaajaa-ei-loytynyt") + " (" + kayttajaOid + ")"))
                ),
            opsOletusRajaus: () => {
                return {
                    sivukoko: 10,
                    tyyppi: ['ops', 'yleinen'],
                    koulutustyyppi: Amosaa.tuetutKoulutustyypit(),
                    tila: _(Constants.tosTilat)
                        .filter(tila => tila !== "poistettu")
                        .value()
                }
            },
            opetussuunnitelmatApi: (koulutustoimija) => koulutustoimija.all("opetussuunnitelmat"),
            opetussuunnitelmatSivu: (koulutustoimija, opsOletusRajaus) =>
                koulutustoimija.one("opetussuunnitelmat").customGET("", opsOletusRajaus),
            ystavaOpsit: koulutustoimija => koulutustoimija.all("opetussuunnitelmat/ystavien").getList(),
            async yhteiset(koulutustoimija, opsOletusRajaus) {
                return (await koulutustoimija.one("opetussuunnitelmat").customGET("", {
                    sivukoko: 10,
                    tyyppi: 'yhteinen',
                    tila: _(Constants.tosTilat)
                        .filter(tila => tila !== "poistettu")
                        .value(),
                }));
            },
            kayttajanTieto: koulutustoimija => kayttajaId => koulutustoimija.one("kayttajat", kayttajaId).get(),
            paikallisetTutkinnonosatEP: koulutustoimija => koulutustoimija.all("tutkinnonosat")
        },
        onEnter: koulutustoimija => Murupolku.register("root.koulutustoimija", koulutustoimija.nimi),
        controller: () => {}
    })
);
