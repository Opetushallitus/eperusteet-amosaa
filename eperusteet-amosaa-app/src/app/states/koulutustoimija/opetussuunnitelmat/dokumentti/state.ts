angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat.dokumentti", {
    url: "/dokumentti",
    resolve: {
        dokumentti: (ops) => ops.one("dokumentti", "tila").get(),
    },
    views: {
        "": {
            controller: ($scope, dokumentti) => {
                $scope.dokumentti = dokumentti;
            }
        }
    }
}));
