package fi.vm.sade.eperusteet.amosaa.resource.locks.contexts;

import fi.vm.sade.eperusteet.amosaa.service.locking.OpsCtx;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SisaltoViiteCtx extends OpsCtx {
    Long svId;

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
