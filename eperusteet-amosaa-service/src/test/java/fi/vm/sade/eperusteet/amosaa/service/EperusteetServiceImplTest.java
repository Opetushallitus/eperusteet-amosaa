package fi.vm.sade.eperusteet.amosaa.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.google.common.collect.Sets;

import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliTunnisteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.SuoritustapaLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SuorituspolkuDto;
import fi.vm.sade.eperusteet.amosaa.service.external.impl.EperusteetServiceImpl;
import fi.vm.sade.eperusteet.amosaa.test.TestClassCreatorUtil;

public class EperusteetServiceImplTest {

    private final List<UUID> UUID_RANDOM_LIST = TestClassCreatorUtil.randomUUIDList();
    private final List<String> TEKSTI_LIST = IntStream.range(0, 10).mapToObj(i -> "teksti"+i).collect(Collectors.toList());
    private final List<String> KOODI_LIST = IntStream.range(0, 10).mapToObj(i -> "koodi"+i).collect(Collectors.toList());
    private final List<UUID> HIDDEN_UUIDS_LIST = Arrays.asList(UUID_RANDOM_LIST.get(3), UUID_RANDOM_LIST.get(4), UUID_RANDOM_LIST.get(6));
    
    private EperusteetServiceImpl eperusteetServiceImpl = new EperusteetServiceImpl();
    
    @Test
    public void getYksittaisenRakenteenSuoritustavatTest() {

        RakenneModuuliTunnisteDto rakenneModuuliTunnisteDto = eperusteetServiceImpl.getYksittaisenRakenteenSuoritustavat(createSuoritustapaLaajaDto(), createSisaltoviite());
        
        List<RakenneModuuliTunnisteDto> returnList = getRakenneHierarchAsList(rakenneModuuliTunnisteDto);       
        
        Assertions.assertThat(returnList).hasSize(4);
        
        // master // uuid 0
        Assertions.assertThat(returnList.get(0).getOsat()).hasSize(2);
        Assertions.assertThat(returnList.get(0).getKuvaus().get(Kieli.FI)).isEqualTo("teksti0");
        Assertions.assertThat(returnList.get(0).getKoodit()).isEqualTo(Sets.newHashSet("koodi0"));
        
        // uuid 1
        Assertions.assertThat(returnList.get(1).getOsat()).hasSize(1);
        Assertions.assertThat(returnList.get(1).getKuvaus().get(Kieli.FI)).isEqualTo("teksti1");
        Assertions.assertThat(returnList.get(1).getKoodit()).isEqualTo(Sets.newHashSet("koodi1"));
        
        // uuid 5
        Assertions.assertThat(returnList.get(2).getOsat()).hasSize(0);
        Assertions.assertThat(returnList.get(2).getKuvaus().get(Kieli.FI)).isEqualTo("teksti5");
        Assertions.assertThat(returnList.get(2).getKoodit()).isEqualTo(Sets.newHashSet("koodi5"));
        
        // uuid 2
        Assertions.assertThat(returnList.get(3).getOsat()).hasSize(0);
        Assertions.assertThat(returnList.get(3).getKuvaus().get(Kieli.FI)).isEqualTo("teksti2");
        Assertions.assertThat(returnList.get(3).getKoodit()).isEqualTo(Sets.newHashSet("koodi2"));
        
    }
    
    private List<RakenneModuuliTunnisteDto> getRakenneHierarchAsList(RakenneModuuliTunnisteDto rakenneModuuliTunnisteDto) {
        
        List<RakenneModuuliTunnisteDto> returnList = new ArrayList<>();
        returnList.add(rakenneModuuliTunnisteDto);
        
        Stack<RakenneModuuliTunnisteDto> stack = new Stack<>();
        stack.push(rakenneModuuliTunnisteDto);
        
        while(!stack.isEmpty()) {
            RakenneModuuliTunnisteDto rakenne = stack.pop();
            if(rakenne.getOsat() != null) {
                rakenne.getOsat().forEach(r -> {
                    stack.push((RakenneModuuliTunnisteDto)r);
                    returnList.add((RakenneModuuliTunnisteDto)r);
                });
            }
        }
        
        return returnList;
    }

    private SisaltoViiteDto createSisaltoviite() {
        SuorituspolkuDto suorituspolkuDto = new SuorituspolkuDto();        
        suorituspolkuDto.setRivit(
            IntStream.range(0, 10)
                .mapToObj(i -> SuorituspolkuRiviDto
                        .of(
                            UUID_RANDOM_LIST.get(i), 
                            HIDDEN_UUIDS_LIST.contains(UUID_RANDOM_LIST.get(i)), 
                            LokalisoituTekstiDto.of(TEKSTI_LIST.get(i)), 
                            Sets.newHashSet(KOODI_LIST.get(i))))
                .collect(Collectors.toSet()));
        
        
        SisaltoViiteDto sisaltoViiteDto = new SisaltoViiteDto();
        sisaltoViiteDto.setSuorituspolku(suorituspolkuDto);
        
        return sisaltoViiteDto;
    }

    private SuoritustapaLaajaDto createSuoritustapaLaajaDto() {
                
        RakenneModuuliDto rakenne = 
                TestClassCreatorUtil.rakenneModuuliDto(
                        UUID_RANDOM_LIST.get(0),
                        Arrays.asList(
                                TestClassCreatorUtil.rakenneModuuliDto(
                                        UUID_RANDOM_LIST.get(1),
                                        Arrays.asList(
                                                TestClassCreatorUtil.rakenneModuuliDto(UUID_RANDOM_LIST.get(2), null),
                                                TestClassCreatorUtil.rakenneModuuliDto(UUID_RANDOM_LIST.get(3), null),
                                                TestClassCreatorUtil.rakenneModuuliDto(UUID_RANDOM_LIST.get(4), null))),
                                TestClassCreatorUtil.rakenneModuuliDto(
                                        UUID_RANDOM_LIST.get(5),
                                        Arrays.asList(
                                                TestClassCreatorUtil.rakenneModuuliDto(
                                                        UUID_RANDOM_LIST.get(6), 
                                                        Arrays.asList(
                                                                TestClassCreatorUtil.rakenneModuuliDto(UUID_RANDOM_LIST.get(7),null),
                                                                TestClassCreatorUtil.rakenneModuuliDto(UUID_RANDOM_LIST.get(8),null),
                                                                TestClassCreatorUtil.rakenneModuuliDto(UUID_RANDOM_LIST.get(9),null)
                                                                ))))
                                )
                        );
          
        SuoritustapaLaajaDto suoritustapaLaajaDto = new SuoritustapaLaajaDto();
        suoritustapaLaajaDto.setRakenne(rakenne);
        
        return suoritustapaLaajaDto;
    }
}
