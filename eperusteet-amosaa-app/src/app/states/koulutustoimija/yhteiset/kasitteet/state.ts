angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.kasitteet", {
    url: "/kasitteet",
    resolve: {
        termisto: (koulutustoimija, Api, $state) => Api.one('koulutustoimija'),
        kasitteet: (termisto, $state) => termisto.one($state.params.ktId).all('termisto').getList(),
        lista: (kasitteet) => Termisto.rakenna(kasitteet)
},
    views: {
        "": {
            controller: ($scope, kasitteet, lista) => {
                //$scope.edit = EditointikontrollitService.createRestangular($scope, "tkv", kasitteet);
                $scope.kasitteet = lista;
                $scope.editKasite = (kasite) => ModalRevisions.kasiteRevision(kasite)
            }
        }
    }
}));