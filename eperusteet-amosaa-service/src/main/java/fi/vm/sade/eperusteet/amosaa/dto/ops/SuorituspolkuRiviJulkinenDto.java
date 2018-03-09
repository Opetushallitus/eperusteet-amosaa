package fi.vm.sade.eperusteet.amosaa.dto.ops;

import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.service.audit.AuditLoggableDto;
import fi.vm.sade.eperusteet.amosaa.service.audit.LogMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class SuorituspolkuRiviJulkinenDto implements AuditLoggableDto {
    @Override
    public void auditLog(LogMessage.LogMessageBuilder msg) {
    }
    private Long id;
    private LokalisoituTekstiDto kuvaus;
    private Set<String> koodit;
}
