angular.module("app")
  .config($stateProvider => $stateProvider
    .state("root.sandbox.bootstrap", {
      url: "/bootstrap",
      resolve: {
      },
      views: {
        "": {
          controller: () => {
          }
        }
      }
    }));
