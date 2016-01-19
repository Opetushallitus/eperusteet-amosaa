interface IEditointikontrollitCallbacks {
    start: () => Promise<any>,
    save: (kommentti?) => Promise<any>,
    cancel: () => Promise<any>,
}

namespace EditointikontrollitService {
    let _$rootScope, _$q, _$log;

    export const init = ($rootScope, $q, $log) => {
        _$rootScope = $rootScope;
        _$log = $log;
        _$q = $q;
    };

    let
        _editing: boolean = false,
        _activeCallbacks: any;

    const stop = () => _$q((resolve) => { _editing = false; });

    const start = (callbacks, isGlobal) => _$q((resolve, reject) => {
        if (_editing) {
            return reject();
        }
        else if (!callbacks) {
            return reject();
        }
        else {
            _editing = true;
            _activeCallbacks = callbacks;
            _$rootScope.$broadcast("editointikontrollit:disable");
            if (isGlobal) {
                _$rootScope.$broadcast("editointikontrollit:start");
            }
            return callbacks.start().then(resolve).catch(reject);
        }
    });

    export const save = (kommentti?) => _$q((resolve, reject) => {
        return _activeCallbacks.save(kommentti).then(() => {
            _editing = false;
            _$rootScope.$broadcast("editointikontrollit:enable");
            _$rootScope.$broadcast("editointikontrollit:cancel");
            resolve();
        })
        .catch(reject);
    });
    export const cancel = () => _$q((resolve, reject) => {
        return _activeCallbacks.cancel().then(() => {
            _editing = false;
            _$rootScope.$broadcast("editointikontrollit:enable");
            _$rootScope.$broadcast("editointikontrollit:cancel");
            resolve();
        })
        .catch(reject);
    });
    export const isEnabled = () => !!_activeCallbacks;
    export const isEditing = () => _editing;
    export const create = (callbacks: IEditointikontrollitCallbacks) => _.partial(start, callbacks, true);
    export const createLocal = (callbacks: IEditointikontrollitCallbacks) => _.partial(start, callbacks, false);
}

module EditointikontrollitImpl {
    export const controller = ($scope, $rootScope, $timeout) => {
        $scope.kommentti = "";

        // $scope.$on("$stateChangeStart", () => {
        //     console.log("moro");
        //     // Editointikontrollit.unregisterCallback();
        //     // setEditControls();
        // });

        $scope.$on("editointikontrollitRefresh", $scope.updatePosition);

        $scope.$on("editointikontrollit:start", () => {
            $scope.editStarted = true;
            $scope.setMargins();
            $scope.kommentti = "";
            $timeout($scope.updatePosition);
        });

        $scope.$on("editointikontrollit:cancel", () => {
            $scope.editStarted = false;
            $scope.setMargins();
        });

        $scope.save = () => {
            EditointikontrollitService.save($scope.kommentti);
        };

        $scope.cancel = EditointikontrollitService.cancel;
    };

    export const directive = ($window) => {
        return {
            templateUrl: "components/editointikontrollit/editointikontrollit.jade",
            restrict: "E",
            controller: controller,
            link: (scope: any) => {
                let window = angular.element($window),
                container = angular.element(".edit-controls"),
                wrapper = angular.element(".editointi-wrapper");

                /**
                * Editointipalkki asettuu staattisesti footerin päälle kun skrollataan
                * tarpeeksi alas. Ylempänä editointipalkki kelluu.
                */
                scope.updatePosition = () => {
                    if (window.scrollTop() + window.innerHeight() < wrapper.offset().top + container.height()) {
                        container.addClass("floating");
                        container.removeClass("static");
                        container.css("width", wrapper.width());
                    } else {
                        container.removeClass("floating");
                        container.addClass("static");
                        container.css("width", "100%");
                    }
                };

                let updatepos = scope.updatePosition;
                window.on("scroll resize", updatepos);
                scope.$on("$destroy", () => {
                    window.off("scroll resize", updatepos);
                });
                scope.updatePosition();

                scope.setMargins = () => {
                    if (scope.editStarted) {
                        wrapper.css("margin-bottom", "50px").css("margin-top", "20px");
                    } else {
                        wrapper.css("margin-bottom", 0).css("margin-top", 0);
                    }
                };
                scope.setMargins();
            }
        };
    };
}

angular.module("app")
.run(($injector) => $injector.invoke(EditointikontrollitService.init))
// .factory("Editointikontrollit", EditointikontrollitImpl.service)
.directive("editointikontrollit", EditointikontrollitImpl.directive)
// .controller("editointicontroller", EditointikontrollitImpl.controller)
.directive("ekbutton", () => ({
    restrict: "A",
    link: (scope, el, attr) => {
        scope.$on("editointikontrollit:disable", () => el.attr("disabled", ""));
        scope.$on("editointikontrollit:enable", () => el.removeAttr("disabled"));
    }
}));
