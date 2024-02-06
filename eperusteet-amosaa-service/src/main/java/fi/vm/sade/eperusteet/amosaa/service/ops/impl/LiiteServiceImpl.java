package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.liite.Liite;
import fi.vm.sade.eperusteet.amosaa.dto.liite.LiiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.KoulutustoimijaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.koulutustoimija.OpetussuunnitelmaRepository;
import fi.vm.sade.eperusteet.amosaa.repository.liite.LiiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.exception.NotExistsException;
import fi.vm.sade.eperusteet.amosaa.service.exception.ServiceException;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.LiiteService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Transactional
public class LiiteServiceImpl implements LiiteService {
    @Autowired
    private LiiteRepository liiteRepository;

    @Autowired
    private KoulutustoimijaRepository koulutustoimijaRepository;

    @Autowired
    private OpetussuunnitelmaRepository opetussuunnitelmaRepository;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    DtoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public void export(Long opsId, UUID id, OutputStream os) {
        Liite liite = liiteRepository.findOne(id);
        InputStream liiteInputStream;

        try {
            if (liite != null) {
                liiteInputStream = liite.getData().getBinaryStream();
            } else {
                liiteInputStream = exportLiitePerusteelta(opsId, id);
            }

            IOUtils.copy(liiteInputStream, os);
        } catch (SQLException | IOException ex) {
            throw new ServiceException("Liiteen lataaminen ei onnistu", ex);
        }
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = NotExistsException.class)
    public InputStream exportLiitePerusteelta(Long opsId, UUID id) {
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        if (ops.getPeruste() == null) {
            throw new NotExistsException("liite-ei-ole");
        }

        try {
            byte[] liite = eperusteetService.getLiite(ops.getPeruste().getPerusteId(), id);
            if (liite.length > 0) {
                return new ByteArrayInputStream(liite);
            } else {
                throw new NotExistsException("liite-ei-ole");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new NotExistsException("liite-ei-ole");
            }

            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LiiteDto get(Long opsId, UUID id) {
        Liite liite = liiteRepository.findOne(id);
        return mapper.map(liite, LiiteDto.class);
    }

    @Override
    public UUID add(Long ktId, Long opsId, String tyyppi, String nimi, long length, InputStream is) {
        Liite liite = liiteRepository.add(tyyppi, nimi, length, is);
//        Koulutustoimija kt = koulutustoimijaRepository.findOne(ktId);
        Opetussuunnitelma ops = opetussuunnitelmaRepository.findOne(opsId);
        ops.attachLiite(liite);
        return liite.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LiiteDto> getAll(Long ktId, Long opsId) {
        return mapper.mapAsList(liiteRepository.findByOpetussuunnitelmaId(opsId), LiiteDto.class);
    }

    @Override
    public void delete(Long ktId, Long opsId, UUID id) {
        Liite liite = liiteRepository.findOne(ktId, id);
        if (liite == null) {
            throw new NotExistsException("Liitettä ei ole");
        }
        koulutustoimijaRepository.findOne(ktId).removeLiite(liite);
    }
}
