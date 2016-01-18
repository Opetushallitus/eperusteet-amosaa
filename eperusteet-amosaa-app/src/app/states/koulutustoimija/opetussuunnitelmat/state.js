angular.module("app")
    .config(function ($stateProvider) { return $stateProvider
    .state("root.etusivu.koulutustoimija.opetussuunnitelmat", {
    url: "/opetussuunnitelmat",
    abstract: true,
    resolve: {
        opetussuunnitelmat: function () { return Fake.Opetussuunnitelmat(); },
        perusteet: function (Eperusteet) { return Eperusteet.one("perusteet").get(); }
    }
})
    .state("root.etusivu.koulutustoimija.opetussuunnitelmat.index", {
    url: "",
    resolve: {},
    controller: function ($scope, $state, opetussuunnitelmat) {
        var urlGenerator = function (id) { return $state.href("^.opetussuunnitelma", { id: id }); };
        $scope.opetussuunnitelmat = Opetussuunnitelmat.parsiPerustiedot(opetussuunnitelmat, urlGenerator);
    }
}); });
//# sourceMappingURL=state.js.map