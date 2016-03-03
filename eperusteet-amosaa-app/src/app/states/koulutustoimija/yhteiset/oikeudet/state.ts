angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.oikeudet", {
    url: "/oikeudet",
    resolve: {
        // poistetut: (yhteiset) => yhteiset.all("poistetut").getList(),
    },
    views: {
        "": {
            controller: ($scope, yhteiset) => {
            }
        }
    }
}));
