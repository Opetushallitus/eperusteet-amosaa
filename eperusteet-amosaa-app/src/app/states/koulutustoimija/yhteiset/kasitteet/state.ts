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
                $scope.newKasite = {
                    placeholder: {termi: "", selitys: "", alaviite: null},
                    creating: false
                };
                $scope.createKasite = () => {
                    $log.info("creating");
                    $scope.newKasite.creating = true;
                };
                $scope.postKasite = (newKasite) => {
                    //validate here
                    $scope.newKasite.creating = false;
                    kasitteet.post(newKasite).then((res) => {
                        $scope.kasittet.unshift(res);
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
