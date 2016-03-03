namespace AmosaaApi {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["Api"]);
    };
};

angular.module("app")
.run(AmosaaApi.init)
.factory("Api", Restangular => Restangular.withConfig(config => {
    config.setBaseUrl("/eperusteet-amosaa-service/api");
}));
