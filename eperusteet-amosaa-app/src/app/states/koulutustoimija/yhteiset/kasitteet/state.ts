angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.kasitteet", {
    url: "/kasitteet",
    resolve: {},
    views: {
        "": {
            controller: ($scope) => {}
        }
    }
}));
