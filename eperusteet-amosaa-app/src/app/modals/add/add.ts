namespace ModalAdd {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    const filterPerusteet = (perusteet = [], query = "") => _(perusteet)
        .filter((peruste) => KaannaService.hae(peruste.nimi, query))
        .value();


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

};

angular.module("app")
.run(ModalAdd.init);
