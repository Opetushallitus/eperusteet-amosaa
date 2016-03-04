angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.yhteiset.oikeudet", {
    url: "/oikeudet",
    resolve: {
        oikeudet: (yhteiset) => yhteiset.all("oikeudet").getList(),
    },
    views: {
        "": {
            controller: ($scope, oikeudet, kayttaja) => {
                $scope.vaihtoehdot = Kayttaja.oikeusVaihtoehdot();

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
