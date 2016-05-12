angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat.oikeudet", {
    url: "/oikeudet",
    resolve: {
        ktKayttajat: (koulutustoimija) => koulutustoimija.all("kayttajat").getList(),
        oikeudet: (ops) => ops.all("oikeudet").getList(),
    },
    views: {
        "": {
            controller: ($scope, ops, oikeudet, kayttaja, ktKayttajat, kayttajanTieto) => {
                $scope.vaihtoehdot = Kayttaja.oikeusVaihtoehdot();
                $scope.suodata = (item) => _.matchStrings($scope.search, item.$$nimi);
                $scope.kayttajat = ktKayttajat;
                $scope.oikeudet = _.indexBy(oikeudet, "_kayttaja");

                $scope.oikeus = (kayttaja) => $scope.oikeudet[kayttaja.id] ? $scope.oikeudet[kayttaja.id].oikeus : 'luku';

                _.each($scope.kayttajat, (kayttaja) => kayttajanTieto(kayttaja.oid)
                    .then(res => {
                        kayttaja.$$nimi = Kayttajatiedot.parsiEsitysnimi(res);
                        $scope.oikeudet[kayttaja.id] = $scope.oikeudet[kayttaja.id] || {
                            oikeus: "luku"
                        };
                    })
                    .catch(() => kayttaja.$$nimi = kayttaja.oid));

                $scope.valitse = (oikeus: string, kayttaja) => {
                    const vaihtoehto = _.clone($scope.oikeudet[kayttaja.id]);
                    vaihtoehto.oikeus = oikeus;
                    ops.one("oikeudet/" + kayttaja.id).customPOST(vaihtoehto)
                        .then(() => {
                            $scope.oikeudet[kayttaja.id].oikeus = oikeus;
                            NotifikaatioService.onnistui("oikeus-paivitetty");
                        })
                        .catch(NotifikaatioService.serverCb);
                };

            }
        }
    }
}));
