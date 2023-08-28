package fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiEdistyminen;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.dto.util.YllapitoAvaimet;
import fi.vm.sade.eperusteet.amosaa.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiBuilderService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiStateService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.ExternalPdfService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiUtils;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
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

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Profile("default")
@Slf4j
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

    @Autowired
    private ExternalPdfService externalPdfService;

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
    @Transactional(noRollbackFor = DokumenttiException.class, propagation = Propagation.REQUIRES_NEW)
    @Async(value = "docTaskExecutor")
    public void generateWithDto(Long ktId, @NotNull Long opsId, DokumenttiDto dto) throws DokumenttiException {
        dto.setTila(DokumenttiTila.LUODAAN);
        Dokumentti dokumentti = dokumenttiStateService.save(dto);

        try {
            boolean isPdfServiceUsed = Boolean.parseBoolean(eperusteetService.getYllapitoAsetus(YllapitoAvaimet.USE_PDF_SERVICE_AMOSAA));
            if (isPdfServiceUsed) {
                externalPdfService.generatePdf(dto, ktId);
            } else {
                Opetussuunnitelma ops = opsRepository.findOne(dokumentti.getOpsId());
                dokumentti.setTila(DokumenttiTila.VALMIS);
                dokumentti.setValmistumisaika(new Date());
                dokumentti.setVirhekoodi("");
                dokumentti.setEdistyminen(DokumenttiEdistyminen.TUNTEMATON);
                dokumentti.setData(builderService.generatePdf(ops, dokumentti, dokumentti.getKieli()));

                dokumenttiRepository.save(dokumentti);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
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

    @Override
    public void updateDokumenttiPdfData(byte[] data, Long dokumenttiId) {
        Dokumentti dokumentti = dokumenttiRepository.findById(dokumenttiId);
        dokumentti.setData(data);
        dokumentti.setTila(DokumenttiTila.VALMIS);
        dokumentti.setValmistumisaika(new Date());
        dokumentti.setVirhekoodi(null);
        dokumenttiRepository.save(dokumentti);
    }

    @Override
    public void updateDokumenttiTila(DokumenttiTila tila, Long dokumenttiId) {
        Dokumentti dokumentti = dokumenttiRepository.findById(dokumenttiId);
        dokumentti.setTila(tila);
        dokumenttiRepository.save(dokumentti);
    }
}
