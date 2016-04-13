angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat.sisalto.tekstikappale", {
    url: "/tekstikappale/:tkvId",
    resolve: {
        tekstikappale: (ops, $stateParams) => ops.one("tekstit", $stateParams.tkvId).get(),
        kommentit: (tekstikappale) => tekstikappale.all("kommentit").getList()
    },
    onEnter: (tekstikappale) =>
        Murupolku.register("root.koulutustoimija.opetussuunnitelmat.sisalto.tekstikappale", tekstikappale.tekstiKappale.nimi),
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
        kommentointi: {
            controller: ($rootScope, $scope, kommentit, tekstikappale, Varmistusdialogi) => {
                $scope.kommentit = _(kommentit)
                    .filter((kommentti: any) => kommentti.parentId === 0)
                    .map((kommentti: any) => {
                        kommentti.lapset = _.filter(kommentit, (lapsi: any) => {
                            if (lapsi.parentId === kommentti.id) {
                                return lapsi;
                            }
                        });
                        return kommentti;
                    })
                    .value();

                $scope.vastaaKommenttiin = (kommentti, parentId) => {
                    $rootScope.$broadcast("notifyCKEditor");
                    kommentti.parentId = parentId;
                    tekstikappale.all("kommentit").post(kommentti)
                        .then((uusi) => console.log(uusi));
                };

                $scope.lisaaKommentti = (kommentti) => {
                    $scope.vastaaKommenttiin(kommentti, 0);
                };

                $scope.muokkaaKommentti = (kommentti) => {
                    $rootScope.$broadcast("notifyCKEditor");
                    kommentti.save()
                        .then((uusi) => _.merge(kommentti, uusi.plain()));
                    kommentti.isMuokkaus = !kommentti.isMuokkaus;
                };

                console.log(_($scope.kommentit)
                    .find({id: 128})
                );

                $scope.poistaKommentti = (kommentti) => {
                    Varmistusdialogi.dialogi({
                        otsikko: 'vahvista-poisto',
                        teksti: 'poistetaanko-kommentti',
                        primaryBtn: 'poista',
                        successCb: () => {
                            kommentti.remove()
                                .then(() => {
                                    if (kommentti.parentId == 0) {
                                        $scope.kommentit = _.without($scope.kommentit, kommentti)
                                    } else {
                                        let parentKommentti: any = _($scope.kommentit).find({id: 128});
                                        parentKommentti.lapset = _.without(parentKommentti.lapset, kommentti);
                                    }
                                });
                        }
                    })();
                };
            }
        }
    }
}));
