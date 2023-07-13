package fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiEdistyminen;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.YllapitoDto;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiBuilderService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiStateService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiUtils;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Profile("default")
public class DokumenttiServiceImpl implements DokumenttiService {
    private static final Logger LOG = LoggerFactory.getLogger(DokumenttiServiceImpl.class);

    @Autowired
    private DokumenttiRepository dokumenttiRepository;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private OpetussuunnitelmaRepository opsRepository;

    @Autowired
    private DokumenttiBuilderService builderService;

    @Autowired
    private DokumenttiStateService dokumenttiStateService;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    private EperusteetService eperusteetService;

    @Lazy
    @Autowired
    private DokumenttiService self;

    @Override
    @Transactional
    public DokumenttiDto getValmisDto(Long ktId, Long opsId, Kieli kieli) {
        Dokumentti dokumentti = getLatestValmisDokumentti(opsId, kieli);

        if (dokumentti != null) {
            return mapper.map(dokumentti, DokumenttiDto.class);
        } else {
            DokumenttiDto dto = new DokumenttiDto();
            dto.setOpsId(opsId);
            dto.setKieli(kieli);
            dto.setTila(DokumenttiTila.EI_OLE);
            return dto;
        }
    }

    private Dokumentti getLatestValmisDokumentti(Long opsId, Kieli kieli) {
        List<Dokumentti> dokumentit = dokumenttiRepository.findByOpsIdAndKieliAndValmistumisaikaIsNotNull(opsId, kieli);
        if (dokumentit.isEmpty()) {
            return null;
        } else {
            return dokumentit.get(0);
        }
    }

    @Override
    public DokumenttiDto update(Long ktId, Long opsId, Kieli kieli, DokumenttiDto dto) {
        Dokumentti dokumentti = getLatestValmisDokumentti(opsId, kieli);
        if (dokumentti == null) {
            return null;
        } else {
            mapper.map(dto, dokumentti);
            return mapper.map(dokumentti, DokumenttiDto.class);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DokumenttiDto createDtoFor(Long ktId, Long id, Kieli kieli) {
        Dokumentti dokumentti = new Dokumentti();
        dokumentti.setTila(DokumenttiTila.EI_OLE);
        dokumentti.setKieli(kieli);
        dokumentti.setAloitusaika(new Date());
        dokumentti.setLuoja(SecurityUtil.getAuthenticatedPrincipal().getName());

        Opetussuunnitelma ops = opsRepository.findOne(id);
        if (ops != null) {
            dokumentti.setOpsId(id);
            Dokumentti saved = dokumenttiRepository.save(dokumentti);
            return mapper.map(saved, DokumenttiDto.class);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public DokumenttiDto getLatestDokumentti(Long ktId, Long opsId, Kieli kieli) {
        Dokumentti dokumentti = dokumenttiRepository.findFirstByOpsIdAndKieliOrderByAloitusaikaDesc(opsId, kieli);

        if (dokumentti != null) {
            // Jos aloitusajasta on kulunut liian kauan, on luonti epäonnistunut
            if (dokumentti.getTila() != DokumenttiTila.VALMIS && dokumentti.getTila() != DokumenttiTila.EI_OLE && DokumenttiUtils.isTimePass(dokumentti)) {
                dokumentti.setTila(DokumenttiTila.EPAONNISTUI);
                dokumenttiRepository.save(dokumentti);
            }
            DokumenttiDto dokumenttiDto = mapper.map(dokumentti, DokumenttiDto.class);
            DokumenttiDto julkaisuDokumentti = getJulkaistuDokumentti(ktId, opsId, kieli, null);
            if (julkaisuDokumentti != null && dokumenttiDto.getId().equals(julkaisuDokumentti.getId())) {
                dokumenttiDto.setJulkaisuDokumentti(true);
            }
            return dokumenttiDto;
        } else {
            DokumenttiDto dto = new DokumenttiDto();
            dto.setOpsId(opsId);
            dto.setKieli(kieli);
            dto.setTila(DokumenttiTila.EI_OLE);
            return dto;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DokumenttiDto getJulkaistuDokumentti(Long ktId, Long opsId, Kieli kieli, Integer revision) {
        Opetussuunnitelma ops = opsRepository.findOne(opsId);

        if (ops == null) {
            return null;
        }

        Julkaisu julkaisu;
        if (revision != null) {
            julkaisu = julkaisuRepository.findByOpetussuunnitelmaAndRevision(ops, revision);
        } else {
            julkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(ops);
        }

        if (julkaisu != null && CollectionUtils.isNotEmpty(julkaisu.getDokumentit())) {
            Dokumentti dokumentti = dokumenttiRepository.findByIdInAndKieli(julkaisu.getDokumentit(), kieli);
            if (dokumentti != null) {
                DokumenttiDto dokumenttiDto = mapper.map(dokumentti, DokumenttiDto.class);
                dokumenttiDto.setJulkaisuDokumentti(true);
                return dokumenttiDto;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void setStarted(Long ktId, Long opsId, DokumenttiDto dto) {
        // Asetetaan dokumentti luonti tilaan
        dto.setAloitusaika(new Date());
        dto.setLuoja(SecurityUtil.getAuthenticatedPrincipal().getName());
        dto.setTila(DokumenttiTila.JONOSSA);
        dokumenttiStateService.save(dto);
    }

    @Override
    @Transactional(noRollbackFor = DokumenttiException.class)
    @Async(value = "docTaskExecutor")
    public void generateWithDto(Long ktId, @NotNull Long opsId, DokumenttiDto dto) throws DokumenttiException {
        dto.setTila(DokumenttiTila.LUODAAN);
        Dokumentti dokumentti = dokumenttiStateService.save(dto);

        try {
            Opetussuunnitelma ops = opsRepository.findOne(dokumentti.getOpsId());
            dokumentti.setTila(DokumenttiTila.VALMIS);
            dokumentti.setValmistumisaika(new Date());
            dokumentti.setVirhekoodi("");
            dokumentti.setEdistyminen(DokumenttiEdistyminen.TUNTEMATON);
            dokumentti.setData(builderService.generatePdf(ops, dokumentti, dokumentti.getKieli()));

            dokumenttiRepository.save(dokumentti);
        } catch (Exception ex) {
            dto.setTila(DokumenttiTila.EPAONNISTUI);
            dto.setVirhekoodi(ex.getLocalizedMessage());
            dokumenttiStateService.save(dto);

            throw new DokumenttiException(ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DokumenttiDto get(Long ktId, Long id, Kieli kieli) {
        Dokumentti dokumentti =  getLatestValmisDokumentti(id, kieli);
        if (dokumentti != null) {
            return mapper.map(dokumentti, DokumenttiDto.class);
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getDataByDokumenttiId(Long ktId, Long id, Long dokumenttiId) {
        Dokumentti dokumentti  = dokumenttiRepository.findOne(dokumenttiId);
        if (dokumentti != null) {
            return dokumentti.getData();
        }
        return null;
    }

    public String getYllapitoValueByKey(String key) {
        return eperusteetService.getYllapitoAsetukset().stream()
                .filter(yp -> key.equals(yp.getKey()))
                .findFirst()
                .map(YllapitoDto::getValue)
                .orElse(null);
    }
}
