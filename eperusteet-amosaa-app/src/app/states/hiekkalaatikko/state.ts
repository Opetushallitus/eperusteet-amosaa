angular.module("app")
.config($stateProvider => $stateProvider.state("root.sandbox", {
    url: "/sandbox",
    templateUrl: "states/view.jade",
    resolve: {
    },
    controller: ($scope, kayttaja) => {
    }
}));
