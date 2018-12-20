angular.module("app").config($stateProvider =>
    $stateProvider.state("root.koulutustoimija.detail", {
        url: "",
        resolve: {
            tiedotteet: koulutustoimija => koulutustoimija.all("tiedotteet").getList(),
            ohjeistus: Api => Api.all("ohjeistus"),
            tilastot: Api => Api.one("tilastot"),
            opsSaver: ($state, opetussuunnitelmatApi) => async uusiOps => {
                console.log(uusiOps);
                if (uusiOps) {
                    const res = await opetussuunnitelmatApi.post(uusiOps);
                    $state.go(
                        "root.koulutustoimija.opetussuunnitelmat.sisalto.tiedot",
                        {
                            opsId: res.id
                        },
                        {
                            reload: true // Reloads koulutustoimijainfo and user permissions
                        }
                    );
                }
            }
        },
        views: {
            "": {
                controller: ($scope, kayttajanKoulutustoimijat, koulutustoimija, oikeudet) => {
                    $scope.isOph = Koulutustoimijat.isOpetushallitus(koulutustoimija);
                    $scope.koulutustoimijat = kayttajanKoulutustoimijat;
                    $scope.koulutustoimija = koulutustoimija;
                    $scope.checkOph = Koulutustoimijat.isOpetushallitus;
                    $scope.oikeusmap = _.zipObject(_.map(oikeudet, "_opetussuunnitelma"), oikeudet);
                    $scope.ktOikeus = Oikeudet.ktOikeus(koulutustoimija);
                    $scope.suodataOpetussuunnitelmat = (opsit, search) =>
                        _.each(opsit, ops => {
                            ops.$$hidden = !_.isEmpty(search) && !Algoritmit.match(search, ops.nimi);
                        });
                }
            },
            tilastot: {
                controller: ($scope, tilastot) => {
                    tilastot.get().then(res => {
                        $scope.tilastot = {
                            type: "bar",
                            data: {
                                labels: _.map(
                                    [
                                        "tilastot-kayttajia",
                                        "tilastot-koulutustoimijoita",
                                        "tilastot-opetussuunnitelmia"
                                    ],
                                    KaannaService.kaanna
                                ),
                                datasets: [
                                    {
                                        label: "Määrä",
                                        data: [res.kayttajia, res.koulutuksenjarjestajia, res.opetussuunnitelmia]
                                    }
                                ]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: true,
                                scales: {
                                    yAxes: [
                                        {
                                            ticks: {
                                                beginAtZero: true
                                            }
                                        }
                                    ]
                                }
                            }
                        };
                    });
                }
            },
            pohjat: {
                controller: ($scope, koulutustoimija, opetussuunnitelmatSivu, opsSaver) => {
                    $scope.opetussuunnitelmat = opetussuunnitelmatSivu.data;
                    console.log($scope.opetussuunnitelmat);
                    $scope.addPohja = () =>
                        ModalAdd.pohja()
                            .then(opsSaver);
                }
            },
            opetussuunnitelmat: {
                controller: (
                    $scope,
                    koulutustoimija,
                    kayttajanTiedot,
                    opsOletusRajaus,
                    opetussuunnitelmatSivu,
                    opsSaver,
                    kayttaja,
                    ystavaOpsit
                ) => {
                    const suosikkiApi = kayttaja.all("suosikki");

                    const updateOpetussuunnitelmat = opetussuunnitelmat => {
                        const opetussuunnitelmaMap = _.merge(_.indexBy(opetussuunnitelmat.data, "id"),
                            _.indexBy(ystavaOpsit, "id"));

                        $scope.pagination.sivukoko = opetussuunnitelmat.sivukoko;
                        $scope.pagination.kokonaismaara = opetussuunnitelmat.kokonaismäärä;

                        $scope.suosikit = _.indexBy(
                            _(kayttajanTiedot.suosikit)
                                .map((suosikki: string) => opetussuunnitelmaMap[suosikki])
                                .filter(_.isObject)
                                .value(), "id"
                        );

                        $scope.opetussuunnitelmat = _(opetussuunnitelmat.data)
                            .reject((ops: any) => ops.tyyppi === "yhteinen")
                            .each(ops => {
                                ops.$$oikeus =
                                    $scope.ktOikeus === "hallinta" ||
                                    Oikeudet.onVahintaan(
                                        "muokkaus",
                                        $scope.oikeusmap[ops.id] && $scope.oikeusmap[ops.id].oikeus
                                    );
                            })
                            .value();

                        $scope.opsitById = _.indexBy(opetussuunnitelmat.data, "id");
                    };

                    const add = tyyppi =>
                        ModalAdd[tyyppi](koulutustoimija.id)
                            .then(opsSaver);

                    $scope.ystavaOpsit = ystavaOpsit;
                    $scope.tilat = _.cloneDeep(Constants.tosTilat);
                    $scope.pagination = {
                        sivu: 1,
                        kokonaismaara: 0
                    };
                    $scope.rajain =  _.assign(_.cloneDeep(opsOletusRajaus), {
                        nimi: "",
                        tila: null
                    });

                    $scope.addOpetussuunnitelma = () => add("opetussuunnitelma");
                    $scope.addYleinen = () => add("yleinen");
                    $scope.isEmpty = _.isEmpty;
                    $scope.toggleSuosikki = ops => {
                        if ($scope.suosikit[ops.id]) {
                            suosikkiApi.customDELETE(ops.id);
                            delete $scope.suosikit[ops.id];
                        } else {
                            suosikkiApi.customPOST(null, ops.id);
                            $scope.suosikit[ops.id] = ops;
                        }
                    };
                    $scope.paivitaRajaus = async (alkuun = true) => {
                        // Aseta oletustilat
                        if ($scope.rajain.tila == null) {
                            $scope.rajain.tila = _.cloneDeep(opsOletusRajaus.tila);
                        }
                        // Jos rajaus muuttuu
                        if (alkuun) {
                            $scope.pagination.sivu = 1;
                        }
                        const res = await opetussuunnitelmatSivu.customGET("", {
                            sivu: $scope.pagination.sivu - 1,
                            sivukoko: $scope.pagination.sivukoko,
                            tila: $scope.rajain.tila,
                            nimi: $scope.rajain.nimi
                        })
                        updateOpetussuunnitelmat(res);
                    };

                    updateOpetussuunnitelmat(opetussuunnitelmatSivu);
                }
            },
            yhteinen: {
                controller: ($scope, yhteiset, koulutustoimija, opsSaver) => {
                    $scope.yhteiset = yhteiset;
                    $scope.addYhteinen = () =>
                        ModalAdd.yhteinen()
                            .then(opsSaver)
                            .then(res => $scope.yhteiset.push(res));
                }
            },
            tiedotteet: {
                controller: ($rootScope, $scope, tiedotteet, nimiLataaja) => {
                    $scope.edit = EditointikontrollitService.createListRestangular($scope, "tiedotteet", tiedotteet);
                    $rootScope.$broadcast("has:tiedotteet", !_.isEmpty(tiedotteet));

                    _.each($scope.tiedotteet, tiedote => nimiLataaja(tiedote.luoja).then(_.cset(tiedote, "$$nimi")));

                    $scope.remove = tiedote => {
                        if (!tiedote) {
                            return;
                        }
                        return ModalConfirm.generalConfirm({ name: tiedote.otsikko }, tiedote)
                            .then(tiedote => tiedote.remove())
                            .then(() => {
                                NotifikaatioService.onnistui("tiedote-poistettu");
                                _.remove($scope.tiedotteet, tiedote);
                                EditointikontrollitService.cancel();
                            });
                    };

                    $scope.kuittaa = tiedote => {
                        tiedote.one("kuittaa").customPOST();
                        _.remove($scope.tiedotteet, tiedote);
                        NotifikaatioService.onnistui("tiedote-kuitattu");
                        if (_.isEmpty($scope.tiedotteet)) {
                            $rootScope.$broadcast("has:tiedotteet", false);
                        }
                    };

                    $scope.$on("toggle:tiedotteet", () => ($scope.$$showTiedotteet = !$scope.$$showTiedotteet));

                    $scope.$$creatingNewTiedote = false;
                    $scope.setCreationState = val => ($scope.$$creatingNewTiedote = val);
                    $scope.addTiedoteToList = tiedote => $scope.tiedotteet.unshift(tiedote);
                    $scope.$$showTiedotteet = false;

                    {
                        // Tiedote editing
                        $scope.cancel = () => $scope.setCreationState(false);
                        $scope.postTiedote = newTiedote => {
                            $rootScope.$broadcast("notifyCKEditor");
                            $scope.setCreationState(false);
                            tiedotteet.post(newTiedote).then(res => {
                                if (res) {
                                    $scope.addTiedoteToList(res);
                                    $scope.newTiedote = {};
                                }
                                NotifikaatioService.onnistui("tallennus-onnistui");
                                $rootScope.$broadcast("has:tiedotteet", true);
                            });
                        };
                    }
                }
            }
        }
    })
);
