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
                $scope.$$kommenttiMaxLength = {
                    maara: 1024
                };
                
                $scope.kommentit = _(kommentit)
                    .filter((kommentti: any) => kommentti.parentId === 0)
                    .map((kommentti: any) => {
                        kommentti.lapset = _.filter(kommentit, (lapsi: any) => {
                            if (lapsi.parentId === kommentti.id) {
                                return lapsi;
                            }
                        });
                        kommentti.sisaltoKlooni = kommentti.sisalto;
                        kommentti.vastaus = {
                            sisalto: "",
                            sisaltoKlooni: ""
                        };
                        return kommentti;
                    })
                    .value();

                $scope.vastaaKommentti = (kommentti, parentId) => {
                    $rootScope.$broadcast("notifyCKEditor");
                    kommentti.parentId = parentId;
                    kommentti.sisalto = kommentti.sisaltoKlooni;
                    tekstikappale.all("kommentit").post(kommentti)
                        .then((uusi) => {
                            uusi.lapset = [];
                            if (parentId === 0) {
                                $scope.kommentit.unshift(uusi);
                                kommentti.isVastaus = false;
                            } else {
                                let parentKommentti: any = _($scope.kommentit).find({ id: uusi.parentId });
                                parentKommentti.lapset.unshift(uusi);
                                parentKommentti.isVastaus = false;
                            }
                        });
                };

                $scope.lisaaKommentti = (kommentti) => {
                    $scope.vastaaKommentti(kommentti, 0);
                };

                $scope.muokkaaKommentti = (kommentti) => {
                    $rootScope.$broadcast("notifyCKEditor");
                    kommentti.sisalto = kommentti.sisaltoKlooni;
                    kommentti.save()
                        .then((uusi) => _.merge(kommentti, uusi.plain()));
                    kommentti.isMuokkaus = false;
                };

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
                                        let parentKommentti: any = _($scope.kommentit).find({id: kommentti.parentId});
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
