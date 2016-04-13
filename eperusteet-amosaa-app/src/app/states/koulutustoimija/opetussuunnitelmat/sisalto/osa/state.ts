angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", {
    url: "/osa/:osaId",
    resolve: {
        osa: (ops, $stateParams) => ops.one("tekstit", $stateParams.osaId).get()
    },
    onEnter: (osa) =>
        Murupolku.register("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", osa.tekstiKappale.nimi),
    views: {
        "": {
            controller: ($state, $stateParams, $location, $scope, $rootScope, $document, $timeout, osa, nimiLataaja) => {
                osa.lapset = undefined;
                $scope.edit = EditointikontrollitService.createRestangular($scope, "osa", osa);
                nimiLataaja(osa.tekstiKappale.muokkaaja)
                    .then(nimi => $scope.osa.tekstiKappale.$$nimi = nimi);
                $scope.remove = () => {
                    osa.remove()
                        .then(() => {
                            NotifikaatioService.onnistui("poisto-tekstikappale-onnistui");
                            EditointikontrollitService.cancel()
                                .then(() => {
                                    $state.go("root.koulutustoimija.opetussuunnitelmat.poistetut", $stateParams, { reload: true });
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
        },
        tekstikappale: {
            controller: ($scope, osa) => {}
        },
        tutkinnonosat: {
            controller: ($scope, osa) => {}
        },
        tutkinnonosa: {
            controller: ($scope, osa) => {}
        },
        tutkinnonosaryhma: {
            controller: ($scope, osa) => {}
        },
        suorituspolut: {
            controller: ($scope, osa) => {}
        },
        suorituspolku: {
            controller: ($scope, osa) => {}
        },
    }
}));
