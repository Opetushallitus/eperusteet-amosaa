package fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DokumenttiBase {
    Document document;
    Element headElement;
    Element bodyElement;
    Opetussuunnitelma opetussuunnitelma;
    PerusteKaikkiDto peruste;
    CharapterNumberGenerator generator;
    Kieli kieli;
    Dokumentti dokumentti;
    DtoMapper mapper;
}
