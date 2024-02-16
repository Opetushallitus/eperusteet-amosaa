package fi.vm.sade.eperusteet.amosaa.service.locking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpsCtx {
    private Long ktId;
    private Long opsId;
}
