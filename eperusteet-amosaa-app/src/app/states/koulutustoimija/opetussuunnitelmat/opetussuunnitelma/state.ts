angular.module("app")
.config($stateProvider => $stateProvider.state("root.etusivu.koulutustoimija.opetussuunnitelmat.opetussuunnitelma", {
    url: "/:id",
    resolve: {
        opetussuunnitelma: ($stateParams, opetussuunnitelmat) => Fake.Opetussuunnitelma($stateParams.id),
        peruste: (opetussuunnitelma, perusteet) => perusteet.one(opetussuunnitelma.pohja.toString()).get()
    },
    controller: ($scope, opetussuunnitelma, peruste) => {
        $scope.peruste = peruste;
        $scope.opetussuunnitelma = opetussuunnitelma;
    }
}));
