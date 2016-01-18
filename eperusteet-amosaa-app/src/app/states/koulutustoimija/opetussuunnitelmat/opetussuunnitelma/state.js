angular.module("app")
    .config(function ($stateProvider) { return $stateProvider.state("root.etusivu.koulutustoimija.opetussuunnitelmat.opetussuunnitelma", {
    url: "/:id",
    resolve: {
        opetussuunnitelma: function ($stateParams, opetussuunnitelmat) { return Fake.Opetussuunnitelma($stateParams.id); },
        peruste: function (opetussuunnitelma, perusteet) { return perusteet.one(opetussuunnitelma.pohja.toString()).get(); }
    },
    controller: function ($scope, opetussuunnitelma, peruste) {
        $scope.peruste = peruste;
        $scope.opetussuunnitelma = opetussuunnitelma;
    }
}); });
//# sourceMappingURL=state.js.map