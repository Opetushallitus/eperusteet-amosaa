angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.detail", {
    url: "",
    resolve: {
        opsSaver: (opetussuunnitelmat) => (uusiOps) => uusiOps && opetussuunnitelmat
            .post(uusiOps)
    },
    views: {
        "": {
            controller: ($scope, kayttajanKoulutustoimijat, koulutustoimija) => {
                $scope.isOph = Koulutustoimijat.isOpetushallitus(koulutustoimija);
                $scope.koulutustoimijat = kayttajanKoulutustoimijat;
                $scope.koulutustoimija = koulutustoimija;
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
                $scope.opetussuunnitelmat = opetussuunnitelmat;
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
                    .then((res) => _.merge(yhteinen, res));
            }
        },
        tiedotteet: {
            controller: ($scope) => {
            }
        }
    }
}));
