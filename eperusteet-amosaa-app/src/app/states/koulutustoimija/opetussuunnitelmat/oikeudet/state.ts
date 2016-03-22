angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat.oikeudet", {
    url: "/oikeudet",
    resolve: {
        oikeudet: (ops) => ops.all("oikeudet").getList(),
    },
    views: {
        "": {
            controller: ($scope, oikeudet, kayttaja) => {
                $scope.vaihtoehdot = Kayttaja.oikeusVaihtoehdot();
                $scope.suodata = (item) => _.matchStrings($scope.search, item.$$kayttajanimi);

                $scope.oikeudet = _.map(oikeudet, (oikeus: any) => {
                    return _.merge(oikeus, {
                        $$kayttajanimi: oikeus.kayttajatieto.kutsumanimi
                            ? oikeus.kayttajatieto.kutsumanimi + " " + oikeus.kayttajatieto.sukunimi
                            : oikeus.kayttajatieto.oidHenkilo
                    });
                });

                $scope.valitse = (oikeus: string, vaihtoehto) => {
                    vaihtoehto.oikeus = oikeus;
                    vaihtoehto = vaihtoehto.save()
                        .then(() => NotifikaatioService.onnistui("oikeus-paivitetty"))
                        .catch(NotifikaatioService.serverCb);
                };

            }
        }
    }
}));
