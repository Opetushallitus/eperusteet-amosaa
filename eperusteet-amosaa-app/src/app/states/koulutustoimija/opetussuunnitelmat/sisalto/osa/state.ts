namespace SuoritustapaRyhmat {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    export const editoi = (spRivit, node, tutkinnonosat, koodisto, paikalliset) => i.$uibModal.open({
            resolve: { },
            templateUrl: "modals/suoritustaparyhma.jade",
            size: "lg",
            controller: ($q, $scope, $state, $uibModalInstance, $rootScope) => {
                $scope.restoreModule = (m) => {
                    $scope.spRivit[m.tunniste] = undefined;
                };

                $scope.alkioitaSivulla = 10;
                $scope.sivu = 1;
                $scope.tosaRajaus = {};
                $scope.spRivit = spRivit;
                $scope.tosat = tutkinnonosat;
                $scope.node = node;
                $scope.editable = _.find(spRivit, (rivi: any) => rivi.rakennemoduuli === node.tunniste) || {
                    rakennemoduuli: node.tunniste
                };

                const valitutKoodit = _.indexBy($scope.editable.koodit || [], _.identity);
                let koodit = [];

                $q.all([
                        paikalliset.getList(),
                        koodisto.all("tutkinnonosat").getList(),
                    ])
                    .then(([paikallisetKoodit, julkisetKoodit]) => {
                        $scope.koodit = koodit = _(paikallisetKoodit)
                            .map(pkoodi => ({
                                arvo: pkoodi.tosa.omatutkinnonosa.koodi,
                                uri: "paikallinen_tutkinnonosa_"
                                    + pkoodi.tosa.omatutkinnonosa.koodiPrefix
                                    + "_" + pkoodi.tosa.omatutkinnonosa.koodi,
                                nimi: pkoodi.tekstiKappale.nimi,
                                $$paikallinen: true
                            }))
                            .concat(Koodisto.parseRawKoodisto(julkisetKoodit))
                            .each((koodi: any) => {
                                koodi.$$valittu = !!valitutKoodit[koodi.uri],
                                koodi.$$piilotettu = false
                            })
                            .sortBy(koodi => KaannaService.kaanna(koodi.nimi))
                            .value()
                    });

                $scope.search = "";
                $scope.piilotetut = {};
                $scope.suodata = (search) =>
                    $scope.koodit = _.reject(koodit, (koodi) =>
                        (!Algoritmit.match(search, koodi.nimi)
                                && !Algoritmit.match(search, koodi.arvo))
                            || (($scope.tosaRajaus.paikalliset && !koodi.$$paikallinen)
                                || ($scope.tosaRajaus.valitut && !koodi.$$valittu)));

                $scope.ok = () => {
                    $rootScope.$broadcast("notifyCKEditor");
                    $scope.editable.koodit = _(koodit)
                        .filter("$$valittu")
                        .map("uri")
                        .value();
                    $scope.spRivit[$scope.editable.rakennemoduuli] = $scope.editable;
                    $uibModalInstance.close($scope.spRivit);
                };

                $scope.peruuta = $uibModalInstance.dismiss;
            }
        }).result;
}

angular.module("app")
.run(SuoritustapaRyhmat.init)
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", {
    url: "/osa/:osaId",
    resolve: {
        osa: (ops, $stateParams) => ops.one("tekstit", $stateParams.osaId).get(),
        historia: osa => osa.getList("versiot"),
        versioId: $stateParams => $stateParams.versio,
        versio: (versioId, historia) => versioId && historia.get(versioId),
        kommentit: (osa) => osa.all("kommentit").getList(),
        pTosat: (Api, osa, ops) => (osa.tyyppi === "suorituspolku"
            && Api.all("perusteet/" + ops.peruste.id + "/tutkinnonosat").getList()),
        pTosa: (Api, osa, ops) => (osa.tyyppi === "tutkinnonosa"
            && osa.tosa.tyyppi === "perusteesta"
            && Api.one("perusteet/" + ops.peruste.id + "/tutkinnonosat/" + osa.tosa.perusteentutkinnonosa).get()),
        pSuoritustavat: (Api, osa, ops) => (osa.tyyppi === "suorituspolku"
            && Api.one("perusteet/" + ops.peruste.id + "/suoritustavat").get()),
        arviointiAsteikot: (Api) => Api.all("arviointiasteikot").getList()
    },
    onEnter: (osa) =>
        Murupolku.register("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", osa.tekstiKappale.nimi),
    views: {
        "": {
            controller: ($q, $state, $stateParams, $location, $scope, $rootScope, $document, $timeout,
                         koulutustoimija, osa, nimiLataaja, Varmistusdialogi, historia, versioId, versio, pTosa) => {
                $scope.pTosa = pTosa;
                nimiLataaja(osa.tekstiKappale.muokkaaja)
                    .then(_.cset(osa, "$$nimi"));

                // Version handling
                $scope.versio = versio;
                [$scope.uusin, $scope.historia] = Revisions.parseAll(historia);
                nimiLataaja($scope.uusin.muokkaaja).then(nimi => $scope.uusin.$$nimi = nimi);
                $scope.listRevisions = () => ModalRevisions.viewRevisions($scope.historia)
                    .then(res => $state.go($state.current.name, { versio: res }));
                if (versio) {
                    $scope.uusin = Revisions.get($scope.historia, versioId);
                    _.merge($scope.ops, versio.plain());
                    $scope.restoreRevision = () => {
                        $scope.ops.put().then(res => {
                            _.merge(osa, versio.plain());
                            $scope.osa = osa;
                            NotifikaatioService.onnistui("versio-palautettu-onnistuneesti");
                            $scope.restoreNew();
                        });
                    };
                }

                // Item handling
                osa.lapset = undefined;
                $scope.edit = EditointikontrollitService.createRestangular($scope, "osa", osa, {
                    after: () => {
                        $rootScope.$broadcast("sivunavi:forcedUpdate", $scope.osa);
                    },
                    preSave: () => $q((resolve, reject) => Osat.deinit($scope.osa, koulutustoimija)
                        ? resolve()
                        : reject()),
                    done: () => historia.get("uusin").then(res => {
                        $scope.uusin = Revisions.parseOne(res);
                        $scope.historia.unshift($scope.uusin);
                    })
                });

                $scope.remove = () => {
                    Varmistusdialogi.dialogi({
                        otsikko: "haluatko-varmasti-poistaa-sisallon",
                        teksti: "sisalto-poistetaa-mahdollinen-palauttaa",
                    })(() => {
                        osa.remove()
                            .then(() => {
                                NotifikaatioService.onnistui("poisto-tekstikappale-onnistui");
                                EditointikontrollitService.cancel()
                                    .then(() => {
                                        $timeout(() => {
                                            $state.reload("root");
                                            $state.go("root.koulutustoimija.opetussuunnitelmat.poistetut", $stateParams, { reload: true });
                                        });
                                    });
                            });
                    });
                };

                const clickHandler = (event) => {
                    var ohjeEl = angular.element(event.target).closest(".popover, .popover-element");
                    if (ohjeEl.length === 0) {
                        $rootScope.$broadcast("ohje:closeAll");
                    }
                };

                const installClickHandler = () => {
                    $document.off("click", clickHandler);
                    $timeout(() => {
                        $document.on("click", clickHandler);
                    });
                };

                $scope.$on("$destroy", function () {
                    $document.off("click", clickHandler);
                });

                installClickHandler();

                // FIXME: Find a better way to check when sivunavi has been fully rendered
                $timeout(() => {
                    if (osa) {
                        const el = document.getElementById("sisalto-item-" + osa.id);
                        const elNavi = document.getElementById("ops-sivunavi");
                        if (el && (el.offsetTop <= elNavi.scrollTop
                                || el.offsetTop >= elNavi.scrollTop + elNavi.offsetHeight)) {
                            el.scrollIntoView();
                        }
                    }
                }, 400);
            }
        },
        tekstikappale: {
            controller: ($scope) => {}
        },
        tutkinnonosat: {
            controller: ($scope) => {}
        },
        tutkinnonosa: {
            controller: ($q, $scope, peruste, arviointiAsteikot, koodisto, koulutustoimija) => {
                const
                    isPaikallinen = _.property("tosa.tyyppi")($scope.osa) === "oma",
                    osaamisalaKoodit = peruste.osaamisalat,
                    paikallisetKoodit = koulutustoimija.all("koodi"),

                    osaAlueKoodit = $scope.pTosa ? _.map($scope.pTosa.osaAlueet, (oa: any) => ({
                        nimi: oa.nimi,
                        arvo: oa.koodiArvo,
                        uri: oa.koodiUri
                    })) : [],

                    koodit = _([])
                        .concat(osaAlueKoodit)
                        .concat(osaamisalaKoodit)
                        .indexBy("uri")
                        .value(),

                    haeKoodiTiedot = (koodiUrit) =>
                        $q.all(_.map(koodiUrit, uri => koodisto.one("uri/" + uri).get()))
                            .then(Koodisto.parseRawKoodisto),

                    paivitaKoodistoTiedot = () => {
                        const
                            toteutuksienKoodit = _($scope.osa.tosa.toteutukset)
                                .map("koodit")
                                .flatten()
                                .uniq()
                                .reject((koodi: string) => !!$scope.koodistoTiedot[koodi])
                                .value();

                        haeKoodiTiedot(toteutuksienKoodit)
                            .then(koodit => {
                                _.each(koodit, koodi => {
                                    $scope.koodistoTiedot[koodi.uri] = koodi;
                                })
                            });
                    },

                    getTutkintonimikekoodit = () =>
                        haeKoodiTiedot(_.map(peruste.tutkintonimikkeet, "tutkintonimikeUri")),

                    addKoodi = (valitut, toteutus) => KoodistoModal.koodi(valitut, toteutus.koodit)
                        .then(res => {
                            toteutus.koodit = res;
                            paivitaKoodistoTiedot();
                        });

                $scope.paikallinenKoodiUpdate = (pkoodi) => {
                    if (pkoodi) {
                        const fullKoodi = Koodisto.paikallinenToFull(koulutustoimija, pkoodi);
                        paikallisetKoodit.one(fullKoodi).getList()
                            .then(res => $scope.tormaavatKoodit = _.reject(res, (koodi: any) => koodi.id === $scope.osa.id));
                    }
                };

                $scope.paikallinenKoodiUpdate(_.property("tosa.omatutkinnonosa.koodi")($scope.osa));

                { // Init block
                    $scope.koodistoTiedot = {};
                    $scope.$$showToteutus = true;
                    $scope.koodit = koodit;
                    $scope.peruste = peruste;
                    $scope.sortableOptions = {
                        handle: ".toteutus-handle",
                        cursor: "move",
                        delay: 100,
                        tolerance: "pointer",
                        placeholder: "toteutus-placeholder"
                    };

                    paivitaKoodistoTiedot();
                }

                // FIXME: Hack to cope with old peruste tutkinnon osa string references
                $scope.pa = {
                    arviointiasteikko: x => x._arviointiasteikko || x._arviointiAsteikko
                };

                $scope.lisaaUusiToteutus = () => {
                    $scope.osa.tosa.toteutukset.push({
                        arvioinnista: {},
                        tavatjaymparisto: {}
                    });
                };

                $scope.removeToteutus = (toteutus) => _.remove($scope.osa.tosa.toteutukset, toteutus);

                $scope.addKoodi = (toteutus) => {
                    getTutkintonimikekoodit()
                        .then(nimikkeet => {
                            if ($scope.pTosa.tyyppi === "tutke2") {
                                koodisto.all("oppiaineetyleissivistava2").getList()
                                    .then(yleissivistavat => {
                                        addKoodi(_([])
                                                .concat(nimikkeet)
                                                .concat(Koodisto.parseRawKoodisto(yleissivistavat))
                                                .concat(osaAlueKoodit)
                                                .indexBy("uri")
                                                .value(), toteutus);
                                    });
                            }
                            else {
                                addKoodi(_([])
                                         .concat(nimikkeet)
                                         .concat(osaamisalaKoodit)
                                         .indexBy("uri")
                                         .value(), toteutus);
                            }
                        });
                };

                $scope.sortableOptionsArvioinninKohdealueet = Sorting.getSortableOptions(".arviointi-kohdealueet");
                $scope.sortableOptionsArvioinninKohteet = Sorting.getSortableOptions(".arviointi-kohteet");
                $scope.sortableOptionsOsaamistasonKriteerit = Sorting.getSortableOptions(".osaamistason-kriteerit");
                $scope.sortableOptionsAmmattitaitovaatimukset = Sorting.getSortableOptions(".ammattitaitovaatimukset");
                $scope.sortableOptionsVaatimuksenKohteet = Sorting.getSortableOptions(".vaatimuksen-kohteet");
                $scope.sortableOptionsVaatimukset = Sorting.getSortableOptions(".vaatimukset");

                $scope.arviointiAsteikot = arviointiAsteikot.plain();

                $scope.lisaaArvioinninKohdealue = () => {
                    $scope.osa.tosa.omatutkinnonosa = $scope.osa.tosa.omatutkinnonosa || {};
                    $scope.osa.tosa.omatutkinnonosa.arviointi = $scope.osa.tosa.omatutkinnonosa.arviointi || {};
                    $scope.osa.tosa.omatutkinnonosa.arviointi.arvioinninKohdealueet
                        = $scope.osa.tosa.omatutkinnonosa.arviointi.arvioinninKohdealueet || [];
                    $scope.osa.tosa.omatutkinnonosa.arviointi.arvioinninKohdealueet.push({});
                };
                $scope.lisaaArviointiKohde = (arvioinninKohdealue) => {
                    arvioinninKohdealue.arvioinninKohteet = arvioinninKohdealue.arvioinninKohteet || [];
                    arvioinninKohdealue.$$uusiArvioinninKohdealue = {};
                    arvioinninKohdealue.$$uusiAuki = true;
                };
                $scope.lisaaArviointiasteikko = (arvioinninKohdealue) => {
                    arvioinninKohdealue.$$uusiArvioinninKohdealue.osaamistasonKriteerit = [];
                    const asteikkoId = arvioinninKohdealue.$$uusiArvioinninKohdealue._arviointiasteikko;
                    const osaamistasot = $scope.arviointiAsteikot[asteikkoId - 1].osaamistasot;
                    _(osaamistasot)
                        .each((osaamistaso) => {
                            arvioinninKohdealue.$$uusiArvioinninKohdealue.osaamistasonKriteerit.push({
                                _osaamistaso: osaamistaso.id,
                                kriteerit: []
                            });
                        })
                        .value();
                    arvioinninKohdealue.arvioinninKohteet.push(arvioinninKohdealue.$$uusiArvioinninKohdealue);
                    arvioinninKohdealue.$$uusiArvioinninKohdealue = {};
                    arvioinninKohdealue.$$uusiAuki = false;
                };
                $scope.lisaaKriteeri = (osaamistasonKriteeri) => {
                    osaamistasonKriteeri.kriteerit = osaamistasonKriteeri.kriteerit || [];
                    osaamistasonKriteeri.kriteerit.push({});
                };
                $scope.poistaArvioinninKohdealue = (kohdealue) => {
                    $scope.osa.tosa.omatutkinnonosa.arviointi.arvioinninKohdealueet
                        = _.without($scope.osa.tosa.omatutkinnonosa.arviointi.arvioinninKohdealueet, kohdealue);
                };
                $scope.poistaArvioinninKohde = (kohdealue, kohde) => {
                    kohdealue.arvioinninKohteet
                        = _.without(kohdealue.arvioinninKohteet, kohde);
                };
                $scope.poistaKriteeri = (osaamistasonKriteeri, kriteeri) => {
                    osaamistasonKriteeri.kriteerit
                        = _.without(osaamistasonKriteeri.kriteerit, kriteeri);
                };

                $scope.lisaaAmmattitaitovaatimus = () => {
                    $scope.osa.tosa.omatutkinnonosa = $scope.osa.tosa.omatutkinnonosa || {};
                    $scope.osa.tosa.omatutkinnonosa.ammattitaitovaatimuksetLista
                        = $scope.osa.tosa.omatutkinnonosa.ammattitaitovaatimuksetLista || [];
                    $scope.osa.tosa.omatutkinnonosa.ammattitaitovaatimuksetLista.push({});
                };
                $scope.lisaaVaatimuksenKohde = (ammattitaitovaatimus) => {
                    ammattitaitovaatimus.vaatimuksenKohteet = ammattitaitovaatimus.vaatimuksenKohteet || [];
                    ammattitaitovaatimus.vaatimuksenKohteet.push({
                    });
                };
                $scope.lisaaVaatimus = (vaatimuksenKohde) => {
                    vaatimuksenKohde.vaatimukset = vaatimuksenKohde.vaatimukset || [];
                    vaatimuksenKohde.vaatimukset.push({});
                };
                $scope.poistaVaatimuksenKohde = (ammattitaitovaatimus, vaatimuksenKohde) => {
                    ammattitaitovaatimus.vaatimuksenKohteet
                        = _.without(ammattitaitovaatimus.vaatimuksenKohteet, vaatimuksenKohde);
                };
                $scope.poistaAmmattitaitovaatimus = (ammattitaitovaatimus) => {
                    $scope.osa.tosa.omatutkinnonosa.ammattitaitovaatimuksetLista
                        = _.without($scope.osa.tosa.omatutkinnonosa.ammattitaitovaatimuksetLista, ammattitaitovaatimus);
                };
                $scope.poistaVaatimus = (vaatimuksenKohde, vaatimus) => {
                    vaatimuksenKohde.vaatimukset = _.without(vaatimuksenKohde.vaatimukset, vaatimus);
                }
            }
        },
        tutkinnonosaryhma: {
            controller: ($scope, osa) => {}
        },
        suorituspolku: {
            controller: ($q, $rootScope, $scope, $state, $stateParams, osa, peruste: REl, pSuoritustavat, pTosat, koodisto, paikallisetTutkinnonosatEP, koulutustoimija) => {
                const
                    suoritustapa = Perusteet.getSuoritustapa(pSuoritustavat),
                    tosat = _.indexBy(pTosat, "id"),
                    tosaViitteet: any = _(_.cloneDeep(Perusteet.getTosaViitteet(suoritustapa)))
                        .each(viite => viite.$$tosa = tosat[viite._tutkinnonOsa])
                        .indexBy("id")
                        .value(),
                    paikallisetKoodit = koulutustoimija.all("koodi"),
                    update = () => {
                        const spRivit: any = _.indexBy($scope.osa.suorituspolku.rivit, "rakennemoduuli");
                        Algoritmit.traverse($scope.perusteRakenne, "osat", (node, depth) => {
                            node.pakollinen = Suorituspolku.pakollinen(node);
                            node.$$poistettu = spRivit[node.tunniste] && spRivit[node.tunniste].piilotettu;
                        });
                        Suorituspolku.calculateRealAmount($scope.perusteRakenne, $scope.misc.tosat, spRivit);
                        $scope.misc.spRivit = spRivit;

                        const
                            uniikitKoodit = _($scope.osa.suorituspolku.rivit)
                                .map("koodit")
                                .flatten()
                                .filter((koodi: string) => !$scope.misc.koodinimet[koodi])
                                .uniq()
                                .value(),
                            pkoodit = _.filter(uniikitKoodit, (koodi: string) => _.startsWith(koodi, Koodisto.paikallinenPrefix)),
                            kkoodit = _.reject(uniikitKoodit, (koodi: string) => _.startsWith(koodi, Koodisto.paikallinenPrefix));

                        $q.all(_.map(kkoodit, koodi => koodisto.one("uri/" + koodi).get()))
                            .then(Koodisto.parseRawKoodisto)
                            .then(res => {
                                _.each(res, (koodi) => {
                                    $scope.misc.koodinimet[koodi.uri] = koodi;
                                });
                            });
                        $q.all(_.map(pkoodit, koodi => paikallisetKoodit.one(koodi).get()))
                            .then(res => {
                                _.each(res, (koodi) => {
                                    $scope.misc.koodinimet[koodi.route] = {
                                        nimi: "tutkinnon-osaa-ei-olemassa",
                                        uri: koodi.route,
                                        arvo: _.last(koodi.route.split("_"))
                                    }

                                    if (_.size(koodi) === 1) {
                                        $scope.misc.koodinimet[koodi.route].nimi = koodi[0].tekstiKappale.nimi;
                                        $scope.misc.koodinimet[koodi.route].url = $state.href("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", {
                                            opsId: koodi[0].owner.id,
                                            osaId: koodi[0].id
                                        });
                                    }
                                    else if (_.size(koodi) > 1) {
                                        const opskohtaiset = _.filter(koodi, (arvo: any) => arvo.owner.id == $stateParams.opsId);
                                        if (_.size(opskohtaiset) === 1) {
                                            $scope.misc.koodinimet[koodi.route].nimi = opskohtaiset[0].tekstiKappale.nimi;
                                            $scope.misc.koodinimet[koodi.route].url = $state.href("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", {
                                                opsId: opskohtaiset[0].owner.id,
                                                osaId: opskohtaiset[0].id
                                            });
                                        }
                                        else {
                                            $scope.misc.koodinimet[koodi.route].nimi = "koodilla-liian-monta-toteutusta";
                                            $scope.misc.koodinimet[koodi.route].nimet = _.map(koodi, "tekstiKappale.nimi");
                                            $scope.misc.koodinimet[koodi.route].rikki = true;
                                            $scope.misc.koodinimet[koodi.route].url = $state.href("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", {
                                                opsId: koodi[0].owner.id,
                                                osaId: koodi[0].id
                                            });
                                        }
                                    }
                                    else {
                                        $scope.misc.koodinimet[koodi.route].rikki = true;
                                    }
                                });
                            });
                    };

                _.merge($scope, {
                    collapsed_dirty: false,
                    perusteRakenne: _.cloneDeep(Perusteet.getRakenne(suoritustapa)),
                    misc: {
                        collapsed_removed: false,
                        root: $rootScope,
                        suoritustapa: suoritustapa,
                        koodinimet: {},
                        editNode: (node) => {
                            const spRivit = _.indexBy($scope.osa.suorituspolku.rivit, "rakennemoduuli");
                            SuoritustapaRyhmat.editoi(spRivit, node, tosaViitteet, koodisto, paikallisetTutkinnonosatEP)
                            .then(res => {
                                $scope.osa.suorituspolku.rivit = _.compact(_.values(res));
                                update();
                            });
                        },
                        tosat: tosaViitteet,
                        hasInput: false,
                        osa: $scope.osa,
                        toggle: model => {
                            model.$$collapsed = !model.$$collapsed;
                            $scope.collapsed_dirty = true;
                        },
                        siirry: obj => {
                            paikallisetKoodit.one(obj.$$tosa ? obj.$$tosa.koodiUri : obj).get()
                                .then(res => {
                                    if (_.isEmpty(res)) {
                                        NotifikaatioService.varoitus("tutkinnon-osalle-ei-toteutusta-koulutustoimijalla");
                                        return;
                                    }

                                    const goToSisalto = (osa) => $state.go("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", {
                                        opsId: osa.owner.id,
                                        osaId: osa.id
                                    });

                                    // TODO: Toteuta toteutuksen valitsin jos useampi toteutus
                                    const opskohtaiset = _.filter(res, (arvo: any) => arvo.owner.id == $stateParams.opsId);
                                    goToSisalto(_.size(opskohtaiset) > 0
                                        ? opskohtaiset[0]
                                        : res[0]);
                                });
                        },
                        poistoToggle: (node) => {
                            let rivi = _.find($scope.osa.suorituspolku.rivit, (rivi: any) => rivi.rakennemoduuli === node.tunniste);
                            if (!rivi) {
                                rivi = {
                                    rakennemoduuli: node.tunniste,
                                    piilotettu: false
                                };
                                $scope.osa.suorituspolku.rivit.push(rivi);
                            }
                            rivi.piilotettu = !rivi.piilotettu;
                            update();
                        }
                    },
                    togglePoistetut: () => $scope.misc.collapsed_removed = !$scope.misc.collapsed_removed,
                    toggleAll: () => {
                        Algoritmit.traverse($scope.perusteRakenne, "osat", (node, depth) =>
                                            node.$$collapsed = $scope.collapsed_dirty ? depth > 0 : false);
                        $scope.collapsed_dirty = !$scope.collapsed_dirty;
                    },
                    suodata: (input) => {
                        $scope.misc.hasInput = !_.isEmpty(input);
                        if ($scope.misc.hasInput) {
                            Algoritmit.traverse($scope.perusteRakenne, "osat", (node) => {
                                node.$$haettu = Algoritmit.match(input, node._tutkinnonOsaViite
                                    ? tosaViitteet[node._tutkinnonOsaViite].$$tosa.nimi
                                    : node.nimi);
                                if (node.$$haettu) {
                                    Algoritmit.traverseUp(node, (pnode) => pnode.$$haettu = true);
                                }
                            });
                        }
                        else {
                            Algoritmit.traverse($scope.perusteRakenne, "osat", (node) => node.$$haettu = false);
                        }
                    }
                });

                { // Initialize
                    update();
                    Algoritmit.traverse($scope.perusteRakenne, "osat", (node, depth) => {
                        node.$$collapsed = depth > 0;
                        node.pakollinen = Suorituspolku.pakollinen(node);
                    });
                    $scope.toggleAll();
                }
            }
        },
        suorituspolut: {
            controller: ($scope, osa) => {}
        },
        kommentointi: {
            controller: ($scope, kommentit, osa, Varmistusdialogi, orgoikeudet, kayttaja, $stateParams) => {
                $scope.$$kommenttiMaxLength = {
                    maara: 1024
                };

                const rikastaKommentti = (kommentti) => {
                    // Sisällön kloonaus peruutusta varten
                    kommentti.$$sisaltoKlooni = kommentti.sisalto;
                    kommentti.$$vastaus = {
                        sisalto: "",
                        $$sisaltoKlooni: ""
                    };

                    // Oikeudet
                    kommentti.$$muokkausSallittu = kommentti.luoja === kayttaja.oidHenkilo;
                    kommentti.$$poistaSallittu = Oikeudet.onVahintaan("hallinta",
                            Oikeudet.opsOikeus($stateParams.opsId)) || kommentti.luoja === kayttaja.oidHenkilo;
                };

                $scope.kommentit = _(kommentit)
                    .forEach(k => rikastaKommentti(k))
                    .filter(kommentti => kommentti.parentId === 0)
                    .map((kommentti: any) => {
                        // Aseta lapset
                        kommentti.$$lapset = _.filter(kommentit, (lapsi: any) => {
                            if (lapsi.parentId === kommentti.id) {
                                return lapsi;
                            }
                        });

                        return kommentti;
                    })
                    .value();

                $scope.avaaTekstikentta = (kommentti) => {
                    kommentti.$$sisaltoKlooni = kommentti.sisalto;
                    kommentti.$$isMuokkaus = true;
                };

                $scope.vastaaKommentti = (kommentti, parentId) => {
                    kommentti.parentId = parentId;
                    kommentti.sisalto = kommentti.$$sisaltoKlooni;
                    osa.all("kommentit").post(kommentti)
                        .then((uusi) => {
                            rikastaKommentti(uusi);
                            uusi.$$lapset = [];
                            if (parentId === 0) {
                                $scope.kommentit.unshift(uusi);
                                kommentti.$$isVastaus = false;
                            } else {
                                let parentKommentti: any = _($scope.kommentit).find({ id: uusi.parentId });
                                parentKommentti.$$lapset.unshift(uusi);
                                parentKommentti.$$isVastaus = false;
                            }
                        });
                };

                $scope.lisaaKommentti = (kommentti) => {
                    $scope.vastaaKommentti(kommentti, 0);
                };

                $scope.muokkaaKommentti = (kommentti) => {
                    kommentti.sisalto = kommentti.$$sisaltoKlooni;
                    kommentti.save()
                        .then((uusi) => _.merge(kommentti, uusi.plain()));
                    kommentti.$$isMuokkaus = false;
                };

                $scope.poistaKommentti = (kommentti) => {
                    Varmistusdialogi.dialogi({
                        otsikko: "vahvista-poisto",
                        teksti: "poistetaanko-kommentti",
                        primaryBtn: "poista",
                        successCb: () => {
                            kommentti.remove()
                                .then(() => {
                                    kommentti.poistettu = true;
                                    kommentti.sisalto = "";
                                });
                        }
                    })();
                };
            }
        }
    }
}));
