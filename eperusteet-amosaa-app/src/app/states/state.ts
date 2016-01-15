angular.module("app")
.config($stateProvider => $stateProvider
.state("root", {
    url: "/:lang",
    resolve: {
        kayttajaprofiili: () => Fake.Kayttajaprofiili(1)
    },
    views: {
        "": {
            resolve: {
                // perusteet: eperusteet => eperusteet.one("perusteet").get()
            },
            controller: ($scope) => {
                $scope.data = "Main controller data";
            },

        },
        "ylanavi": {
            controller: ($scope) => {
                $scope.data = "ylanavi";
            }
        },
        "footer": {
            controller: ($scope) => {
                $scope.data = "footer";
            }
        }
    }
}));
