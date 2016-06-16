namespace ModalValidointi {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    const validointiController = ($scope, $state, $stateParams, $uibModalInstance, validointi) => {
        _.merge($scope, {
            virheet: validointi.virheet,
            varoitukset: validointi.varoitukset,
            huomiot: validointi.huomiot,
            eiVirheita:
                _.isEmpty(validointi.virheet) &&
                _.isEmpty(validointi.varoitukset) &&
                _.isEmpty(validointi.huomiot),
            ok: $uibModalInstance.dismiss
        });
    };

    export const validoi = (ops) => ops.one("validoi").get()
        .then(res => {
            return i.$uibModal.open({
                templateUrl: "modals/validointi/validointi.jade",
                controller: validointiController,
                size: "lg",
                resolve: { validointi: _.constant(res) }
            })
            .result;
        });
};

angular.module("app")
.run(ModalValidointi.init);
