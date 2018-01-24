angular.module("app")
    .service("opsSiirtoModalService", ($rootScope, $uibModal, $q, Varmistusdialogi, Api) => {
        return {
            siirra(koulutustoimija, ops) {
                return $uibModal.open({
                    templateUrl: "modals/yhteistyo/yhteistyo.jade",
                    // size: 'sm',
                    resolve: {
                        koulutustoimija,
                        ops,
                        yhteistyo: () => koulutustoimija.all("ystavat").getList(),
                    },
                    controller($uibModalInstance, $scope, $timeout, $state, yhteistyo, koulutustoimija, ops) {
                        $scope.koulutustoimija = koulutustoimija;
                        $scope.organisaatiot = yhteistyo;
                        $scope.cancel = $uibModalInstance.dismiss;
                        $scope.ok = org => {
                            Varmistusdialogi.dialogi({
                                otsikko: "varmista-siirto",
                                data: {
                                    nimi: KaannaService.kaanna(org.nimi)
                                },
                                teksti: "haluatko-varmasti-siirtaa-opetussuunnitelman-organisaatiolle"
                            })(() => {
                                ops.customPOST(org, "/siirra")
                                    .then(() => {
                                        $timeout(() => {
                                            $uibModalInstance.close();
                                            $state.go("root.koulutustoimija.detail", $state.params, {
                                                reload: true
                                            });
                                        }, 100);
                                    });
                            }, () => {
                            });
                        };
                    }
                }).result;
            }
        };
    });
