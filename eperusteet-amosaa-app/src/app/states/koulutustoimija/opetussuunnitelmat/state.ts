angular.module("app")
.config($stateProvider => $stateProvider
.state("root.etusivu.koulutustoimija.opetussuunnitelmat", {
    url: "/opetussuunnitelmat",
    abstract: true,
    resolve: {
        opetussuunnitelmat: () => Fake.Opetussuunnitelmat(),
        perusteet: Eperusteet => Eperusteet.one("perusteet").get()
    }
})
.state("root.etusivu.koulutustoimija.opetussuunnitelmat.index", {
    url: "",
    resolve: { },
    controller: ($scope, $state, opetussuunnitelmat) => {
        const urlGenerator = (id) => $state.href("^.opetussuunnitelma", { id: id });
        $scope.opetussuunnitelmat = Opetussuunnitelmat.parsiPerustiedot(opetussuunnitelmat, urlGenerator);
    }
}));
