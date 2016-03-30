angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.detail", {
    url: "",
    resolve: {
        opsSaver: ($state, opetussuunnitelmat) => (uusiOps) => uusiOps && opetussuunnitelmat
            .post(uusiOps)
            .then((res) => $state.go("root.koulutustoimija.opetussuunnitelmat.sisalto.tiedot", { opsId: res.id }))
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
            controller: ($scope) => {
            }
        }
    }
}));
