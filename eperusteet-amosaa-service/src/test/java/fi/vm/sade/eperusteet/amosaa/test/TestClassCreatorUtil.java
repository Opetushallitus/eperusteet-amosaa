package fi.vm.sade.eperusteet.amosaa.test;

import java.util.List;
import java.util.UUID;

import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.peruste.CachedPeruste;
import fi.vm.sade.eperusteet.amosaa.dto.ops.SuorituspolkuRiviDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.AbstractRakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.OsaamisalaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliRooli;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestClassCreatorUtil {

    public static final Long CACHEPERUSTE_ID = 44l;

    public static RakenneModuuliDto rakenneModuuliDtoWithOsaamisalaUri(UUID osaamisaalaUri) {
        return rakenneModuuliDto(osaamisaalaUri, null, null);
    }

    public static RakenneModuuliDto rakenneModuuliDtoWithTutkimusnimikeUri(UUID tutkimusnimikeUri) {
        return rakenneModuuliDto(null, tutkimusnimikeUri, null);
    }

    public static RakenneModuuliDto rakenneModuuliDtoWithOsaamisalaUri(UUID osaamisaalaUri,
            List<AbstractRakenneOsaDto> osat) {
        return rakenneModuuliDto(osaamisaalaUri, null, osat);
    }

    public static RakenneModuuliDto rakenneModuuliDtoWithTutkimusnimikeUri(UUID tutkimusnimikeUri,
            List<AbstractRakenneOsaDto> osat) {
        return rakenneModuuliDto(null, tutkimusnimikeUri, osat);
    }

    public static RakenneModuuliDto rakenneModuuliDto() {
        return rakenneModuuliDto(null, null, null);
    }

    public static RakenneModuuliDto rakenneModuuliDto(UUID osaamisalaUri, UUID tutkimusnimikeUri,
            List<AbstractRakenneOsaDto> osat) {

        RakenneModuuliDto rakenneDto = new RakenneModuuliDto();

        if (osaamisalaUri != null) {
            rakenneDto.setTunniste(osaamisalaUri);

            rakenneDto.setRooli(RakenneModuuliRooli.OSAAMISALA);
            rakenneDto.setOsaamisala(new OsaamisalaDto());
            rakenneDto.getOsaamisala().setOsaamisalakoodiUri(osaamisalaUri.toString());
        }

        if (tutkimusnimikeUri != null) {
            rakenneDto.setTunniste(tutkimusnimikeUri);

            rakenneDto.setRooli(RakenneModuuliRooli.TUTKINTONIMIKE);
            rakenneDto.setTutkintonimike(new KoodiDto());
            rakenneDto.getTutkintonimike().setUri(tutkimusnimikeUri.toString());
        }

        if (osat != null) {
            rakenneDto.setOsat(osat);
        }

        return rakenneDto;
    }

    public static Opetussuunnitelma opetussuunnitelma() {

        Opetussuunnitelma opetussuunnitelma = new Opetussuunnitelma();
        opetussuunnitelma.setPeruste(cachedPeruste(CACHEPERUSTE_ID));

        return opetussuunnitelma;
    }

    public static CachedPeruste cachedPeruste(Long id) {

        CachedPeruste cachedPeruste = new CachedPeruste();
        cachedPeruste.setId(id);

        return cachedPeruste;
    }

}
