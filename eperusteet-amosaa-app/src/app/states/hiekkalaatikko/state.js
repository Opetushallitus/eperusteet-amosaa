angular.module("app")
    .config(function ($stateProvider) { return $stateProvider.state("root.sandbox", {
    url: "/sandbox",
    templateUrl: "states/view.jade",
    resolve: {},
    controller: function ($scope, kayttaja) {
    }
}); });
//# sourceMappingURL=state.js.map