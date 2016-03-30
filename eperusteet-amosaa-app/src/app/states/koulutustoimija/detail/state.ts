angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.detail", {
    url: "",
    resolve: {
        tiedotteet: (koulutustoimija) => koulutustoimija.all('tiedotteet').getList(),
        opsSaver: (opetussuunnitelmat) => (uusiOps) => uusiOps && opetussuunnitelmat
            .post(uusiOps)
    },
    views: {
        "": {
            controller: ($scope, kayttajanKoulutustoimijat, koulutustoimija) => {
                $scope.isOph = Koulutustoimijat.isOpetushallitus(koulutustoimija);
                $scope.koulutustoimijat = kayttajanKoulutustoimijat;
                $scope.koulutustoimija = koulutustoimija;
                $scope.checkOph = Koulutustoimijat.isOpetushallitus;
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
            controller: ($scope, yhteinen, koulutustoimija, opetussuunnitelmat, opsSaver) => {
                $scope.yhteinen = yhteinen;
                $scope.addYhteinen = () => ModalAdd.yhteinen()
                    .then(opsSaver)
                    .then((res) => {
                        $scope.yhteinen = _.merge(yhteinen, res)
                    });
            }
        },
        tiedotteet: {
            controller: ($scope, tiedotteet) => {
                $scope.edit = EditointikontrollitService.createListRestangular($scope, "tiedotteet", tiedotteet);
                $scope.remove = (tiedote) =>
                    ModalConfirm.generalConfirm({ name: tiedote.otsikko }, tiedote)
                        .then(tiedote => tiedote.remove())
                        .then(() => {
                            _.remove($scope.tiedotteet, tiedote);
                        });

                $scope.creatingNewTiedote = false;
                $scope.setCreationState = (val) => $scope.creatingNewTiedote = val;
                $scope.addTiedoteToList = (tiedote) => $scope.tiedotteet0 = _.merge(tiedotteet, tiedote);
            }
        },
        uusi_tiedote_div: {
            controller: ($scope, tiedotteet) => {
                $scope.newTiedote = {};
                $scope.cancel = () => $scope.setCreationState(false);
                $scope.postTiedote = (newTiedote) => {
                    $scope.setCreationState(false);
                    tiedotteet.post(newTiedote)
                        .then((res) => {
                            if (res) {
                                $scope.addTiedoteToList(res);
                            }
                            NotifikaatioService.onnistui("tallennus-onnistui");
                        })
                };
            }
        }
    }
}));
