angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.poistetut", {
    url: "/poistetut",
    resolve: {},
    views: {
        "": {
            controller: ($scope) => {}
        }
    }
}));
