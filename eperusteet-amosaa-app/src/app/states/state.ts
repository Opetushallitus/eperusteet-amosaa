angular.module("app")
.config($stateProvider => $stateProvider
.state("root", {
    url: "/:lang",
    resolve: {
        // kayttaja: Api => Api.one("kayttaja"),
        // kayttajaKoulutustoimijat: kayttaja => kayttaja.one("koulutustoimijat").get(),
        // casMe: () => Kayttaja.casMe(),
        // casRoles: () => Kayttaja.casRoles(),
        kayttajaprofiili: () => Fake.Kayttajaprofiili(1)
    },
    views: {
        "": {
            // controller: ($scope, $state, kayttajaKoulutustoimijat) => {
            controller: ($scope, $state) => {
                // FIXME Find generic solution
                // const redirect = () => $state.go("root.koulutustoimija.detail", { ktId: kayttajaKoulutustoimijat[0].koulutustoimija });
                const redirect = () => $state.go("root.koulutustoimija.detail", { ktId: 1 });
                $scope.$on("$stateChangeSuccess", (_, state) => {
                    if (state.name === "root") {
                        redirect();
                    }
                });
            },
        },
        notifikaatiot: {
            templateUrl: "components/notifikaatiot/notifikaatiot.jade",
            controller: "NotifikaatioController"
        },
        ylanavi: {
            controller: ($scope, $rootScope, $state, $templateCache) => {
                $scope.langs = KieliService.getSisaltokielet();
                $scope.$on("help:updated", (_, helpUrl) => $scope.helpUrl = helpUrl);
                OhjeService.updateHelp($state.current.name);
            }
        },
        footer: {
            controller: ($scope) => { }
        }
    }
}));
