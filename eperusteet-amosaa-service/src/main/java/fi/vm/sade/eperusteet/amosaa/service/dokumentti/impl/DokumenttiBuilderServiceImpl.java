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

import fi.vm.sade.eperusteet.amosaa.domain.Osaamistaso;
import fi.vm.sade.eperusteet.amosaa.domain.OsaamistasonKriteeri;
import fi.vm.sade.eperusteet.amosaa.domain.SisaltoTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset.AmmattitaitovaatimuksenKohde;
import fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset.AmmattitaitovaatimuksenKohdealue;
import fi.vm.sade.eperusteet.amosaa.domain.ammattitaitovaatimukset.Ammattitaitovaatimus;
import fi.vm.sade.eperusteet.amosaa.domain.arviointi.ArvioinninKohde;
import fi.vm.sade.eperusteet.amosaa.domain.arviointi.ArvioinninKohdealue;
import fi.vm.sade.eperusteet.amosaa.domain.arviointi.Arviointi;
import fi.vm.sade.eperusteet.amosaa.domain.arviointi.Arviointiasteikko;
import fi.vm.sade.eperusteet.amosaa.domain.dokumentti.Dokumentti;
import fi.vm.sade.eperusteet.amosaa.domain.koulutustoimija.Opetussuunnitelma;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Kieli;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Opintokokonaisuus;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Suorituspolku;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.SuorituspolkuRivi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.*;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.AmmattitaitovaatimusKohdealueetDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusTavoiteDto;
import fi.vm.sade.eperusteet.amosaa.repository.dokumentti.DokumenttiRepository;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiBuilderService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.LocalizedMessagesService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.PdfService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.*;
import fi.vm.sade.eperusteet.amosaa.service.external.ArviointiasteikkoService;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.LiiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.TermistoService;
import fi.vm.sade.eperusteet.amosaa.service.util.KoodistoClient;
import fi.vm.sade.eperusteet.utils.dto.dokumentti.DokumenttiMetaDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliRooli.VIRTUAALINEN;
import static fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiUtils.*;

/**
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
    private SisaltoViiteService svService;

    @Autowired
    private LocalizedMessagesService messages;

    @Autowired
    private KoodistoClient koodistoClient;

    @Autowired
    private ArviointiasteikkoService arviointiasteikkoService;

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

        if (ops.getPeruste() != null) {
            PerusteKaikkiDto perusteKaikkiDto = eperusteetService.getPerusteSisalto(ops.getPeruste(), PerusteKaikkiDto.class);
            docBase.setPeruste(perusteKaikkiDto);
        }

        // Kansilehti & Infosivu
        addMetaPages(docBase);

        // Sisältöelementit
        addTekstit(docBase);

        // Alaviitteet
        buildFootnotes(docBase);

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Kuvat
        buildImages(docBase);
        buildKansilehti(docBase);
        buildYlatunniste(docBase);
        buildAlatunniste(docBase);

        DokumenttiMetaDto meta = DokumenttiMetaDto.builder()
                .title(DokumenttiUtils.getTextString(docBase, ops.getNimi()))
                .subject(messages.translate("docgen.meta.subject.ops", kieli))
                .build();

        // PDF luonti XHTML dokumentista
        LOG.info("Generate PDF (opsId=" + docBase.getOpetussuunnitelma().getId() + ")");
        return pdfService.xhtml2pdf(doc, meta);
    }

    private void addMetaPages(DokumenttiBase docBase) {
        Opetussuunnitelma ops = docBase.getOpetussuunnitelma();

        Element title = docBase.getDocument().createElement("title");
        String nimi = getTextString(docBase, ops.getNimi());
        title.appendChild(docBase.getDocument().createTextNode(nimi));
        docBase.getHeadElement().appendChild(title);

        // Kuvaus
        String kuvaus = getTextString(docBase, ops.getKuvaus());
        if (kuvaus != null && kuvaus.length() != 0) {
            Element description = docBase.getDocument().createElement("description");
            addTeksti(docBase, kuvaus, "div", description);
            docBase.getHeadElement().appendChild(description);
        }

        if (ops.getPeruste() != null) {
            String perusteNimi = getTextString(docBase, ops.getPeruste().getNimi());
            if (perusteNimi != null) {
                Element perusteNimiEl = docBase.getDocument().createElement("meta");
                perusteNimiEl.setAttribute("name", "perusteNimi");
                perusteNimiEl.setAttribute("content", perusteNimi);
                docBase.getHeadElement().appendChild(perusteNimiEl);
            }
        }

        // Päätösnumero
        String paatosnumero = ops.getPaatosnumero();
        if (paatosnumero != null) {
            Element paatosnumeroEl = docBase.getDocument().createElement("meta");
            paatosnumeroEl.setAttribute("name", "paatosnumero");
            paatosnumeroEl.setAttribute("content", paatosnumero);
            docBase.getHeadElement().appendChild(paatosnumeroEl);
        }

        // Hyväksyjä
        String hyvaksyja = ops.getHyvaksyja();
        if (hyvaksyja != null) {
            Element hyvaksyjaEl = docBase.getDocument().createElement("meta");
            hyvaksyjaEl.setAttribute("name", "hyvaksyja");
            hyvaksyjaEl.setAttribute("content", hyvaksyja);
            docBase.getHeadElement().appendChild(hyvaksyjaEl);
        }

        // Päätöspäivämäärä
        Date paatospaivamaara = ops.getPaatospaivamaara();
        if (paatospaivamaara != null) {
            Element paatospaivamaaraEl = docBase.getDocument().createElement("meta");
            paatospaivamaaraEl.setAttribute("name", "paatospaivamaara");
            String paatospaivamaaraText = new SimpleDateFormat("d.M.yyyy").format(paatospaivamaara);
            paatospaivamaaraEl.setAttribute("content", paatospaivamaaraText);
            docBase.getHeadElement().appendChild(paatospaivamaaraEl);
        }

        // Voimaantulopäivämäärä
        Date voimaantulopaivamaara = ops.getVoimaantulo();
        if (voimaantulopaivamaara != null) {
            Element voimaantulopaivamaaraEl = docBase.getDocument().createElement("meta");
            voimaantulopaivamaaraEl.setAttribute("name", "voimaantulopaivamaara");
            String voimaantulopaivamaaraText = new SimpleDateFormat("d.M.yyyy").format(voimaantulopaivamaara);
            voimaantulopaivamaaraEl.setAttribute("content", voimaantulopaivamaaraText);
            docBase.getHeadElement().appendChild(voimaantulopaivamaaraEl);
        }

        Element pdfluotu = docBase.getDocument().createElement("meta");
        pdfluotu.setAttribute("name", "pdfluotu");
        pdfluotu.setAttribute("content", new SimpleDateFormat("d.M.yyyy").format(new Date()));
        pdfluotu.setAttribute("translate", messages.translate("docgen.pdf-luotu", docBase.getKieli()));
        docBase.getHeadElement().appendChild(pdfluotu);
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

            if (lapsi.getTyyppi().equals(SisaltoTyyppi.SUORITUSPOLUT) && CollectionUtils.isEmpty(lapsi.getLapset())) {
                continue;
            }

            TekstiKappale tekstiKappale = lapsi.getTekstiKappale();
            StringBuilder otsikkoBuilder = new StringBuilder();
            otsikkoBuilder.append(getTextString(docBase, tekstiKappale.getNimi()));

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
                        PerusteKaikkiDto peruste = docBase.getPeruste();
                        if (perusteenTutkinnonosaId == null || peruste == null) {
                            break;
                        }

                        peruste.getTutkinnonOsat().stream()
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

            if (lapsi.getTyyppi().equals(SisaltoTyyppi.OPINTOKOKONAISUUS) && lapsi.getOpintokokonaisuus() != null && lapsi.getOpintokokonaisuus().getLaajuus() != null) {
                otsikkoBuilder.append(", " + lapsi.getOpintokokonaisuus().getLaajuus() + " " + messages.translate("docgen.laajuus.op", docBase.getKieli()));
            }

            addHeader(docBase, otsikkoBuilder.toString());

            if (lapsi.isNaytaPerusteenTeksti() && lapsi.getPerusteteksti() != null && lapsi.getPerusteteksti().getTeksti() != null) {
                addLokalisoituteksti(docBase, lapsi.getPerusteteksti(), "div");
            }

            if (lapsi.isNaytaPohjanTeksti() && lapsi.getPohjanTekstikappale() != null && lapsi.getPohjanTekstikappale().getTeksti() != null) {
                addLokalisoituteksti(docBase, lapsi.getPohjanTekstikappale().getTeksti(), "div");
            }

            if (tekstiKappale.getTeksti() != null) {
                addLokalisoituteksti(docBase, tekstiKappale.getTeksti(), "div");
            } else {
                addTeksti(docBase, "", "p"); // Sivutuksen kannalta välttämätön
            }

            switch (lapsi.getTyyppi()) {
                case OSASUORITUSPOLKU:
                case SUORITUSPOLKU:
                    addSuorituspolku(docBase, lapsi, otsikkoBuilder.toString());
                    break;
                case TUTKINNONOSA:
                    addTutkinnonosa(docBase, lapsi);
                    break;
                case OPINTOKOKONAISUUS:
                    addOpintokokonaisuus(docBase, lapsi);
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

    private void addSuorituspolku(DokumenttiBase docBase, SisaltoViite viite, String suorituspolkuNimi) {
        Suorituspolku suorituspolku = viite.getSuorituspolku();
        Map<UUID, SuorituspolkuRivi> suorituspolkuMap = new HashMap<>();

        if (suorituspolku != null) {
            suorituspolku.getRivit().forEach(rivi -> {
                if (rivi.getPiilotettu() != null && rivi.getPiilotettu()) {
                    suorituspolkuMap.put(rivi.getRakennemoduuli(), rivi);
                }
            });
        }

        PerusteKaikkiDto peruste = docBase.getPeruste();

        if (peruste != null) {
            peruste.getSuoritustavat().stream()
                    .filter(suoritustapaLaajaDto -> suoritustapaLaajaDto.getSuoritustapakoodi().equals(Suoritustapakoodi.OPS)
                            || suoritustapaLaajaDto.getSuoritustapakoodi().equals(Suoritustapakoodi.REFORMI))
                    .findAny()
                    .filter(suoritustapaLaajaDto -> suoritustapaLaajaDto.getRakenne() != null)
                    .ifPresent(suoritustapaLaajaDto -> {

                        // Luodaan muodostumistaulukko
                        Element taulukko = docBase.getDocument().createElement("table");
                        taulukko.setAttribute("border", "1");
                        Element otsikko = docBase.getDocument().createElement("caption");
                        otsikko.setTextContent(suorituspolkuNimi);
                        taulukko.appendChild(otsikko);
                        docBase.getBodyElement().appendChild(taulukko);
                        Element tbody = docBase.getDocument().createElement("tbody");
                        taulukko.appendChild(tbody);

                        suoritustapaLaajaDto.getRakenne().getOsat().stream()
                                .filter(dto -> dto.getTunniste() != null && !suorituspolkuMap.containsKey(dto.getTunniste()))
                                .filter(dto -> dto instanceof RakenneModuuliDto)
                                .map(dto -> (RakenneModuuliDto) dto)
                                .forEach(rakenneModuuliDto -> addSuorituspolkuOsa(
                                        docBase, rakenneModuuliDto, tbody, 1, suoritustapaLaajaDto, suorituspolkuMap));
                    });
        }
    }

    private void addSuorituspolkuOsa(DokumenttiBase docBase,
                                     RakenneModuuliDto rakenneModuuliDto,
                                     Element tbody,
                                     int depth,
                                     SuoritustapaLaajaDto suoritustapaLaajaDto,
                                     Map<UUID, SuorituspolkuRivi> suorituspolkuMap) {

        Element tr = docBase.getDocument().createElement("tr");
        tr.setAttribute("bgcolor", "#F1F2F3");
        if (rakenneModuuliDto.getOsaamisala() != null) {
            tr.setAttribute("bgcolor", "#F3FAFD");
        }
        tbody.appendChild(tr);
        Element td = docBase.getDocument().createElement("td");
        tr.appendChild(td);
        td.setAttribute("class", "td" + depth);

        // Nimi
        StringBuilder nimiBuilder = new StringBuilder();
        nimiBuilder.append(getTextString(docBase, rakenneModuuliDto.getNimi()));
        if (rakenneModuuliDto.getOsaamisala() != null
                && rakenneModuuliDto.getOsaamisala().getOsaamisalakoodiArvo() != null) {
            nimiBuilder.append(" (");
            nimiBuilder.append(rakenneModuuliDto.getOsaamisala().getOsaamisalakoodiArvo());
            nimiBuilder.append(")");
        }
        addMuodostumisSaanto(docBase, rakenneModuuliDto.getMuodostumisSaanto(), nimiBuilder);
        addTeksti(docBase, nimiBuilder.toString(), "p", td);

        // Kuvaus
        if (rakenneModuuliDto.getKuvaus() != null) {
            addTeksti(docBase, getTextString(docBase, rakenneModuuliDto.getKuvaus()), "em", td);
        }

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
                addSuorituspolkuOsa(docBase, lapsiDto, tbody, depth + 1, suoritustapaLaajaDto, suorituspolkuMap);
            } else if (lapsi instanceof RakenneOsaDto) {
                RakenneOsaDto lapsiDto = (RakenneOsaDto) lapsi;
                if (lapsiDto.getTutkinnonOsaViite() != null) {
                    suoritustapaLaajaDto.getTutkinnonOsat().stream()
                            .filter(dto -> dto.getId().equals(lapsiDto.getTutkinnonOsaViite()))
                            .findAny()
                            .ifPresent(dto -> {
                                PerusteKaikkiDto peruste = docBase.getPeruste();
                                if (peruste != null) {
                                    peruste.getTutkinnonOsat().stream()
                                            .filter(tutkinnonOsaDto -> tutkinnonOsaDto.getId().equals(dto.getTutkinnonOsa()))
                                            .findAny()
                                            .ifPresent(tutkinnonOsaKaikkiDto -> addSuorituspolunTutkinnonOsa(
                                                    docBase, tutkinnonOsaKaikkiDto, dto, tbody, depth + 1));
                                }
                            });
                }
            }
        });

        // Lisätään tutkinnossa määritettävä rakenne osan aliosat
        if (VIRTUAALINEN.equals(rakenneModuuliDto.getRooli())) {
            if (suorituspolkuMap.containsKey(rakenneModuuliDto.getTunniste())) {
                SuorituspolkuRivi rivi = suorituspolkuMap.get(rakenneModuuliDto.getTunniste());
                Set<String> koodit = rivi.getKoodit();
                if (koodit != null) {
                    koodit.forEach(koodi -> {
                        KoodistoKoodiDto koodistoKoodiDto = koodistoClient.getByUri(koodi);
                        if (koodistoKoodiDto != null) {
                            addSuorituspolunKoodiOsa(docBase, koodistoKoodiDto, tbody, depth + 1);
                        }
                    });
                }
            }
        }
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
                builder.append(" ").append(messages.translate("docgen.laajuus.osp", docBase.getKieli()));
            }
        }
    }

    private void addSuorituspolunKoodiOsa(
            DokumenttiBase docBase,
            KoodistoKoodiDto koodistoKoodiDto,
            Element tbody,
            int depth
    ) {
        if (koodistoKoodiDto != null && koodistoKoodiDto.getMetadata() != null) {
            KoodistoMetadataDto[] metadata = koodistoKoodiDto.getMetadata();
            for (KoodistoMetadataDto metadataDto : metadata) {

                // Valitaan dokumentin kieli
                if (docBase.getKieli().toString().equals(metadataDto.getKieli().toLowerCase())) {

                    Element tr = docBase.getDocument().createElement("tr");
                    tbody.appendChild(tr);
                    Element td = docBase.getDocument().createElement("td");
                    tr.appendChild(td);
                    td.setAttribute("class", "td" + depth);

                    // Nimi
                    StringBuilder nimiBuilder = new StringBuilder();
                    nimiBuilder.append(metadataDto.getNimi());
                    if (koodistoKoodiDto.getKoodiArvo() != null) {
                        nimiBuilder.append(" (");
                        nimiBuilder.append(koodistoKoodiDto.getKoodiArvo());
                        nimiBuilder.append(")");
                    }

                    addTeksti(docBase, nimiBuilder.toString(), "p", td);

                }
            }
        }
    }

    private void addSuorituspolunTutkinnonOsa(DokumenttiBase docBase,
                                              TutkinnonosaKaikkiDto tutkinnonOsaKaikkiDto,
                                              TutkinnonOsaViiteSuppeaDto tutkinnonOsaViiteSuppeaDto,
                                              Element tbody,
                                              int depth) {
        Element tr = docBase.getDocument().createElement("tr");
        tbody.appendChild(tr);
        Element td = docBase.getDocument().createElement("td");
        tr.appendChild(td);
        td.setAttribute("class", "td" + String.valueOf(depth));

        // Nimi
        StringBuilder nimiBuilder = new StringBuilder();
        nimiBuilder.append(getTextString(docBase, tutkinnonOsaKaikkiDto.getNimi()));
        if (tutkinnonOsaKaikkiDto.getKoodiArvo() != null) {
            nimiBuilder.append(" (");
            nimiBuilder.append(tutkinnonOsaKaikkiDto.getKoodiArvo());
            nimiBuilder.append(")");
        }
        if (tutkinnonOsaViiteSuppeaDto.getLaajuus() != null) {
            nimiBuilder.append(" ");
            if (tutkinnonOsaViiteSuppeaDto.getLaajuus().stripTrailingZeros().scale() <= 0) {
                nimiBuilder.append(String.valueOf(tutkinnonOsaViiteSuppeaDto.getLaajuus().intValue()));
            } else {
                nimiBuilder.append(tutkinnonOsaViiteSuppeaDto.getLaajuus().toString());
            }
            nimiBuilder.append(" osp");
        }
        addTeksti(docBase, nimiBuilder.toString(), "p", td);
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
                if (tutkinnonOsa.getPerusteentutkinnonosa() != null && docBase.getDokumentti().isPerusteenSisalto()) {
                    addPerusteenTutkinnonOsa(docBase, tutkinnonOsa.getPerusteentutkinnonosa());
                }
                break;
            default:
                break;
        }

        // Tutkinnon osan vapaat tekstit
        tutkinnonOsa.getVapaat().forEach(vapaaTeksti -> {
            addLokalisoituteksti(docBase, vapaaTeksti.getNimi(), "h5");
            addLokalisoituteksti(docBase, vapaaTeksti.getTeksti(), "div");
        });

        // Osaamisen osoittaminen
        if (tutkinnonOsa.getOsaamisenOsoittaminen() != null) {
            addTeksti(docBase, messages.translate("docgen.osaamisen-osoittaminen", docBase.getKieli()), "h5");
            addLokalisoituteksti(docBase, tutkinnonOsa.getOsaamisenOsoittaminen(), "div");
        }

        // Toteutukset
        if (tutkinnonOsa.getToteutukset().size() > 0) {
            addTeksti(docBase, messages.translate("docgen.toteutukset", docBase.getKieli()), "h5");

            tutkinnonOsa.getToteutukset().forEach(toteutus -> {
                addLokalisoituteksti(docBase, toteutus.getOtsikko(), "h6");
                boolean toteutuksellaSisaltoa = false;

                Element toteutusTaulukko = docBase.getDocument().createElement("table");
                toteutusTaulukko.setAttribute("border", "1");


                Element kooditTr = docBase.getDocument().createElement("tr");
                Element kooditTd = docBase.getDocument().createElement("th");
                kooditTr.appendChild(kooditTd);
                Element koodit = docBase.getDocument().createElement("div");
                kooditTd.appendChild(koodit);

                for (String koodiUri : toteutus.getKoodit()) {
                    KoodistoKoodiDto koodistoKoodiDto = koodistoClient.getByUri(koodiUri);
                    KoodistoMetadataDto[] metadata = koodistoKoodiDto.getMetadata();
                    for (KoodistoMetadataDto metadataDto : metadata) {
                        // Valitaan haluttu kieli
                        if (metadataDto.getKieli().toLowerCase().equals(docBase.getKieli().toString())) {
                            addTeksti(docBase,
                                    metadataDto.getNimi() + " (" + koodistoKoodiDto.getKoodiArvo() + ")",
                                    "p",
                                    koodit);
                            toteutuksellaSisaltoa = true;
                        }

                    }
                }

                if (toteutuksellaSisaltoa) {
                    DokumenttiTaulukko.addRow(docBase, toteutusTaulukko,
                            messages.translate("docgen.liitetyt-koodit", docBase.getKieli()), true);
                    toteutusTaulukko.appendChild(kooditTr);
                }

                Tekstiosa tavatjaymparisto = toteutus.getTavatjaymparisto();
                if (tavatjaymparisto != null && tavatjaymparisto.getTeksti() != null) {
                    DokumenttiTaulukko.addRow(docBase,
                            toteutusTaulukko,
                            messages.translate("docgen.tavat-ja-ymparisto", docBase.getKieli()),
                            true);

                    Element tr = docBase.getDocument().createElement("tr");
                    toteutusTaulukko.appendChild(tr);
                    Element td = docBase.getDocument().createElement("th");
                    tr.appendChild(td);
                    addLokalisoituteksti(docBase, tavatjaymparisto.getTeksti(), "div", td);
                    toteutuksellaSisaltoa = true;
                }

                Tekstiosa arvioinnista = toteutus.getArvioinnista();
                if (arvioinnista != null && arvioinnista.getTeksti() != null) {
                    DokumenttiTaulukko.addRow(docBase,
                            toteutusTaulukko,
                            messages.translate("docgen.osaamisen-arvioinnista", docBase.getKieli()),
                            true);

                    Element tr = docBase.getDocument().createElement("tr");
                    toteutusTaulukko.appendChild(tr);
                    Element td = docBase.getDocument().createElement("th");
                    tr.appendChild(td);
                    addLokalisoituteksti(docBase, arvioinnista.getTeksti(), "div", td);

                    toteutuksellaSisaltoa = true;
                }

                // Lisätään toteutuksen taulukko jos on sisältöä
                if (toteutuksellaSisaltoa) {
                    docBase.getBodyElement().appendChild(toteutusTaulukko);
                }

                toteutus.getVapaat().forEach(vapaaTeksti -> {
                    addLokalisoituteksti(docBase, vapaaTeksti.getNimi(), "h5");
                    addLokalisoituteksti(docBase, vapaaTeksti.getTeksti(), "div");
                });
            });
        }
    }


    private void addOmaTutkinnonOsa(DokumenttiBase docBase, OmaTutkinnonosa omaTutkinnonosa) {

        // Laajuus
        if (omaTutkinnonosa.getLaajuus() != null) {
            addTeksti(docBase, messages.translate("docgen.laajuus", docBase.getKieli()), "h5");
            addTeksti(docBase, omaTutkinnonosa.getLaajuus().toString(), "div");
        }

        // Koodi
        if (omaTutkinnonosa.getKoodi() != null) {
            addTeksti(docBase, messages.translate("docgen.koodi", docBase.getKieli()), "h5");
            addTeksti(docBase, omaTutkinnonosa.getKoodi(), "div");
        }

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

        if (!getTextString(docBase, arvioinninKohdealue.getOtsikko()).equals("automaattinen")) {
            arviointiTaulukko.addOtsikkoSarake(getTextString(docBase, arvioinninKohdealue.getOtsikko()));
        }
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
                .filter(o -> o.getOsaamistaso() != null)
                .sorted(Comparator.comparing(o -> o.getOsaamistaso().getId()))
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
        PerusteKaikkiDto peruste = docBase.getPeruste();
        if (peruste == null) {
            return;
        }

        Optional<TutkinnonosaKaikkiDto> optPerusteenTutkinnonosa = peruste.getTutkinnonOsat().stream()
                .filter(dto -> dto.getId().equals(perusteenTutkinnonosaId))
                .findFirst();

        if (optPerusteenTutkinnonosa.isPresent()) {
            TutkinnonosaKaikkiDto perusteenTutkinnonosa = optPerusteenTutkinnonosa.get();

            if (perusteenTutkinnonosa.getTyyppi().equals(TutkinnonOsaTyyppi.NORMAALI)) {

                // Tavoitteet
                if (perusteenTutkinnonosa.getTavoitteet() != null) {
                    addTeksti(docBase, messages.translate("docgen.tavoitteet", docBase.getKieli()), "h5");
                    addLokalisoituteksti(docBase, perusteenTutkinnonosa.getTavoitteet(), "div");
                }


                // Ammattitaitovaatimukset
                if (perusteenTutkinnonosa.getAmmattitaitovaatimuksetLista() != null) {
                    addTeksti(docBase, messages.translate("docgen.ammattitaitovaatimukset", docBase.getKieli()), "h5");
                    perusteenTutkinnonosa.getAmmattitaitovaatimuksetLista().forEach(dto -> {
                        AmmattitaitovaatimuksenKohdealue ammattitaitovaatimuksenKohdealue = mapper.map(dto,
                                AmmattitaitovaatimuksenKohdealue.class);
                        addAmmattitaitovaatimuksenKohdealue(docBase, ammattitaitovaatimuksenKohdealue);
                    });
                } else if (perusteenTutkinnonosa.getAmmattitaitovaatimukset() != null) {
                    addTeksti(docBase, messages.translate("docgen.ammattitaitovaatimukset", docBase.getKieli()), "h5");
                    addLokalisoituteksti(docBase, perusteenTutkinnonosa.getAmmattitaitovaatimukset(), "div");
                }

                // Arviointi
                if (perusteenTutkinnonosa.getArviointi() != null) {
                    addTeksti(docBase, messages.translate("docgen.arviointi", docBase.getKieli()), "h5");
                    ArviointiDto arviointiDto = perusteenTutkinnonosa.getArviointi();

                    arviointiDto.getArvioinninKohdealueet().forEach(arvioinninKohdealueDto -> {
                        ArvioinninKohdealue arvioinninKohdealue = mapper.map(arvioinninKohdealueDto, ArvioinninKohdealue.class);

                        for (int i = 0; i < arvioinninKohdealue.getArvioinninKohteet().size(); i++) {
                            ArvioinninKohde arvioinninKohde = arvioinninKohdealue.getArvioinninKohteet().get(i);
                            ArvioinninKohdeDto arvioinninKohdeDto = arvioinninKohdealueDto.getArvioinninKohteet().get(i);
                            Reference arviointiasteikkoRef = arvioinninKohdeDto.getArviointiasteikko();
                            if (arviointiasteikkoRef != null) {
                                Long arviointiasteikkoId = arviointiasteikkoRef.getIdLong();
                                ArviointiasteikkoDto dto = arviointiasteikkoService.get(arviointiasteikkoId);
                                Arviointiasteikko arviointiasteikko = mapper.map(dto, Arviointiasteikko.class);

                                arvioinninKohde.setArviointiasteikko(arviointiasteikko);

                                Set<OsaamistasonKriteeri> osaamistasonKriteerit = arvioinninKohdeDto.getOsaamistasonKriteerit().stream()
                                        .sorted(Comparator.comparing(osaamistasonKriteeriDto -> osaamistasonKriteeriDto.getOsaamistaso().getIdLong()))
                                        .map(osaamistasonKriteeriDto -> {
                                            Optional<Osaamistaso> optOsaamistaso = arviointiasteikko.getOsaamistasot().stream()
                                                    .filter(o -> o.getId().equals(osaamistasonKriteeriDto.getOsaamistaso().getIdLong()))
                                                    .findFirst();

                                            OsaamistasonKriteeri osaamistasonKriteeri = mapper.map(osaamistasonKriteeriDto, OsaamistasonKriteeri.class);

                                            if (optOsaamistaso.isPresent()) {
                                                Osaamistaso osaamistaso = optOsaamistaso.get();
                                                osaamistasonKriteeri.setOsaamistaso(osaamistaso);
                                            }

                                            return osaamistasonKriteeri;
                                        })
                                        .collect(Collectors.toSet());

                                arvioinninKohde.setOsaamistasonKriteerit(osaamistasonKriteerit);
                            }
                        }

                        addArvioinninKohdealue(docBase, arvioinninKohdealue);
                    });
                }

                // Ammattitaidon osoittamistavat
                if (perusteenTutkinnonosa.getAmmattitaidonOsoittamistavat() != null) {
                    addTeksti(docBase, messages.translate("docgen.ammattitaidon-osoittamistavat", docBase.getKieli()), "h5");
                    addLokalisoituteksti(docBase, perusteenTutkinnonosa.getAmmattitaidonOsoittamistavat(), "div");
                }

                // Valma/Telma-sisältö
                if (perusteenTutkinnonosa.getValmaTelmaSisalto() != null) {
                    addValmatelmaSisalto(docBase, perusteenTutkinnonosa.getValmaTelmaSisalto());
                }

            } else if (TutkinnonOsaTyyppi.isTutke(perusteenTutkinnonosa.getTyyppi())) {
                if (!ObjectUtils.isEmpty(perusteenTutkinnonosa.getOsaAlueet())) {
                    perusteenTutkinnonosa.getOsaAlueet().stream()
                            .filter(osaAlue -> osaAlue.getNimi() != null)
                            .forEach(osaAlue -> {

                        // Nimi
                        addTeksti(docBase, getTextString(docBase, osaAlue.getNimi()), "h5");

                        // Kuvaus
                        if (osaAlue.getKuvaus() != null) {
                            addTeksti(docBase, getTextString(docBase, osaAlue.getKuvaus()), "div");
                        }

                        // Osaamistavoitteet
                        if (!ObjectUtils.isEmpty(osaAlue.getOsaamistavoitteet())) {
                            osaAlue.getOsaamistavoitteet().forEach(osaamistavoite -> {

                                addTeksti(docBase, getTextString(docBase, osaamistavoite.getNimi()), "h5");

                                String otsikkoAvain = osaamistavoite.isPakollinen() ? "docgen.tutke2.pakolliset_osaamistavoitteet.title"
                                        : "docgen.tutke2.valinnaiset_osaamistavoitteet.title";
                                String otsikko = messages.translate(otsikkoAvain, docBase.getKieli());
                                addTeksti(docBase, otsikko, "h5");

                                addTeksti(docBase, getTextString(docBase, osaamistavoite.getTavoitteet()), "div");

                                // Arviointi
                                addTeksti(docBase, messages.translate("docgen.tutke2.arvioinnin_kohteet.title", docBase.getKieli()), "h6");
                                ArviointiDto arviointi = osaamistavoite.getArviointi();
                                arviointi.getArvioinninKohdealueet().forEach(dto -> addArvioinninKohdealue(docBase,
                                        mapper.map(dto, ArvioinninKohdealue.class)));

                                // Tunnustaminen
                                addTeksti(docBase, messages.translate("docgen.tutke2.tunnustaminen.title", docBase.getKieli()), "h6");
                                addTeksti(docBase, getTextString(docBase, osaamistavoite.getTunnustaminen()), "div");

                                // Ammattitaitovaatimukset
                                List<AmmattitaitovaatimusKohdealueetDto> ammattitaitovaatimukset = osaamistavoite.getAmmattitaitovaatimuksetLista();
                                ammattitaitovaatimukset.forEach(dto -> addAmmattitaitovaatimuksenKohdealue(docBase,
                                        mapper.map(dto, AmmattitaitovaatimuksenKohdealue.class)));
                            });
                        }

                                if (osaAlue.getValmaTelmaSisalto() != null) {
                                    addValmatelmaSisalto(docBase, osaAlue.getValmaTelmaSisalto());
                                }
                            });
                }
            }
        }
    }

    private void addOpintokokonaisuus(DokumenttiBase docBase, SisaltoViite lapsi) {
        Opintokokonaisuus opintokokonaisuus = lapsi.getOpintokokonaisuus();

        addTeksti(docBase, getTextString(docBase, opintokokonaisuus.getKuvaus()), "div");

        addTeksti(docBase, messages.translate("docgen.opetuksen-tavoitteet.title", docBase.getKieli()), "h5");
        addTeksti(docBase, getTextString(docBase, opintokokonaisuus.getOpetuksenTavoiteOtsikko()), "h6");
        addTeksti(docBase, getTextString(docBase, opintokokonaisuus.getTavoitteidenKuvaus()), "p");

        Element tavoitteetEl = docBase.getDocument().createElement("ul");

        opintokokonaisuus.getTavoitteet().forEach(tavoite -> {
            Element tavoiteEl = docBase.getDocument().createElement("li");
            OpintokokonaisuusTavoiteDto tavoiteDto = mapper.map(tavoite, OpintokokonaisuusTavoiteDto.class);
            String rivi = getTextString(docBase, tavoiteDto.getTavoite());
            tavoiteEl.setTextContent(rivi);
            tavoitteetEl.appendChild(tavoiteEl);
        });
        docBase.getBodyElement().appendChild(tavoitteetEl);

        addTeksti(docBase, messages.translate("docgen.keskeiset-sisallot.title", docBase.getKieli()), "h5");
        addTeksti(docBase, getTextString(docBase, opintokokonaisuus.getKeskeisetSisallot()), "div");

        addTeksti(docBase, messages.translate("docgen.arviointi.title", docBase.getKieli()), "h5");
        addTeksti(docBase, getTextString(docBase, opintokokonaisuus.getArvioinninKuvaus()), "div");

        Element arvioinnitEl = docBase.getDocument().createElement("ul");

        opintokokonaisuus.getArvioinnit().forEach(arviointi -> {
            Element arviointiEl = docBase.getDocument().createElement("li");
            String rivi = getTextString(docBase, arviointi.getArviointi());
            arviointiEl.setTextContent(rivi);
            arvioinnitEl.appendChild(arviointiEl);
        });
        docBase.getBodyElement().appendChild(arvioinnitEl);
    }

    private void addValmatelmaSisalto(DokumenttiBase docBase, ValmaTelmaSisaltoDto valmaTelmaSisalto) {
        addValmaOsaamistavoitteet(docBase, valmaTelmaSisalto.getOsaamistavoite());
        addValmaArviointi(docBase, valmaTelmaSisalto);
    }

    private void addValmaOsaamistavoitteet(DokumenttiBase docBase, List<OsaamisenTavoiteDto> OsaamisenTavoiteet) {
        if (ObjectUtils.isEmpty(OsaamisenTavoiteet)) {
            return;
        }

        addTeksti(docBase, messages.translate("docgen.valma.osaamistavoitteet.title", docBase.getKieli()), "h5");

        for (OsaamisenTavoiteDto osaamisenTavoite : OsaamisenTavoiteet) {
            if (osaamisenTavoite.getNimi() != null) {
                addTeksti(docBase, getTextString(docBase, osaamisenTavoite.getNimi()), "h6");
            }

            if (osaamisenTavoite.getKohde() != null) {
                addTeksti(docBase, getTextString(docBase, osaamisenTavoite.getKohde()), "div");
            }

            Element lista = docBase.getDocument().createElement("ul");
            docBase.getBodyElement().appendChild(lista);
            osaamisenTavoite.getTavoitteet().forEach(tavoite -> {
                Element alkio = docBase.getDocument().createElement("li");
                alkio.setTextContent(getTextString(docBase, tavoite));
                lista.appendChild(alkio);
            });

            if (osaamisenTavoite.getSelite() != null) {
                addTeksti(docBase, getTextString(docBase, osaamisenTavoite.getSelite()), "div");
            }
        }
    }

    private void addValmaArviointi(DokumenttiBase docBase, ValmaTelmaSisaltoDto valmaTelmaSisalto) {
        if (valmaTelmaSisalto.getOsaamisenarviointi() != null
                || valmaTelmaSisalto.getOsaamisenarviointiTekstina() != null) {

            addTeksti(docBase, messages.translate("docgen.valma.osaamisenarviointi.title", docBase.getKieli()), "h5");

            if (valmaTelmaSisalto.getOsaamisenarviointi() != null) {
                if (valmaTelmaSisalto.getOsaamisenarviointi().getKohde() != null) {
                    addTeksti(docBase,
                            getTextString(docBase, valmaTelmaSisalto.getOsaamisenarviointi().getKohde()),
                            "div");
                }

                if (!ObjectUtils.isEmpty(valmaTelmaSisalto.getOsaamisenarviointi().getTavoitteet())) {
                    Element lista = docBase.getDocument().createElement("ul");
                    docBase.getBodyElement().appendChild(lista);

                    valmaTelmaSisalto.getOsaamisenarviointi().getTavoitteet().forEach(tavoite -> {
                        Element alkio = docBase.getDocument().createElement("li");
                        alkio.setTextContent(getTextString(docBase, tavoite));
                        lista.appendChild(alkio);
                    });
                }
            }

            if (valmaTelmaSisalto.getOsaamisenarviointiTekstina() != null) {
                addTeksti(docBase,
                        valmaTelmaSisalto.getOsaamisenarviointiTekstina().getTeksti().get(docBase.getKieli()),
                        "div");
            }
        }
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

                        // Todo: Perusteen viite
                        /*if (termiDto == null) {

                        }*/

                        if (termiDto != null && termiDto.getAlaviite() != null && termiDto.getAlaviite() && termiDto.getSelitys() != null) {
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
                liiteService.export(docBase.getOpetussuunnitelma().getId(), uuid, byteArrayOutputStream);

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
                tempImage.getGraphics().fillRect(0, 0, width, height);
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
                String base64 = Base64.getEncoder().encodeToString(out.toByteArray());

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

        String base64 = Base64.getEncoder().encodeToString(image);
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

        String base64 = Base64.getEncoder().encodeToString(image);
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

        String base64 = Base64.getEncoder().encodeToString(image);
        kuva.setAttribute("src", "data:image/jpg;base64," + base64);

        alatunniste.appendChild(kuva);
        head.appendChild(alatunniste);
    }
}
