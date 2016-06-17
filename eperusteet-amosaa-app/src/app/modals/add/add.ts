namespace ModalAdd {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    const filterPerusteet = (perusteet = [], query = "") => _(perusteet)
        .filter((peruste) => KaannaService.hae(peruste.nimi, query))
        .value();

    export const yhteinen = () => i.$uibModal.open({
            resolve: {
                pohjat: Api => Api.all("opetussuunnitelmat").all("pohjat").getList()
            },
            templateUrl: "modals/add/yhteinen.jade",
            controller: ($scope, $state, $stateParams, $uibModalInstance, pohjat) => {
                $scope.pohjat = pohjat;
                $scope.ok = $uibModalInstance.close;
                $scope.peruuta = $uibModalInstance.dismiss;
                $scope.yhteinen = {
                    tyyppi: "yhteinen"
                };

                $scope.valitsePohja = (pohja) => {
                    $scope.valittuPohja = pohja;
                    $scope.yhteinen._pohja = "" + pohja.id;
                };

                if (_.size($scope.pohjat) === 1) {
                    $scope.valitsePohja(_.first($scope.pohjat));
                }

                // Kielivalitsin
                $scope.langs = KieliService.getSisaltokielet();
                $scope.currentLang = $stateParams.lang;
                $scope.selectLang = (lang) => {
                    $scope.currentLang = lang;
                    KieliService.setSisaltokieli(lang);
                };
            }
        }).result;

    export const yleinen = () => i.$uibModal.open({
            resolve: { },
            templateUrl: "modals/add/yleinen.jade",
            controller: ($scope, $state, $stateParams, $uibModalInstance) => {
                $scope.ok = $uibModalInstance.close;
                $scope.peruuta = $uibModalInstance.dismiss;
                $scope.ops = { tyyppi: "yleinen" };
                // Kielivalitsin
                $scope.langs = KieliService.getSisaltokielet();
                $scope.currentLang = $stateParams.lang;
                $scope.selectLang = (lang) => {
                    $scope.currentLang = lang;
                    KieliService.setSisaltokieli(lang);
                };
            }
        }).result;

    export const pohja = () => i.$uibModal.open({
            resolve: { },
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
                $scope.selectLang = (lang) => {
                    $scope.currentLang = lang;
                    KieliService.setSisaltokieli(lang);
                };
            }
        }).result;

    export const opetussuunnitelma = () => i.$uibModal.open({
            resolve: {
                perusteet: Eperusteet => Eperusteet.one("perusteet").get({
                    sivukoko: 9999,
                    koulutustyyppi: "koulutustyyppi_1"
                }) // FIXME paginointihärveli
            },
            templateUrl: "modals/add/opetussuunnitelma.jade",
            controller: ($scope, $state, $stateParams, $uibModalInstance, perusteet) => {
                $scope.perusteet = filterPerusteet(perusteet.data);
                $scope.peruste = undefined;
                $scope.ok = $uibModalInstance.close;
                $scope.peruuta = $uibModalInstance.dismiss;

                $scope.update = (input) => {
                    if (!_.isEmpty(input)) {
                        $scope.peruste = undefined;
                    }
                    $scope.perusteet = filterPerusteet(perusteet.data, input);
                };

                $scope.valitsePeruste = (peruste) => {
                    $scope.input = "";
                    $scope.peruste = peruste;
                    $scope.stMap = _.indexBy($scope.peruste.suoritustavat, "suoritustapakoodi");
                    const opsSt = $scope.stMap["ops"];
                    $scope.ops = {
                        tyyppi: "ops",
                        perusteDiaarinumero: peruste.diaarinumero,
                        suoritustapa: (opsSt ? "ops" : $scope.peruste.suoritustavat[0].suoritustapakoodi)
                    };
                };

                // Kielivalitsin
                $scope.langs = KieliService.getSisaltokielet();
                $scope.currentLang = $stateParams.lang;
                $scope.selectLang = (lang) => {
                    $scope.currentLang = lang;
                    KieliService.setSisaltokieli(lang);
                };
            }
        }).result;

    export const sisaltoAdder = (sallitut = ["tekstikappale"]) => i.$uibModal.open({
            resolve: { },
            templateUrl: "modals/add/sisalto.jade",
            controller: ($uibModalInstance, $scope, $stateParams) => {
                $scope.sallitut = sallitut;
                $scope.valittu = undefined;

                $scope.valitse = (tyyppi) => {
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
                $scope.selectLang = (lang) => {
                    $scope.currentLang = lang;
                    KieliService.setSisaltokieli(lang);
                };
            }
        }).result;
}

angular.module("app")
.run(ModalAdd.init);
