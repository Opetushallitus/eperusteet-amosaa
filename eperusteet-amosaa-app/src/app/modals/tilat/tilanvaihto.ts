namespace TilanvaihtoModal {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    export const sallitutTilat = () => ({
        luonnos: ["valmis", "poistettu"],
        valmis: ["luonnos", "julkaistu", "poistettu"],
        poistettu: ["luonnos"],
        julkaistu: ["luonnos"]
    });

    export const pohja = (ops) => i.$uibModal.open({
            resolve: { },
            templateUrl: "modals/tilat/tilanvaihto.jade",
            controller: ($scope, $state, $uibModalInstance) => {
                $scope.ok = $uibModalInstance.close;
                $scope.peruuta = $uibModalInstance.dismiss;
                $scope.sallitutTilat();
                $scope.ops = ops;
            }
        }).result;
}

angular.module("app")
.run(TilanvaihtoModal.init);
