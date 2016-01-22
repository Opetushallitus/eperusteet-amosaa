const rootUrl = "http://jsonplaceholder.typicode.com";

angular.module("app")
    .factory("TestApi", Restangular => Restangular.withConfig(config => {
        let response = config.setBaseUrl(rootUrl);
        return response;
    }));
