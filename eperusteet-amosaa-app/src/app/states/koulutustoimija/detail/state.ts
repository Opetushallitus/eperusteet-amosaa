angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.detail", {
    url: "",
    resolve: {
        tiedotteet: (koulutustoimija) => koulutustoimija.all("tiedotteet").getList(),
        ohjeistus: (Api) => Api.all("ohjeistus"),
        tilastot: (Api) => Api.one("tilastot"),
        opsSaver: ($state, opetussuunnitelmat) => (uusiOps) => uusiOps && opetussuunnitelmat
            .post(uusiOps)
            .then((res) => $state.go("root.koulutustoimija.opetussuunnitelmat.sisalto.tiedot", {
                opsId: res.id
            }, {
                reload: true // Reloads koulutustoimijainfo and user permissions
            })),
    },
    views: {
        "": {
            controller: ($scope, kayttajanKoulutustoimijat, koulutustoimija) => {
                $scope.isOph = Koulutustoimijat.isOpetushallitus(koulutustoimija);
                $scope.koulutustoimijat = kayttajanKoulutustoimijat;
                $scope.koulutustoimija = koulutustoimija;
                $scope.checkOph = Koulutustoimijat.isOpetushallitus;
                $scope.suodataOpetussuunnitelmat = (opsit, search) =>
                    _.each(opsit, (ops) => {
                        ops.$$hidden = !_.isEmpty(search) && !Algoritmit.match(search, ops.nimi) });
            }
        },
        tilastot: {
            controller: ($scope, tilastot) => {
                tilastot.get()
                    .then((res) => {
                        $scope.tilastot = {
                            type: "bar",
                            data: {
                                labels: _.map(["tilastot-kayttajia", "tilastot-koulutustoimijoita", "tilastot-opetussuunnitelmia"], KaannaService.kaanna),
                                datasets: [{
                                    label: "Määrä",
                                    data: [res.kayttajia, res.koulutuksenjarjestajia, res.opetussuunnitelmia]
                                }]
                            },
                            options: {
                                responsive: false,
                                maintainAspectRatio: true,
                                scales: {
                                    yAxes: [{
                                        ticks: {
                                            beginAtZero:true
                                        }
                                    }]
                                }
                            }
                        };
                    });
            }
        },
        pohjat: {
            controller: ($scope, koulutustoimija, opetussuunnitelmat, opsSaver) => {
                $scope.opetussuunnitelmat = opetussuunnitelmat;
                $scope.addPohja = () => ModalAdd.pohja()
                    .then(opsSaver)
                    .then((res) => $scope.opetussuunnitelmat.push(res));
            }
        },
        opetussuunnitelmat: {
            controller: ($scope, koulutustoimija, kayttajanTiedot, opetussuunnitelmat, opsSaver, kayttaja) => {
                const suosikkiApi = kayttaja.all("suosikki");
                const opetussuunnitelmaMap = _.indexBy(opetussuunnitelmat, "id");

                $scope.suosikit = _.indexBy(_.map(kayttajanTiedot.suosikit, (suosikki: string) => opetussuunnitelmaMap[suosikki]), "id");
                $scope.opetussuunnitelmat = _.reject(opetussuunnitelmat, (ops: any) => ops.tyyppi === "yhteinen");
                $scope.opsitById = _.indexBy(opetussuunnitelmat, "id");

                const add = (tyyppi) => ModalAdd[tyyppi]()
                    .then(opsSaver)
                    .then((res) => $scope.opetussuunnitelmat.push(res));

                $scope.addOpetussuunnitelma = () => add("opetussuunnitelma");
                $scope.addYleinen = () => add("yleinen");
                $scope.isEmpty = _.isEmpty;
                $scope.toggleSuosikki = (ops) => {
                    if ($scope.suosikit[ops.id]) {
                        suosikkiApi.customDELETE(ops.id);
                        delete $scope.suosikit[ops.id];
                    }
                    else {
                        suosikkiApi.customPOST(null, ops.id);
                        $scope.suosikit[ops.id] = ops;
                    }
                };
            }
        },
        yhteinen: {
            controller: ($scope, yhteiset, koulutustoimija, opetussuunnitelmat, opsSaver) => {
                $scope.yhteiset = yhteiset;
                $scope.addYhteinen = () => ModalAdd.yhteinen()
                    .then(opsSaver)
                    .then((res) => $scope.yhteiset.push(res));
            }
        },
        tiedotteet: {
            controller: ($rootScope, $scope, tiedotteet, nimiLataaja) => {
                $scope.edit = EditointikontrollitService.createListRestangular($scope, "tiedotteet", tiedotteet);
                $rootScope.$broadcast("has:tiedotteet", !_.isEmpty(tiedotteet));

                _.each($scope.tiedotteet, (tiedote) =>
                    nimiLataaja(tiedote.luoja)
                        .then(_.cset(tiedote, "$$nimi")));

                $scope.remove = (tiedote) => {
                    if (!tiedote) {
                        return;
                    }
                    return ModalConfirm.generalConfirm({ name: tiedote.otsikko }, tiedote)
                        .then(tiedote => tiedote.remove())
                        .then(() => {
                            NotifikaatioService.onnistui("tiedote-poistettu");
                            _.remove($scope.tiedotteet, tiedote);
                            EditointikontrollitService.cancel();
                        });
                };

                $scope.kuittaa = (tiedote) => {
                    tiedote.one("kuittaa").customPOST();
                    _.remove($scope.tiedotteet, tiedote);
                    NotifikaatioService.onnistui("tiedote-kuitattu");
                    if (_.isEmpty($scope.tiedotteet)) {
                        $rootScope.$broadcast("has:tiedotteet", false);
                    }
                };

                $scope.$on("toggle:tiedotteet", () => $scope.$$showTiedotteet = !$scope.$$showTiedotteet);

                $scope.$$creatingNewTiedote = false;
                $scope.setCreationState = (val) => $scope.$$creatingNewTiedote = val;
                $scope.addTiedoteToList = (tiedote) => $scope.tiedotteet.unshift(tiedote);
                $scope.$$showTiedotteet = false;

                { // Tiedote editing
                    $scope.cancel = () => $scope.setCreationState(false);
                    $scope.postTiedote = (newTiedote) => {
                        $rootScope.$broadcast("notifyCKEditor");
                        $scope.setCreationState(false);
                        tiedotteet.post(newTiedote)
                            .then((res) => {
                                if (res) {
                                    $scope.addTiedoteToList(res);
                                    $scope.newTiedote = {};
                                }
                                NotifikaatioService.onnistui("tallennus-onnistui");
                                $rootScope.$broadcast("has:tiedotteet", true);
                            })
                    };
                }
            }
        }
    }
}));
