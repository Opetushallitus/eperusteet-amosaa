package fi.vm.sade.eperusteet.amosaa.service.util.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.MuokkausTapahtuma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Julkaisu;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.JulkaisuData;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.OpsTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.dto.koulutustoimija.OpetussuunnitelmaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.JulkaisuRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaMuokkaustietoService;
import fi.vm.sade.eperusteet.amosaa.service.koulutustoimija.OpetussuunnitelmaService;
import fi.vm.sade.eperusteet.amosaa.service.util.JsonMapper;
import fi.vm.sade.eperusteet.amosaa.service.util.MaintenanceService;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

@Service
@Transactional
@Slf4j
@Profile("!test")
public class MaintenanceServiceImpl implements MaintenanceService {

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

    @Override
    @Async
    @Transactional(propagation = Propagation.NEVER)
    public void teeJulkaisut(boolean julkaiseKaikki, Set<KoulutusTyyppi> koulutustyypit) {
        List<Opetussuunnitelma> opetussuunnitelmat;
        if (koulutustyypit != null) {
            opetussuunnitelmat = opetussuunnitelmaRepository.findJulkaistutByTyyppi(OpsTyyppi.OPS, koulutustyypit);
        } else {
            opetussuunnitelmat = opetussuunnitelmaRepository.findJulkaistutByTyyppi(OpsTyyppi.OPS);
        }

        List<Long> ids = opetussuunnitelmat.stream()
                .filter(peruste -> julkaiseKaikki || CollectionUtils.isEmpty(peruste.getJulkaisut()))
                .map(Opetussuunnitelma::getId)
                .collect(Collectors.toList());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        for (Long opsId : ids) {
            try {
                teeJulkaisu(username, opsId);
            } catch (RuntimeException ex) {
                log.error(ex.getLocalizedMessage(), ex);
            }
        }

        log.info("julkaisut tehty");
    }

    private void teeJulkaisu(String username, Long opsId) {
        TransactionTemplate template = new TransactionTemplate(ptm);
        template.execute(status -> {
            try {
                Opetussuunnitelma opetussuunnitelma = opetussuunnitelmaRepository.findOne(opsId);
                log.info("Luodaan julkaisu opetussuunnitelmalle: " + opetussuunnitelma.getId());
                Julkaisu viimeisinJulkaisu = julkaisuRepository.findFirstByOpetussuunnitelmaOrderByRevisionDesc(opetussuunnitelma);

                OpetussuunnitelmaKaikkiDto sisalto = opetussuunnitelmaService.getOpetussuunnitelmaKaikki(opetussuunnitelma.getKoulutustoimija().getId(), opetussuunnitelma.getId());
                Julkaisu julkaisu = new Julkaisu();
                julkaisu.setRevision(viimeisinJulkaisu != null ? viimeisinJulkaisu.getRevision() + 1 : 1);
                julkaisu.setLuoja("maintenance");
                julkaisu.setTiedote(LokalisoituTeksti.of(Kieli.FI, "Julkaisu"));
                julkaisu.setLuotu(new Date());
                julkaisu.setOpetussuunnitelma(opetussuunnitelma);

                ObjectNode dataJson = (ObjectNode) jsonMapper.toJson(sisalto);
                julkaisu.setData(new JulkaisuData(dataJson));
                julkaisuRepository.save(julkaisu);

                opetussuunnitelmaMuokkaustietoService.addOpsMuokkausTieto(opsId, opetussuunnitelma, MuokkausTapahtuma.JULKAISU);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        });
    }
}
