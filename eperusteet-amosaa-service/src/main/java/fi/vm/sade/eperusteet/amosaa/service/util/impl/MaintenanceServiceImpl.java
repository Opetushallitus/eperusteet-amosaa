package fi.vm.sade.eperusteet.amosaa.service.util.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.Tila;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaisuData;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Koulutustoimija;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.CachedPerusteRepository;
import fi.vm.sade.eperusteet.amosaa.repository.peruste.KoulutuskoodiRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.BusinessRuleViolationException;
import fi.vm.sade.eperusteet.amosaa.service.external.OrganisaatioService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaMuokkaustietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.util.JsonMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.MaintenanceService;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import fi.vm.sade.eperusteet.amosaa.service.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@Profile("!test")
public class MaintenanceServiceImpl implements MaintenanceService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceServiceImpl.class);

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private PlatformTransactionManager ptm;

    @Autowired
    private JulkaisuRepository julkaisuRepository;

    @Autowired
    private OpetussuunnitelmaService opetussuunnitelmaService;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private OpetussuunnitelmaMuokkaustietoService opetussuunnitelmaMuokkaustietoService;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired()
    private List<? extends Job> jobs;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CachedPerusteRepository cachedPerusteRepository;

    @Autowired
    private KoulutuskoodiRepository koulutuskoodiRepository;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private OrganisaatioService organisaatioService;

    @Deprecated
    @Override
    @Async
    @Transactional(propagation = Propagation.NEVER)
    public void teeJulkaisut(boolean julkaiseKaikki, Set<KoulutusTyyppi> koulutustyypit, OpsTyyppi opsTyyppi) {
        List<Opetussuunnitelma> opetussuunnitelmat;
        if (koulutustyypit != null) {
            opetussuunnitelmat = opetussuunnitelmaRepository.findJulkaistutByTyyppi(opsTyyppi, koulutustyypit);
        } else {
            opetussuunnitelmat = opetussuunnitelmaRepository.findJulkaistutByTyyppi(opsTyyppi);
        }

        List<Long> ids = opetussuunnitelmat.stream()
                .filter(peruste -> julkaiseKaikki || CollectionUtils.isEmpty(peruste.getJulkaisut()))
                .map(Opetussuunnitelma::getId)
                .collect(Collectors.toList());

        logger.info("Julkaistavien perusteiden lukumäärä: " + ids.size());

        for (Long opsId : ids) {
            try {
                teeJulkaisu(opsId);
            } catch (RuntimeException ex) {
                logger.error(ex.getLocalizedMessage(), ex);
            }
        }

        logger.info("julkaisut tehty");
    }

    @Override
    @Async
    public void kaynnistaJob(String job, Map<String, String> parametrit) throws Exception {
        SecurityContextHolder.getContext().setAuthentication(SecurityUtil.useAdminAuth());
        parametrit.put("kaynnistysaika", String.valueOf(new Date().getTime()));
        jobLauncher.run(jobs.stream().filter(j -> j.getName().equals(job))
                        .findFirst()
                        .orElseThrow(() -> new BusinessRuleViolationException("Jobia ei löydy")),
                new JobParameters(parametrit.keySet().stream().collect(Collectors.toMap(k -> k, k -> new JobParameter(parametrit.get(k))))));
    }

    private void teeJulkaisu(Long opsId) {
        TransactionTemplate template = new TransactionTemplate(ptm);
        template.execute(status -> {
            try {
                Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);
                logger.info("Luodaan julkaisu opetussuunnitelmalle: " + opetussuunnitelma.getId());
                Julkaisu viimeisinJulkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(opetussuunnitelma);

                OpetussuunnitelmaKaikkiDto sisalto = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opetussuunnitelma.getKoulutustoimija().getId(), opetussuunnitelma.getId());
                Julkaisu julkaisu = new Julkaisu();
                julkaisu.setRevision(viimeisinJulkaisu != null ? viimeisinJulkaisu.getRevision() + 1 : 1);
                julkaisu.setLuoja("maintenance");
                julkaisu.setTiedote(LokalisoituTeksti.of(Kieli.FI, "Ylläpidon suorittama julkaisu"));
                julkaisu.setLuotu(new Date());
                julkaisu.setOpetussuunnitelma(opetussuunnitelma);

                ObjectNode dataJson = (ObjectNode) jsonMapper.toJson(sisalto);
                julkaisu.setData(new JulkaisuData(dataJson));
                julkaisuRepository.save(julkaisu);

                opetussuunnitelmaMuokkaustietoService.addOpsMuokkausTieto(opsId, opetussuunnitelma, MuokkausTapahtuma.JULKAISU, opetussuunnitelma.getNavigationType(), null, "maintenance");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        });
    }

    @Override
    public void clearCache(String cache) {
        Objects.requireNonNull(cacheManager.getCache(cache)).clear();
    }

    @Override
    public void poistaJulkaisut(Long opsId) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);

        List<Julkaisu> julkaisut = julkaisuRepository.findAllByOpetussuunnitelma(ops);
        julkaisut.forEach(julkaisu -> julkaisuRepository.delete(julkaisu));

        ops.setTila(Tila.LUONNOS);
        opetussuunnitelmaRepository.save(ops);
    }

    @Override
    public void paivitaKoulutustoimijaOppilaitostyypi() {
        koulutustoimijaRepository.findAll().forEach(kt -> {
            JsonNode organisaatio = organisaatioService.getOrganisaatio(kt.getOrganisaatio());
            if (organisaatio == null) {
                return;
            }

            if (!ObjectUtils.isEmpty(organisaatio.get("oppilaitosTyyppiUri"))) {
                kt.setOppilaitosTyyppiKoodiUri(organisaatio.get("oppilaitosTyyppiUri").asText().replaceAll("#[0-9]+", ""));
            }

            koulutustoimijaRepository.save(kt);
        });
    }

}
