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
            $scope.revisions = _.take(revisions, 10);
            $scope.ok = $uibModalInstance.close;
        }
    }).result;

};

angular.module("app")
.run(ModalRevisions.init);
