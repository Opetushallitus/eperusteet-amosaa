var EditointikontrollitService;
(function (EditointikontrollitService) {
    var _$rootScope, _$q, _$log;
    EditointikontrollitService.init = function ($rootScope, $q, $log) {
        _$rootScope = $rootScope;
        _$log = $log;
        _$q = $q;
    };
    var _editing = false, _activeCallbacks;
    var stop = function () { return _$q(function (resolve) {
        _editing = false;
    }); };
    var start = function (callbacks, isGlobal) { return _$q(function (resolve, reject) {
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
    }); };
    EditointikontrollitService.save = function (kommentti) { return _$q(function (resolve, reject) {
        return _activeCallbacks.save(kommentti).then(function () {
            _editing = false;
            _$rootScope.$broadcast("editointikontrollit:enable");
            _$rootScope.$broadcast("editointikontrollit:cancel");
            resolve();
        })
            .catch(reject);
    }); };
    EditointikontrollitService.cancel = function () { return _$q(function (resolve, reject) {
        return _activeCallbacks.cancel().then(function () {
            _editing = false;
            _$rootScope.$broadcast("editointikontrollit:enable");
            _$rootScope.$broadcast("editointikontrollit:cancel");
            resolve();
        })
            .catch(reject);
    }); };
    EditointikontrollitService.isEnabled = function () { return !!_activeCallbacks; };
    EditointikontrollitService.isEditing = function () { return _editing; };
    EditointikontrollitService.create = function (callbacks) { return _.partial(start, callbacks, true); };
    EditointikontrollitService.createLocal = function (callbacks) { return _.partial(start, callbacks, false); };
})(EditointikontrollitService || (EditointikontrollitService = {}));
var EditointikontrollitImpl;
(function (EditointikontrollitImpl) {
    EditointikontrollitImpl.controller = function ($scope, $rootScope, $timeout) {
        $scope.kommentti = "";
        // TODO: Enable
        // $scope.$on("$stateChangeStart", () => {
        //     Editointikontrollit.unregisterCallback();
        //     setEditControls();
        // });
        $scope.$on("editointikontrollitRefresh", $scope.updatePosition);
        $scope.$on("editointikontrollit:start", function () {
            $scope.editStarted = true;
            $scope.setMargins();
            $scope.kommentti = "";
            $timeout($scope.updatePosition);
        });
        $scope.$on("editointikontrollit:cancel", function () {
            $scope.editStarted = false;
            $scope.setMargins();
        });
        $scope.save = function () {
            EditointikontrollitService.save($scope.kommentti);
        };
        $scope.cancel = EditointikontrollitService.cancel;
    };
    EditointikontrollitImpl.directive = function ($window) {
        return {
            templateUrl: "components/editointikontrollit/editointikontrollit.jade",
            restrict: "E",
            controller: EditointikontrollitImpl.controller,
            link: function (scope) {
                var window = angular.element($window), container = angular.element(".edit-controls"), wrapper = angular.element(".editointi-wrapper");
                /**
                * Editointipalkki asettuu staattisesti footerin päälle kun skrollataan
                * tarpeeksi alas. Ylempänä editointipalkki kelluu.
                */
                scope.updatePosition = function () {
                    if (window.scrollTop() + window.innerHeight() < wrapper.offset().top + container.height()) {
                        container.addClass("floating");
                        container.removeClass("static");
                        container.css("width", wrapper.width());
                    }
                    else {
                        container.removeClass("floating");
                        container.addClass("static");
                        container.css("width", "100%");
                    }
                };
                var updatepos = scope.updatePosition;
                window.on("scroll resize", updatepos);
                scope.$on("$destroy", function () {
                    window.off("scroll resize", updatepos);
                });
                scope.updatePosition();
                scope.setMargins = function () {
                    if (scope.editStarted) {
                        wrapper.css("margin-bottom", "50px").css("margin-top", "20px");
                    }
                    else {
                        wrapper.css("margin-bottom", 0).css("margin-top", 0);
                    }
                };
                scope.setMargins();
            }
        };
    };
})(EditointikontrollitImpl || (EditointikontrollitImpl = {}));
angular.module("app")
    .run(function ($injector) { return $injector.invoke(EditointikontrollitService.init); })
    .directive("editointikontrollit", EditointikontrollitImpl.directive)
    .directive("ekbutton", function () { return ({
    restrict: "A",
    link: function (scope, el, attr) {
        scope.$on("editointikontrollit:disable", function () { return el.attr('disabled', ''); });
        scope.$on("editointikontrollit:enable", function () { return el.removeAttr('disabled'); });
    }
}); });
//# sourceMappingURL=editointikontrollit.js.map