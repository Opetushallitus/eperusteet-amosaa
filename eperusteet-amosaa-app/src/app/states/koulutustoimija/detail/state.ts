angular.module("app").config($stateProvider =>
    $stateProvider.state("root.koulutustoimija.detail", {
        url: "",
        resolve: {
            tiedotteet: koulutustoimija => koulutustoimija.all("tiedotteet").getList(),
            ohjeistus: Api => Api.all("ohjeistus"),
            tilastot: Api => Api.one("tilastot"),
            toimijatilastot: tilastot => tilastot.one("toimijat"),
            opsSaver: ($state, opetussuunnitelmatApi) => async uusiOps => {
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
                controller: ($scope, tilastot, toimijatilastot) => {
                    $scope.toimijoittain = null;
                    async function getToimijoittain() {
                        const result = await toimijatilastot.get();
                        $scope.toimijoittain = result;
                    }

                    $scope.getToimijoittain = getToimijoittain;

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
                controller: (
                    $scope,
                    koulutustoimija,
                    opetussuunnitelmatSivu,
                    opsSaver,
                    $q: angular.IQService,
                ) => {

                    const updateOpetussuunnitelmat = opetussuunnitelmat => {
                        $scope.opetussuunnitelmat = opetussuunnitelmat.data;
                        $scope.pagination = {
                            sivu: opetussuunnitelmat.sivu + 1,
                            sivukoko: opetussuunnitelmat.sivukoko,
                            kokonaismaara: opetussuunnitelmat.kokonaismäärä,
                        };

                    };
                    $scope.addPohja = () =>
                        ModalAdd.pohja()
                            .then(opsSaver);

                    let timeout: ng.IDeferred<void>;
                    $scope.paivitaRajaus = async (alkuun = true) => {
                        // Jos rajaus muuttuu
                        if (alkuun) {
                            $scope.pagination.sivu = 1;
                        }

                        if (timeout) {
                            timeout.resolve();
                        }

                        timeout = $q.defer();

                        try {
                            const res = await opetussuunnitelmatSivu.customGET("", {
                                sivu: $scope.pagination.sivu - 1,
                                sivukoko: $scope.pagination.sivukoko,
                                nimi: $scope.rajain && $scope.rajain.nimi,
                                koulutustyyppi: Amosaa.tuetutKoulutustyypit(),
                                tyyppi: ['pohja'],
                            }, { timeout });
                            $scope.$applyAsync(() => updateOpetussuunnitelmat(res));
                        }
                        finally {
                            timeout = null;
                        }
                    };

                    $scope.pagination = {
                        sivu: 1,
                        sivukoko: 10,
                    };
                    $scope.paivitaRajaus(true);
                }
            },
            opetussuunnitelmat: {
                controller: (
                    $q: angular.IQService,
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
                        sivukoko: 10,
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

                    let timeout: ng.IDeferred<void>;
                    $scope.paivitaRajaus = async (alkuun = true) => {
                        // Aseta oletustilat
                        if ($scope.rajain.tila == null) {
                            $scope.rajain.tila = _.cloneDeep(opsOletusRajaus.tila);
                        }
                        // Jos rajaus muuttuu
                        if (alkuun) {
                            $scope.pagination.sivu = 1;
                        }

                        if (timeout) {
                            timeout.resolve();
                        }

                        timeout = $q.defer();

                        try {
                            const res = await opetussuunnitelmatSivu.customGET("", {
                                sivu: $scope.pagination.sivu - 1,
                                sivukoko: $scope.pagination.sivukoko,
                                tila: $scope.rajain.tila,
                                nimi: $scope.rajain.nimi,
                                tyyppi: ['ops', 'yleinen'],
                                koulutustyyppi: Amosaa.tuetutKoulutustyypit(),
                            }, { timeout });
                            $scope.$applyAsync(() => updateOpetussuunnitelmat(res));
                        }
                        finally {
                            timeout = null;
                        }
                    };

                    $scope.paivitaRajaus(true);
                }
            },
            yhteinen: {
                controller: (
                    $q: angular.IQService,$scope,
                     yhteiset,
                     koulutustoimija,
                     opsSaver,
                     opetussuunnitelmatSivu) => {

                    const updateOpetussuunnitelmat = opetussuunnitelmat => {
                        $scope.yhteiset = opetussuunnitelmat.data;
                        $scope.pagination = {
                            sivu: opetussuunnitelmat.sivu + 1,
                            sivukoko: opetussuunnitelmat.sivukoko,
                            kokonaismaara: opetussuunnitelmat.kokonaismäärä,
                        };

                    };

                    $scope.pagination = {
                        sivu: 1,
                        sivukoko: 10,
                        kokonaismaara: 0
                    };

                    updateOpetussuunnitelmat(yhteiset);

                    let timeout: ng.IDeferred<void>;
                    $scope.paivitaRajaus = async () => {

                        if (timeout) {
                            timeout.resolve();
                        }

                        timeout = $q.defer();

                        try {
                            const res = await opetussuunnitelmatSivu.customGET("", {
                                sivu: $scope.pagination.sivu - 1,
                                sivukoko: $scope.pagination.sivukoko,
                                tyyppi: ['yhteinen'],
                                tila: _(Constants.tosTilat)
                                    .filter(tila => tila !== "poistettu")
                                    .value(),
                            }, { timeout });
                            $scope.$applyAsync(() => updateOpetussuunnitelmat(res));
                        }
                        finally {
                            timeout = null;
                        }
                    };

                    $scope.addYhteinen = () =>
                        ModalAdd.yhteinen($scope.yhteiset)
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
