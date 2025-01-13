package fi.vm.sade.eperusteet.amosaa.domain.validation;

import fi.vm.sade.eperusteet.amosaa.domain.Osaamistaso;
import fi.vm.sade.eperusteet.amosaa.domain.OsaamistasonKriteeri;
import fi.vm.sade.eperusteet.amosaa.domain.arviointi.ArvioinninKohde;
import fi.vm.sade.eperusteet.amosaa.domain.arviointi.Arviointiasteikko;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidArvioinninKohdeValidator implements ConstraintValidator<ValidArvioinninKohde, ArvioinninKohde> {

    @Override
    public void initialize(ValidArvioinninKohde constraintAnnotation) {
        //NOP
    }

    @Override
    public boolean isValid(ArvioinninKohde arvioinninKohde, ConstraintValidatorContext context) {
        if (arvioinninKohde == null || arvioinninKohde.getOsaamistasonKriteerit() == null || arvioinninKohde.getOsaamistasonKriteerit().isEmpty()) {
            return true;
        }

        if (arvioinninKohde.getArviointiasteikko() == null
                || arvioinninKohde.getArviointiasteikko().getOsaamistasot().size() != arvioinninKohde.getOsaamistasonKriteerit().size()) {
            return false;
        }

        for (OsaamistasonKriteeri kriteeri : arvioinninKohde.getOsaamistasonKriteerit()) {
            if (kriteeri.getOsaamistaso() == null || !osaamistasoExistsInArviointiasteikko(kriteeri.getOsaamistaso(), arvioinninKohde.getArviointiasteikko())) {
                return false;
            }
        }

        return true;
    }

    private boolean osaamistasoExistsInArviointiasteikko(Osaamistaso targetOsaamistaso, Arviointiasteikko arviointiasteikko) {
        for (Osaamistaso osaamistaso : arviointiasteikko.getOsaamistasot()) {
            if (osaamistaso.getId().equals(targetOsaamistaso.getId())) {
                return true;
            }
        }
        return false;
    }
}
