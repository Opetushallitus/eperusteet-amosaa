angular.module("app")
    .config(function ($stateProvider) { return $stateProvider
    .state("root.koulutustoimija", {
    url: "/koulutustoimija/:ktId",
    resolve: {
        koulutustoimija: function ($stateParams) { return Fake.Koulutustoimijat()[$stateParams.ktId]; },
        yhteinen: function (koulutustoimija) { return Fake.YhteisetOsat()[koulutustoimija.yhteinenOsa]; },
        opetussuunnitelmat: function (koulutustoimija) { return Fake.Opetussuunnitelmat(koulutustoimija.id); }
    },
    controller: function ($scope, yhteinen, kayttajaprofiili, koulutustoimija, opetussuunnitelmat) {
        $scope.yhteinen = yhteinen;
        $scope.kayttajaprofiili = kayttajaprofiili;
        $scope.koulutustoimija = koulutustoimija;
        $scope.opetussuunnitelmat = opetussuunnitelmat;
    }
}); });
//# sourceMappingURL=state.js.map