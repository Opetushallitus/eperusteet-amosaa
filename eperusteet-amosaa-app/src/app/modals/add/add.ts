namespace ModalAdd {
    let i;
    export const init = $injector => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q", "$timeout"]);
    };

    const filterPerusteet = (perusteet = [], query = "") =>
        _(perusteet)
            .filter(peruste => KaannaService.hae(peruste.nimi, query))
            .value();

    export const yhteinen = () =>
        i.$uibModal.open({
            resolve: {
                pohjat: Api =>
                    Api.all("opetussuunnitelmat")
                        .all("pohjat")
                        .getList()
            },
            templateUrl: "modals/add/yhteinen.jade",
            controller: ($scope, $state, $stateParams, $uibModalInstance, pohjat) => {
                $scope.pohjat = pohjat;
                $scope.ok = $uibModalInstance.close;
                $scope.peruuta = $uibModalInstance.dismiss;
                $scope.yhteinen = {
                    tyyppi: "yhteinen"
                };

                $scope.valitsePohja = pohja => {
                    $scope.valittuPohja = pohja;
                    $scope.yhteinen._pohja = "" + pohja.id;
                };

                if (_.size($scope.pohjat) === 1) {
                    $scope.valitsePohja(_.first($scope.pohjat));
                }

                // Kielivalitsin
                $scope.langs = KieliService.getSisaltokielet();
                $scope.currentLang = $stateParams.lang;
                $scope.selectLang = lang => {
                    $scope.currentLang = lang;
                    KieliService.setSisaltokieli(lang);
                };
            }
        }).result;

    export const yleinen = () =>
        i.$uibModal.open({
            resolve: {},
            templateUrl: "modals/add/yleinen.jade",
            controller: ($scope, $state, $stateParams, $uibModalInstance) => {
                $scope.ok = $uibModalInstance.close;
                $scope.peruuta = $uibModalInstance.dismiss;
                $scope.ops = { tyyppi: "yleinen" };
                // Kielivalitsin
                $scope.langs = KieliService.getSisaltokielet();
                $scope.currentLang = $stateParams.lang;
                $scope.selectLang = lang => {
                    $scope.currentLang = lang;
                    KieliService.setSisaltokieli(lang);
                };
            }
        }).result;

    export const pohja = () =>
        i.$uibModal.open({
            resolve: {},
            templateUrl: "modals/add/pohja.jade",
            controller: ($scope, $state, $stateParams, $uibModalInstance) => {
                $scope.ok = $uibModalInstance.close;
                $scope.peruuta = $uibModalInstance.dismiss;
                $scope.pohja = {
                    tyyppi: "pohja"
                };
                // Kielivalitsin
                $scope.langs = KieliService.getSisaltokielet();
                $scope.currentLang = $stateParams.lang;
                $scope.selectLang = lang => {
                    $scope.currentLang = lang;
                    KieliService.setSisaltokieli(lang);
                };
            }
        }).result;

    export const opetussuunnitelma = () =>
        i.$uibModal.open({
            resolve: {
                perusteet: Api => Api.all("perusteet").getList()
            },
            templateUrl: "modals/add/opetussuunnitelma.jade",
            controller: ($scope, $state, $stateParams, $uibModalInstance, perusteet) => {
                // perusteet: Api => Api.all("perusteet")
                const amosaaPerusteet = _(perusteet)
                    .filter(peruste => _.includes(Amosaa.tuetutKoulutustyypit(), peruste.koulutustyyppi))
                    .reject(Perusteet.isVanhentunut)
                    .map((peruste: any) => {
                        peruste.$$tuleva = Perusteet.isTuleva(peruste);
                        peruste.$$siirtymalla = Perusteet.isSiirtymalla(peruste);
                        return peruste;
                    })
                    .value();

                $scope.perusteet = amosaaPerusteet;
                $scope.peruste = undefined;
                $scope.ok = $uibModalInstance.close;
                $scope.peruuta = $uibModalInstance.dismiss;

                $scope.update = input => {
                    if (!_.isEmpty(input)) {
                        $scope.perusteet = filterPerusteet(amosaaPerusteet, input);
                        for (let peruste of $scope.perusteet) {
                            peruste.$$haettu = KaannaService.hae(peruste.nimi, input);
                        }
                    } else {
                        $scope.perusteet = [];
                        $scope.peruste = undefined;
                    }
                };

                $scope.valitsePeruste = peruste => {
                    $scope.input = "";
                    $scope.peruste = peruste;
                    $scope.stMap = _.indexBy($scope.peruste.suoritustavat, "suoritustapakoodi");
                    const opsSt = $scope.stMap["ops"];
                    $scope.ops = {
                        tyyppi: "ops",
                        perusteId: peruste.id,
                        perusteDiaarinumero: peruste.diaarinumero,
                        suoritustapa: opsSt ? "ops" : $scope.peruste.suoritustavat[0].suoritustapakoodi
                    };
                };

                // Kielivalitsin
                $scope.langs = KieliService.getSisaltokielet();
                $scope.currentLang = $stateParams.lang;
                $scope.selectLang = lang => {
                    $scope.currentLang = lang;
                    KieliService.setSisaltokieli(lang);
                };
            }
        }).result;

    export const sisaltoAdder = (koulutustoimija, sallitut = ["tekstikappale"], peruste) =>
        i.$uibModal.open({
            resolve: {},
            templateUrl: "modals/add/sisalto.jade",
            controller: ($uibModalInstance, $scope, $stateParams, Api) => {
                $scope.currentStage = "sisaltotyyppi";
                $scope.$$tutkinnonosatuonti = _.indexOf(sallitut, "tutkinnonosatuonti") !== -1;
                $scope.$$sisaltotuonti = _.indexOf(sallitut, "sisaltotuonti") !== -1;

                $scope.sallitut = _.reject(
                    sallitut,
                    sallittu => sallittu === "sisaltotuonti" || sallittu === "tutkinnonosatuonti"
                );
                $scope.valittu = undefined;
                $scope.koulutustyyppi = peruste.koulutustyyppi;
                $scope.$$isValmaTelma = _.includes(Amosaa.valmaTelmaKoulutustyypit(), peruste.koulutustyyppi);


                {
                    // Opetussuunnitelmat
                    $scope.valitseOps = ops => {
                        $scope.currentStage = "opssisalto";
                        ops
                            .one("otsikot")
                            .get()
                            .then(otsikot => {
                                const root = Tekstikappaleet.root(otsikot);
                                const rakenne = _.tail(
                                    _.flattenBy(
                                        Tekstikappaleet.teeRakenne(Tekstikappaleet.uniikit(otsikot), root.id),
                                        "lapset"
                                    )
                                );
                                _.each(rakenne, (osa: any) => {
                                    osa.$$depth -= 1;
                                });
                                $scope.rakenne = rakenne;
                            });
                    };

                    $scope.tuoSisaltoa = async () => {
                        $scope.currentStage = "opetussuunnitelma";
                        const opsit = await koulutustoimija.all("opetussuunnitelmat").customGETLIST('', {
                            koulutustyyppi: $scope.koulutustyyppi
                        });
                        $scope.opsp = PaginationV2.addPagination(
                            opsit,
                            (search: string, ops: any): boolean =>
                                ($scope.poistetut || ops.tila !== "poistettu") &&
                                (!search || _.isEmpty(search) || Algoritmit.match(search, ops.nimi))
                        );
                    };

                    $scope.lisaaOpsSisalto = () =>
                        $scope.ok(
                            _($scope.rakenne)
                                .filter(viite => viite.$$valittu)
                                .map("id")
                                .map(_.parseInt)
                                .value()
                        );
                }

                // Perusteet
                {
                    let valittuPeruste = null;
                    $scope.valitseTutkinnonosa = tosa => {
                        $scope.ok({
                            tyyppi: "tutkinnonosa",
                            tekstiKappale: {
                                nimi: {}
                            },
                            tosa: {
                                tyyppi: "vieras",
                                vierastutkinnonosa: {
                                    perusteId: valittuPeruste.id,
                                    tosaId: tosa.id
                                }
                            }
                        });
                    };

                    $scope.valitsePeruste = async peruste => {
                        $scope.currentStage = "perusteentutkinnonosat";
                        const perusteKaikki = await Api.all("perusteet")
                            .one(peruste.id + "/perusteesta")
                            .get();
                        valittuPeruste = perusteKaikki;
                        $scope.tosap = PaginationV2.addPagination(
                            Algoritmit.doSortByNimi(perusteKaikki.tutkinnonOsat),
                            (search: string, tosa: any): boolean =>
                                !search || _.isEmpty(search) || Algoritmit.match(search, tosa.nimi)
                        );
                    };

                    $scope.tuoTutkinnonosa = async () => {
                        $scope.currentStage = "perusteet";
                        const perusteet = await Api.one("perusteet").get();
                        $scope.perustep = PaginationV2.addPagination(
                            Algoritmit.doSortByNimi(perusteet),
                            (search: string, peruste: any): boolean =>
                                ($scope.poistetut || peruste.tila !== "poistettu") &&
                                (!search || _.isEmpty(search) || Algoritmit.match(search, peruste.nimi))
                        );
                    };
                }

                $scope.valitse = tyyppi => {
                    $scope.currentStage = "nimenvalinta";
                    $scope.obj = {
                        tyyppi: tyyppi,
                        tekstiKappale: {
                            nimi: {}
                        }
                    };
                };

                $scope.ok = $uibModalInstance.close;

                // Kielivalitsin
                $scope.langs = KieliService.getSisaltokielet();
                $scope.currentLang = $stateParams.lang;
                $scope.selectLang = lang => {
                    $scope.currentLang = lang;
                    KieliService.setSisaltokieli(lang);
                };
            }
        }).result;
}

angular.module("app").run(ModalAdd.init);
