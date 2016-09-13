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
                    $scope.misc = {
                        sortableOptions: {
                            start: (e, ui) => {
                                ui.placeholder.height(ui.item.height() - 10);
                            },
                            update: (e, ui) => {
                                let node = ui.item.sortable.model;
                                if (!ui.item.sortable.received
                                        && ui.item.sortable.source[0] !== ui.item.sortable.droptarget[0]
                                        && node.$$obj.liikkumaton) {
                                    ui.item.sortable.cancel();
                                    NotifikaatioService.varoitus("liikkumaton-ei-saa-liikkua");
                                } else {
                                    Tekstikappaleet.paivitaRakenne($scope.rakenne);
                                }
                            },
                            connectWith: ".sisalto-list",
                            handle: ".sisalto-handle",
                            cursor: "move",
                            delay: 100,
                            tolerance: "pointer",
                            placeholder: "sisalto-item-placeholder"
                        },
                        poista: Tekstikappaleet.poista,
                        palauta: Tekstikappaleet.palauta,
                        palautaYksi: Tekstikappaleet.palautaYksi
                    };
                    $scope.$$sorting = true;
                    EditointikontrollitService.create({
                        cancel: () => $q((resolve, reject) => {
                            $scope.$$sorting = false;
                            resolve();
                        }),
                        save: (kommentti) => $q((resolve, reject) => {
                            Tekstikappaleet.poistaPoistetut($scope.rakenne);
                            tekstit.one("rakenne")
                                .customPUT($scope.rakenne)
                                .then(() => {
                                    resolve();
                                    NotifikaatioService.onnistui("tallennus-onnistui");
                                    $timeout(() => $state.reload("root.koulutustoimija.opetussuunnitelmat"));
                                }, () => {
                                    EditointikontrollitService.cancel();
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

                $scope.suodata = (search) => {
                    if (!!$scope.misc.isSearching !== !_.isEmpty(search)) {
                        $scope.misc.isSearching = !_.isEmpty(search);
                    }

                    Algoritmit.traverse($scope.sivunavi, "lapset", (item) => {
                        item.$$hidden = !Algoritmit.match(search, item.$$obj.tekstiKappale.nimi);
                        !item.$$hidden && Algoritmit.traverseUp(item, parentItem => parentItem.$$hidden = false);
                    });
                };

                $scope.add = () => {
                    ModalAdd.sisaltoAdder(Opetussuunnitelmat.sallitutSisaltoTyypit(ops))
                        .then(uusi => {
                            const parentNode = tekstit.clone();

                            if (!uusi || !uusi.hasOwnProperty("tyyppi")) {
                                return;
                            }

                            if (uusi.tyyppi === "tutkinnonosa") {
                                parentNode.id = Tekstikappaleet.tutkinnonosat(otsikot).id;
                            }
                            else if (uusi.tyyppi === "suorituspolku") {
                                parentNode.id = Tekstikappaleet.suorituspolut(otsikot).id;
                            }

                            parentNode.post("", uusi)
                                .then(res => {
                                    res.$$depth = 1;
                                    otsikot.push(res);
                                    const parentOtsikko = _.find(otsikot, (otsikko: any) => otsikko.id == res._vanhempi)
                                    parentOtsikko.lapset.push(res.id);
                                    updateSivunavi();
                                    $timeout(() => {
                                        $state.go("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", { osaId: res.id })
                                            .then(() => {
                                                const el = document.getElementById("sisalto-item-" + res.id);
                                                if (el) {
                                                    el.scrollIntoView();
                                                }
                                            });
                                    });
                                });
                        });
                };

                $scope.misc = {
                    toggleItem: (event, item) => {
                        event.stopPropagation();
                        item.$$closed = !item.$$closed;
                    }
                };

            }
        },
    }
}));
