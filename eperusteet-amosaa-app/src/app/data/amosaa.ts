angular.module("app")
.factory("Api", Restangular => Restangular.withConfig(config => {
    config.setBaseUrl("/eperusteet-amosaa-service/api");
}));
