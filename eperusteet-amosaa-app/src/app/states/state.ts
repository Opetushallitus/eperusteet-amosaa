angular.module("app")
.config($stateProvider => $stateProvider
.state("root", {
    url: "/:lang",
    resolve: {
        // kayttaja: () => Kayttaja.kayttaja(),
        // casMe: () => Kayttaja.casMe(),
        // casRoles: () => Kayttaja.casRoles(),
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
        "notifikaatiot": {
            templateUrl: "components/notifikaatiot/notifikaatiot.jade",
            controller: "NotifikaatioController"
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
