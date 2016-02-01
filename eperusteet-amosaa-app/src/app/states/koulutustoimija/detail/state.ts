angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.detail", {
    url: "",
    resolve: {
    },
    views: {
        "": {
            controller: ($scope, yhteinen, kayttajaprofiili, koulutustoimija) => {
                $scope.yhteinen = yhteinen;
                $scope.kayttajaprofiili = kayttajaprofiili;
                $scope.koulutustoimija = koulutustoimija;

                $scope.addOpetussuunnitelma = () => {
                    ModalAdd.opetussuunnitelma();
                };
            }
        },
        opetussuunnitelmat: {
            controller: ($scope, perusteet) => {
                $scope.opsit = perusteet;
            }
        },
        yhteiset: {
            controller: ($scope) => {}
        },
        tiedotteet: {
            controller: ($scope) => {}
        }
    }
}));
