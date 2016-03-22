angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.dokumentti", {
    url: "/dokumentti",
    resolve: {
        dokumentti: (yhteiset) => yhteiset.one("dokumentti", "tila").get(),
    },
    views: {
        "": {
            controller: ($scope, dokumentti) => {
                $scope.dokumentti = dokumentti;
            }
        }
    }
}));
