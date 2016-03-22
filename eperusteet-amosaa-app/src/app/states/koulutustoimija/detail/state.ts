angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.detail", {
    url: "",
    views: {
        "": {
            controller: ($scope, kayttajanKoulutustoimijat, koulutustoimija) => {
                $scope.koulutustoimijat = kayttajanKoulutustoimijat;
                $scope.koulutustoimija = koulutustoimija;
                $scope.addOpetussuunnitelma = () => {
                    ModalAdd.opetussuunnitelma().then((ops) => {
                        console.log(ops);
                    });
                };
            }
        },
        opetussuunnitelmat: {
            controller: ($scope) => {
                $scope.opsit = [];
            }
        },
        yhteinen: {
            controller: ($scope, yhteinen) => {
                $scope.yhteinen = yhteinen;
            }
        },
        tiedotteet: {
            controller: ($scope) => {
            }
        }
    }
}));
