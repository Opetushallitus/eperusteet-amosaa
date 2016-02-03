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
            controller: ($scope) => {
                $scope.data = "Main controller data";
            },
        },
        notifikaatiot: {
            templateUrl: "components/notifikaatiot/notifikaatiot.jade",
            controller: "NotifikaatioController"
        },
        ylanavi: {
            controller: ($scope, $rootScope, $state, $templateCache) => {
                $scope.langs = KieliService.getSisaltokielet();
                $scope.$on('help:updated', (_, helpUrl) => $scope.helpUrl = helpUrl);
                OhjeService.updateHelp($state.current.name);
            }
        },
        footer: {
            controller: ($scope) => { }
        }
    }
}));
