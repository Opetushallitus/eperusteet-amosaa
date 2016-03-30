angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat.poistetut", {
    url: "/poistetut",
    resolve: {
        poistetut: (ops) => ops.all("poistetut").getList(),
    },
    views: {
        "": {
            controller: ($scope, poistetut) => {
                $scope.vaihdaJarjestys = Taulukot.bindSivutus($scope, "poistoAika", poistetut);
                $scope.suodata = (item) => KaannaService.hae(item.nimi, $scope.search);
            }
        }
    }
}));
