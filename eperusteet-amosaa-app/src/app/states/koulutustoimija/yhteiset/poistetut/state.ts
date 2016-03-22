angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.poistetut", {
    url: "/poistetut",
    resolve: {
        poistetut: (yhteiset) => yhteiset.all("poistetut").getList(),
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
