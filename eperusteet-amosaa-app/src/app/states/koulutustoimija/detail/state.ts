angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.detail", {
    url: "",
    views: {
        "": {
            controller: ($scope, yhteinen, koulutustoimija) => {
                $scope.yhteinen = yhteinen;
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
            controller: ($scope) => {}
        },
        tiedotteet: {
            controller: ($scope) => {}
        }
    }
}));
