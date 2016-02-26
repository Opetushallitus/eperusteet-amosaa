namespace ModalRevisions {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    export const viewRevisions = (revisions) => i.$uibModal.open({
        resolve: {
            revisions: () => revisions
        },
        templateUrl: "modals/revisions/revisions.jade",
        controller: ($uibModalInstance, $scope, $state, revisions) => {
            $scope.update = (search) => {
                $scope.frevisions = _.filter(revisions, (rev: any) => _.matchStrings($scope.search, rev.kommentti));
                $scope.pagination = Pagination.paginate($scope.frevisions, 8);
                $scope.selectPage();
            };

            $scope.selectPage = (page = 0) => {
                $scope.pagination.current = page;
                $scope.revisions = Pagination.selectItems($scope.frevisions, $scope.pagination);
            };

            $scope.toggle = (revision) => revision.$$show = !revision.$$show;
            $scope.ok = $uibModalInstance.close;

            $scope.update();
        }
    }).result;

};

angular.module("app")
.run(ModalRevisions.init);
