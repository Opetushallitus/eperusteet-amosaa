angular.module("app")
    .config($stateProvider => $stateProvider
        .state("root.koulutustoimija.yhteiset.sisalto.tekstikappale", {
            url: "/tekstikappale",
            resolve: {},
            views: {
                "": {
                    controller: ($scope) => {}
                }
            }
        }));
