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
                $scope.cancel = () => {
                    $scope.newKasite.creating = false;
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

                $scope.sortOptions = [{value:"-date", text:"Sort by publish date"},
                    {value:"name", text:"Sort by blog name"},
                    {value:"author.name", text:"Sort by author"}];

                $scope.sortOrder = $scope.sortOptions[0].value;

            }


        }
}}));
