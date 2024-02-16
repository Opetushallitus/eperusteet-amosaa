package fi.vm.sade.eperusteet.amosaa.resource.config;

import fi.vm.sade.eperusteet.amosaa.domain.KoulutusTyyppi;

import java.beans.PropertyEditorSupport;

public class KoulutustyyppiEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(KoulutusTyyppi.of(text));
    }
}
