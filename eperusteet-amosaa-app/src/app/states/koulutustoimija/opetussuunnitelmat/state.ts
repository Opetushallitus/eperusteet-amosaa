angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat", {
    url: "/ops/:opsId",
    resolve: {
        ops: ($stateParams, koulutustoimija) => koulutustoimija.one("opetussuunnitelmat", $stateParams.opsId).get(),
        oikeustarkastelunKonteksti: (ops) => Oikeudet.asetaOpetussuunnitelma(ops),
        otsikot: (ops) => ops.all("otsikot").getList(),
        sisaltoRoot: (otsikot) => Tekstikappaleet.root(otsikot),
        tekstit: (ops, sisaltoRoot) => ops.one("tekstit", sisaltoRoot.id),
        peruste: ($q, Api, ops) => (ops.tyyppi === "ops" ? Api.all("perusteet").get(ops.peruste.id) : $q.when({}))
    },
    onEnter: (ops) => Murupolku.register("root.koulutustoimija.opetussuunnitelmat", ops.nimi),
    views: {
        "": {
            controller: ($scope, $location, $stateParams, koulutustoimija, ops) => {
                $scope.koulutustoimija = koulutustoimija;
                $scope.ops = ops;
                $scope.validoi = () => ModalValidointi.validoi(ops);
                $scope.muutaTila = () => TilanvaihtoModal.vaihdaTila(ops)
                        .then(paivitetty => _.merge(ops, paivitetty.plain()));

                const selectEsitkatseluURL = () => {
                    let currentHost= $location.host() + "";
                    if (/localhost/.test(currentHost)) return  'http://localhost:9020/#/';
                    else if (/testi|itest/.test(currentHost)) return 'https://testi-eperusteet.opintopolku.fi/#/';
                    else return 'https://eperusteet.opintopolku.fi/#/';
                };

                $scope.createUrl = model => selectEsitkatseluURL() + $stateParams.lang + '/amops/' + model.id;
            }
        }
    }
}));
