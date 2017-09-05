angular.module("app").config($stateProvider =>
    $stateProvider.state("root.oph", {
        url: "/oph",
        resolve: {},
        views: {
            "": {
                controller: $scope => {}
            },
            pohjat: {
                controller: $scope => {}
            },
            tiedotteet: {
                controller: $scope => {}
            },
            tilastot: {
                controller: $scope => {}
            }
        }
    })
);
