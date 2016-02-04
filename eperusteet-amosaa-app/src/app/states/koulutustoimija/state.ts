angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija", {
    abstract: true,
    url: "/koulutustoimija/:ktId",
    resolve: {
        jotain: (Api, $stateParams) => Api.one("koulutustoimijat/" + $stateParams.ktId).get(),
        koulutustoimija: ($stateParams) => Fake.Koulutustoimijat()[1],
        // wat: jotain => jotain.one("yhteinen").get(),
        // yhteinen: (Api, $stateParams) => Api.one("koulutustoimijat/" + $stateParams.ktId + "/yhteinen").get(),
        // yhteinen: {},
        yhteinen: ($stateParams) => Fake.YhteisetOsat()[1],
        opetussuunnitelmat: (koulutustoimija) => Fake.Opetussuunnitelmat(koulutustoimija.id),
        perusteet: Eperusteet => Eperusteet.one("perusteet").get()
    },
    controller: () => {}
}));
