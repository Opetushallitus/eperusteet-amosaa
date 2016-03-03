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
                $scope.oikeudet = oikeudet;
                _.each($scope.oikeudet, (oikeus) => {
                    kayttaja.one(oikeus._kayttaja, "nimi").get().then(res => {
                        oikeus.$$kayttajanimi = res.kutsumanimi
                            ? res.kutsumanimi + " " + res.sukunimi
                            : res.oidHenkilo;
                    });
                });
            }
        }
    }
}));
