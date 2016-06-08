namespace KoodistoModal {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    export const koodi = (koodisto, valitut = []) => i.$uibModal.open({
            resolve: { },
            templateUrl: "modals/koodisto/koodisto.jade",
            controller: ($scope, $state, $uibModalInstance) => {
                $scope.koodisto = _(koodisto)
                    .groupBy((koodi: any) => _.first(koodi.uri.split("_")))
                    .mapValues((arr: Array<any>) => _.sortBy(arr, "uri"))
                    .value();

                $scope.valitut = _.zipObject(valitut, _.map(valitut, _.constant(true)));
                $scope.search = "";
                $scope.piilotetut = {};

                $scope.suodata = (search) => {
                    if (search.length === 0) {
                        $scope.piilotetut = {};
                    }
                    else {
                        _.each(koodisto, item => {
                            $scope.piilotetut[item.uri] = !Algoritmit.match(search, item.nimi) && !Algoritmit.match(search, item.arvo);
                        });
                    }
                };

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
