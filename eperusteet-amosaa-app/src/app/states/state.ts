angular.module("app")
.config($stateProvider => $stateProvider
.state("root", {
    url: "/:lang",
    resolve: {
        kayttajanKoulutustoimijat: Api => Api.all("kayttaja/koulutustoimijat").getList(),
        kayttaja: Api => Api.one("kayttaja").get(),
        casMe: () => Kayttaja.casMe(),
        casRoles: () => Kayttaja.casRoles(),
        kayttajaprofiili: () => Fake.Kayttajaprofiili(1)
    },
    views: {
        "": {
            controller: ($scope, $state, $stateParams, kayttajanKoulutustoimijat) => {
                if (_.isEmpty(kayttajanKoulutustoimijat)) {
                    console.log("Ei koulutustoimijoita");
                }
                else if ($state.current.name === "root") {
                    // TODO: Valitse preferattu koulutustoimija
                    $state.go("root.koulutustoimija.detail", {
                        ktId: kayttajanKoulutustoimijat[0].id
                    });
                }
            },
        },
        notifikaatiot: {
            templateUrl: "components/notifikaatiot/notifikaatiot.jade",
            controller: "NotifikaatioController"
        },
        ylanavi: {
            controller: ($scope, $state) => {
                $scope.langs = KieliService.getSisaltokielet();
                $scope.$on("help:updated", (x, helpUrl) => $scope.helpUrl = helpUrl);
                $scope.$on("$stateChangeStart", (x, helpUrl) => $scope.helpUrl = undefined);
                OhjeService.updateHelp($state.current.name);
            }
        },
        footer: {
            controller: ($scope) => { }
        }
    }
}));
