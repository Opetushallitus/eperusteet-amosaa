angular.module("app").config($stateProvider =>
    $stateProvider.state("root.koulutustoimija.hallinta.siirravanhat", {
        url: "/siirravanhat",
        resolve: {
            historialiitokset: koulutustoimija => koulutustoimija.all("historialiitokset").getList(),
            opetussuunnitelmatApi: koulutustoimija => koulutustoimija.one("opetussuunnitelmat")
        },
        controller: ($scope, $state, koulutustoimija, historialiitokset, opetussuunnitelmatApi, opsSiirtoModalService) => {
                $scope.koulutustoimija = koulutustoimija;
                $scope.historialiitokset = historialiitokset;
                $scope.opetussuunnitelmaLkm = -1;
                $scope.hakukesken = true;
                
                $scope.laskeOpetussuunnitelmat = () => {
                    $scope.opetussuunnitelmaLkm = $scope.historialiitokset.reduce((opsLkm, historialiitos) => opsLkm + (historialiitos.opetussuunnitelmat || []).length, 0);
                    $scope.historialiitokset = _.filter($scope.historialiitokset, (historialiitos) => historialiitos.opetussuunnitelmat.length > 0);
                };

                $scope.haeHistorialiitoksienOpetussuunnitelmat = async () => {

                    try {
                        const fetchedOpetussuunnitelmat = await Promise.all(
                            _.map(historialiitokset, (historialiitos) => opetussuunnitelmatApi.all("organisaatio/"+historialiitos.organisaatio.oid).getList()));

                        _.each($scope.historialiitokset, historialiitos => 
                            historialiitos.opetussuunnitelmat = _.filter(fetchedOpetussuunnitelmat, (opetussuunnitelmat) => 
                                opetussuunnitelmat.length > 0 && opetussuunnitelmat[0].koulutustoimija.organisaatio === historialiitos.organisaatio.oid)[0] || []);  
                        
                        $scope.laskeOpetussuunnitelmat();
                        $scope.hakukesken = false;
                    } catch(e) {
                        NotifikaatioService.fataali(e.message || 'palvelin-virhetilanne');
                    }

                };

                $scope.haeHistorialiitoksienOpetussuunnitelmat();

                $scope.siirraOpetussuunnitelmat = async (opetussuunnitelmat) => {
                    await Promise.all(_.map(opetussuunnitelmat, (opetussuunnitelma) => opetussuunnitelmatApi.customPOST({}, opetussuunnitelma.id+"/siirraPassivoidusta")));
                    _.each($scope.historialiitokset, historialiitos => historialiitos.opetussuunnitelmat = 
                        _.filter(historialiitos.opetussuunnitelmat, opetussuunnitelma => 
                            !_.includes(opetussuunnitelmat, opetussuunnitelma)));
                        
                    $scope.laskeOpetussuunnitelmat();
                }

                $scope.siirra = async (opetussuunnitelma) => {
                    $scope.siirraOpetussuunnitelmat([opetussuunnitelma]);
                };

                $scope.siirraKaikki = () => {
                    $scope.siirraOpetussuunnitelmat($scope.historialiitokset.flatMap(historialiitos => historialiitos.opetussuunnitelmat));
                };
            }
        
        
    })
);
