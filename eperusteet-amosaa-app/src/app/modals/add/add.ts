namespace ModalAdd {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    }; const filterPerusteet = (perusteet = [], query = "") => _(perusteet)
        .filter((peruste) => KaannaService.hae(peruste.nimi, query))
        .value();

    export const kayttaja = (koulutustoimijat) => i.$uibModal.open({
        resolve: {
        },
        templateUrl: "modals/add/kayttaja.jade",
        controller: ($uibModalInstance, $scope, $state, kayttajat) => {
            $scope.perusteet = filterPerusteet(perusteet.data);
            $scope.peruste = undefined;
            $scope.ops = {};
            $scope.ok = $uibModalInstance.close;

            $scope.update = (input) => {
                if (!_.isEmpty(input)) {
                    $scope.peruste = undefined;
                }
                $scope.perusteet = filterPerusteet(perusteet.data, input);
            };

            $scope.valitsePeruste = (peruste) => {
                $scope.input = "";
                $scope.peruste = peruste;
            }
        }
    }).result;

    export const opetussuunnitelma = () => i.$uibModal.open({
        resolve: {
            perusteet: Eperusteet => Eperusteet.one("perusteet").get()
        },
        templateUrl: "modals/add/opetussuunnitelma.jade",
        controller: ($uibModalInstance, $scope, $state, perusteet) => {
            $scope.perusteet = filterPerusteet(perusteet.data);
            $scope.peruste = undefined;
            $scope.ops = {};
            $scope.ok = $uibModalInstance.close;

            $scope.update = (input) => {
                if (!_.isEmpty(input)) {
                    $scope.peruste = undefined;
                }
                $scope.perusteet = filterPerusteet(perusteet.data, input);
            };

            $scope.valitsePeruste = (peruste) => {
                $scope.input = "";
                $scope.peruste = peruste;
            }
        }
    }).result;

    export const sisaltoAdder = (sallitut = ["tekstikappale"]) => i.$uibModal.open({
            resolve: { },
            templateUrl: "modals/add/sisalto.jade",
            controller: ($uibModalInstance, $scope, $state) => {
                $scope.sallitut = sallitut;
                $scope.valittu = undefined;

                $scope.valitse = (tyyppi) => {
                    $scope.obj = {
                        tyyppi: tyyppi,
                        data: {
                            tekstiKappale: {
                                nimi: {}
                            },
                        }
                    };
                };
                $scope.ok = $uibModalInstance.close;
            }
        }).result;

    export const kasiteAdder = (sallitut = ["kasite"]) => i.$uibModal.open({
        resolve: { },
        templateUrl: "modals/add/kasite.jade",
        controller: ($uibModalInstance, $scope, $state) => {
            $scope.sallitut = sallitut;
            $scope.valittu = undefined;
            //Todo
            $scope.valitse = (tyyppi) => {
                $scope.obj = {
                    tyyppi: tyyppi,
                    data: {
                        kasite: {
                            otsikko: {}
                        },
                    }
                };
            };
            $scope.ok = $uibModalInstance.close;
        }
    }).result;
};

angular.module("app")
.run(ModalAdd.init);
