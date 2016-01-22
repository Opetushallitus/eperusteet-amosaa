angular.module("app")
.config($stateProvider => $stateProvider
.state("root.routelist", {
    url: "/routelist",
    resolve: {},
    controller: ($scope, $state) => {
        $scope.params = _($state.get())
            .map((state) => {
                return state.url.match(/:.*/g);
            })
            .compact()
            .flatten()
            .map((param) => {
                return [param.slice(1), undefined];
            })
            .fromPairs()
            .value();

        $scope.states = $state.get();
        $scope.urlify = (state) => $state.href(state, $scope.params);
    }
}));
