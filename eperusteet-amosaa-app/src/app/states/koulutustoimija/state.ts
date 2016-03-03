angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija", {
    abstract: true,
    url: "/koulutustoimija/:ktId",
    resolve: {
        koulutustoimijat: (Api) => Api.all("koulutustoimijat"),
        koulutustoimija: ($stateParams, koulutustoimijat) => koulutustoimijat.get($stateParams.ktId),
        yhteiset: (koulutustoimija) => koulutustoimija.yhteiset,
        opetussuunnitelmat: (koulutustoimija) => Fake.Opetussuunnitelmat(koulutustoimija.id),
        perusteet: Eperusteet => Eperusteet.one("perusteet").get()
    },
    controller: (koulutustoimija) => {
    }
}));
