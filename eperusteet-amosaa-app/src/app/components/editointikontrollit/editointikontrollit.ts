interface IEditointikontrollitCallbacks {
    start: () => Promise<any>,
    save: (kommentti?) => Promise<any>,
    cancel: () => Promise<any>,
}

module EditointikontrollitService {
    let _$rootScope, _$q, _$log;

    export const init = ($rootScope, $q, $log) => {
        _$rootScope = $rootScope;
        _$log = $log;
        _$q = $q;
    };

    let _editing: boolean = false;
    const stop = () => _$q((resolve) => {
        _editing = false;
    });


    export const create = (callbacks: IEditointikontrollitCallbacks) => {
        const cleanup = () => _$q((resolve) => {
            stop();
            callbacks = undefined;
        });


        return {
            start: () => _$q((resolve, reject) => {
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
            }),
            save: (kommentti?) => _$q((resolve, reject) => {
                return callbacks.save(kommentti).then(() => {
                    _editing = false;
                    _$rootScope.$broadcast("editointikontrollit:save");
                    resolve();
                })
                .catch(reject);
            }),
            cancel: () => _$q((resolve, reject) => {
                return callbacks.cancel().then(() => {
                    _editing = false;
                    _$rootScope.$broadcast("editointikontrollit:cancel");
                    resolve();
                })
                .catch(reject);
            }),
            isEnabled: () => !!callbacks,
            isEditing: () => _editing
        };
    };
}

module EditointikontrollitImpl {
    export const directive = ($window) => {
        return {
            templateUrl: "components/editointikontrollit/editointikontrollit.jade",
            restrict: "E",
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

    export const controller = ($scope, $rootScope, Editointikontrollit, $timeout) => {
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

        $scope.$on("$stateChangeStart", () => {
            Editointikontrollit.unregisterCallback();
            setEditControls();
        });

        Editointikontrollit.registerCallbackListener(setEditControls);

        $scope.$on("editointikontrollitRefresh", () => {
            $scope.updatePosition();
        });

        $scope.$on("enableEditing", () => {
            $scope.editStarted = true;
            $scope.setMargins();
            $scope.kommentti = "";
            $timeout(() => {
                $scope.updatePosition();
            });
        });
        $scope.$on("disableEditing", () => {
            $scope.editStarted = false;
            $scope.setMargins();
        });

        $scope.start = () => {
            Editointikontrollit.startEditing();
        };
        $scope.save = () => {
            Editointikontrollit.saveEditing($scope.kommentti);
        };
        $scope.cancel = () => {
            Editointikontrollit.cancelEditing();
        };
    };
}

angular.module("app")
.run(($injector) => $injector.invoke(EditointikontrollitService.init))
// .factory("Editointikontrollit", EditointikontrollitImpl.service)
.directive("editointikontrollit", EditointikontrollitImpl.directive)
.controller("EditointiController", EditointikontrollitImpl.controller)
.directive("ekbutton", () => ({
    restrict: "A",
    link: (scope, el, attr) => {
        scope.$on("editointikontrollit:start", () => el.attr('disabled', ''));
        scope.$on("editointikontrollit:cancel", () => el.removeAttr('disabled'));
        scope.$on("editointikontrollit:stop", () => el.removeAttr('disabled'));
    }
}));
