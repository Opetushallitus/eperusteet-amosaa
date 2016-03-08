angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.kasitteet", {
    url: "/kasitteet",
    resolve: {
        termisto: (koulutustoimija, Api, $state) => Api.one('koulutustoimija'),
        kasitteet: (termisto, $state) => termisto.one($state.params.ktId).all('termisto').getList(),
        //experimenting
        lista: (kasitteet) => Termisto.rakenna(kasitteet)
},
    views: {
        "": {
            controller: ($scope, kasitteet, lista) => {
                /*$scope.edit = EditointikontrollitService.createRestangular($scope, "kasitteet", kasitteet, {
                    done: () => kasitteet.getList().then((res) => {
                        $scope.selected = res
                    })
                });*/
                $scope.kasitteet = lista;
            }
        }
    }
}));