angular.module("app")
    .config(function ($stateProvider) { return $stateProvider
    .state("root", {
    url: "/:lang",
    resolve: {
        kayttajaprofiili: function () { return Fake.Kayttajaprofiili(1); }
    },
    views: {
        "": {
            resolve: {},
            controller: function ($scope) {
                $scope.data = "Main controller data";
            }
        },
        "ylanavi": {
            controller: function ($scope) {
                $scope.data = "ylanavi";
            }
        },
        "footer": {
            controller: function ($scope) {
                $scope.data = "footer";
            }
        }
    }
}); });
//# sourceMappingURL=state.js.map