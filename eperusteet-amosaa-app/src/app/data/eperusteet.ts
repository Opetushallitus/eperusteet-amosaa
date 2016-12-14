angular.module("app")
.factory("Eperusteet", Restangular => Restangular.withConfig(config => {
    config.setBaseUrl("/eperusteet-service/api");
}));
