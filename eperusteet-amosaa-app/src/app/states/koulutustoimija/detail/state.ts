angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.detail", {
    url: "",
    resolve: {
        tiedotteet: (koulutustoimija) => koulutustoimija.all("tiedotteet").getList(),
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
            controller: ($scope, kayttajanKoulutustoimijat, koulutustoimija, tiedotteet) => {
                $scope.isOph = Koulutustoimijat.isOpetushallitus(koulutustoimija);
                $scope.koulutustoimijat = kayttajanKoulutustoimijat;
                $scope.koulutustoimija = koulutustoimija;
                $scope.checkOph = Koulutustoimijat.isOpetushallitus;
                $scope.tiedotteet = tiedotteet;
                $scope.toggleTiedotteet = () => $scope.$$showTiedotteet = !$scope.$$showTiedotteet;
                $scope.suodataOpetussuunnitelmat = (opsit, search) =>
                    _.each(opsit, (ops) => {
                        ops.$$hidden = !_.isEmpty(search) && !Algoritmit.match(search, ops.nimi) });
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
            controller: ($scope, koulutustoimija, opetussuunnitelmat, opsSaver) => {
                $scope.opetussuunnitelmat = _.reject(opetussuunnitelmat, (ops: any) => ops.tyyppi === "yhteinen");
                $scope.addOpetussuunnitelma = () => ModalAdd.opetussuunnitelma()
                    .then(opsSaver)
                    .then((res) => $scope.opetussuunnitelmat.push(res));
            }
        },
        yhteinen: {
            controller: ($scope, yhteiset, koulutustoimija, opetussuunnitelmat, opsSaver) => {
                $scope.yhteiset = yhteiset;
                $scope.addYhteinen = () => ModalAdd.yhteinen()
                    .then(opsSaver)
                    .then((res) => {
                        $scope.yhteiset.push(res);
                    });
            }
        },
        tiedotteet: {
            controller: ($scope, tiedotteet, kayttajanTieto) => {
                $scope.edit = EditointikontrollitService.createListRestangular($scope, "tiedotteet", tiedotteet);
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

                _.each(tiedotteet, (tiedote) =>
                    kayttajanTieto(tiedote.luoja)
                        .then(res => tiedote.$$nimi = Kayttajatiedot.parsiEsitysnimi(res)));

                $scope.kuittaa = (tiedote) => {
                    tiedote.one("kuittaa").customPOST();
                    _.remove($scope.tiedotteet, tiedote);
                    NotifikaatioService.onnistui("tiedote-kuitattu");
                };

                $scope.creatingNewTiedote = false;
                $scope.setCreationState = (val) => $scope.creatingNewTiedote = val;
                $scope.addTiedoteToList = (tiedote) => $scope.tiedotteet.unshift(tiedote);
            }
        },
        uusi_tiedote_div: {
            controller: ($rootScope, $scope, tiedotteet) => {
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
                        })
                };
            }
        }
    }
}));
