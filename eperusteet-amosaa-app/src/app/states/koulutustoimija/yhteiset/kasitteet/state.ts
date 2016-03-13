angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.kasitteet", {
    url: "/kasitteet",
    resolve: {
        kasitteet: (koulutustoimija) => koulutustoimija.all('termisto').getList(),
    },
    views: {
        "": {
            controller: ($scope, $rootScope, kasitteet, $log) => {
                $scope.newKasite = {};
                $scope.creatingNewKasite = false;
                $scope.cancel = () => $scope.creatingNewKasite = false;
                $scope.createKasite = () => $scope.creatingNewKasite = true;
                $scope.postKasite = (newKasite) => {
                    $scope.creatingNewKasite = false;
                    Termisto.post(kasitteet, newKasite)
                        .then((res) => {
                            $scope.kasitteet.unshift(res);
                            $scope.newKasite = {};
                        })
                };
                $scope.edit = EditointikontrollitService.createListRestangular($scope, "kasitteet", kasitteet);
                $scope.remove = (kasite) =>
                    ModalConfirm.generalConfirm({ name: kasite.termi }, kasite)
                        .then(kasite => kasite.remove())
                        .then(() => _.remove($scope.kasitteet, kasite));

                $scope.sortByAlaviite = (order) => {
                    $scope.kasitteet = _.sortBy($scope.kasitteet, (k) => k.alaviite === order);
                }

            }


        }
}}));
