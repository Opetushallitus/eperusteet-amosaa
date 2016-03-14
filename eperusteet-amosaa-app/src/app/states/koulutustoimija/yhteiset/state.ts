angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset", {
    url: "/yhteiset/:yhteisetId",
    ncyBreadcrumb: {
        label: '{{"koulutustoimija" | kaanna}}'
    },
    resolve: {
        yhteiset: (koulutustoimija, yhteiset) => koulutustoimija.one("yhteiset", yhteiset.id).get(),
        tekstit: yhteiset => yhteiset.one("tekstit", yhteiset._tekstit),
        otsikot: yhteiset => yhteiset.one("tekstit", "otsikot").get(),
    },
    views: {
        "": {
            controller: ($scope, yhteiset) => {
                $scope.yhteiset = yhteiset;
            }
        }
    }
}));
