namespace Yhteistyo {
    let i;
    export const init = $injector => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    export const modal = koulutustoimija =>
        i.$uibModal.open({
            templateUrl: "modals/yhteistyo/yhteistyo.jade",
            // size: 'sm',
            resolve: {
                yhteistyo: () => koulutustoimija.all("yhteistyo").getList()
            },
            controller: ($uibModalInstance, $scope, yhteistyo) => {
                $scope.koulutustoimija = koulutustoimija;
                $scope.organisaatiot = Algoritmit.doSortByNimi(yhteistyo);
                $scope.ok = org => $uibModalInstance.close(org);
                $scope.cancel = $uibModalInstance.dismiss;

                Pagination.addPagination(
                    $scope,
                    (search: string, org: any): boolean => {
                        return !search || _.isEmpty(search) || Algoritmit.match(search, org.nimi);
                    },
                    "organisaatiot",
                    "org"
                );
            }
        }).result;
}

angular.module("app").run(Yhteistyo.init);
