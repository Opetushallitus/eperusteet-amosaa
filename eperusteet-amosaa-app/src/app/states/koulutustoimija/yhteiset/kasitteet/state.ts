angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.kasitteet", {
    url: "/kasitteet",
    resolve: {
        kasitteet: (koulutustoimija) => koulutustoimija.all('termisto').getList(),
    },
    views: {
        "": {
            controller: ($scope, $rootScope, kasitteet, $log, Termisto) => {
                //$scope.newKasite = {};
                $scope.creatingNewKasite = false;
                $scope.cancel = () => $scope.creatingNewKasite = false;
                $scope.createKasite = () => $scope.creatingNewKasite = true;
                $scope.postKasite = (newKasite) => {
                    if (!newKasite.avain) {
                        newKasite.avain =  Termisto.makeKey(newKasite);
                    }
                    $scope.creatingNewKasite = false;
                    kasitteet.post(newKasite).then((kasite) => {
                        $scope.kasitteet.unshift(kasite);
                    })
                };
                $scope.edit = EditointikontrollitService.createListRestangular($scope, "kasitteet", kasitteet);
                $scope.remove = (kasite) =>
                    ModalConfirm.generalConfirm({ name: kasite.termi }, kasite)
                        .then(kasite => kasite.remove())
                        .then(() => _.remove($scope.kasitteet, kasite));

            }


        }
}}));
