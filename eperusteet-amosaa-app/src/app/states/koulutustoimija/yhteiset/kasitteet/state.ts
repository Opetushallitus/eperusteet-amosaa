angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.kasitteet", {
    url: "/poistetut",
    resolve: {},
    views: {
        "": {
            controller: ($scope) => {}
        }
    }
}));
