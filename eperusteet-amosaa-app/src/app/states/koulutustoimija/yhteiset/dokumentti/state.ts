angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.dokumentti", {
    url: "/dokumentti",
    resolve: {
    },
    views: {
        "": {
            controller: () => {

            }
        }
    }
}));
