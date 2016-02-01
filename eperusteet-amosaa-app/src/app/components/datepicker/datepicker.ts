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

module DatePicker {

    export const directive = ($parse, $timeout) => {

        const checkInputType = (scope) => {

            scope.datePicker = {
                state: false,
                open: ($event) => {
                    $event.preventDefault();
                    $event.stopPropagation();
                    scope.datePicker.state = !scope.datePicker.state;
                }
            };
        };

        return {
            templateUrl: "components/datepicker/datepicker.jade",
            transclude: true,
            restrict: "E",
            scope: {
                model: "=",
                label: "@",
                type: "@?",
                options: "=?",
                modelVar: "@",
                form: "=",
                min: "@?",
                max: "@?",
                name: "@",
                placeholder: "@",
                step: "@?"
            },

            link: (scope, element, attrs) => {

                scope.type = scope.type || "text";
                attrs.$observe("required", (value) => {
                    if (value === "required" || value === "true" || value === "") {
                        scope.postfix = "*";
                        $timeout(() => {
                            element.find("input").attr("required", "");
                        });
                    } else if (value) {
                        const parsed = $parse(value);
                        scope.$watch(() => {
                            return parsed(scope.$parent);
                        }, (newValue) => {
                            scope.postfix = newValue ? "*" : "";
                        });
                    }
                });

                // Two-way binding with deep object hierarchies needs some tricks
                const getter = $parse(scope.modelVar);
                const setter = getter.assign;
                scope.input = {};
                scope.input.model = getter(scope.model);

                // inner => outside
                scope.$watch("input.model", () => {
                    checkInputType(scope);
                    if (scope.input && !_.isUndefined(scope.input.model)) {
                        setter(scope.model, scope.input.model);
                    }
                });

                // outside => inner
                scope.$watch(() => {
                    return getter(scope.model);
                }, (value) => {
                    checkInputType(scope);
                    scope.input.model = value;
                });
                checkInputType(scope);
            }
        }
    };

    export const dateformatvalidator = () => {

        return {
            restrict: "A",
            require: "ngModel",
            link: (scope, element, attrs, ngModel) => {
                let parsedMoment;
                ngModel.$parsers.unshift(function (viewValue) {
                    return validate(viewValue);
                });
                ngModel.$formatters.unshift((viewValue) => {
                    return validate(viewValue);
                });
                const validate = (viewValue) => {
                    if (_.isDate(viewValue) || viewValue === "" || viewValue === null || viewValue === undefined) {
                        ngModel.$setValidity("dateformatvalidator", true);
                        return viewValue;
                    } else if (_.isString(viewValue)) {
                        parsedMoment = moment(viewValue, "D.M.YYYY", true);
                    } else if (_.isNumber(viewValue)) {
                        parsedMoment = moment(viewValue);
                    } else {
                        ngModel.$setValidity("dateformatvalidator", false);
                        return undefined;
                    }

                    if(parsedMoment.isValid()) {
                        ngModel.$setValidity("dateformatvalidator", true);
                        return viewValue;
                    } else {
                        ngModel.$setValidity("dateformatvalidator", false);
                        return undefined;
                    }
                }
            }
        };
    };

}

angular.module("app")
    .directive("datepick", DatePicker.directive)
    .directive("dateformatvalidator", DatePicker.dateformatvalidator);

