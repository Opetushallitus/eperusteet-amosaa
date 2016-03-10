angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.kasitteet", {
    url: "/kasitteet",
    resolve: {
        kasitteet: (koulutustoimija) => koulutustoimija.all('termisto').getList(),
    },
    views: {
        "": {
            controller: ($scope, $rootScope, kasitteet) => {
                $scope.edit = EditointikontrollitService.createListRestangular($scope, "kasitteet", kasitteet);
                $scope.remove = (kasite) =>
                    ModalConfirm.generalConfirm({ name: kasite.termi }, kasite)
                        .then(kasite => kasite.remove())
                        .then(() => _.remove($scope.kasitteet, kasite));

            }

        }
}}));
