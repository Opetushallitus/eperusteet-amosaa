angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.sisalto", {
    url: "/sisalto",
    ncyBreadcrumb: {
        label: "{{'sisalto' | kaanna}}"
    },
    resolve: {
        sivunavi: (otsikot, yhteiset) => Tekstikappaleet.rakenna(otsikot, yhteiset._tekstit)
    },
    views: {
        "": {
            controller: () => { }
        },
        sivunavi: {
            controller: ($scope, sivunavi, tekstit) => {
                $scope.sivunavi = sivunavi;
                $scope.suodata = (item) => KaannaService.hae(item, $scope.search);
                $scope.add = () => {
                    ModalAdd.sisaltoAdder()
                        .then(uusi => {
                            switch(uusi.tyyppi) {
                                case "tekstikappale": {
                                    tekstit.post("", uusi.data).then(res => sivunavi.unshift(res));
                                }
                                default: {}
                            }
                        });
                };
            }
        }
    }
}));
