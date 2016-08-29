interface IEditointikontrollitCallbacks {
    start?: (val?) => Promise<any>,
    preStart?: () => Promise<any>,
    preSave?: () => Promise<any>,
    save?: (kommentti?) => Promise<any>,
    cancel?: () => Promise<any>,
    after?: (res?) => void
    done?: () => void
}

namespace EditointikontrollitService {
    let _$rootScope, _$q, _$log, _$timeout;

    export const init = ($rootScope, $q, $log, $timeout) => {
        _$rootScope = $rootScope;
        _$timeout = $timeout;
        _$log = $log;
        _$q = $q;
    };

    let _activeCallbacks: IEditointikontrollitCallbacks;

    const defaultCallbacks = () => ({
        start: (val) => _$q((resolve, reject) => resolve(val)),
        preSave: () => _$q((resolve, reject) => resolve()),
        preStart: () => _$q((resolve, reject) => resolve()),
        save: (val) => _$q((resolve, reject) => resolve(val)),
        cancel: (res) => _$q((resolve, reject) => resolve(res)),
        after: _.noop,
        done: _.noop
    });

    const stop = () => _$q((resolve) => {
        _$rootScope.$$ekEditing = false;
    });

    const handleError = (reject) => ((err) => {
        return reject(err);
    });

    const start = (callbacks: IEditointikontrollitCallbacks, isGlobal: boolean) => _$q((resolve, reject) => {
        callbacks = _.merge(defaultCallbacks(), callbacks);
        if (_$rootScope.$$ekEditing) {
            return reject();
        }
        else {
            return callbacks.preStart()
                .then(callbacks.start)
                .then(res => {
                    _$rootScope.$$ekEditing = true;
                    _activeCallbacks = callbacks;
                    _$rootScope.$broadcast("editointikontrollit:disable");
                    if (isGlobal) {
                        _$rootScope.$broadcast("editointikontrollit:start");
                    }
                    return resolve(res);
                })
                .catch(handleError(reject));
        }
    });

    export const save = (kommentti?) => _$q((resolve, reject) => {
        return _activeCallbacks.preSave()
            .then(() => _activeCallbacks.save(kommentti))
            .then((res) => {
                _$rootScope.$broadcast("editointikontrollit:saving");
                _$rootScope.$$ekEditing = false;
                _$rootScope.$broadcast("editointikontrollit:enable");
                _$rootScope.$broadcast("editointikontrollit:cancel");
                _$rootScope.$broadcast("notifyCKEditor");
                _activeCallbacks.after(res);
                _activeCallbacks.done();
            })
            .catch(handleError(reject));
    });

    export const cancel = () => _$q((resolve, reject) => {
        _$rootScope.$broadcast("editointikontrollit:canceling");
        return _activeCallbacks.cancel()
            .then(() => {
                _$rootScope.$$ekEditing = false;
                _$rootScope.$broadcast("editointikontrollit:enable");
                _$rootScope.$broadcast("editointikontrollit:cancel");
                _$rootScope.$broadcast("notifyCKEditor");
                resolve();
                _activeCallbacks.done();
            })
            .catch(reject);
    });

    export const createListRestangular = (
        scope,
        field: string,
        resolvedObj: restangular.IElement) => {
        scope[field] = resolvedObj.clone();
        return (idx: number) => {
            scope.$$ekEditingIndex = idx;
            return EditointikontrollitService.create({
                start: () => _$q((resolve, reject) => scope[field][idx].get()
                    .then(res => {
                        _.merge(resolvedObj, res);
                        scope[field][idx] = resolvedObj.clone();
                        resolve(res);
                    })
                    .catch(reject)),
                save: (kommentti) => _$q((resolve, reject) => {
                    _$rootScope.$broadcast("notifyCKEditor");
                    scope[field][idx].kommentti = kommentti;
                    return scope[field][idx].put()
                        .then(res => {
                            NotifikaatioService.onnistui("tallennus-onnistui");
                            TermistoData.refresh();
                            return resolve(res);
                        })
                        .catch(reject);
                }),
                cancel: (res) => _$q((resolve, reject) => {
                    scope[field][idx] = resolvedObj.clone();
                    resolve(res);
                }),
                after: (res) => _.merge(resolvedObj, res),
            })();
        };
    };

    export const createRestangular = (
        scope,
        field: string,
        resolvedObj: restangular.IElement,
        callbacks: IEditointikontrollitCallbacks = {}) => {
        scope[field] = resolvedObj.clone();

        return EditointikontrollitService.create(_.merge({
            start: () => _$q((resolve, reject) => scope[field].get()
                .then(res => {
                    _.merge(resolvedObj, res);
                    scope[field] = resolvedObj.clone();
                    resolve();
                })
                .catch(reject)),
            save: (kommentti) => _$q((resolve, reject) => {
                _$rootScope.$broadcast("notifyCKEditor");
                scope[field].kommentti = kommentti;
                return scope[field].put()
                    .then((res) => {
                        NotifikaatioService.onnistui("tallennus-onnistui");
                        return resolve(res);
                    })
                    .catch(reject);
            }),
            cancel: (res) => _$q((resolve, reject) => {
                scope[field] = resolvedObj.clone();
                resolve();
            }),
            after: (res) => _.merge(resolvedObj, res),
        }, callbacks));
    };

    export const isEnabled = () => !!_activeCallbacks;
    export const isEditing = () => _$rootScope.$$ekEditing;
    export const create = (callbacks = {}) => _.partial(start, callbacks, true);
    export const createLocal = (callbacks = {}) => _.partial(start, callbacks, false);
    export const enableSaving = (yes) => yes
        ? _$rootScope.$broadcast("editointikontrollit:enableSave")
        : _$rootScope.$broadcast("editointikontrollit:disableSave");
}

module EditointikontrollitImpl {
    export const controller = ($scope, $rootScope, $timeout) => {
        $scope.kommentti = "";
        $scope.disableButtons = false;
        $scope.$$disableSave = false;

        const progress = (promise) => {
            $scope.disableButtons = true;
            promise.finally(() => $scope.disableButtons = false);
        };

        // $scope.$on("$stateChangeStart", () => {
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
            $scope.disableButtons = false;
            $scope.setMargins();
        });

        $rootScope.$on("editointikontrollit:disableSave", () => $scope.$$disableSave = true);
        $rootScope.$on("editointikontrollit:enableSave", () => $scope.$$disableSave = false);

        $scope.save = () => progress(EditointikontrollitService.save($scope.kommentti));
        $scope.cancel = () => progress(EditointikontrollitService.cancel());
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
                    container.addClass("floating");
                    container.removeClass("static");
                    container.css("width", wrapper.width());
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
.directive("editointikontrollit", EditointikontrollitImpl.directive)
.directive("ekbutton", () => ({
    restrict: "A",
    link: (scope, el, attr) => {
        scope.$on("editointikontrollit:disable", () => el.attr("disabled", ""));
        scope.$on("editointikontrollit:enable", () => el.removeAttr("disabled"));
    }
}));
// .factory("Editointikontrollit", EditointikontrollitImpl.service)
// .controller("editointicontroller", EditointikontrollitImpl.controller)
