namespace TilanvaihtoModal {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    export const sallitutTilat = () => ({
        luonnos: ["valmis", "poistettu"],
        valmis: ["luonnos", "julkaistu", "poistettu"],
        poistettu: ["luonnos"],
        julkaistu: ["luonnos", "poistettu"]
    });

    export const vaihdaTila = (ops) => i.$uibModal.open({
            resolve: { },
            templateUrl: "modals/tilat/tilanvaihto.jade",
            controller: ($scope, $state, $uibModalInstance) => {
                $scope.ok = $uibModalInstance.close;
                $scope.peruuta = $uibModalInstance.dismiss;
                $scope.sallitutTilat = sallitutTilat();
                $scope.ops = ops;
                $scope.valitseTila = (tila) => {
                    ops.customPOST(null, "tila/" + tila)
                        .then($uibModalInstance.close);
                };
            }
        }).result;
}

angular.module("app")
.run(TilanvaihtoModal.init);
