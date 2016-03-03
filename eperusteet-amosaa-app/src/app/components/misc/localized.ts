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

angular.module("app")
.directive("slocalized", ($parse, $rootScope) => ({
    priority: 5,
    restrict: "A",
    require: "ngModel",
    scope: false,
    link: (scope, element, attrs, ngModelCtrl: any) => {
        ngModelCtrl.$formatters.push((modelValue) =>
            modelValue && modelValue[KieliService.getSisaltokieli()]);

        ngModelCtrl.$parsers.push((viewValue) => {
            let localizedModelValue = ngModelCtrl.$modelValue || {};
            localizedModelValue[KieliService.getSisaltokieli()] = viewValue;
            return localizedModelValue;
        });

        scope.$on("changed:sisaltokieli", (event, sisaltokieli) => {
            ngModelCtrl.$setViewValue(ngModelCtrl.$modelValue ? ngModelCtrl.$modelValue[sisaltokieli] : "");
            ngModelCtrl.$render();
        });
    }
}));
