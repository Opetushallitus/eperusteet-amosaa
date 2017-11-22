angular.module("app").config($stateProvider =>
    $stateProvider.state("root.koulutustoimija.opetussuunnitelmat.oikeudet", {
        url: "/oikeudet",
        resolve: {
            ktKayttajat: koulutustoimija => koulutustoimija.all("kaikkiKayttajat").getList(),
            ystavat: koulutustoimija => koulutustoimija.all("ystavat").getList(),
            rekisteroimattomat: koulutustoimija => koulutustoimija.all("rekisteroimattomat").getList(),
            oikeudet: ops => ops.all("oikeudet").getList()
        },
        views: {
            "": {
                controller: ($scope, ops, oikeudet, kayttaja, ktKayttajat, nimiLataaja, koulutustoimija, ystavat,
                             rekisteroimattomat) => {
                    const kayttajatByKt = _.groupBy(ktKayttajat, "koulutustoimija");

                    $scope.koulutustoimijat = _.groupBy(ystavat, "id");
                    $scope.koulutustoimijat[koulutustoimija.id] = [koulutustoimija];
                    $scope.kayttajat = ktKayttajat;
                    $scope.rekisteroimattomat = rekisteroimattomat;
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
                            return $scope.oikeudet[kayttaja.id] ? $scope.oikeudet[kayttaja.id].oikeus : "luku";
                        }
                    };

                    _.each($scope.kayttajat, kayttaja => {
                        _.remove($scope.rekisteroimattomat,
                            (rekisteroimaton: any) => rekisteroimaton.oidHenkilo === kayttaja.oid);
                        nimiLataaja(kayttaja.oid)
                            .then(res => {
                                kayttaja.$$nimi = res;
                                $scope.oikeudet[kayttaja.id] = $scope.oikeudet[kayttaja.id] || { oikeus: "luku" };
                            })
                            .catch(() => (kayttaja.$$nimi = kayttaja.oid));
                    });

                    _.each($scope.rekisteroimattomat, rekisteroimaton =>rekisteroimaton
                        .$$nimi = Kayttajatiedot.parsiEsitysnimi(rekisteroimaton));

                    $scope.valitse = (oikeus: string, kayttaja) => {
                        const vaihtoehto = _.clone($scope.oikeudet[kayttaja.id]);
                        vaihtoehto.oikeus = oikeus;
                        ops
                            .one("oikeudet/" + kayttaja.id)
                            .customPOST(vaihtoehto)
                            .then(() => {
                                $scope.oikeudet[kayttaja.id].oikeus = oikeus;
                                NotifikaatioService.onnistui("oikeus-paivitetty");
                            })
                            .catch(NotifikaatioService.serverCb);
                    };
                }
            }
        }
    })
);
