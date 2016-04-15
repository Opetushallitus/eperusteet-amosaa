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
            controller: ($scope, $state, $uibModalInstance, pohjat) => {
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
            }
        }).result;

    export const pohja = () => i.$uibModal.open({
            resolve: { },
            templateUrl: "modals/add/pohja.jade",
            controller: ($scope, $state, $uibModalInstance) => {
                $scope.ok = $uibModalInstance.close;
                $scope.peruuta = $uibModalInstance.dismiss;
                $scope.pohja = {
                    tyyppi: "pohja"
                };
            }
        }).result;

    export const opetussuunnitelma = () => i.$uibModal.open({
            resolve: {
                perusteet: Eperusteet => Eperusteet.one("perusteet").get()
            },
            templateUrl: "modals/add/opetussuunnitelma.jade",
            controller: ($scope, $state, $uibModalInstance, perusteet) => {
                $scope.perusteet = filterPerusteet(perusteet.data);
                $scope.peruste = undefined;
                $scope.ops = {
                    tyyppi: "ops",
                };
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
                    $scope.ops.perusteDiaarinumero = peruste.diaarinumero;
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
                        tekstiKappale: {
                            nimi: {}
                        }
                    };
                };
                $scope.ok = $uibModalInstance.close;
            }
        }).result;
};

angular.module("app")
.run(ModalAdd.init);
