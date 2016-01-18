/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
var Spinner;
(function (Spinner) {
    var _pyynnot = 0;
    var _$timeout, _$rootScope;
    Spinner.init = function ($timeout, $rootScope) {
        _$timeout = $timeout;
        _$rootScope = $rootScope;
    };
    Spinner.enableSpinner = function (SPINNER_WAIT) {
        ++_pyynnot;
        _$timeout(function () {
            if (_pyynnot > 0) {
                _$rootScope.$emit('event:spinner_on');
            }
        }, SPINNER_WAIT);
    };
    Spinner.disableSpinner = function () {
        --_pyynnot;
        if (_pyynnot === 0) {
            _$rootScope.$emit('event:spinner_off');
        }
    };
})(Spinner || (Spinner = {}));
var SpinnerImp;
(function (SpinnerImp) {
    SpinnerImp.directive = function ($scope) {
        return {
            templateUrl: 'components/spinner/spinner.jade',
            restrict: 'E',
            link: function ($scope) {
                $scope.isSpinning = false;
                var spin = function (state) {
                    $scope.isSpinning = state;
                };
                $scope.$on('event:spinner_on', function () {
                    spin(true);
                });
                $scope.$on('event:spinner_off', function () {
                    spin(false);
                });
            }
        };
    };
})(SpinnerImp || (SpinnerImp = {}));
angular.module("app")
    .run(function ($injector) { return $injector.invoke(Spinner.init); })
    .directive("spinner", SpinnerImp.directive);
//# sourceMappingURL=spinner.js.map