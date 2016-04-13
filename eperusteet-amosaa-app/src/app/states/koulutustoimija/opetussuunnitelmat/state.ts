angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat", {
    url: "/ops/:opsId",
    resolve: {
        ops: ($stateParams, koulutustoimija) => koulutustoimija.one("opetussuunnitelmat", $stateParams.opsId).get(),
        otsikot: (ops) => ops.all("otsikot").getList(),
        sisaltoRoot: (otsikot) => Tekstikappaleet.root(otsikot),
        tekstit: (ops, sisaltoRoot) => ops.one("tekstit", sisaltoRoot.id),
    },
    onEnter: (ops) => Murupolku.register("root.koulutustoimija.opetussuunnitelmat", ops.nimi),
    views: {
        "": {
            controller: ($scope, ops) => {
                $scope.ops = ops;
                $scope.muutaTila = () => TilanvaihtoModal.vaihdaTila(ops)
                        .then(paivitetty => _.merge(ops, paivitetty.plain()));
            }
        }
    }
}));
