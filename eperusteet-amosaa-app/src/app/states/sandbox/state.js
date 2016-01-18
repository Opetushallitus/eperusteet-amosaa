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
                var ek = EditointikontrollitService.create({
                    start: function () { return $q(function (resolve, reject) { return $timeout(function () {
                        console.log("starting editing");
                        $scope.isEditing = true;
                        resolve();
                    }, 1000); }); },
                    save: function () { return $q(function (resolve, reject) { return $timeout(function () {
                        console.log("saving");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000); }); },
                    cancel: function () { return $q(function (resolve, reject) { return $timeout(function () {
                        console.log("cancelling editing");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000); }); }
                });
                $scope.edit = ek.start;
                $scope.save = ek.save;
                $scope.cancel = ek.cancel;
            }
        },
        "editointikontrollit_global": {
            controller: function ($scope, $q, $timeout) {
                var ek = EditointikontrollitService.create({
                    start: function () { return $q(function (resolve, reject) { return $timeout(function () {
                        console.log("starting editing");
                        $scope.isEditing = true;
                        resolve();
                    }, 1000); }); },
                    save: function () { return $q(function (resolve, reject) { return $timeout(function () {
                        console.log("saving");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000); }); },
                    cancel: function () { return $q(function (resolve, reject) { return $timeout(function () {
                        console.log("cancelling editing");
                        $scope.isEditing = false;
                        resolve();
                    }, 1000); }); }
                });
                $scope.edit = ek.start;
                $scope.save = ek.save;
                $scope.cancel = ek.cancel;
            }
        }
    }
}); });
//# sourceMappingURL=state.js.map