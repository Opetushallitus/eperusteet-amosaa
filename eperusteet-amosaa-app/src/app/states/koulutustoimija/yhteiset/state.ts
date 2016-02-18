angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset", {
    url: "/yhteiset/:yhteisetId",
    resolve: {
        yhteiset: (koulutustoimija, yhteiset) => koulutustoimija.one("yhteiset", yhteiset.id).get(),
    },
    views: {
        "": {
            controller: ($scope, yhteiset) => {
                $scope.yhteiset = yhteiset;
            }
        }
    }
}));
