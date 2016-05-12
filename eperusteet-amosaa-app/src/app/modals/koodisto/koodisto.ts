namespace KoodistoModal {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    export const oppiaine = (peruste) => i.$uibModal.open({
            resolve: { },
            templateUrl: "modals/koodisto/oppiaine.jade",
            controller: ($scope, $state, $uibModalInstance) => {
                console.log(peruste);
                $scope.ok = $uibModalInstance.close;
                $scope.peruuta = $uibModalInstance.dismiss;
            }
        }).result;

    export const osaamisala = (koodit) => i.$uibModal.open({
            resolve: { },
            templateUrl: "modals/koodisto/osaamisala.jade",
            controller: ($scope, $state, $uibModalInstance) => {
                $scope.koodit = _.cloneDeep(koodit);
                $scope.ok = () => $uibModalInstance.close(_.filter($scope.koodit, (koodi: any) => koodi.$$valittu));
                $scope.peruuta = $uibModalInstance.dismiss;
            }
        }).result;
};


angular.module("app")
.run(KoodistoModal.init);
