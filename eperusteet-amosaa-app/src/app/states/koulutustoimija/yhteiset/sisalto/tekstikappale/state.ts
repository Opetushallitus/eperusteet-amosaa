angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.sisalto.tekstikappale", {
    url: "/tekstikappale/:tkvId",
    resolve: {
        tekstikappale: (yhteiset, $stateParams) => yhteiset.one("tekstit", $stateParams.tkvId).get()
    },
    views: {
        "": {
            controller: ($state, $stateParams, $location, $scope, $timeout, tekstikappale, nimiLataaja) => {
                tekstikappale.lapset = undefined;
                $scope.edit = EditointikontrollitService.createRestangular($scope, "tkv", tekstikappale);
                nimiLataaja(tekstikappale.tekstiKappale.muokkaaja)
                    .then(nimi => $scope.tkv.tekstiKappale.$$nimi = nimi);
                $scope.remove = () => {
                    tekstikappale.remove()
                        .then(() => {
                            NotifikaatioService.onnistui("poisto-tekstikappale-onnistui");
                            EditointikontrollitService.cancel()
                                .then(() => {
                                    $state.go("root.koulutustoimija.yhteiset.poistetut", $stateParams, { reload: true });
                                });
                        });
                };
            }
        }
    }
}));
