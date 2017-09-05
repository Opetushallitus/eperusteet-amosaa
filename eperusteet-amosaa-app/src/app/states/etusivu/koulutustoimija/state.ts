angular.module("app").config($stateProvider =>
    $stateProvider.state("@StateName", {
        url: "/koulutustoimija/:ktId",
        resolve: {
            // opetussuunnitelmat: () => Fake.Opetussuunnitelmat(),
            // perusteet: eperusteet => eperusteet.one("perusteet").get()
        },
        controller: $scope => {
            $scope.world = 5;
        }
    })
);
