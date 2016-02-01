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

module OhjeService {
    let i;
    export const init = _.once(() => {
        i = InjectorService.inject([
            "$templateCache",
            "$rootScope",
            "$state"]);

        i.$rootScope.$on("$stateChangeSuccess", (_, state) => updateHelp(state.name));
    });

    const stateName2Url = (state) =>
        ("misc/guidance/" + state + ".jade").replace(".detail", "");

    export const updateHelp = (state: string) => {
        const templateUrl = stateName2Url(state);
        if (i.$templateCache.get(templateUrl)) {
            i.$rootScope.$broadcast('help:updated', templateUrl);
        }
    };

    export const getHelpTemplates = () => _(i.$state.get())
        .map(state => [state.name, stateName2Url(state.name)])
        .filter(([name, url]) => i.$templateCache.get(url))
        .fromPairs()
        .value();
}

module OhjeImpl {
    export const controller = ($scope) => {
        $scope.isOpen = false;
    }

    export const directive = () => {
        return {
            templateUrl: "components/ohje/ohje.jade",
            restrict: "AE",
            scope: {
                teksti: "@",
                otsikko: "@?",
                suunta: "@?",
                ohje: "@?"
            },
            controller: controller
        }
    };

    export const controller = ($scope) => {
        $scope.isOpen = false;
    }

}

angular.module("app")
    .run(OhjeService.init)
    .directive("ohje", OhjeImpl.directive);
