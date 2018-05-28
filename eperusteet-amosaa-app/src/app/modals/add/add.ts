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

    export const opetussuunnitelma = (koulutustoimija) =>
        i.$uibModal.open({
            size: "lg",
            resolve: {
                perusteetApi: Api => Api.one("perusteet"),
                opetussuunnitelmatApi: Api => Api.one("koulutustoimijat", koulutustoimija).all("opetussuunnitelmat"),
            },
            templateUrl: "modals/add/opetussuunnitelma.jade",
            controller: ($timeout, $scope, $state, $stateParams, $uibModalInstance, perusteetApi, opetussuunnitelmatApi) => {
                console.log(opetussuunnitelmatApi);
                $scope.nimi = "";
                $scope.sivu = 1;
                $scope.sivuja = 1;
                $scope.total = 0;
                $scope.perusteet = null;
                $scope.ladataan = true;
                $scope.valittuTyyppi = null;
                $scope.valitseTyyppi = (tyyppi) => {
                    $scope.valittuTyyppi = tyyppi;
                };

                opetussuunnitelmatApi.getList().then(res => {
                    $scope.opetussuunnitelmat = res;
                    console.log(res.plain());
                });

                $scope.update = function(nimi = "", sivu = 1) {
                    $scope.ladataan = true;
                    $timeout(async () => {
                        const res = await perusteetApi.customGET("haku", {
                            nimi,
                            sivu: sivu - 1,
                            sivukoko: 10,
                            kieli: KieliService.getSisaltokieli()
                        });
                        $scope.ladataan = false;
                        const { data, ...params } = res;
                        $scope.perusteet = data;
                        $scope.sivuja = params.sivuja;
                        $scope.total = params.kokonaismäärä;
                    });
                };
                $scope.update();

                $scope.peruste = undefined;
                $scope.ok = $uibModalInstance.close;
                $scope.peruuta = $uibModalInstance.dismiss;

                $scope.valitseOpetussuunnitelma = ops => {
                    $scope.input = "";
                    $scope.peruste = ops;
                    $scope.ops = {
                        perusteId: ops.peruste.id,
                        perusteDiaarinumero: ops.peruste.diaarinumero,
                        opsId: ops.id
                    };
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
                    $scope.perusteet = null;
                    $scope.update();
                };
            }
        }).result;

    export const sisaltoAdder = (koulutustoimija, sallitut = ["tekstikappale"], peruste) =>
        i.$uibModal.open({
            resolve: {
                perusteetApi: Api => Api.one("perusteet")
            },
            templateUrl: "modals/add/sisalto.jade",
            controller: ($uibModalInstance, $scope, $stateParams, Api, perusteetApi, $timeout) => {
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

                    $scope.nimi = "";
                    $scope.sivu = 1;
                    $scope.sivuja = 1;
                    $scope.total = 0;
                    $scope.perusteet = null;
                    $scope.ladataan = true;

                    $scope.tuoTutkinnonosa = async () => {
                        $scope.currentStage = "perusteet";


                        $scope.update = function(nimi = "", sivu = 1) {
                            $scope.ladataan = true;
                            $timeout(async () => {
                                const res = await perusteetApi.customGET("haku", {
                                    nimi,
                                    sivu: sivu - 1,
                                    sivukoko: 10,
                                    kieli: KieliService.getSisaltokieli()
                                });
                                $scope.ladataan = false;
                                const { data, ...params } = res;
                                $scope.perusteet = data;
                                $scope.sivuja = params.sivuja;
                                $scope.total = params.kokonaismäärä;
                            });
                        };
                        $scope.update();
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
                    $scope.perusteet = null;
                    $scope.update();
                };
            }
        }).result;
}

angular.module("app").run(ModalAdd.init);
