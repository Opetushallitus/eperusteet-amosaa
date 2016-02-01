angular.module("app")
  .config($stateProvider => $stateProvider
    .state("root.koulutustoimija.yhteiset", {
      url: "/yhteiset",
      resolve: {
      },
      views: {
        "": {
          controller: ($scope) => {
            console.log("test");
            console.log("test");
          }
        }
      }
    }));
