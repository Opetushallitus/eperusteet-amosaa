angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija", {
    url: "/koulutustoimija/:ktId",
    resolve: {
        koulutustoimija: ($stateParams) => Fake.Koulutustoimijat()[$stateParams.ktId],
        yhteinen: (koulutustoimija) => Fake.YhteisetOsat()[koulutustoimija.yhteinenOsa],
        opetussuunnitelmat: (koulutustoimija) => Fake.Opetussuunnitelmat(koulutustoimija.id)
    },
    controller: ($scope, yhteinen, kayttajaprofiili, koulutustoimija, opetussuunnitelmat) => {
        $scope.yhteinen = yhteinen;
        $scope.kayttajaprofiili = kayttajaprofiili;
        $scope.koulutustoimija = koulutustoimija;
        $scope.opetussuunnitelmat = opetussuunnitelmat;
    }
}))
