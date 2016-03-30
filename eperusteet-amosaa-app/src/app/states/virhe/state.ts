angular.module("app")
    .config($stateProvider => $stateProvider
        .state("root.virhe", {
            url: "/virhe",
            params: {
                tyyppi: null
            },
            controller: ($scope, $stateParams) => {
                $scope.tyyppi = $stateParams.tyyppi;
            }
        }));
