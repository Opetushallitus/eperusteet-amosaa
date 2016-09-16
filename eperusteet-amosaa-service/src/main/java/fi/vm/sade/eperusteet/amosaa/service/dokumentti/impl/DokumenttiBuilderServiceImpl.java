/*
 * Copyright (c) 2013 The Finnish Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://ec.europa.eu/idabc/eupl
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */

package fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl;

import fi.vm.sade.eperusteet.amosaa.domain.OsaamistasonKriteeri;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset.AmmattitaitovaatimuksenKohde;
import fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset.AmmattitaitovaatimuksenKohdealue;
import fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset.Ammattitaitovaatimus;
import fi.vm.sade.eperusteet.amosaa.domain.arviointi.ArvioinninKohde;
import fi.vm.sade.eperusteet.amosaa.domain.arviointi.ArvioinninKohdealue;
import fi.vm.sade.eperusteet.amosaa.domain.arviointi.Arviointi;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.DokumenttiEdistyminen;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.SuorituspolkuRivi;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Suorituspolku;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.TutkinnonosaToteutus;
import fi.vm.sade.eperusteet.amosaa.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.*;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiBuilderService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.LocalizedMessagesService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.PdfService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.CharapterNumberGenerator;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiBase;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiRivi;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiTaulukko;
import static fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiUtils.*;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.LiiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.TermistoService;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.*;
import org.apache.xml.security.utils.Base64;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author iSaul
 */
@Service
public class DokumenttiBuilderServiceImpl implements DokumenttiBuilderService {

    private static final Logger LOG = LoggerFactory.getLogger(DokumenttiBuilderServiceImpl.class);

    private static final float COMPRESSION_LEVEL = 0.9f;

    @Autowired
    private LiiteService liiteService;

    @Autowired
    private DokumenttiRepository dokumenttiRepository;

    @Autowired
    private SisaltoviiteRepository tkvRepository;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private TermistoService termistoService;

    @Autowired
    private EperusteetService eperusteetService;

    @Autowired
    private LocalizedMessagesService messages;

    @Override
    public byte[] generatePdf(Opetussuunnitelma ops, Dokumentti dokumentti, Kieli kieli)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // Luodaan XHTML pohja
        Element rootElement = doc.createElement("html");
        rootElement.setAttribute("lang", kieli.toString());
        doc.appendChild(rootElement);

        Element headElement = doc.createElement("head");

        // Poistetaan HEAD:in <META http-equiv="Content-Type" content="text/html; charset=UTF-8">
        if (headElement.hasChildNodes()) {
            headElement.removeChild(headElement.getFirstChild());
        }

        Element bodyElement = doc.createElement("body");

        rootElement.appendChild(headElement);
        rootElement.appendChild(bodyElement);

        // Apuluokka datan säilömiseen generoinin ajaksi
        DokumenttiBase docBase = new DokumenttiBase();
        docBase.setDocument(doc);
        docBase.setHeadElement(headElement);
        docBase.setBodyElement(bodyElement);
        docBase.setOpetussuunnitelma(ops);
        docBase.setGenerator(new CharapterNumberGenerator());
        docBase.setKieli(kieli);
        docBase.setDokumentti(dokumentti);
        docBase.setMapper(mapper);

        PerusteKaikkiDto perusteKaikkiDto = null;
        if (ops.getPeruste() != null) {
            eperusteetService.getPerusteSisalto(ops.getPeruste(), PerusteKaikkiDto.class);
            docBase.setPeruste(perusteKaikkiDto);
        }

        // Kansilehti & Infosivu
        addMetaPages(docBase);

        docBase.getDokumentti().setEdistyminen(DokumenttiEdistyminen.TEKSTIKAPPALEET);
        dokumenttiRepository.save(docBase.getDokumentti());

        // Sisältöelementit
        addTekstit(docBase);

        docBase.getDokumentti().setEdistyminen(DokumenttiEdistyminen.VIITTEET);
        dokumenttiRepository.save(docBase.getDokumentti());


        // Alaviitteet
        buildFootnotes(docBase);

        docBase.getDokumentti().setEdistyminen(DokumenttiEdistyminen.KUVAT);
        dokumenttiRepository.save(docBase.getDokumentti());

        // Kuvat
        buildImages(docBase);
        buildKansilehti(docBase);
        buildYlatunniste(docBase);
        buildAlatunniste(docBase);

        docBase.getDokumentti().setEdistyminen(DokumenttiEdistyminen.TYYLIT);
        dokumenttiRepository.save(docBase.getDokumentti());

        // PDF luonti XHTML dokumentista
        return pdfService.xhtml2pdf(doc);
    }

    private void addMetaPages(DokumenttiBase docBase) {
        Element title = docBase.getDocument().createElement("title");
        String nimi = getTextString(docBase, docBase.getOpetussuunnitelma().getNimi());
        title.appendChild(docBase.getDocument().createTextNode(nimi));
        docBase.getHeadElement().appendChild(title);

        String kuvaus = getTextString(docBase, docBase.getOpetussuunnitelma().getKuvaus());
        if (kuvaus != null && kuvaus.length() != 0) {
            Element description = docBase.getDocument().createElement("meta");
            description.setAttribute("name", "description");
            description.setAttribute("content", Jsoup.parse(kuvaus).text());
            docBase.getHeadElement().appendChild(description);
        }

        /*Set<KoodistoKoodi> koodistoKoodit = docBase.getOpetussuunnitelma().getKunnat();
        if (koodistoKoodit != null) {
            Element municipalities = docBase.getDocument().createElement("kunnat");
            for (KoodistoKoodi koodistoKoodi : koodistoKoodit) {
                Element kuntaEl = docBase.getDocument().createElement("kunta");
                KoodistoKoodiDto koodistoKoodiDto = koodistoService.get("kunta", koodistoKoodi.getKoodiUri());
                if (koodistoKoodiDto != null && koodistoKoodiDto.getMetadata() != null) {
                    for (KoodistoMetadataDto metadata : koodistoKoodiDto.getMetadata()) {
                        if (metadata.getNimi() != null && metadata.getKieli().toLowerCase()
                                .equals(docBase.getKieli().toString().toLowerCase())) {
                            kuntaEl.setTextContent(metadata.getNimi());
                        }
                    }
                }
                municipalities.appendChild(kuntaEl);
            }
            docBase.getHeadElement().appendChild(municipalities);
        }*/

        // Organisaatiot
        Element organisaatiot = docBase.getDocument().createElement("organisaatiot");

        /*docBase.getOpetussuunnitelma().getOrganisaatiot().stream()
                .map(org -> organisaatioService.getOrganisaatio(org))
                .filter(node -> {
                    JsonNode tyypit = node.get("tyypit");
                    if (tyypit.isArray()) {
                        for (JsonNode asd : tyypit) {
                            if (asd.textValue().equals("Koulutustoimija")) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .map(node -> node.get("nimi"))
                .filter(Objects::nonNull)
                .map(x -> x.get(docBase.getKieli().toString()))
                .filter(Objects::nonNull)
                .map(JsonNode::asText)
                .forEach(koulu -> {
                    Element orgEl = docBase.getDocument().createElement("koulu");
                    orgEl.setTextContent(koulu);
                    organisaatiot.appendChild(orgEl);
                });*/

        docBase.getHeadElement().appendChild(organisaatiot);


        // Päätöspäivämäärä
        Date paatospaivamaara = docBase.getOpetussuunnitelma().getPaatospaivamaara();
        Element dateEl = docBase.getDocument().createElement("meta");
        dateEl.setAttribute("name", "date");
        if (paatospaivamaara != null) {
            String paatospaivamaaraText = new SimpleDateFormat("d.M.yyyy").format(paatospaivamaara);
            LOG.info(paatospaivamaaraText);
            dateEl.setAttribute("content", paatospaivamaaraText);
        } else {
            dateEl.setAttribute("content", "");
        }
        docBase.getHeadElement().appendChild(dateEl);


        // Koulun nimi
        Element koulutEl = docBase.getDocument().createElement("koulut");

        /*docBase.getOps().getOrganisaatiot().stream()
                .map(org -> organisaatioService.getOrganisaatio(org))
                .filter(node -> {
                    JsonNode tyypit = node.get("tyypit");
                    if (tyypit.isArray()) {
                        for (JsonNode asd : tyypit) {
                            if (asd.textValue().equals("Oppilaitos")) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .map(node -> node.get("nimi"))
                .filter(Objects::nonNull)
                .map(x -> x.get(docBase.getKieli().toString()))
                .filter(Objects::nonNull)
                .map(JsonNode::asText)
                .forEach(koulu -> {
                    Element kouluEl = docBase.getDocument().createElement("koulu");
                    kouluEl.setTextContent(koulu);
                    koulutEl.appendChild(kouluEl);
                });*/

        docBase.getHeadElement().appendChild(koulutEl);
    }

    private void addTekstit(DokumenttiBase docBase)
            throws IOException, SAXException, ParserConfigurationException {

        SisaltoViite tekstit = tkvRepository.findOneRoot(docBase.getOpetussuunnitelma());
        if (tekstit != null) {
            addSisaltoviite(docBase, tekstit);
        }
    }

    private void addSisaltoviite(DokumenttiBase docBase, SisaltoViite viite)
            throws ParserConfigurationException, IOException, SAXException {

        for (SisaltoViite lapsi : viite.getLapset()) {
            if (lapsi == null || lapsi.getTekstiKappale() == null || lapsi.getTekstiKappale().getNimi() == null) {
                return;
            }

            TekstiKappale kappale = lapsi.getTekstiKappale();
            StringBuilder otsikkoBuilder = new StringBuilder();
            otsikkoBuilder.append(getTextString(docBase, kappale.getNimi()));

            if (lapsi.getTyyppi().equals(SisaltoTyyppi.TUTKINNONOSA) && lapsi.getTosa() != null) {
                Tutkinnonosa tosa = lapsi.getTosa();
                switch (tosa.getTyyppi()) {
                    case OMA:
                        if (tosa.getOmatutkinnonosa() == null
                                || tosa.getOmatutkinnonosa().getKoodi() == null) {
                            break;
                        }
                        String koodi = tosa.getOmatutkinnonosa().getKoodi();

                        otsikkoBuilder
                                .append(" (")
                                .append(koodi)
                                .append(")");
                        break;
                    case PERUSTEESTA:
                        Long perusteenTutkinnonosaId = tosa.getPerusteentutkinnonosa();
                        if (perusteenTutkinnonosaId == null) {
                            break;
                        }

                        docBase.getPeruste().getTutkinnonOsat().stream()
                                .filter(dto -> dto.getId().equals(perusteenTutkinnonosaId))
                                .findAny()
                                .ifPresent(dto -> otsikkoBuilder
                                        .append(" (")
                                        .append(dto.getKoodiArvo())
                                        .append(")"));

                        break;
                    default:
                        break;
                }
            }

            addHeader(docBase, otsikkoBuilder.toString());

            if (kappale.getTeksti() != null) {
                addLokalisoituteksti(docBase, kappale.getTeksti(), "div");
            } else {
                addTeksti(docBase, "", "p"); // Sivutuksen kannalta välttämätön
            }

            switch (lapsi.getTyyppi()) {
                case SUORITUSPOLKU:
                    addSuorituspolku(docBase, lapsi);
                    break;
                case TUTKINNONOSA:
                    addTutkinnonosa(docBase, lapsi);
                    break;
                default:
                    break;
            }

            docBase.getGenerator().increaseDepth();

            // Rekursiivisesti
            addSisaltoviite(docBase, lapsi);

            docBase.getGenerator().decreaseDepth();

            docBase.getGenerator().increaseNumber();
        }
    }

    private void addSuorituspolku(DokumenttiBase docBase, SisaltoViite viite) {
        Suorituspolku suorituspolku = viite.getSuorituspolku();
        Map<UUID, SuorituspolkuRivi> suorituspolkuMap = new HashMap<>();
        StringBuilder builder = new StringBuilder();

        if (suorituspolku != null) {
            suorituspolku.getRivit().forEach(rivi -> suorituspolkuMap.put(rivi.getRakennemoduuli(), rivi));
        }

        builder.append("<ul>");
        docBase.getPeruste().getSuoritustavat().stream()
                .filter(suoritustapaLaajaDto -> suoritustapaLaajaDto.getSuoritustapakoodi().toString().equals("ops"))
                .findAny()
                .filter(suoritustapaLaajaDto -> suoritustapaLaajaDto.getRakenne() != null)
                .ifPresent(suoritustapaLaajaDto -> suoritustapaLaajaDto.getRakenne().getOsat().stream()
                        .filter(dto -> dto instanceof RakenneModuuliDto)
                        .map(dto -> (RakenneModuuliDto) dto)
                        .forEach(rakenneModuuliDto -> addSuorituspolkuOsa(docBase, rakenneModuuliDto, builder, suoritustapaLaajaDto, suorituspolkuMap)));
        builder.append("</ul>");

        addTeksti(docBase, builder.toString(), "div");
    }

    private void addSuorituspolkuOsa(DokumenttiBase docBase,
                                     RakenneModuuliDto rakenneModuuliDto,
                                     StringBuilder builder,
                                     SuoritustapaLaajaDto suoritustapaLaajaDto,
                                     Map<UUID, SuorituspolkuRivi> suorituspolkuMap) {
        builder.append("<li>");

        builder.append(getTextString(docBase, rakenneModuuliDto.getNimi()));
        if (rakenneModuuliDto.getOsaamisala() != null
                && rakenneModuuliDto.getOsaamisala().getOsaamisalakoodiArvo() != null) {
            builder.append(" (");
            builder.append(rakenneModuuliDto.getOsaamisala().getOsaamisalakoodiArvo());
            builder.append(")");
        }
        addMuodostumisSaanto(docBase, rakenneModuuliDto.getMuodostumisSaanto(), builder);

        builder.append("<ul>");
        rakenneModuuliDto.getOsat().forEach(lapsi -> {
            // Piilotettuja ei generoida PDF:ään
            if (suorituspolkuMap.containsKey(lapsi.getTunniste())) {
                SuorituspolkuRivi rivi = suorituspolkuMap.get(lapsi.getTunniste());
                if (rivi.getPiilotettu() != null && rivi.getPiilotettu()) {
                    return;
                }
            }

            if (lapsi instanceof RakenneModuuliDto) {
                RakenneModuuliDto lapsiDto = (RakenneModuuliDto) lapsi;
                addSuorituspolkuOsa(docBase, lapsiDto, builder, suoritustapaLaajaDto, suorituspolkuMap);
            } else if (lapsi instanceof RakenneOsaDto) {
                RakenneOsaDto lapsiDto = (RakenneOsaDto) lapsi;
                if (lapsiDto.getTutkinnonOsaViite() != null) {
                    suoritustapaLaajaDto.getTutkinnonOsat().stream()
                            .filter(dto -> dto.getId().equals(lapsiDto.getTutkinnonOsaViite()))
                            .findAny()
                            .ifPresent(dto -> docBase.getPeruste().getTutkinnonOsat().stream()
                                    .filter(tutkinnonOsaDto -> tutkinnonOsaDto.getId().equals(dto.getTutkinnonOsa()))
                                    .findAny()
                                    .ifPresent(tutkinnonOsaKaikkiDto -> addSuorituspolunTutkinnonOsa(
                                            docBase, tutkinnonOsaKaikkiDto, dto, builder)));
                }
            }
        });
        builder.append("</ul>");

        builder.append("</li>");
    }

    private void addMuodostumisSaanto(DokumenttiBase docBase,
                                      MuodostumisSaantoDto muodostumisSaantoDto,
                                      StringBuilder builder) {
        if (muodostumisSaantoDto != null && muodostumisSaantoDto.getLaajuus() != null) {
            builder.append(" ");
            if (muodostumisSaantoDto.getLaajuus().getMinimi() != null
                    && muodostumisSaantoDto.getLaajuus().getMaksimi() != null
                    && muodostumisSaantoDto.getLaajuus().getMinimi()
                    .equals(muodostumisSaantoDto.getLaajuus().getMaksimi())) {
                builder.append(muodostumisSaantoDto.getLaajuus().getMinimi());
            } else if (muodostumisSaantoDto.getLaajuus().getMinimi() != null
                    && muodostumisSaantoDto.getLaajuus().getMaksimi() != null) {
                builder.append(muodostumisSaantoDto.getLaajuus().getMinimi());
                builder.append("-");
                builder.append(muodostumisSaantoDto.getLaajuus().getMaksimi());
            } else if (muodostumisSaantoDto.getLaajuus().getMinimi() != null) {
                builder.append(muodostumisSaantoDto.getLaajuus().getMinimi());
            } else if (muodostumisSaantoDto.getLaajuus().getMaksimi() != null) {
                builder.append(muodostumisSaantoDto.getLaajuus().getMaksimi());
            }

            if (muodostumisSaantoDto.getLaajuus().getYksikko() != null) {
                builder.append(" ");
                builder.append(muodostumisSaantoDto.getLaajuus().getYksikko());
            } else {
                // Todo: lokalisoitu yksikkö
                builder.append(" osp");
            }
        }
    }

    private void addSuorituspolunTutkinnonOsa(DokumenttiBase docBase,
                                              TutkinnonOsaKaikkiDto tutkinnonOsaKaikkiDto,
                                              TutkinnonOsaViiteSuppeaDto tutkinnonOsaViiteSuppeaDto,
                                              StringBuilder builder) {
        builder.append("<li>");
        builder.append(getTextString(docBase, tutkinnonOsaKaikkiDto.getNimi()));

        if (tutkinnonOsaKaikkiDto.getKoodiArvo() != null) {
            builder.append(" (");
            builder.append(tutkinnonOsaKaikkiDto.getKoodiArvo());
            builder.append(")");
        }

        if (tutkinnonOsaViiteSuppeaDto.getLaajuus() != null) {
            builder.append(" ");
            if (tutkinnonOsaViiteSuppeaDto.getLaajuus().stripTrailingZeros().scale() <= 0) {
                builder.append(String.valueOf(tutkinnonOsaViiteSuppeaDto.getLaajuus().intValue()));
            } else {
                builder.append(tutkinnonOsaViiteSuppeaDto.getLaajuus().toString());
            }
            builder.append(" osp");
        }
        builder.append("</li>");
    }

    private void addTutkinnonosa(DokumenttiBase docBase, SisaltoViite lapsi) {
        Tutkinnonosa tutkinnonOsa = lapsi.getTosa();

        switch (tutkinnonOsa.getTyyppi()) {
            case OMA:
                if (tutkinnonOsa.getOmatutkinnonosa() != null) {
                    addOmaTutkinnonOsa(docBase, tutkinnonOsa.getOmatutkinnonosa());
                }
                break;
            case PERUSTEESTA:
                if (tutkinnonOsa.getPerusteentutkinnonosa() != null) {
                    addPerusteenTutkinnonOsa(docBase, tutkinnonOsa.getPerusteentutkinnonosa());
                }
                break;
            default:
                break;
        }
        // Osaamisen osoittaminen
        addLokalisoituteksti(docBase, tutkinnonOsa.getOsaamisenOsoittaminen(), "div");

        // Toteutukset
        if (tutkinnonOsa.getToteutukset().size() > 0) {
            addTeksti(docBase, messages.translate("docgen.toteutukset", docBase.getKieli()), "h5");

            DokumenttiTaulukko taulukko = new DokumenttiTaulukko();

            taulukko.addOtsikkoSarake("");
            taulukko.addOtsikkoSarake(messages.translate("docgen.osaamisen-arvioinnista", docBase.getKieli()));
            taulukko.addOtsikkoSarake(messages.translate("docgen.tavat-ja-ymparisto", docBase.getKieli()));

            for (TutkinnonosaToteutus toteutus : tutkinnonOsa.getToteutukset()) {

                DokumenttiRivi rivi = new DokumenttiRivi();
                StringJoiner koodit = new StringJoiner(", ");
                toteutus.getKoodit().forEach(koodit::add);

                rivi.addSarake(koodit.toString());
                rivi.addSarake(getTextString(docBase, toteutus.getArvioinnista().getTeksti()));
                rivi.addSarake(getTextString(docBase, toteutus.getTavatjaymparisto().getTeksti()));

                taulukko.addRivi(rivi);
            }

            taulukko.addToDokumentti(docBase);
        }
    }

    private void addOmaTutkinnonOsa(DokumenttiBase docBase, OmaTutkinnonosa omaTutkinnonosa) {

        // Koodi
        /*if (omaTutkinnonosa.getKoodi() != null) {
            addTeksti(docBase, messages.translate("docgen.koodi", docBase.getKieli()), "h5");
            addTeksti(docBase, String.valueOf(omaTutkinnonosa.getKoodi()), "div");
        }*/

        // Tavoitteet
        if (omaTutkinnonosa.getTavoitteet() != null) {
            addTeksti(docBase, messages.translate("docgen.tavoitteet", docBase.getKieli()), "h5");
            addLokalisoituteksti(docBase, omaTutkinnonosa.getTavoitteet(), "div");
        }


        // Ammattitaitovaatimukset
        if (omaTutkinnonosa.getAmmattitaitovaatimuksetLista().size() > 0) {
            addTeksti(docBase, messages.translate("docgen.ammattitaitovaatimukset", docBase.getKieli()), "h5");
            omaTutkinnonosa.getAmmattitaitovaatimuksetLista()
                    .forEach(ammattitaitovaatimuksenKohdealue -> addAmmattitaitovaatimuksenKohdealue(docBase, ammattitaitovaatimuksenKohdealue));
        }

        // Arviointi
        if (omaTutkinnonosa.getArviointi() != null) {
            addTeksti(docBase, messages.translate("docgen.arviointi", docBase.getKieli()), "h5");
            Arviointi arviointi = omaTutkinnonosa.getArviointi();
            arviointi.getArvioinninKohdealueet()
                    .forEach(arvioinninKohdealue -> addArvioinninKohdealue(docBase, arvioinninKohdealue));
        }

        // Ammattitaidon osoittamistavat
        if (omaTutkinnonosa.getAmmattitaidonOsoittamistavat() != null) {
            addTeksti(docBase, messages.translate("docgen.ammattitaidon-osoittamistavat", docBase.getKieli()), "h5");
            addLokalisoituteksti(docBase, omaTutkinnonosa.getAmmattitaidonOsoittamistavat(), "div");
        }
    }

    private void addAmmattitaitovaatimuksenKohdealue(DokumenttiBase docBase,
                                                     AmmattitaitovaatimuksenKohdealue ammattitaitovaatimuksenKohdealue) {
        DokumenttiTaulukko taulukko = new DokumenttiTaulukko();

        //taulukko.addOtsikkoSarake(getTextString(docBase, ammattitaitovaatimuksenKohdealue.getOtsikko()));
        addVaatimuksenKohteet(docBase, ammattitaitovaatimuksenKohdealue.getVaatimuksenKohteet(), taulukko);

        taulukko.addToDokumentti(docBase);
    }

    private void addVaatimuksenKohteet(DokumenttiBase docBase,
                                       List<AmmattitaitovaatimuksenKohde> vaatimuksenKohteet,
                                       DokumenttiTaulukko taulukko) {
        StringBuilder vaatimuksenKohteetBuilder = new StringBuilder();
        vaatimuksenKohteet.forEach(ammattitaitovaatimuksenKohde -> {
            if (ammattitaitovaatimuksenKohde.getOtsikko() != null) {
                vaatimuksenKohteetBuilder.append("<h6>");
                vaatimuksenKohteetBuilder.append(getTextString(docBase, ammattitaitovaatimuksenKohde.getOtsikko()));
                vaatimuksenKohteetBuilder.append("</h6>");
            }

            if (ammattitaitovaatimuksenKohde.getSelite() != null) {
                vaatimuksenKohteetBuilder.append("<p>");
                vaatimuksenKohteetBuilder.append(ammattitaitovaatimuksenKohde.getSelite());
                vaatimuksenKohteetBuilder.append("</p>");
            }

            addVaatimukset(docBase, ammattitaitovaatimuksenKohde.getVaatimukset(), vaatimuksenKohteetBuilder);
        });

        DokumenttiRivi rivi = new DokumenttiRivi();
        rivi.addSarake(vaatimuksenKohteetBuilder.toString());
        taulukko.addRivi(rivi);
    }

    private void addVaatimukset(DokumenttiBase docBase,
                                List<Ammattitaitovaatimus> ammattitaitovaatimukset,
                                StringBuilder vaatimuksenKohteetBuilder) {
        vaatimuksenKohteetBuilder.append("<ul>");
        ammattitaitovaatimukset.forEach(vaatimus -> {
            vaatimuksenKohteetBuilder.append("<li>");
            vaatimuksenKohteetBuilder.append(getTextString(docBase, vaatimus.getSelite()));
            if (vaatimus.getAmmattitaitovaatimusKoodi() != null) {
                vaatimuksenKohteetBuilder.append(" (");
                vaatimuksenKohteetBuilder.append(vaatimus.getAmmattitaitovaatimusKoodi());
                vaatimuksenKohteetBuilder.append(")");
            }
            vaatimuksenKohteetBuilder.append("</li>");
        });
        vaatimuksenKohteetBuilder.append("</ul>");
    }

    private void addArvioinninKohdealue(DokumenttiBase docBase, ArvioinninKohdealue arvioinninKohdealue) {
        DokumenttiTaulukko arviointiTaulukko = new DokumenttiTaulukko();

        arviointiTaulukko.addOtsikkoSarake(getTextString(docBase, arvioinninKohdealue.getOtsikko()));
        addArvioinninKohteet(docBase, arvioinninKohdealue.getArvioinninKohteet(), arviointiTaulukko);

        arviointiTaulukko.addToDokumentti(docBase);
    }

    private void addArvioinninKohteet(DokumenttiBase docBase, List<ArvioinninKohde> arvioinninKohteet, DokumenttiTaulukko arviointiTaulukko) {
        StringBuilder arvioinninKohteetBuilder = new StringBuilder();

        arvioinninKohteet.forEach(arvioinninKohde -> {
            if (arvioinninKohde.getOtsikko() != null) {
                arvioinninKohteetBuilder.append("<h6>");
                arvioinninKohteetBuilder.append(getTextString(docBase, arvioinninKohde.getOtsikko()));
                arvioinninKohteetBuilder.append("</h6>");
            }

            if (arvioinninKohde.getSelite() != null) {
                arvioinninKohteetBuilder.append("<p>");
                arvioinninKohteetBuilder.append(arvioinninKohde.getSelite());
                arvioinninKohteetBuilder.append("</p>");
            }

            // Add kohteen taulukko
            addOsaamistasonKriteerit(docBase, arvioinninKohde.getOsaamistasonKriteerit(), arvioinninKohteetBuilder);
        });


        DokumenttiRivi rivi = new DokumenttiRivi();
        rivi.addSarake(arvioinninKohteetBuilder.toString());
        arviointiTaulukko.addRivi(rivi);
    }

    private void addOsaamistasonKriteerit(DokumenttiBase docBase,
                                          Set<OsaamistasonKriteeri> osaamistasonKriteerit,
                                          StringBuilder arvioinninKohteetBuilder) {
        DokumenttiTaulukko taulukko = new DokumenttiTaulukko();

        osaamistasonKriteerit.stream()
                .filter(k -> k.getOsaamistaso() != null)
                .sorted((k1, k2) -> k2.getOsaamistaso().getId().compareTo(
                        k1.getOsaamistaso().getId()))
                .forEach(osaamistasonKriteeri -> {
                    DokumenttiRivi rivi = new DokumenttiRivi();

                    rivi.addSarake(getTextString(docBase, osaamistasonKriteeri.getOsaamistaso().getOtsikko()));

                    StringBuilder kriteeritBuilder = new StringBuilder();
                    kriteeritBuilder.append("<ul>");
                    osaamistasonKriteeri.getKriteerit().forEach(kriteeri -> {
                        kriteeritBuilder.append("<li>");
                        kriteeritBuilder.append(getTextString(docBase, kriteeri));
                        kriteeritBuilder.append("</li>");
                    });
                    kriteeritBuilder.append("</ul>");
                    rivi.addSarake(kriteeritBuilder.toString());

                    taulukko.addRivi(rivi);
                });

        arvioinninKohteetBuilder.append(taulukko.toString());
    }

    private void addPerusteenTutkinnonOsa(DokumenttiBase docBase, Long perusteenTutkinnonosaId) {
        // Todo: koodi, nimi, laajuus
        /*addTeksti(docBase, messages.translate("docgen.koodi", docBase.getKieli()), "h5");
        docBase.getPeruste().getTutkinnonOsat().stream()
                .filter(dto -> dto.getId().equals(perusteenTutkinnonosaId))
                .findAny()
                .ifPresent(dto -> addTeksti(docBase, dto.getKoodiArvo(), "div"));*/

    }

    private void buildFootnotes(DokumenttiBase docBase) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        try {
            XPathExpression expression = xpath.compile("//abbr");
            NodeList list = (NodeList) expression.evaluate(docBase.getDocument(), XPathConstants.NODESET);

            int noteNumber = 1;
            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                Node node = list.item(i);
                if (node.getAttributes() != null & node.getAttributes().getNamedItem("data-viite") != null) {
                    String avain = node.getAttributes().getNamedItem("data-viite").getNodeValue();

                    if (docBase.getOpetussuunnitelma() != null && docBase.getOpetussuunnitelma().getId() != null) {
                        TermiDto termiDto = termistoService.getTermiByAvain(
                                docBase.getOpetussuunnitelma().getKoulutustoimija().getId(), avain);

                        // todo: perusteen viite
                        /*if (termiDto == null) {

                        }*/

                        if (termiDto != null && termiDto.getAlaviite() && termiDto.getSelitys() != null) {
                            element.setAttribute("number", String.valueOf(noteNumber));

                            LokalisoituTekstiDto tekstiDto = termiDto.getSelitys();
                            String selitys = getTextString(docBase, tekstiDto).replaceAll("<[^>]+>", "");
                            element.setAttribute("text", selitys);
                            noteNumber++;
                        }
                    }
                }
            }
        } catch (XPathExpressionException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    private void buildImages(DokumenttiBase docBase) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        try {
            XPathExpression expression = xpath.compile("//img");
            NodeList list = (NodeList) expression.evaluate(docBase.getDocument(), XPathConstants.NODESET);

            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                String id = element.getAttribute("data-uid");

                UUID uuid = UUID.fromString(id);

                // Ladataan kuvan data muistiin
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                liiteService.export(docBase.getOpetussuunnitelma().getKoulutustoimija().getId(), uuid, byteArrayOutputStream);

                // Tehdään muistissa olevasta datasta kuva
                InputStream in = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                BufferedImage bufferedImage = ImageIO.read(in);

                int width = bufferedImage.getWidth();
                int height = bufferedImage.getHeight();

                // Muutetaan kaikkien kuvien väriavaruus RGB:ksi jotta PDF/A validointi menee läpi
                // Asetetaan lisäksi läpinäkyvien kuvien taustaksi valkoinen väri
                BufferedImage tempImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                        BufferedImage.TYPE_3BYTE_BGR);
                tempImage.getGraphics().setColor(new Color(255, 255, 255, 0));
                tempImage.getGraphics().fillRect (0, 0, width, height);
                tempImage.getGraphics().drawImage(bufferedImage, 0, 0, null);
                bufferedImage = tempImage;

                ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
                jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                jpgWriteParam.setCompressionQuality(COMPRESSION_LEVEL);

                // Muunnetaan kuva base64 enkoodatuksi
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                MemoryCacheImageOutputStream imageStream = new MemoryCacheImageOutputStream(out);
                jpgWriter.setOutput(imageStream);
                IIOImage outputImage = new IIOImage(bufferedImage, null, null);
                jpgWriter.write(null, outputImage, jpgWriteParam);
                jpgWriter.dispose();
                String base64 = Base64.encode(out.toByteArray());

                // Lisätään bas64 kuva img elementtiin
                element.setAttribute("width", String.valueOf(width));
                element.setAttribute("height", String.valueOf(height));
                element.setAttribute("src", "data:image/jpg;base64," + base64);
            }

        } catch (XPathExpressionException | IOException | NullPointerException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }

    private void buildKansilehti(DokumenttiBase docBase) {
        Element head = docBase.getHeadElement();
        Element kansikuva = docBase.getDocument().createElement("kansikuva");
        Element kuva = docBase.getDocument().createElement("img");

        byte[] image = docBase.getDokumentti().getKansikuva();
        if (image == null) {
            return;
        }

        String base64 = Base64.encode(image);
        kuva.setAttribute("src", "data:image/jpg;base64," + base64);

        kansikuva.appendChild(kuva);
        head.appendChild(kansikuva);
    }

    private void buildYlatunniste(DokumenttiBase docBase) {
        Element head = docBase.getHeadElement();
        Element ylatunniste = docBase.getDocument().createElement("ylatunniste");
        Element kuva = docBase.getDocument().createElement("img");

        byte[] image = docBase.getDokumentti().getYlatunniste();
        if (image == null) {
            return;
        }

        String base64 = Base64.encode(image);
        kuva.setAttribute("src", "data:image/jpg;base64," + base64);

        ylatunniste.appendChild(kuva);
        head.appendChild(ylatunniste);
    }

    private void buildAlatunniste(DokumenttiBase docBase) {
        Element head = docBase.getHeadElement();
        Element alatunniste = docBase.getDocument().createElement("alatunniste");
        Element kuva = docBase.getDocument().createElement("img");

        byte[] image = docBase.getDokumentti().getAlatunniste();
        if (image == null) {
            return;
        }

        String base64 = Base64.encode(image);
        kuva.setAttribute("src", "data:image/jpg;base64," + base64);

        alatunniste.appendChild(kuva);
        head.appendChild(alatunniste);
    }
}
