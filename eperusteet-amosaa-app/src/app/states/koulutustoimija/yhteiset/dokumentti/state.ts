angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.dokumentti", {
    url: "/dokumentti",
    resolve: {
        dokumentti: (yhteiset) => yhteiset.one("dokumentti").get(),
    },
    views: {
        "": {
            controller: () => {

            }
        }
    }
}));
