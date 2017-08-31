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
package fi.vm.sade.eperusteet.amosaa.resource.ops;

import fi.vm.sade.eperusteet.amosaa.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.amosaa.resource.config.InternalApi;
import fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija.KoulutustoimijaIdGetterAbstractController;
import fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaAudit;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaMessageFields.OPETUSSUUNNITELMA;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.TERMI_MUOKKAUS;
import static fi.vm.sade.eperusteet.amosaa.service.audit.EperusteetAmosaaOperation.TERMI_POISTO;
import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;
import fi.vm.sade.eperusteet.amosaa.service.ops.TermistoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * @author apvilkko
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}")
@InternalApi
public class TermistoController extends KoulutustoimijaIdGetterAbstractController {

    @Autowired
    private EperusteetAmosaaAudit audit;

    @Autowired
    private TermistoService termistoService;

    @RequestMapping(value = "/termisto", method = GET)
    public ResponseEntity<List<TermiDto>> getAll(
            @ModelAttribute("solvedKtId") final Long ktId) {
        return ResponseEntity.ok(termistoService.getTermit(ktId));
    }

    @RequestMapping(value = "/termisto/{termiId}", method = GET)
    public ResponseEntity<TermiDto> get(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long termiId) {
        return ResponseEntity.ok(termistoService.getTermi(ktId, termiId));
    }

    @RequestMapping(value = "/termisto/{avain}/avain", method = GET)
    public ResponseEntity<TermiDto> get(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final String avain) {
        return ResponseEntity.ok(termistoService.getTermiByAvain(ktId, avain));
    }

    @RequestMapping(value = "/termisto", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TermiDto> addTermi(
            @ModelAttribute("solvedKtId") final Long ktId,
            @RequestBody TermiDto dto) {
        dto.setId(null);
        return audit.withAudit(LogMessage.builder(ktId, null, OPETUSSUUNNITELMA, TERMI_POISTO), (Void) -> {
            return ResponseEntity.ok(termistoService.addTermi(ktId, dto));
        });
    }

    @RequestMapping(value = "/termisto/{termiId}", method = PUT)
    public ResponseEntity<TermiDto> updateTermi(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long termiId,
            @RequestBody TermiDto dto) {
        dto.setId(termiId);
        return audit.withAudit(LogMessage.builder(ktId, null, OPETUSSUUNNITELMA, TERMI_MUOKKAUS), (Void) -> {
            return ResponseEntity.ok(termistoService.updateTermi(ktId, dto));
        });
    }

    @RequestMapping(value = "/termisto/{termiId}", method = DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTermi(
            @ModelAttribute("solvedKtId") final Long ktId,
            @PathVariable final Long termiId) {
        audit.withAudit(LogMessage.builder(ktId, null, OPETUSSUUNNITELMA, TERMI_POISTO), (Void) -> {
            termistoService.deleteTermi(ktId, termiId);
            return null;
        });
    }
}
