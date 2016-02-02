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
                $scope.help = {
                    title: KaannaService.kaanna("ohje"),
                    template: ""
                };

                const updateHelp = _.callAndGive((state: string) => {
                    const templateUrl = ("misc/guidance/" + state + ".jade")
                                .replace(".detail", "");
                    $scope.help.template = $templateCache.get(templateUrl)
                        ? templateUrl
                        : "";
                }, $state.current.name);

                $rootScope.$on("$stateChangeSuccess", (_, state) =>
                        updateHelp(state.name));
            }
        },
        footer: {
            controller: ($scope) => {
            }
        }
    }
}));
