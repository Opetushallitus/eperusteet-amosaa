var EditointikontrollitService;
(function (EditointikontrollitService) {
    var _$rootScope, _$q, _$log;
    EditointikontrollitService.init = function ($rootScope, $q, $log) {
        _$rootScope = $rootScope;
        _$log = $log;
        _$q = $q;
    };
    var _editing = false;
    var stop = function () { return _$q(function (resolve) {
        _editing = false;
    }); };
    EditointikontrollitService.create = function (callbacks) {
        var cleanup = function () { return _$q(function (resolve) {
            stop();
            callbacks = undefined;
        }); };
        return {
            start: function () { return _$q(function (resolve, reject) {
                if (_editing) {
                    _$log.warn("Already editing");
                    return reject();
                }
                else if (!callbacks) {
                    _$log.error("Callbacks not provided");
                    return reject();
                }
                else {
                    console.log("Enabling directive");
                    _editing = true;
                    _$rootScope.$broadcast("editointikontrollit:start");
                    return callbacks.start().then(resolve).catch(reject);
                }
            }); },
            save: function (kommentti) { return _$q(function (resolve, reject) {
                return callbacks.save(kommentti).then(function () {
                    _editing = false;
                    _$rootScope.$broadcast("editointikontrollit:save");
                    resolve();
                })
                    .catch(reject);
            }); },
            cancel: function () { return _$q(function (resolve, reject) {
                return callbacks.cancel().then(function () {
                    _editing = false;
                    _$rootScope.$broadcast("editointikontrollit:cancel");
                    resolve();
                })
                    .catch(reject);
            }); },
            isEnabled: function () { return !!callbacks; },
            isEditing: function () { return _editing; }
        };
    };
})(EditointikontrollitService || (EditointikontrollitService = {}));
var EditointikontrollitImpl;
(function (EditointikontrollitImpl) {
    EditointikontrollitImpl.directive = function ($window) {
        return {
            templateUrl: "components/editointikontrollit/editointikontrollit.jade",
            restrict: "E",
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
    EditointikontrollitImpl.controller = function ($scope, $rootScope, Editointikontrollit, $timeout) {
        $scope.kommentti = "";
        $scope.hideControls = true;
        function setEditControls() {
            if (Editointikontrollit.editingEnabled()) {
                $scope.hideControls = false;
            }
            else {
                $scope.hideControls = true;
                $scope.editStarted = false;
            }
        }
        setEditControls();
        $scope.$on("$stateChangeStart", function () {
            Editointikontrollit.unregisterCallback();
            setEditControls();
        });
        Editointikontrollit.registerCallbackListener(setEditControls);
        $scope.$on("editointikontrollitRefresh", function () {
            $scope.updatePosition();
        });
        $scope.$on("enableEditing", function () {
            $scope.editStarted = true;
            $scope.setMargins();
            $scope.kommentti = "";
            $timeout(function () {
                $scope.updatePosition();
            });
        });
        $scope.$on("disableEditing", function () {
            $scope.editStarted = false;
            $scope.setMargins();
        });
        $scope.start = function () {
            Editointikontrollit.startEditing();
        };
        $scope.save = function () {
            Editointikontrollit.saveEditing($scope.kommentti);
        };
        $scope.cancel = function () {
            Editointikontrollit.cancelEditing();
        };
    };
})(EditointikontrollitImpl || (EditointikontrollitImpl = {}));
angular.module("app")
    .run(function ($injector) { return $injector.invoke(EditointikontrollitService.init); })
    .directive("editointikontrollit", EditointikontrollitImpl.directive)
    .controller("EditointiController", EditointikontrollitImpl.controller)
    .directive("ekbutton", function () { return ({
    restrict: "A",
    link: function (scope, el, attr) {
        scope.$on("editointikontrollit:start", function () { return el.attr('disabled', ''); });
        scope.$on("editointikontrollit:cancel", function () { return el.removeAttr('disabled'); });
        scope.$on("editointikontrollit:stop", function () { return el.removeAttr('disabled'); });
    }
}); });
//# sourceMappingURL=editointikontrollit.js.map