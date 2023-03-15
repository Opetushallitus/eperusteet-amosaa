package fi.vm.sade.eperusteet.amosaa.service.ops.impl;

import fi.vm.sade.eperusteet.amosaa.dto.OletusToteutusDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OmaOsaAlueDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OmaOsaAlueToteutusDto;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.OmaOsaAlueRepository;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.OsaAlueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OsaAlueServiceImpl implements OsaAlueService {

    @Autowired
    private OmaOsaAlueRepository omaOsaAlueRepository;

    @Autowired
    private DtoMapper dtoMapper;

    @Override
    public List<OletusToteutusDto> osaAlueidenOletusToteutukset(Long ktId, Long opsId) {
        List<OmaOsaAlueDto> osaAlueet = dtoMapper.mapAsList(omaOsaAlueRepository.findTutkinnonosienOletusotetutukset(opsId), OmaOsaAlueDto.class);
        return osaAlueet.stream().map(osaAlue -> osaAlue.getToteutukset().stream().filter(OmaOsaAlueToteutusDto::isOletustoteutus)
                .map(toteutus -> {
                    OletusToteutusDto oletustoteutus = dtoMapper.map(toteutus, OletusToteutusDto.class);
                    oletustoteutus.setLahdeNimi(osaAlue.getNimi());
                    return oletustoteutus;
                }).collect(Collectors.toList())
        ).flatMap(Collection::stream).collect(Collectors.toList());
    }

}
