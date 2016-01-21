angular.module("app")
.config($stateProvider => $stateProvider
.state("root.sandbox", {
    url: "/sandbox",
    resolve: {
    },
    views: {
        "": {
            controller: () => {
            }
        },
        "loading_bar": {
            controller: function ($scope, cfpLoadingBar, TestApi) {
                TestApi.all("users").getList()
                  .then((res) => {
                      $scope.users = res;
                  });
                $scope.startLoading = () => {
                    cfpLoadingBar.start();
                };
                $scope.jumpAhead = () => {
                    cfpLoadingBar.inc(30);
                };
                $scope.stopLoading = () => {
                    cfpLoadingBar.complete();
                };
            }
        },
        "kaanna": {
            controller: ($scope) => {
                $scope.obj = {
                    fi: "Hei",
                    en: "Hello"
                };
                $scope.objFi = KaannaService.kaanna($scope.obj);
            }
        },
        "editointikontrollit_local": {
            controller: ($scope, $q, $timeout) => {
                $scope.edit = EditointikontrollitService.createLocal({
                    start: () => $q((resolve, reject) => $timeout(() => {
                        $scope.isEditing = true;
                        resolve();
                    }, 1000)),
                    save: () => $q((resolve, reject) => $timeout(() => {
                        console.log("save local");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000)),
                    cancel: () => $q((resolve, reject) => $timeout(() => {
                        console.log("cancel local");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000)),
                });

                $scope.cancel = EditointikontrollitService.cancel;
                $scope.save = EditointikontrollitService.save;
            }
        },
        "editointikontrollit_global": {
            controller: ($scope, $q, $timeout) => {
                $scope.edit = EditointikontrollitService.create({
                    start: () => $q((resolve, reject) => $timeout(resolve, 1000)),
                    save: () => $q((resolve, reject) => $timeout(resolve, 1000)),
                    cancel: () => $q((resolve, reject) => $timeout(resolve, 1000)),
                });
            }
        }
    }
}));
