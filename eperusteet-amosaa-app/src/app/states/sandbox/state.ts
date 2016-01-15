angular.module("app")
.config($stateProvider => $stateProvider
.state("root.sandbox", {
    url: "/sandbox",
    resolve: {
    },
    views: {
        "": {
            controller: ($scope) => {
            }
        },
        "editointikontrollit_local": {
            controller: ($scope, $q, $timeout) => {
                let ek = EditointikontrollitService.create({
                    start: () => $q((resolve, reject) => $timeout(() => {
                        console.log("starting editing");
                        $scope.isEditing = true;
                        resolve();
                    }, 1000)),
                    save: () => $q((resolve, reject) => $timeout(() => {
                        console.log("saving");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000)),
                    cancel: () => $q((resolve, reject) => $timeout(() => {
                        console.log("cancelling editing");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000)),
                });

                $scope.edit = ek.start;
                $scope.save = ek.save;
                $scope.cancel = ek.cancel;
            }
        },
        "editointikontrollit_global": {
            controller: ($scope, $q, $timeout) => {
                let ek = EditointikontrollitService.create({
                    start: () => $q((resolve, reject) => $timeout(() => {
                        console.log("starting editing");
                        $scope.isEditing = true;
                        resolve();
                    }, 1000)),
                    save: () => $q((resolve, reject) => $timeout(() => {
                        console.log("saving");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000)),
                    cancel: () => $q((resolve, reject) => $timeout(() => {
                        console.log("cancelling editing");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000)),
                });

                $scope.edit = ek.start;
                $scope.save = ek.save;
                $scope.cancel = ek.cancel;
            }
        }
    }
}));
