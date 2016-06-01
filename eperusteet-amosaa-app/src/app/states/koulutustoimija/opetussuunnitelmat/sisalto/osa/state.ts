namespace SuoritustapaRyhmat {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    export const editoi = (spRivit, node, tutkinnonosat) => i.$uibModal.open({
            resolve: { },
            templateUrl: "modals/suoritustaparyhma.jade",
            controller: ($scope, $state, $uibModalInstance, $rootScope) => {
                $scope.restoreModule = (m) => {
                    $scope.spRivit[m.tunniste] = undefined;
                };

                $scope.spRivit = spRivit;
                $scope.tosat = tutkinnonosat;
                $scope.node = node;
                $scope.editable = _.find(spRivit, (rivi: any) => rivi.rakennemoduuli === node.tunniste) || {
                    rakennemoduuli: node.tunniste
                };

                $scope.ok = () => {
                    $rootScope.$broadcast("notifyCKEditor");
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
            controller: ($state, $stateParams, $location, $scope, $rootScope, $document, $timeout,
                         osa, nimiLataaja, Varmistusdialogi, historia, versioId, versio, pTosa) => {
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
            controller: ($scope, peruste, arviointiAsteikot) => {
                const koodit = _([])
                    .concat($scope.pTosa ? _.map($scope.pTosa.osaAlueet, (oa: any) => ({
                        nimi: oa.nimi,
                        arvo: oa.koodiArvo,
                        uri: oa.koodiUri
                    })) : [])
                    .concat(peruste.osaamisalat)
                    .indexBy("uri")
                    .value();

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

                $scope.addKoodi = (toteutus) => KoodistoModal.koodi(koodit, toteutus.koodit)
                    .then(res => toteutus.koodit = res);

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
            controller: ($rootScope, $scope, osa, peruste: REl, pSuoritustavat, pTosat) => {
                const suoritustapa = Perusteet.getSuoritustapa(pSuoritustavat);
                const tosat = _.indexBy(pTosat, "id");
                const tosaViitteet: any = _(_.cloneDeep(Perusteet.getTosaViitteet(suoritustapa)))
                    .each(viite => viite.$$tosa = tosat[viite._tutkinnonOsa])
                    .indexBy("id")
                    .value();

                const update = () => {
                    const spRivit = _.indexBy($scope.osa.suorituspolku.rivit, "rakennemoduuli");
                    Algoritmit.traverse($scope.perusteRakenne, "osat", (node, depth) => {
                        node.pakollinen = Suorituspolku.pakollinen(node);
                        node.$$poistettu = spRivit[node.tunniste] && spRivit[node.tunniste].piilotettu;
                    });
                    Suorituspolku.calculateRealAmount($scope.perusteRakenne, $scope.misc.tosat, spRivit);
                };

                $scope.collapsed_dirty = false;
                $scope.perusteRakenne = _.cloneDeep(Perusteet.getRakenne(suoritustapa));
                $scope.misc = {
                    collapsed_removed: false,
                    root: $rootScope,
                    suoritustapa: suoritustapa,
                    editNode: (node) => {
                        const spRivit = _.indexBy($scope.osa.suorituspolku.rivit, "rakennemoduuli");
                        SuoritustapaRyhmat.editoi(spRivit, node, tosaViitteet)
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
                    poistoToggle: (node) => {
                        let rivi = _.find($scope.osa.suorituspolku.rivit, (rivi: any) => rivi.rakennemoduuli === node.tunniste);
                        if (!rivi) {
                            rivi = {
                                rakennemoduuli: node.tunniste,
                                piilotettu: false
                            }
                            $scope.osa.suorituspolku.rivit.push(rivi);
                        }
                        rivi.piilotettu = !rivi.piilotettu;
                        update();
                    }
                };

                { // Initialize
                    update();
                    Algoritmit.traverse($scope.perusteRakenne, "osat", (node, depth) => {
                        node.$$collapsed = depth > 0;
                        node.pakollinen = Suorituspolku.pakollinen(node);
                    });
                }

                $scope.togglePoistetut = () => {
                    $scope.misc.collapsed_removed = !$scope.misc.collapsed_removed;
                };

                $scope.toggleAll = () => {
                    Algoritmit.traverse($scope.perusteRakenne, "osat", (node, depth) => {
                        node.$$collapsed = $scope.collapsed_dirty ? depth > 0 : false;
                    });
                    $scope.collapsed_dirty = !$scope.collapsed_dirty;
                };

                $scope.suodata = (input) => {
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
                };
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
