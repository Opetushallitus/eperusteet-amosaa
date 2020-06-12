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

package fi.vm.sade.eperusteet.amosaa.resource.locks.contexts;

import fi.vm.sade.eperusteet.amosaa.service.locking.OpsCtx;
import lombok.Getter;
import lombok.Setter;

/**
 * @author nkala
 */
@Getter
@Setter
public class SisaltoViiteCtx extends OpsCtx {
    Long svId;

    public SisaltoViiteCtx() {
    }

    public SisaltoViiteCtx(Long svId) {
        this.svId = svId;
    }

    public SisaltoViiteCtx(Long ktId, Long opsId, Long svId) {
        super(ktId, opsId);
        this.svId = svId;
    }

    public void setOsanId(Long svId) {
        this.svId = svId;
    }

    public static SisaltoViiteCtx of(Long svId) {
        return new SisaltoViiteCtx(svId);
    }

    public static SisaltoViiteCtx of(Long ktId, Long opsId, Long svId) {
        return new SisaltoViiteCtx(ktId, opsId, svId);
    }

}
