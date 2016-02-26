angular.module("app")
    .config($stateProvider => $stateProvider
        .state("root.koulutustoimija.yhteiset.sisalto.tekstikappale", {
            url: "/tekstikappale/:tkvId",
            resolve: {
                tekstikappale: (yhteiset, $stateParams) => yhteiset.one("tekstit", $stateParams.tkvId).get()
            },
            views: {
                "": {
                    controller: ($scope, tekstikappale) => {
                        $scope.edit = EditointikontrollitService.createRestangular($scope, "tkv", tekstikappale);
                    }
                }
            }
        }));
