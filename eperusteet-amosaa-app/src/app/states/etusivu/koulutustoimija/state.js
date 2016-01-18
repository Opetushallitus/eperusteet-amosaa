angular.module("app")
    .config(function ($stateProvider) { return $stateProvider
    .state("@StateName", {
    url: "/koulutustoimija/:ktId",
    resolve: {},
    controller: function ($scope) {
        $scope.world = 5;
    }
}); });
//# sourceMappingURL=state.js.map