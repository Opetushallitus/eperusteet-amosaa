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

module Spinner {
    let _pyynnot: number = 0;
    let _$timeout, _$rootScope;

    export const init = ($timeout, $rootScope) => {
        _$timeout = $timeout;
        _$rootScope = $rootScope;
    };

    export const enableSpinner = (SPINNER_WAIT:number) : void => {
        ++_pyynnot;
        _$timeout(() => {
            if (_pyynnot > 0) {
                _$rootScope.$emit('event:spinner_on');
            }
        }, SPINNER_WAIT);
    };

    export const disableSpinner= () : void => {
        --_pyynnot;
        if (_pyynnot === 0) {
            _$rootScope.$emit('event:spinner_off');
        }
    };
}

module SpinnerImp {
    export const directive = ($scope) => {
        return {
            templateUrl: 'components/spinner/spinner.jade',
            restrict: 'E',
            link: ($scope: any) => {
                $scope.isSpinning = false;

                const spin = (state) => {
                    $scope.isSpinning = state;
                };

                $scope.$on('event:spinner_on', () => {
                    spin(true);
                });

                $scope.$on('event:spinner_off', () => {
                    spin(false);
                });
            }
        };
    }
}

angular.module("app")
    .run(($injector) => $injector.invoke(Spinner.init))
    .directive("spinner", SpinnerImp.directive);