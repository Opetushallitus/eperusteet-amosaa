angular.module("app")
    .factory("Eperusteet", function (Restangular) { return Restangular.withConfig(function (config) {
    config.setBaseUrl("/eperusteet-service/api");
}); });
//# sourceMappingURL=eperusteet.js.map