angular.module("app")
    .config($stateProvider => $stateProvider
        .state("root.virhe", {
            abstract: true,
            url: "/virhe",
            controller: () => {}
        }));
