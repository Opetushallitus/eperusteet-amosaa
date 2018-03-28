angular.module("app").config($stateProvider =>
    $stateProvider.state("root.koulutustoimija.opetussuunnitelmat.poistetut", {
        url: "/poistetut",
        resolve: {
            poistetut: ops => ops.all("poistetut").getList()
        },
        views: {
            "": {
                controller: ($scope, $state, $timeout, poistetut, peruste, nimiLataaja) => {
                    $scope.$$isValmaTelma = _.includes(Amosaa.valmaTelmaKoulutustyypit(), peruste.koulutustyyppi);
                    $scope.vaihdaJarjestys = Taulukot.bindSivutus($scope, "poistoAika", poistetut);
                    $scope.suodata = item => KaannaService.hae(item.nimi, $scope.search);
                    $scope.palauta = item => {
                        item.post("palauta").then(res => {
                            _.remove(poistetut, item);
                            // Tarvitaan päivittämään sivunavi
                            $timeout(() => $state.reload("root.koulutustoimija.opetussuunnitelmat"));
                        });
                    };
                    $scope.palautaJaSiirry = item => {
                        item.post("palauta").then(res => {
                            _.remove(poistetut, item);
                            $timeout(() =>
                                $state.go(
                                    "root.koulutustoimija.opetussuunnitelmat.sisalto.osa",
                                    {
                                        osaId: res.id
                                    },
                                    {
                                        reload: true
                                    }
                                )
                            );
                        });
                    };
                    _.each(poistetut, poistettu => {
                        nimiLataaja(poistettu.muokkaajaOid).then(_.cset(poistettu, "$$nimi"));
                    });
                }
            }
        }
    })
);
