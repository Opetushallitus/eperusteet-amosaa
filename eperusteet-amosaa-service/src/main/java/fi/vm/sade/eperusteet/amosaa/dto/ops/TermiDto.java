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
<<<<<<< 6600c7bd757c6d2715b5d2302ce5c6e82ca2180e:eperusteet-amosaa-service/src/main/java/fi/vm/sade/eperusteet/amosaa/dto/ops/TermiDto.java
package fi.vm.sade.eperusteet.amosaa.dto.ops;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TermiDto {
    private Long id;
    private String avain;
    private LokalisoituTekstiDto termi;
    private LokalisoituTekstiDto selitys;
}
=======
'use strict';
var Ohje;
(function (Ohje) {
    Ohje.directive = function () {
        return {
            templateUrl: "components/ohje/ohje.jade",
            restrict: "AE",
            scope: {
                teksti: "@",
                otsikko: "@?",
                suunta: "@?",
                ohje: "@?"
            },
            controller: "OhjeController"
        };
    };
    Ohje.controller = function ($scope) {
        $scope.isOpen = false;
    };
})(Ohje || (Ohje = {}));
angular.module("app")
    .directive("ohje", Ohje.directive)
    .controller("OhjeController", Ohje.controller);
//# sourceMappingURL=ohje.js.map
>>>>>>> added angular-spinner:eperusteet-amosaa-app/src/app/components/ohje/ohje.js
