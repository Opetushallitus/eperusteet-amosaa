angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija", {
    abstract: true,
    url: "/koulutustoimija/:ktId",
    resolve: {
        koulutustoimijat: (Api) => Api.all("koulutustoimijat"),
        koulutustoimija: ($stateParams, koulutustoimijat) => koulutustoimijat.get($stateParams.ktId),
        nimiLataaja: ($q, koulutustoimija) => (kayttajaOid) =>
            $q((resolve) =>
                koulutustoimija.one("kayttajat", kayttajaOid).get()
                    .then(res => resolve(Kayttajatiedot.parsiEsitysnimi(res)))
                    .catch(() => resolve(KaannaService.kaanna("muokkaajaa-ei-loytynyt") + " (" + kayttajaOid + ")"))),
        opetussuunnitelmat: (koulutustoimija) => koulutustoimija.all("opetussuunnitelmat").getList(),
        yhteiset: (opetussuunnitelmat) => _.filter(opetussuunnitelmat, { tyyppi: "yhteinen" }),
        kayttajanTieto: (koulutustoimija) => (kayttajaId) => koulutustoimija.one("kayttajat", kayttajaId).get(),
        paikallisetTutkinnonosatEP: (koulutustoimija) => koulutustoimija.all("tutkinnonosat"),
    },
    onEnter: (koulutustoimija) => Murupolku.register("root.koulutustoimija", koulutustoimija.nimi),
    controller: () => {}
}));
