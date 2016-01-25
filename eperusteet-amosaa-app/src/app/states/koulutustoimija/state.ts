angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija", {
    url: "/koulutustoimija/:ktId",
    resolve: {
        koulutustoimija: ($stateParams) => Fake.Koulutustoimijat()[$stateParams.ktId],
        yhteinen: (koulutustoimija) => Fake.YhteisetOsat()[koulutustoimija.yhteinenOsa],
        opetussuunnitelmat: (koulutustoimija) => Fake.Opetussuunnitelmat(koulutustoimija.id),
        perusteet: Eperusteet => Eperusteet.one("perusteet").get()
    },
    views: {
        "": {
            controller: ($scope, yhteinen, kayttajaprofiili, koulutustoimija) => {
                $scope.yhteinen = yhteinen;
                $scope.kayttajaprofiili = kayttajaprofiili;
                $scope.koulutustoimija = koulutustoimija;
            }
        },
        opetussuunnitelmat: {
            controller: ($scope, perusteet) => {
                $scope.opsit = perusteet;
            }
        }
    }
}))
