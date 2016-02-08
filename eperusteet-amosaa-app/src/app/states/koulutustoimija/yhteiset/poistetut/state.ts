angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.poistetut", {
    url: "/poistetut",
    resolve: {
        poistetut: (koulutustoimija) => Fake.Poistetut(koulutustoimija.id),
    },
    views: {
        "": {
            controller: ($scope, poistetut) => {
                $scope.vaihdaJarjestys = TaulukkoService.bindSivutus($scope, "poistoAika", poistetut);
            }
        }
    }
}));
