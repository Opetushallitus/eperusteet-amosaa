angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.detail.tiedot", {
    url: "/tiedot",
    resolve: {
    },
    controller: ($scope, koulutustoimija) => {
        $scope.kaverit = [{
            nimi: { fi: "Kokkolan ammattikoulu" },
            tila: "yhteistyo"
        }, {
            nimi: { fi: "Hervannan ammattioppilaitos" },
            tila: "odotetaan"
        }, {
            nimi: { fi: "Hierontasalonki Yi" },
            tila: "odottaa"
        }];
    }
}));
