angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.sisalto", {
    url: "/sisalto",
    resolve: {
    },
    views: {
        "": {
            controller: ($q, $scope, $state, reresolver, otsikot, tekstit, yhteiset) => {
                $scope.startSorting = () => {
                    $scope.rakenne = Tekstikappaleet.teeRakenne(Tekstikappaleet.uniikit(otsikot), yhteiset._tekstit);
                    $scope.sortableOptions = {
                        // update: (e, ui) => {},
                        connectWith: ".sisalto-list",
                        handle: ".sisalto-handle",
                        cursorAt: { top: 2, left: 2 },
                        cursor: "move",
                        delay: 100,
                        tolerance: "pointer",
                        placeholder: "sisalto-item-placeholder"
                    };

                    $scope.$$sorting = true;
                    EditointikontrollitService.create({
                        cancel: () => $q((resolve, reject) => {
                            $scope.$$sorting = false;
                            resolve();
                        }),
                        save: (kommentti) => $q((resolve, reject) => {
                            tekstit.one("rakenne").customPUT($scope.rakenne)
                                .then(() => {
                                    NotifikaatioService.onnistui("tallennus-onnistui")
                                    $state.reload("root.koulutustoimija.yhteiset");
                                });
                            resolve();
                        }),
                        after: () => $scope.$$sorting = false
                    })();
                };
            }
        },
        sivunavi: {
            controller: ($q, $scope, $state, $timeout, otsikot, tekstit, yhteiset) => {
                $scope.suodata = (item) => KaannaService.hae(item, $scope.search);
                const updateSivunavi = () =>
                    $scope.sivunavi = Tekstikappaleet.teeRakenne(Tekstikappaleet.uniikit(otsikot), yhteiset._tekstit);
                updateSivunavi();

                $scope.add = () => {
                    ModalAdd.sisaltoAdder()
                        .then(uusi => {
                            switch(uusi.tyyppi) {
                                case "tekstikappale":
                                    tekstit.post("", uusi.data)
                                        .then(res => {
                                            res.$$depth = 0;
                                            otsikot.push(res);
                                            _.find(otsikot, (otsikko: any) =>
                                                   otsikko.id == yhteiset._tekstit).lapset.push(res.id);
                                            updateSivunavi();
                                            $timeout(() =>
                                                $state.go("root.koulutustoimija.yhteiset.sisalto.tekstikappale", { tkvId: res.id }));
                                        });
                                default: {}
                            }
                        });
                };
            }
        },
    }
}));
