angular.module("app")
    .config(function ($stateProvider) { return $stateProvider
    .state("root.sandbox", {
    url: "/sandbox",
    resolve: {},
    views: {
        "": {
            controller: function ($scope) {
            }
        },
        "editointikontrollit_local": {
            controller: function ($scope, $q, $timeout) {
                $scope.edit = EditointikontrollitService.createLocal({
                    start: function () { return $q(function (resolve, reject) { return $timeout(function () {
                        $scope.isEditing = true;
                        resolve();
                    }, 1000); }); },
                    save: function () { return $q(function (resolve, reject) { return $timeout(function () {
                        console.log("save local");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000); }); },
                    cancel: function () { return $q(function (resolve, reject) { return $timeout(function () {
                        console.log("cancel local");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000); }); }
                });
                $scope.cancel = EditointikontrollitService.cancel;
                $scope.save = EditointikontrollitService.save;
            }
        },
        "editointikontrollit_global": {
            controller: function ($scope, $q, $timeout) {
                $scope.edit = EditointikontrollitService.create({
                    start: function () { return $q(function (resolve, reject) { return $timeout(function () {
                        $scope.isEditing = true;
                        resolve();
                    }, 1000); }); },
                    save: function () { return $q(function (resolve, reject) { return $timeout(function () {
                        console.log("save global");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000); }); },
                    cancel: function () { return $q(function (resolve, reject) { return $timeout(function () {
                        console.log("cancel global");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000); }); }
                });
            }
        }
    }
}); });
//# sourceMappingURL=state.js.map