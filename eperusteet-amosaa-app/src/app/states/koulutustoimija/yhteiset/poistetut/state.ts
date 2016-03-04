angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.poistetut", {
    url: "/poistetut",
    resolve: {
        poistetut: (yhteiset) => yhteiset.all("poistetut").getList(),
    },
    views: {
        "": {
            controller: ($scope, poistetut, yhteiset) => {
                $scope.vaihdaJarjestys = Taulukot.bindSivutus($scope, "poistoAika", poistetut);
            }
        }
    }
}));
