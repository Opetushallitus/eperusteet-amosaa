/// Composes multiple modals into a chain
namespace Modals {
    let i;
    export const init = $injector => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    interface ModalStep {
        ctx: any;
    }

    export const composeModals = (...steps: ModalStep[]) => {};
}

angular.module("app").run(Modals.init);
