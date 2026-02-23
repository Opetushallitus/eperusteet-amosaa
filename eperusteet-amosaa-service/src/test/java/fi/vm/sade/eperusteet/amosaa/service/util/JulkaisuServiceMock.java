package fi.vm.sade.eperusteet.amosaa.service.util;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaistuOpetussuunnitelmaTila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaisuTila;
import fi.vm.sade.eperusteet.amosaa.dto.external.SisaltoviiteOpintokokonaisuusExternalDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.JulkaisuBaseDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaJulkaistuQueryDto;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.JulkaisuService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class JulkaisuServiceMock implements JulkaisuService {
    @Override
    public List<JulkaisuBaseDto> getJulkaisut(long ktId, long opsId) {
        return null;
    }

    @Override
    public void teeJulkaisu(long ktId, long opsId, JulkaisuBaseDto julkaisuBaseDto) {
    }

    @Override
    public void teeJulkaisuAsync(long ktId, long opsId, JulkaisuBaseDto julkaisuBaseDto) {

    }

    @Override
    public JulkaisuBaseDto aktivoiJulkaisu(long ktId, long opsId, int revision) {
        return null;
    }

    @Override
    public void kooditaSisaltoviitteet(long ktId, long opsId) {
        
    }

    @Override
    public boolean julkaisemattomiaMuutoksia(long ktId, long opsId) {
        return false;
    }

    @Override
    public JulkaisuTila viimeisinJulkaisuTila(long ktId, long opsId) {
        return null;
    }

    @Override
    public void saveJulkaistuOpetussuunnitelmaTila(JulkaistuOpetussuunnitelmaTila julkaistuOpetussuunnitelmaTila) {

    }

    @Override
    public Page<OpetussuunnitelmaDto> findOpetussuunnitelmatJulkaisut(OpetussuunnitelmaJulkaistuQueryDto pquery) {
        return null;
    }

    @Override
    public OpetussuunnitelmaKaikkiDto getOpetussuunnitelmaJulkaistuSisalto(Long opsId) {
        return null;
    }

    @Override
    public SisaltoviiteOpintokokonaisuusExternalDto findJulkaistuOpintokokonaisuus(String koodiArvo) throws IOException {
        return null;
    }

    @Override
    public Object getJulkaistuSisaltoObjectNode(Long opetussuunnitelmaId, List<String> paths, Map<String, String> filters) {
        return null;
    }

}
