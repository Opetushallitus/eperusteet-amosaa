package fi.vm.sade.eperusteet.amosaa.service.external.impl;

import fi.vm.sade.eperusteet.amosaa.domain.arviointi.Arviointiasteikko;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiasteikkoDto;
import fi.vm.sade.eperusteet.amosaa.repository.ops.ArviointiasteikkoRepository;
import fi.vm.sade.eperusteet.amosaa.service.external.ArviointiasteikkoService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Transactional
public class ArviointiasteikkoServiceImpl implements ArviointiasteikkoService {

    @Value("${fi.vm.sade.eperusteet.amosaa.eperusteet-service: ''}")
    private String eperusteetServiceUrl;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private ArviointiasteikkoRepository arviointiasteikkoRepository;

    @Autowired
    private HttpEntity httpEntity;

    @Override
    public List<ArviointiasteikkoDto> getAll() {
        List<Arviointiasteikko> arviointiasteikot = arviointiasteikkoRepository.findAll();

        // Ladataan perusteesta
        if (arviointiasteikot.isEmpty()) {
            arviointiasteikot = getAllFromPeruste();
        }

        return mapper.mapAsList(arviointiasteikot, ArviointiasteikkoDto.class);
    }

    private List<Arviointiasteikko> getAllFromPeruste() {
        ResponseEntity<List<ArviointiasteikkoDto>> res = new RestTemplate()
                .exchange(eperusteetServiceUrl + "/api/arviointiasteikot",
                        HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {
                        });
        List<Arviointiasteikko> arviointiasteikot = mapper.mapAsList(res.getBody(), Arviointiasteikko.class);

        arviointiasteikot.forEach(asteikko -> arviointiasteikkoRepository.save(asteikko));
        return arviointiasteikot;
    }

    @Override
    public ArviointiasteikkoDto get(Long id) {
        if (arviointiasteikkoRepository.count() == 0) {
            getAllFromPeruste();
        }

        return mapper.map(arviointiasteikkoRepository.findOne(id), ArviointiasteikkoDto.class);
    }

    @Override
    public void update() {
        ResponseEntity<List<ArviointiasteikkoDto>> res = new RestTemplate()
                .exchange(eperusteetServiceUrl + "/api/arviointiasteikot",
                        HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {
                        });
        mapper.mapAsList(res.getBody(), Arviointiasteikko.class)
                .forEach(arviointiasteikko -> arviointiasteikkoRepository.save(arviointiasteikko));
    }
}
