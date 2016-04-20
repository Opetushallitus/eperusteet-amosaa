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
        bootstrap: {
            controller: ($scope) => {

            }
        },
        boxtable: {
            controller: ($scope) => {}
        },
        notifikaatiot: {
            controller: ($scope) => {
                $scope.normaali = () => NotifikaatioService.normaali("Infot");
                $scope.onnistui = () => NotifikaatioService.onnistui("Success!");
                $scope.varoitus = () => NotifikaatioService.varoitus("Voi ei!");
                $scope.fataali = () => NotifikaatioService.fataali("Jotain meni todella pahasti mönkään :(")
                    .finally(() => NotifikaatioService.onnistui("Tilanne palautui normaaliksi :)"));
            }
        },
        datepicker: {
            controller: ($scope) => {
                $scope.pvm = "";
            }
        },
        kaanna: {
            controller: ($scope) => {
                $scope.obj = {
                    fi: "Hei",
                    en: "Hello"
                };
                $scope.objFi = KaannaService.kaanna($scope.obj);
            }
        },
        editointikontrollit_local: {
            controller: ($scope, $q, $timeout) => {
                $scope.edit = EditointikontrollitService.createLocal({
                    start: () => $q((resolve, reject) => $timeout(() => {
                        $scope.isEditing = true;
                        resolve();
                    }, 1000)),
                    save: () => $q((resolve, reject) => $timeout(() => {
                        $scope.isEditing = false;
                        resolve();
                    }, 1000)),
                    cancel: () => $q((resolve, reject) => $timeout(() => {
                        $scope.isEditing = false;
                        resolve();
                    }, 1000)),
                });

                $scope.cancel = EditointikontrollitService.cancel;
                $scope.save = EditointikontrollitService.save;
            }
        },
        editointikontrollit_global: {
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
