namespace Murupolku {
    const svars = {};

    export const register = (sname, name) => svars[sname] = name;
    export const get = (sname) => _.clone(svars[sname]);
}


angular.module("app")
.config($stateProvider => $stateProvider
.state("root", {
    url: "/:lang",
    resolve: {
        kkAlustus: Api => Api.one("kayttaja/koulutustoimijat").customPOST({}),
        kayttajanKoulutustoimijat: (kkAlustus, Api) => Api.all("kayttaja/koulutustoimijat").getList(),
        kayttaja: (kkAlustus, Api) => Api.one("kayttaja").get(),
        kayttajanTiedot: (kayttaja) => kayttaja.one("tiedot").get(),
        oikeudet: (kkAlustus, Api) => Api.all("kayttaja/oikeudet").getList(),
        orgoikeudet: (kkAlustus, Api) => Api.one("kayttaja/organisaatiooikeudet").get(),
        koodisto: (Api) => Api.one("koodisto")
    },
    views: {
        "": {
            controller: ($scope, $state, $stateParams, kayttajanKoulutustoimijat, oikeudet, orgoikeudet) => {
                Oikeudet.init(oikeudet, orgoikeudet);
                if (_.isEmpty(kayttajanKoulutustoimijat)) {
                    $state.go("root.virhe", { tyyppi: "ei-koulutustoimijoita" });
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
            controller: ($rootScope, $scope, $state, $stateParams) => {
                $scope.langs = KieliService.getSisaltokielet();
                $scope.currentLang = $stateParams.lang;
                $scope.selectLang = (lang) => {
                    $scope.currentLang = lang;
                    KieliService.setSisaltokieli(lang);
                };

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
