angular.module("app")
    .factory("Api", function (Restangular) { return Restangular.withConfig(function (config) {
    config.setBaseUrl("/eperusteet-amosaa-service/api");
}); });
//# sourceMappingURL=amosaa.js.map