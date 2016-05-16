namespace KoodistoModal {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    export const koodi = (koodisto, valitut = []) => i.$uibModal.open({
            resolve: { },
            templateUrl: "modals/koodisto/koodisto.jade",
            controller: ($scope, $state, $uibModalInstance) => {
                $scope.koodisto = _.groupBy(koodisto, (koodi) => _.first(koodi.uri.split("_")));
                $scope.valitut = _.zipObject(valitut, _.map(valitut, _.constant(true)));
                $scope.ok = () => $uibModalInstance.close(_($scope.valitut)
                    .keys()
                    .filter(valittu => $scope.valitut[valittu])
                    .value());
                $scope.peruuta = $uibModalInstance.dismiss;
            }
        }).result;
};


angular.module("app")
.run(KoodistoModal.init);
