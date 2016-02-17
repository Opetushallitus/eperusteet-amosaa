angular.module("app")
    .config($stateProvider => $stateProvider
        .state("root.virhe", {
            url: "/virhe",
            controller: () => {}
        }));
