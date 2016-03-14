angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.detail", {
    url: "",
    ncyBreadcrumb: {
        label: '{{"koulutustoimija" | kaanna}}'
    },
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
            controller: ($scope, perusteet) => {
                $scope.opsit = perusteet;
            }
        },
        yhteiset: {
            controller: ($scope, yhteiset) => {
                $scope.yhteiset = yhteiset;
            }
        },
        tiedotteet: {
            controller: ($scope) => {
            }
        }
    }
}));
