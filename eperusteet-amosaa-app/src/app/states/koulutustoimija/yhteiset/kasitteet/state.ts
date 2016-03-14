angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.kasitteet", {
    url: "/kasitteet",
    ncyBreadcrumb: {
        label: "{{'kasitteet' | kaanna}}"
    },
    resolve: {},
    views: {
        "": {
            controller: ($scope) => {}
        }
    }
}));
