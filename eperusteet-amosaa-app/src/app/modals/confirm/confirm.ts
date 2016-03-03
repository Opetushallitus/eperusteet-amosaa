namespace ModalConfirm {
    let i;
    export const init = () => {
        i = InjectorService.inject([
            "$rootScope",
            "$uibModal",
            "$q"]);
    };

}

angular.module("app")
    .run(ModalConfirm.init);
