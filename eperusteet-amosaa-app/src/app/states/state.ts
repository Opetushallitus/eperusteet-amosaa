namespace Murupolku {
    const svars = {};

    export const register = (sname, name) => svars[sname] = name;
    export const get = (sname) => _.clone(svars[sname]);
};


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
            controller: ($scope, $state, $interpolate) => {
                $scope.langs = KieliService.getSisaltokielet();
                $scope.$on("help:updated", (event, helpUrl) => $scope.helpUrl = helpUrl);
                $scope.$on("$stateChangeStart", (event, helpUrl) => $scope.helpUrl = undefined);
                $scope.$on("$stateChangeSuccess", (event, toState, toParams, fromState, fromParams) => {
                    const path = toState.name.split(".");
                    $scope.muruPath = _(_.rest(path)
                        .reduce((acc: Array<string>, next: string) =>
                                _.append(acc, _.last(acc) + "." + next), ["root"]))
                        .reject(state => _.endsWith(state, ".detail"))
                        .map($state.get)
                        .map((sconfig: any) => ({
                            name: Murupolku.get(sconfig.name) || "muru-" + sconfig.name,
                            state: sconfig.name + (sconfig.abstract ? ".detail" : "")
                        }))
                        .value()
                        .slice(1);
                });
                OhjeService.updateHelp($state.current.name);
            }
        },
        footer: {
            controller: ($scope) => { }
        }
    }
}));
