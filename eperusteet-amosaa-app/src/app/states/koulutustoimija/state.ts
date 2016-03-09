angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija", {
    abstract: true,
    url: "/koulutustoimija/:ktId",
    resolve: {
        koulutustoimijat: (Api) => Api.all("koulutustoimijat"),
        koulutustoimija: ($stateParams, koulutustoimijat) => koulutustoimijat.get($stateParams.ktId),
        yhteiset: (koulutustoimija) => koulutustoimija.yhteiset,
        nimiLataaja: ($q, koulutustoimija) => (kayttajaOid) =>
            $q((resolve) =>
                koulutustoimija.one("kayttaja", kayttajaOid).get()
                    .then(res => resolve(Kayttajatiedot.parsiEsitysnimi(res)))
                    .catch(() => resolve(KaannaService.kaanna("muokkaajaa-ei-loytynyt")))),
        opetussuunnitelmat: (koulutustoimija) => Fake.Opetussuunnitelmat(koulutustoimija.id),
        perusteet: Eperusteet => Eperusteet.one("perusteet").get()
    },
    controller: (koulutustoimija) => {
    }
}));
