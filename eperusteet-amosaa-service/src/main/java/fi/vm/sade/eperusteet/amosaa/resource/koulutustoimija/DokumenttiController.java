package fi.vm.sade.eperusteet.amosaa.resource.koulutustoimija;

import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiTila;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.dto.dokumentti.DokumenttiDto;
import fi.vm.sade.eperusteet.amosaa.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.amosaa.resource.util.CacheControl;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiService;
import fi.vm.sade.eperusteet.amosaa.service.exception.DokumenttiException;
import io.swagger.annotations.Api;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * @author isaul
 */
@RestController
@RequestMapping("/koulutustoimijat/{ktId}/opetussuunnitelmat/{opsId}/dokumentti")
@Api(value = "dokumentit")
public class DokumenttiController {
    @Autowired
    DokumenttiService service;

    @Autowired
    DokumenttiRepository repository;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<DokumenttiDto> create(
            @PathVariable Long ktId,
            @PathVariable Long opsId,
            @RequestParam(value = "kieli", defaultValue = "fi") String kieli) throws DokumenttiException {
        DokumenttiDto dokumenttiDto = service.getDto(opsId, Kieli.of(kieli));
        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpStatus status = HttpStatus.ACCEPTED;
        int maxTimeInMinutes = 2;

        // Aloitetaan luonti jos luonti ei ole jo päällä tai maksimi luontiaika ylitetty
        if (dokumenttiDto.getAloitusaika() == null
                || DateUtils.addMinutes(dokumenttiDto.getAloitusaika(), maxTimeInMinutes).before(new Date())
                || dokumenttiDto.getTila() != DokumenttiTila.LUODAAN) {
            // Vaihdetaan dokumentin tila luonniksi
            service.setStarted(dokumenttiDto);

            // Generoidaan dokumentin datasisältö
            // Asynkroninen metodi
            service.generateWithDto(dokumenttiDto);
        } else {
            status = HttpStatus.FORBIDDEN;
        }

        return new ResponseEntity<>(dokumenttiDto, status);
    }

    @RequestMapping(method = RequestMethod.GET)
    @CacheControl(age = CacheControl.ONE_YEAR, nonpublic = false)
    public ResponseEntity<byte[]> get(
            @PathVariable Long ktId,
            @PathVariable Long opsId,
            @RequestParam(value = "kieli", defaultValue = "fi") String kieli) {
        byte[] pdfdata = service.get(opsId, Kieli.of(kieli));

        if (pdfdata == null || pdfdata.length == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "pdf"));
        headers.set("Content-disposition", "inline; filename=\"" + opsId + ".pdf\"");
        headers.setContentLength(pdfdata.length);

        return new ResponseEntity<>(pdfdata, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/tila", method = RequestMethod.GET)
    public ResponseEntity<DokumenttiDto> query(@PathVariable Long ktId,
                                                @PathVariable Long opsId,
                                                @RequestParam(value = "kieli", defaultValue = "fi") String kieli) {

        // Tehdään DokumenttiDto jos ei löydy jo valmiina
        DokumenttiDto dokumenttiDto = service.getDto(opsId, Kieli.of(kieli));

        if (dokumenttiDto == null) {
            dokumenttiDto = service.createDtoFor(opsId, Kieli.of(kieli));
        }

        if (dokumenttiDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(dokumenttiDto);
        }
    }

    @Transactional
    @RequestMapping(value = "/kuva", method=RequestMethod.POST)
    public ResponseEntity<Object> addImage(@PathVariable Long ktId,
                                            @PathVariable Long opsId,
                                            @RequestParam String tyyppi,
                                            @RequestParam(defaultValue = "fi") String kieli,
                                            @RequestPart(required = true) MultipartFile file) {

        try {
            if (tyyppi != null && !file.isEmpty()) {
                //byte[] image = file.getBytes();

                BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

                /*int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();

                // Muutetaan kaikkien kuvien väriavaruus RGB:ksi jotta PDF/A validointi menee läpi
                // Asetetaan lisäksi läpinäkyvien kuvien taustaksi valkoinen väri
                BufferedImage tempImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                        BufferedImage.TYPE_3BYTE_BGR);
                tempImage.getGraphics().setColor(new Color(255, 255, 255, 0));
                tempImage.getGraphics().fillRect (0, 0, width, height);
                tempImage.getGraphics().drawImage(bufferedImage, 0, 0, null);

                bufferedImage = tempImage;*/

                // Muutetaan kuva PNG:ksi
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", baos);
                baos.flush();
                byte[] image = baos.toByteArray();
                baos.close();

                // todo: tarkista onko tiedosto sallittu kuva

                // Tehdään DokumenttiDto jos ei löydy jo valmiina
                DokumenttiDto dokumenttiDto = service.getDto(opsId, Kieli.of(kieli));
                if (dokumenttiDto == null) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                // Haetaan domain dokumentti
                Dokumentti dokumentti = repository.findByOpsIdAndKieli(opsId, Kieli.of(kieli));

                switch (tyyppi) {
                    case "kansi":
                        dokumentti.setKansikuva(image);
                        break;
                    case "ylatunniste":
                        dokumentti.setYlatunniste(image);
                        break;
                    case "alatunniste":
                        dokumentti.setAlatunniste(image);
                        break;
                    default:
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                repository.save(dokumentti);

                return new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (IOException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Transactional
    @RequestMapping(value = "/kuva", method=RequestMethod.GET)
    public ResponseEntity<Object> addImage(@PathVariable Long ktId,
                                            @PathVariable Long opsId,
                                            @RequestParam String tyyppi,
                                            @RequestParam(defaultValue = "fi") String kieli) {
        if (tyyppi != null) {
            // Tehdään DokumenttiDto jos ei löydy jo valmiina
            DokumenttiDto dokumenttiDto = service.getDto(opsId, Kieli.of(kieli));
            if (dokumenttiDto == null) {
                return new ResponseEntity<>(HttpStatus.OK);
            }

            // Haetaan kuva
            Dokumentti dokumentti = repository.findByOpsIdAndKieli(opsId, Kieli.of(kieli));
            if (dokumentti == null) {
                return new ResponseEntity<>(HttpStatus.OK);
            }

            byte[] image;

            switch (tyyppi) {
                case "kansi":
                    image = dokumentti.getKansikuva();
                    break;
                case "ylatunniste":
                    image = dokumentti.getYlatunniste();
                    break;
                case "alatunniste":
                    image = dokumentti.getAlatunniste();
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (image == null || image.length == 0) {
                return new ResponseEntity<>(HttpStatus.OK);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(image.length);

            return new ResponseEntity<>(image, headers, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
