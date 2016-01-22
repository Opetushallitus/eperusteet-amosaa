angular.module("app")
.config($stateProvider => $stateProvider
.state("root.routelist", {
    url: "/routelist",
    resolve: {},
    controller: ($scope, $state) => {
        $scope.params = _($state.get())
            .map((state) => state.url.split("/"))
            .compact()
            .flatten(true)
            .filter((param) => _.size(param) > 1 && param[0] === ":")
            .map((param) => {
                return [param.slice(1), undefined];
            })
            .fromPairs()
            .value();

        $scope.states = $state.get();
        $scope.urlify = (state) => $state.href(state, $scope.params);
    }
}));
