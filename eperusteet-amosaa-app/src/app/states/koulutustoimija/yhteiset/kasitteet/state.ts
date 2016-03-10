angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.kasitteet", {
    url: "/kasitteet",
    resolve: {
        kasitteet: (koulutustoimija) => koulutustoimija.all('termisto').getList(),
        //experimenting
        //lista: (kasitteet) => Termisto.rakenna(kasitteet)
    },
    views: {
        "": {
            controller: ($scope, $rootScope, kasitteet) => {

                $scope.kasitteet = kasitteet;

                const createKasitteet = () => {
                    $scope.kasitteet = _(kasitteet).map((kasite, idx) => {
                            kasite.$$edit = EditointikontrollitService.createRestangular($scope.kasitteet, idx, kasite);
                            return kasite;
                        }).sortBy('termi').value();
                };

                createKasitteet();

                $scope.selected = {value: undefined};

                $rootScope.$on('editointikontrollit:cancel', (e) => {
                    $scope.selected = {value: undefined}
                });

                const refresh = (kasite) => {
                    $scope.kasitteet = _.without($scope.kasitteet, kasite);
                }

                $scope.delete = (kasite) => {
                    let options = { name: kasite.termi };
                    ModalConfirm.generalConfirm(options, kasite).then((kasite) => {
                        if (kasite) {
                            kasite.remove().then(() => {
                                NotifikaatioService.onnistui("poistaminen-onnistui");
                                refresh(kasite);
                            });
                        }
                    });
                };

            }

        }
}}));
