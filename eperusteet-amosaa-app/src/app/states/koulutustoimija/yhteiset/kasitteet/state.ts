angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.kasitteet", {
    url: "/kasitteet",
    resolve: {
        kasitteet: (koulutustoimija) => koulutustoimija.all('termisto').getList(),
    },
    views: {
        "": {
            controller: ($scope, kasitteet) => {
                $scope.edit = EditointikontrollitService.createListRestangular($scope, "kasitteet", kasitteet);
                $scope.remove = (kasite) =>
                    ModalConfirm.generalConfirm({ name: kasite.termi }, kasite)
                        .then(kasite => kasite.remove())
                        .then(() => _.remove($scope.kasitteet, kasite));

                $scope.sortByAlaviite = (order) => {
                    $scope.kasitteet = _.sortBy($scope.kasitteet, (k) => k.alaviite === order);
                };
                $scope.creatingNewKasite = false;
                $scope.setCreationState = (val) => $scope.creatingNewKasite = val;
            }
        },
        "uusi_kasite_row": {
            controller: ($scope, kasitteet) => {
                $scope.cancel = () => $scope.setCreationState(false);
                $scope.createKasite = () => $scope.setCreationState(true);
                $scope.postKasite = (newKasite) => {
                    $scope.setCreationState(false);
                    Termisto.post(kasitteet, newKasite)
                        .then((res) => {
                            $scope.kasitteet.unshift(res);
                            $scope.newKasite = {};
                        })
                };

            }
        }
}}));
