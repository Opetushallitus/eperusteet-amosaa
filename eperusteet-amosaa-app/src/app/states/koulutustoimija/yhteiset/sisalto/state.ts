angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.sisalto", {
    url: "/sisalto",
    resolve: {},
    views: {
        "": {
            controller: ($scope) => {}
        },
        "sivunavi": {

        }
    }
}));
