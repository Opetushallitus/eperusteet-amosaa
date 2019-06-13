namespace ModalAdd {
    let i;
    export const init = $injector => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q", "$timeout"]);
    };

    const filterPerusteet = (perusteet = [], query = "") =>
        _(perusteet)
            .filter(peruste => KaannaService.hae(peruste.nimi, query))
            .value();

    export const yhteinen = (yhteiset) =>
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
                $scope.valittuTyyppi = null;
                $scope.yhteiset = _(yhteiset)
                    .filter(ops => ops.tila === 'julkaistu')
                    .value();

                $scope.valitseTyyppi = (tyyppi) => {
                    $scope.valittuTyyppi = tyyppi;
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
                opetussuunnitelmatApi: Api => Api.one("koulutustoimijat", koulutustoimija).one("opetussuunnitelmat")
            },
            templateUrl: "modals/add/opetussuunnitelma.jade",
            controller: ($timeout, $scope, $state, $stateParams, $uibModalInstance, perusteetApi, opetussuunnitelmatApi) => {
                $scope.nimi = "";
                $scope.sivu = 1;
                $scope.sivuja = 1;
                $scope.total = 0;
                $scope.sivukoko = 10;
                $scope.perusteet = null;
                $scope.ladataan = true;
                $scope.valittuTyyppi = null;
                $scope.valitseTyyppi = (tyyppi) => {
                    $scope.valittuTyyppi = tyyppi;
                };

                $scope.tilat = _.cloneDeep(Constants.tosTilat);
                $scope.pagination = {
                    sivu: 1,
                    kokonaismaara: 0
                };
                $scope.rajain =  {
                    nimi: "",
                    tila: _.cloneDeep(Constants.tosTilat)
                };

                $scope.paivitaRajaus = () => {
                    opetussuunnitelmatApi.customGET("", {
                        sivu: $scope.pagination.sivu - 1,
                        sivukoko: $scope.pagination.sivukoko,
                        tila: $scope.rajain.tila,
                        nimi: $scope.rajain.nimi,
                        tyyppi: _(_.cloneDeep(Constants.tosTyypit))
                            .filter(tila => tila === "ops")
                            .value()
                    }).then(opetussuunnitelmat => {
                        $scope.opetussuunnitelmat = opetussuunnitelmat.data;
                        $scope.pagination.sivukoko = opetussuunnitelmat.sivukoko;
                        $scope.pagination.kokonaismaara = opetussuunnitelmat.kokonaismäärä;
                    });
                };
                $scope.paivitaRajaus();

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
                perusteetApi: Api => Api.one("perusteet"),
                opetussuunnitelmatApi: () => koulutustoimija.one("opetussuunnitelmat")
            },
            templateUrl: "modals/add/sisalto.jade",
            controller: ($uibModalInstance, $scope, $stateParams, $timeout,
                         Api, perusteetApi, opetussuunnitelmatApi) => {
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
                $scope.pagination = {
                    sivu: 1,
                    kokonaismaara: 0
                };

                {
                    // Opetussuunnitelmat
                    $scope.valitseOps = ops => {
                        $scope.currentStage = "opssisalto";
                        opetussuunnitelmatApi
                            .one(ops.id + "/otsikot")
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

                    $scope.nimi = "";
                    $scope.sivu = 1;
                    $scope.sivuja = 1;
                    $scope.total = 0;
                    $scope.opsit = null;
                    $scope.ladataan = true;
                    $scope.sivukoko = 10;

                    $scope.tuoSisaltoa = async () => {
                        $scope.currentStage = "opetussuunnitelma";

                        $scope.update = function(nimi = "", sivu = 1) {
                            $scope.ladataan = true;
                            $timeout(async () => {
                                const res = await opetussuunnitelmatApi.customGET("", {
                                    nimi,
                                    sivu: sivu - 1,
                                    sivukoko: $scope.sivukoko,
                                    kieli: KieliService.getSisaltokieli(),
                                    tila: ["luonnos", "valmis", "julkaistu"]
                                });
                                $scope.ladataan = false;
                                const { data, ...params } = res;
                                $scope.opsit = data;
                                $scope.sivuja = params.sivuja;
                                $scope.total = params.kokonaismäärä;
                            });
                        };
                        $scope.update();
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
