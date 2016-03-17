namespace ModalConfirm {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    let modalOptions = {
        cancelBtnText: "Peruuta",
        actionBtnText: "OK",
        headText: "Vahvista poisto",
        bodyText: "Poistetaanko",
        name: ''
    };

    export const generalConfirm = (options = {}, data) => i.$uibModal.open({
        templateUrl: "modals/confirm/confirm.jade",
        size: 'sm',
        controller: ($uibModalInstance, $scope) => {

            $scope.opts = _.merge(modalOptions, options);

            $scope.ok = () => {
                if (data !== null && data !== undefined) {
                    $uibModalInstance.close(data);
                } else {
                    $uibModalInstance.close();
                }
            };

            $scope.cancel = $uibModalInstance.close;
        }
    }).result;

}

angular.module("app")
.run(ModalConfirm.init);
