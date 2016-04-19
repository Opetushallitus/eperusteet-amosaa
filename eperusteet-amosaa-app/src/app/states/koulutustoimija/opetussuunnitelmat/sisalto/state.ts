angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat.sisalto", {
    url: "/sisalto",
    resolve: {
    },
    views: {
        "": {
            controller: ($q, $scope, $timeout, $state, reresolver, otsikot, tekstit, sisaltoRoot) => {
                $scope.startSorting = () => {
                    $scope.rakenne = Tekstikappaleet.teeRakenne(Tekstikappaleet.uniikit(otsikot), sisaltoRoot.id);
                    $scope.sortableOptions = {
                        update: (e, ui) => {
                        },
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
                            tekstit.one("rakenne")
                                .customPUT($scope.rakenne)
                                .then(() => {
                                    resolve();
                                    NotifikaatioService.onnistui("tallennus-onnistui");
                                    $timeout(() => $state.reload("root.koulutustoimija.opetussuunnitelmat"));
                                });
                        }),
                        after: () => $scope.$$sorting = false
                    })();
                };
            }
        },
        sivunavi: {
            controller: ($q, $scope, $state, $timeout, otsikot, tekstit, sisaltoRoot, ops) => {
                const updateSivunavi = _.callAndGive(() => {
                    $scope.sivunavi = Tekstikappaleet.teeRakenne(Tekstikappaleet.uniikit(otsikot), sisaltoRoot.id);
                });

                // TODO: find cleaner solution (Low priority)
                $scope.$on("sivunavi:forcedUpdate", (event, osa) => {
                    const uniikit = Tekstikappaleet.uniikit(otsikot);
                    _.merge(uniikit[osa.id], osa.plain());
                });

                const traverseItems = (item) => {
                    let childsVisible = false;
                    _.each(item.lapset, (item) => {
                        let itemVisible = traverseItems(item);
                        if (itemVisible) {
                            childsVisible = true;
                        }
                    });

                    const
                        isMatchingName = KaannaService.hae(item.$$obj.tekstiKappale.nimi || {}, $scope.search);

                    item.$$hidden = !isMatchingName && !childsVisible;
                    return item.$$hidden;
                };

                $scope.suodata = (search) => {
                    _.each($scope.sivunavi.lapset, (item) => {
                        traverseItems(item);
                    });
                };

                $scope.add = () => {
                    ModalAdd.sisaltoAdder(Opetussuunnitelmat.sallitutSisaltoTyypit(ops))
                        .then(uusi => {
                            let parentNode = tekstit.clone();
                            if (uusi.tyyppi === "tutkinnonosa") {
                                parentNode.id = Tekstikappaleet.tutkinnonosat(otsikot).id;
                            }
                            else if (uusi.tyyppi === "suorituspolku") {
                                parentNode.id = Tekstikappaleet.suorituspolut(otsikot).id;
                            }

                            if (!uusi) {
                                return;
                            }
                            parentNode.post("", uusi)
                                .then(res => {
                                    res.$$depth = 0;
                                    otsikot.push(res);
                                    _.find(otsikot, (otsikko: any) => otsikko.id == parentNode.id).lapset.push(res.id);
                                    updateSivunavi();
                                    $timeout(() =>
                                        $state.go("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", { osaId: res.id }));
                                });
                        });
                };
            }
        },
    }
}));
