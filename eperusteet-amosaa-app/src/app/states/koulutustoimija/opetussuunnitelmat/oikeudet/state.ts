angular.module("app").config($stateProvider =>
    $stateProvider.state("root.koulutustoimija.opetussuunnitelmat.oikeudet", {
        url: "/oikeudet",
        resolve: {
            ktKayttajat: koulutustoimija => koulutustoimija.all("kaikkiKayttajat").getList(),
            ystavat: koulutustoimija => koulutustoimija.all("ystavat").getList(),
            oikeudet: ops => ops.all("oikeudet").getList()
        },
        views: {
            "": {
                controller: ($scope, ops, oikeudet, kayttaja, ktKayttajat, nimiLataaja, koulutustoimija, ystavat) => {
                    const kayttajatByKt = _.groupBy(ktKayttajat, "koulutustoimija");

                    $scope.koulutustoimijat = _.groupBy(ystavat, "id");
                    $scope.koulutustoimijat[koulutustoimija.id] = [koulutustoimija];

                    $scope.kayttajat = ktKayttajat;
                    $scope.vaihtoehdot = Kayttaja.oikeusVaihtoehdot();
                    $scope.suodata = item => _.matchStrings($scope.search, item.$$nimi);
                    $scope.me = kayttaja;
                    $scope.oikeudet = _.indexBy(oikeudet, "_kayttaja");
                    $scope.vainOikeudelliset = true;

                    $scope.oikeus = kayttaja => {
                        if (kayttaja.oid === $scope.me.oidHenkilo) {
                            const kto = Oikeudet.ktOikeus(koulutustoimija),
                                opso = Oikeudet.opsOikeus(ops);
                            return kto && Oikeudet.onVahintaan(opso, kto) ? kto : opso;
                        } else {
                            return $scope.oikeudet[kayttaja.id] ? $scope.oikeudet[kayttaja.id].oikeus : "estetty";
                        }
                    };

                    _.each($scope.kayttajat, kayttaja => {

                        if (kayttaja.sukunimi) {
                            kayttaja.$$nimi = Kayttajatiedot.parsiEsitysnimi(kayttaja);
                        } else {
                            nimiLataaja(kayttaja.oid)
                                .then(res => {
                                    kayttaja.$$nimi = res;
                                })
                                .catch(() => (kayttaja.$$nimi = kayttaja.oid))
                        }

                        $scope.oikeudet[kayttaja.id] = $scope.oikeudet[kayttaja.id] || { oikeus: "estetty" };
                    });

                    $scope.valitse = async (oikeus: string, valittuKayttaja) => {
                        const vaihtoehto = _.clone($scope.oikeudet[valittuKayttaja.id]);
                        vaihtoehto.oikeus = oikeus;

                        if (!valittuKayttaja.id) {
                            try {
                                const uusiKayttaja = await kayttaja.one(valittuKayttaja.oid + '/' + valittuKayttaja.koulutustoimija).customPOST();
                                valittuKayttaja.id = uusiKayttaja.id;
                                $scope.oikeudet[valittuKayttaja.id] = { oikeus: "estetty" };
                            } catch (error) {
                                NotifikaatioService.serverCb;
                            }
                        }    

                        ops
                            .one("oikeudet/" + valittuKayttaja.id)
                            .customPOST(vaihtoehto)
                            .then(() => {
                                $scope.oikeudet[valittuKayttaja.id].oikeus = oikeus;
                                NotifikaatioService.onnistui("oikeus-paivitetty");
                            })
                            .catch(NotifikaatioService.serverCb);
                    };
                }
            }
        }
    })
);
