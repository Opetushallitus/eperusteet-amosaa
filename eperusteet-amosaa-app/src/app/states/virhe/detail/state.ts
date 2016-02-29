angular.module("app")
    .config($stateProvider => $stateProvider
        .state("root.virhe.detail", {
            url: "",
            views: {
                "": {
                    controller: () => {}
                }
            }
        }));
