angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.kasitteet", {
    url: "/kasitteet",
    resolve: {
        kasitteet: (koulutustoimija) => Fake.Poistetut(koulutustoimija.id),
},
    views: {
        "": {
            controller: ($scope, kasitteet) => {
                //Todo
            }
        }
    }
}));