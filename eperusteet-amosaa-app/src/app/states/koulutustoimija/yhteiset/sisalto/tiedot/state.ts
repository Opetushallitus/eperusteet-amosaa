angular.module("app")
    .config($stateProvider => $stateProvider
        .state("root.koulutustoimija.yhteiset.sisalto.tiedot", {
            url: "/tiedot",
            resolve: {},
            views: {
                "": {
                    controller: ($scope) => {}
                }
            }
        }));
