package fi.vm.sade.eperusteet.amosaa.dto.koodisto;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.KooditettuDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class KoodistoKoodiDtoTest {

    private static final String KOODISTO_JSON = """
            {
              "koodiArvo":"004",
              "koodiUri":"tutkintokoulutukseenvalmentavakoulutuslaajaalainenosaaminen_004",
              "koodisto":{
                "koodistoUri":"tutkintokoulutukseenvalmentavakoulutuslaajaalainenosaaminen",
                "koodistoVersios":[1],
                "organisaatioOid":"1.2.246.562.10.00000000001"
              },
              "metadata":[
                {
                  "nimi":"Vuorovaikutusosaaminen",
                  "kuvaus":"käytössä TUVA",
                  "lyhytNimi":"",
                  "kayttoohje":"",
                  "kasite":"",
                  "sisaltaaMerkityksen":"",
                  "eiSisallaMerkitysta":"",
                  "huomioitavaKoodi":"",
                  "sisaltaaKoodiston":"",
                  "kieli":"FI"
                },
                {
                  "nimi":"Kommunikativ kompetens",
                  "kuvaus":"",
                  "lyhytNimi":"",
                  "kayttoohje":"",
                  "kasite":"",
                  "sisaltaaMerkityksen":"",
                  "eiSisallaMerkitysta":"",
                  "huomioitavaKoodi":"",
                  "sisaltaaKoodiston":"",
                  "kieli":"SV"
                }
              ],
              "paivittajaOid":"1.2.246.562.24.77925329174",
              "paivitysPvm":"2021-06-07",
              "resourceUri":"https://virkailija.testiopintopolku.fi/koodisto-service/rest/codeelement/tutkintokoulutukseenvalmentavakoulutuslaajaalainenosaaminen_004",
              "tila":"LUONNOS",
              "versio":1,
              "version":1,
              "voimassaAlkuPvm":"2021-05-25",
              "voimassaLoppuPvm":null
            }
            """;

    @Test
    public void nimiDerivedFromMetadataWhenJacksonDoesNotCallSetters() throws Exception {
        ObjectMapper mapper = new MappingJackson2HttpMessageConverter().getObjectMapper();

        KoodistoKoodiDto koodi = mapper.readValue(KOODISTO_JSON, KoodistoKoodiDto.class);

        assertThat(koodi.getMetadata()).hasSize(2);
        assertThat(koodi.getNimi()).isNotNull();
        assertThat(koodi.getNimi().get(Kieli.FI)).isEqualTo("Vuorovaikutusosaaminen");
        assertThat(koodi.getNimi().get(Kieli.SV)).isEqualTo("Kommunikativ kompetens");
    }

    @Test
    public void nimiAvailableForKooditettuMapping() {
        KoodistoKoodiDto koodi = KoodistoKoodiDto.builder()
                .metadata(new KoodistoMetadataDto[]{
                        KoodistoMetadataDto.of("Vuorovaikutusosaaminen", "FI", "käytössä TUVA"),
                        KoodistoMetadataDto.of("Kommunikativ kompetens", "SV", "")
                })
                .build();

        TestKooditettuDto target = new TestKooditettuDto();
        target.setKoodistoKoodi(koodi);

        assertThat(target.nimi.get(Kieli.FI)).isEqualTo("Vuorovaikutusosaaminen");
        assertThat(target.nimi.get(Kieli.SV)).isEqualTo("Kommunikativ kompetens");
    }

    private static class TestKooditettuDto implements KooditettuDto {
        private LokalisoituTekstiDto nimi;

        @Override
        public void setKooditettu(LokalisoituTekstiDto kooditettu) {
            this.nimi = kooditettu;
        }

        @Override
        public void setKooditettu(LokalisoituTekstiDto kooditettu, Date voimassaAlkuPvm, Date voimassaLoppuPvm) {
            this.nimi = kooditettu;
        }
    }
}
