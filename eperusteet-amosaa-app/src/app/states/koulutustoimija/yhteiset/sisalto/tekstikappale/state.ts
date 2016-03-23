angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.sisalto.tekstikappale", {
    url: "/tekstikappale/:tkvId",
    resolve: {
        tekstikappale: (yhteiset, $stateParams) => yhteiset.one("tekstit", $stateParams.tkvId).get()
    },
    views: {
        "": {
            controller: ($state, $stateParams, $location, $scope, $rootScope, $document, $timeout, tekstikappale, nimiLataaja) => {
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
                const clickHandler = (event) => {
                    var ohjeEl = angular.element(event.target).closest('.popover, .popover-element');
                    if (ohjeEl.length === 0) {
                        $rootScope.$broadcast('ohje:closeAll');
                    }
                };

                const installClickHandler = () => {
                    $document.off('click', clickHandler);
                    $timeout(() => {
                        $document.on('click', clickHandler);
                    });
                };

                $scope.$on('$destroy', function () {
                    $document.off('click', clickHandler);
                });

                installClickHandler();
            }
        }
    }
}));
