angular.module("app").config($stateProvider =>
    $stateProvider.state("root.routelist", {
        url: "/routelist",
        resolve: {},
        controller: ($scope, $state) => {
            /*$scope.params = _($state.get())
            .map(state => state.url.split("/"))
            .compact()
            .flatten(true)
            .filter(param => _.size(param) > 1 && param[0] === ":")
            .map(param => [param.slice(1), "1"])
            .fromPairs()
            .value();*/

            $scope.params.lang = "fi";

            const helpTemplates = OhjeService.getHelpTemplates();

            $scope.states = _($state.get())
                .each(state => {
                    state.$$depth = _.size(state.name.split("."));
                    state.$$helpUrl = helpTemplates[state.name];
                })
                .value();

            $scope.urlify = state => $state.href(state, $scope.params);
        }
    })
);
