angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", {
    url: "/osa/:osaId",
    resolve: {
        osa: (ops, $stateParams) => ops.one("tekstit", $stateParams.osaId).get(),
        kommentit: (osa) => osa.all("kommentit").getList()
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
                                    $timeout(() => {
                                        $state.reload("root");
                                        $state.go("root.koulutustoimija.opetussuunnitelmat.poistetut", $stateParams, { reload: true });
                                    });
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
            controller: ($scope, osa) => {
                osa.tosa.arvioinnista = osa.tosa.arvioinnista || {};
                osa.tosa.tavatjaymparisto = osa.tosa.tavatjaymparisto || {};
            }
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
        kommentointi: {
            controller: ($scope, kommentit, osa, Varmistusdialogi, orgoikeudet, kayttaja, $stateParams) => {
                $scope.$$kommenttiMaxLength = {
                    maara: 1024
                };

                const rikastaKommentti = (kommentti) => {
                    // Sisällön kloonaus peruutusta varten
                    kommentti.$$sisaltoKlooni = kommentti.sisalto;
                    kommentti.$$vastaus = {
                        sisalto: "",
                        $$sisaltoKlooni: ""
                    };

                    // Oikeudet
                    kommentti.$$muokkausSallittu = kommentti.luoja === kayttaja.oidHenkilo;
                    kommentti.$$poistaSallittu = Oikeudet.onVahintaan("hallinta",
                            Oikeudet.opsOikeus($stateParams.opsId)) || kommentti.luoja === kayttaja.oidHenkilo;
                };

                $scope.kommentit = _(kommentit)
                    .forEach(k => rikastaKommentti(k))
                    .filter(kommentti => kommentti.parentId === 0)
                    .map((kommentti: any) => {
                        // Aseta lapset
                        kommentti.$$lapset = _.filter(kommentit, (lapsi: any) => {
                            if (lapsi.parentId === kommentti.id) {
                                return lapsi;
                            }
                        });

                        return kommentti;
                    })
                    .value();

                $scope.avaaTekstikentta = (kommentti) => {
                    kommentti.$$sisaltoKlooni = kommentti.sisalto;
                    kommentti.$$isMuokkaus = true;
                };

                $scope.vastaaKommentti = (kommentti, parentId) => {
                    kommentti.parentId = parentId;
                    kommentti.sisalto = kommentti.$$sisaltoKlooni;
                    osa.all("kommentit").post(kommentti)
                        .then((uusi) => {
                            rikastaKommentti(uusi);
                            uusi.$$lapset = [];
                            if (parentId === 0) {
                                $scope.kommentit.unshift(uusi);
                                kommentti.$$isVastaus = false;
                            } else {
                                let parentKommentti: any = _($scope.kommentit).find({ id: uusi.parentId });
                                parentKommentti.$$lapset.unshift(uusi);
                                parentKommentti.$$isVastaus = false;
                            }
                        });
                };

                $scope.lisaaKommentti = (kommentti) => {
                    $scope.vastaaKommentti(kommentti, 0);
                };

                $scope.muokkaaKommentti = (kommentti) => {
                    kommentti.sisalto = kommentti.$$sisaltoKlooni;
                    kommentti.save()
                        .then((uusi) => _.merge(kommentti, uusi.plain()));
                    kommentti.$$isMuokkaus = false;
                };

                $scope.poistaKommentti = (kommentti) => {
                    Varmistusdialogi.dialogi({
                        otsikko: 'vahvista-poisto',
                        teksti: 'poistetaanko-kommentti',
                        primaryBtn: 'poista',
                        successCb: () => {
                            kommentti.remove()
                                .then(() => {
                                    kommentti.poistettu = true;
                                    kommentti.sisalto = "";
                                });
                        }
                    })();
                };
            }
        }
    }
}));
