angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija", {
    abstract: true,
    url: "/koulutustoimija/:ktId",
    resolve: {
        koulutustoimija: ($stateParams) => Fake.Koulutustoimijat()[$stateParams.ktId],
        yhteinen: (koulutustoimija) => Fake.YhteisetOsat()[koulutustoimija.yhteinenOsa],
        opetussuunnitelmat: (koulutustoimija) => Fake.Opetussuunnitelmat(koulutustoimija.id),
        perusteet: Eperusteet => Eperusteet.one("perusteet").get()
    },
    controller: () => {}
}));
