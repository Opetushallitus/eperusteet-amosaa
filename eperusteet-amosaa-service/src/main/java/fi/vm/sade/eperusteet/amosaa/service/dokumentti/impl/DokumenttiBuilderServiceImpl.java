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
import fi.vm.sade.eperusteet.amosaa.domain.teksti.LokalisoituTeksti;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.SisaltoViite;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.TekstiKappale;
import fi.vm.sade.eperusteet.amosaa.domain.teksti.Tekstiosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Ammattitaitovaatimukset2019;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Ammattitaitovaatimus2019;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Ammattitaitovaatimus2019Kohdealue;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.KotoTaitotaso;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.KotoTaitotasoLaajaAlainenOsaaminen;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlue;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaOsaAlueTyyppi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.OmaTutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Opintokokonaisuus;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Suorituspolku;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.SuorituspolkuRivi;
import fi.vm.sade.eperusteet.amosaa.domain.tutkinnonosa.Tutkinnonosa;
import fi.vm.sade.eperusteet.amosaa.dto.Reference;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoKoodiDto;
import fi.vm.sade.eperusteet.amosaa.dto.koodisto.KoodistoMetadataDto;
import fi.vm.sade.eperusteet.amosaa.dto.ops.TermiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.Ammattitaitovaatimukset2019Dto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.Ammattitaitovaatimus2019Dto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArvioinninKohdeDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ArviointiasteikkoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoKielitaitotasoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoLaajaAlainenOsaaminenDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoLaajaAlaisenOsaamisenAlueDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoOpintoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.KotoTaitotasoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.MuodostumisSaantoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.OsaAlueKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.OsaamisenTavoiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.PerusteKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.SuoritustapaLaajaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.Suoritustapakoodi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonOsaTyyppi;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonOsaViiteSuppeaDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.TutkinnonosaKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.ValmaTelmaSisaltoDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.geneerinenarviointiasteikko.GeneerinenArviointiasteikkoKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.peruste.geneerinenarviointiasteikko.GeneerisenArvioinninOsaamistasonKriteeriKaikkiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.AmmattitaitovaatimusKohdealueetDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.KoulutuksenOsaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.LokalisoituTekstiDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OmaOsaAlueExportDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OmaTutkinnonosaExportDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.OpintokokonaisuusTavoiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.SisaltoViiteKevytDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TekstiosaDto;
import fi.vm.sade.eperusteet.amosaa.dto.teksti.TuvaLaajaAlainenOsaaminenDto;
import fi.vm.sade.eperusteet.amosaa.repository.teksti.SisaltoviiteRepository;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiBuilderService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.DokumenttiKuvaService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.LocalizedMessagesService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.PdfService;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.CharapterNumberGenerator;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiBase;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiRivi;
import fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiTaulukko;
import fi.vm.sade.eperusteet.amosaa.service.external.ArviointiasteikkoService;
import fi.vm.sade.eperusteet.amosaa.service.external.EperusteetService;
import fi.vm.sade.eperusteet.amosaa.service.mapping.DtoMapper;
import fi.vm.sade.eperusteet.amosaa.service.ops.LiiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.SisaltoViiteService;
import fi.vm.sade.eperusteet.amosaa.service.ops.TermistoService;
import fi.vm.sade.eperusteet.amosaa.service.util.KoodistoClient;
import fi.vm.sade.eperusteet.utils.dto.dokumentti.DokumenttiMetaDto;
import org.apache.commons.lang3.StringUtils;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.vm.sade.eperusteet.amosaa.dto.peruste.RakenneModuuliRooli.VIRTUAALINEN;
import static fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiUtils.addHeader;
import static fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiUtils.addLokalisoituteksti;
import static fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiUtils.addTeksti;
import static fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiUtils.getTextString;
import static fi.vm.sade.eperusteet.amosaa.service.dokumentti.impl.util.DokumenttiUtils.selectLaajuusYksikkoMessage;

@Service
public class DokumenttiBuilderServiceImpl implements DokumenttiBuilderService {

    private static final Logger LOG = LoggerFactory.getLogger(DokumenttiBuilderServiceImpl.class);

    private static final float COMPRESSION_LEVEL = 0.9f;
    private static final DecimalFormat df2 = new DecimalFormat("0.##");

    @Autowired
    private LiiteService liiteService;

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

    @Autowired
    private DokumenttiKuvaService dokumenttiKuvaService;

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

        // Kuvat
        buildImages(docBase);
        buildKansilehti(docBase, ops.getId(), kieli);
        buildYlatunniste(docBase, ops.getId(), kieli);
        buildAlatunniste(docBase, ops.getId(), kieli);

        DokumenttiMetaDto meta = DokumenttiMetaDto.builder()
                .title(getTextString(docBase, ops.getNimi()))
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
            SisaltoViiteKevytDto sisaltoViiteKevytDto = mapper.map(lapsi, SisaltoViiteKevytDto.class);

            if (lapsi == null || sisaltoViiteKevytDto == null || sisaltoViiteKevytDto.getNimi() == null) {
                return;
            }

            if (lapsi.getTyyppi().equals(SisaltoTyyppi.SUORITUSPOLUT) && CollectionUtils.isEmpty(lapsi.getLapset())) {
                continue;
            }

            TekstiKappale tekstiKappale = lapsi.getTekstiKappale();
            StringBuilder otsikkoBuilder = new StringBuilder();
            otsikkoBuilder.append(getTextString(docBase, sisaltoViiteKevytDto.getNimi()));

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

            if (lapsi.getTyyppi().equals(SisaltoTyyppi.OPINTOKOKONAISUUS)
                    && lapsi.getOpintokokonaisuus() != null
                    && lapsi.getOpintokokonaisuus().getLaajuus() != null && lapsi.getOpintokokonaisuus().getLaajuus().compareTo(BigDecimal.ZERO) > 0
                    && lapsi.getOpintokokonaisuus().getLaajuusYksikko() != null) {
                otsikkoBuilder.append(", " + df2.format(lapsi.getOpintokokonaisuus().getLaajuus()) + " " + messages.translate(selectLaajuusYksikkoMessage(lapsi.getOpintokokonaisuus().getLaajuusYksikko()), docBase.getKieli()));
            }

            if (lapsi.getTyyppi().equals(SisaltoTyyppi.KOULUTUKSENOSA)) {
                Integer minimilaajuus = lapsi.getKoulutuksenosa().getLaajuusMinimi();
                Integer maksimiLAajuus = lapsi.getKoulutuksenosa().getLaajuusMaksimi();
                if (lapsi.getPerusteenOsaId() != null) {
                    fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutuksenOsaDto perusteenOsaDto = (fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutuksenOsaDto) eperusteetService.getPerusteenOsa(docBase.getOpetussuunnitelma().getPeruste().getId(), lapsi.getPerusteenOsaId());
                    minimilaajuus = perusteenOsaDto.getLaajuusMinimi();
                    maksimiLAajuus = perusteenOsaDto.getLaajuusMaksimi();
                }

                otsikkoBuilder.append(", " + minimilaajuus + " - " + maksimiLAajuus + " " + messages.translate("docgen.laajuus.viikkoa", docBase.getKieli()));
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
                case LAAJAALAINENOSAAMINEN:
                    addLaajaalainenOsaaminen(docBase, lapsi);
                    break;
                case KOULUTUKSENOSA:
                    addKoulutuksenosa(docBase, lapsi);
                    break;
                case KOTO_LAAJAALAINENOSAAMINEN:
                    addKotoLaajaAlainenOsaaminen(docBase, lapsi);
                    break;
                case KOTO_KIELITAITOTASO:
                    addKotoKielitaitotaso(docBase, lapsi);
                    break;
                case KOTO_OPINTO:
                    addKotoOpinto(docBase, lapsi);
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
                if (tutkinnonOsa.getPerusteentutkinnonosa() != null) {
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

        if (!CollectionUtils.isEmpty(lapsi.getOsaAlueet())) {
            docBase.getGenerator().increaseDepth();
            lapsi.getOsaAlueet().stream()
                    .sorted(Comparator.comparingInt(OmaOsaAlue::sort) )
                    .forEach(osaAlue -> {
                addOmaOsaAlue(docBase, osaAlue);
                docBase.getGenerator().increaseNumber();
            });
            docBase.getGenerator().decreaseDepth();
        }
    }

    private void addOmaOsaAlue(DokumenttiBase docBase, OmaOsaAlue osaAlue) {
        OmaOsaAlueExportDto omaOsaAlueDto = mapper.map(osaAlue, OmaOsaAlueExportDto.class);
        PerusteKaikkiDto peruste = docBase.getPeruste();

        StringBuilder otsikko = new StringBuilder(getTextString(docBase, omaOsaAlueDto.getNimi()));
        if (omaOsaAlueDto.getKoodiArvo() != null)  {
            otsikko.append(" (" + omaOsaAlueDto.getKoodiArvo().toUpperCase() + ")");
        }
        addHeader(docBase, otsikko.toString());

        if (!CollectionUtils.isEmpty(omaOsaAlueDto.getToteutukset())) {
            addTeksti(docBase, messages.translate("docgen.koulutuksen-jarjestajan-toteutus", docBase.getKieli()), "h5");

            omaOsaAlueDto.getToteutukset().forEach(toteutus -> {
                addLokalisoituteksti(docBase, toteutus.getOtsikko(), "h6");

                Element toteutusTaulukko = docBase.getDocument().createElement("table");
                toteutusTaulukko.setAttribute("border", "1");

                Element kooditTr = docBase.getDocument().createElement("tr");
                Element kooditTd = docBase.getDocument().createElement("th");
                kooditTr.appendChild(kooditTd);
                Element koodit = docBase.getDocument().createElement("div");
                kooditTd.appendChild(koodit);

                TekstiosaDto tavatjaymparisto = toteutus.getTavatjaymparisto();
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
                }

                TekstiosaDto arvioinnista = toteutus.getArvioinnista();
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
                }

                docBase.getBodyElement().appendChild(toteutusTaulukko);

                toteutus.getVapaat().forEach(vapaaTeksti -> {
                    addLokalisoituteksti(docBase, vapaaTeksti.getNimi(), "h5");
                    addLokalisoituteksti(docBase, vapaaTeksti.getTeksti(), "div");
                });
            });

            StringBuilder sisaltoOtsikko = new StringBuilder();
            OsaAlueKaikkiDto perusteenOsaAlue = null;

            if (omaOsaAlueDto.getPerusteenOsaAlueId() != null) {
                perusteenOsaAlue = peruste.getTutkinnonOsat().stream()
                        .map(TutkinnonosaKaikkiDto::getOsaAlueet)
                        .flatMap(Collection::stream)
                        .filter(posaAlue -> posaAlue.getId().equals(omaOsaAlueDto.getPerusteenOsaAlueId()))
                        .findFirst().orElse(null);

                if (perusteenOsaAlue == null) {
                    return;
                }
            }

            if (perusteenOsaAlue != null) {
                addTeksti(docBase, messages.translate("docgen.perusteen-sisalto", docBase.getKieli()), "h5");
                if (omaOsaAlueDto.getTyyppi().equals(OmaOsaAlueTyyppi.PAKOLLINEN)) {
                    sisaltoOtsikko.append(messages.translate("docgen.tutke2.pakolliset_osaamistavoitteet.title", docBase.getKieli()));
                    sisaltoOtsikko.append(", " + perusteenOsaAlue.getPakollisetOsaamistavoitteet().getLaajuus());
                }

                if (omaOsaAlueDto.getTyyppi().equals(OmaOsaAlueTyyppi.VALINNAINEN)) {
                    sisaltoOtsikko.append(messages.translate("docgen.tutke2.valinnaiset_osaamistavoitteet.title", docBase.getKieli()));
                    sisaltoOtsikko.append(", " + perusteenOsaAlue.getValinnaisetOsaamistavoitteet().getLaajuus());
                }
            } else {
                addTeksti(docBase, messages.translate("docgen.sisalto", docBase.getKieli()), "h5");
                sisaltoOtsikko.append(messages.translate("docgen.osaamistavoitteet", docBase.getKieli()));
                sisaltoOtsikko.append(", " + omaOsaAlueDto.getLaajuus());
            }

            sisaltoOtsikko.append(" " + messages.translate("docgen.laajuus.osp", docBase.getKieli()));
            addTeksti(docBase, sisaltoOtsikko.toString(), "h6");

            if (perusteenOsaAlue != null) {

                if (omaOsaAlueDto.getTyyppi().equals(OmaOsaAlueTyyppi.PAKOLLINEN)) {
                    addAmmattitaitovaatimuksenKohdealue(docBase, perusteenOsaAlue.getPakollisetOsaamistavoitteet().getTavoitteet());
                }

                if (omaOsaAlueDto.getTyyppi().equals(OmaOsaAlueTyyppi.VALINNAINEN)) {
                    addAmmattitaitovaatimuksenKohdealue(docBase, perusteenOsaAlue.getValinnaisetOsaamistavoitteet().getTavoitteet());
                }

                addGeneerinenArviointi(docBase, mapper.map(perusteenOsaAlue.getArviointi(), GeneerinenArviointiasteikkoKaikkiDto.class));
            } else {
                addAmmattitaitovaatimuksenKohdealue(docBase, mapper.map(omaOsaAlueDto.getOsaamistavoitteet(), Ammattitaitovaatimukset2019Dto.class));
                addGeneerinenArviointi(docBase, omaOsaAlueDto.getGeneerinenArviointiasteikko());
            }
        }
    }

    private void addGeneerinenArviointi(DokumenttiBase docBase, GeneerinenArviointiasteikkoKaikkiDto geneerinenArviointiasteikko) {
        if (geneerinenArviointiasteikko != null) {
            addTeksti(docBase, messages.translate("docgen.valma.osaamisenarviointi.title", docBase.getKieli()), "h5");

            LokalisoituTekstiDto nimi = geneerinenArviointiasteikko.getNimi();
            LokalisoituTekstiDto kohde = geneerinenArviointiasteikko.getKohde();
            Set<GeneerisenArvioinninOsaamistasonKriteeriKaikkiDto> osaamistasonKriteerit = geneerinenArviointiasteikko.getOsaamistasonKriteerit();

            Element taulukko = docBase.getDocument().createElement("table");
            taulukko.setAttribute("border", "1");
            docBase.getBodyElement().appendChild(taulukko);
            Element tbody = docBase.getDocument().createElement("tbody");
            taulukko.appendChild(tbody);

            // Nimi ja kohde
            {
                Element tr = docBase.getDocument().createElement("tr");
                tr.setAttribute("bgcolor", "#EEEEEE");
                tbody.appendChild(tr);

                Element th = docBase.getDocument().createElement("th");
                th.setAttribute("colspan", "4");
                // EP-1996
                // th.appendChild(newBoldElement(docBase.getDocument(), getTextString(docBase, nimi)));
                Element kohdeEl = docBase.getDocument().createElement("p");
                kohdeEl.setTextContent(getTextString(docBase, kohde));
                th.appendChild(kohdeEl);
                tr.appendChild(th);
            }

            // Osaamistason kriteerit
            {
                osaamistasonKriteerit.stream()
                        .filter(ok -> ok.getOsaamistaso() != null && !ObjectUtils.isEmpty(ok.getKriteerit()))
                        .sorted(Comparator.comparing(k -> k.getOsaamistaso().getId()))
                        .forEach(osaamistasonKriteeri -> {
                            Element tr = docBase.getDocument().createElement("tr");
                            tbody.appendChild(tr);

                            Element kriteeriTaso = docBase.getDocument().createElement("td");
                            kriteeriTaso.setAttribute("colspan", "1");
                            kriteeriTaso.setTextContent(getTextString(docBase,
                                    osaamistasonKriteeri.getOsaamistaso().getOtsikko()));
                            tr.appendChild(kriteeriTaso);

                            Element kriteeriKriteerit = docBase.getDocument().createElement("td");
                            kriteeriKriteerit.setAttribute("colspan", "3");

                            Element kriteeriLista = docBase.getDocument().createElement("ul");
                            kriteeriKriteerit.appendChild(kriteeriLista);

                            osaamistasonKriteeri.getKriteerit().forEach(kriteeriKriteeri -> {
                                String kriteeriKriteeriText = getTextString(docBase, kriteeriKriteeri);
                                if (org.apache.commons.lang.StringUtils.isNotEmpty(kriteeriKriteeriText)) {
                                    addTeksti(docBase, kriteeriKriteeriText, "li", kriteeriLista);
                                }
                            });
                            kriteeriKriteerit.appendChild(kriteeriLista);
                            tr.appendChild(kriteeriKriteerit);
                        });
            }
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
        if (omaTutkinnonosa.getAmmattitaitovaatimuksetLista().size() > 0 || omaTutkinnonosa.getAmmattitaitovaatimukset() != null) {
            addTeksti(docBase, messages.translate("docgen.ammattitaitovaatimukset", docBase.getKieli()), "h5");

            if (omaTutkinnonosa.getAmmattitaitovaatimukset() != null) {
                addAmmattitaitovaatimukset2019(docBase, omaTutkinnonosa.getAmmattitaitovaatimukset());
            }

            if (omaTutkinnonosa.getAmmattitaitovaatimuksetLista().size() > 0) {
                omaTutkinnonosa.getAmmattitaitovaatimuksetLista()
                        .forEach(ammattitaitovaatimuksenKohdealue -> addAmmattitaitovaatimuksenKohdealue(docBase, ammattitaitovaatimuksenKohdealue));
            }
        }

        // Arviointi
        if (omaTutkinnonosa.getArviointi() != null) {
            addTeksti(docBase, messages.translate("docgen.arviointi", docBase.getKieli()), "h5");
            Arviointi arviointi = omaTutkinnonosa.getArviointi();
            arviointi.getArvioinninKohdealueet()
                    .forEach(arvioinninKohdealue -> addArvioinninKohdealue(docBase, arvioinninKohdealue));
        }

        if (omaTutkinnonosa.getGeneerinenarviointi() != null) {
            addGeneerinenArviointi(docBase, mapper.map(omaTutkinnonosa, OmaTutkinnonosaExportDto.class).getGeneerinenArviointiasteikko());
        }

        // Ammattitaidon osoittamistavat
        if (omaTutkinnonosa.getAmmattitaidonOsoittamistavat() != null) {
            addTeksti(docBase, messages.translate("docgen.ammattitaidon-osoittamistavat", docBase.getKieli()), "h5");
            addLokalisoituteksti(docBase, omaTutkinnonosa.getAmmattitaidonOsoittamistavat(), "div");
        }
    }

    private void addAmmattitaitovaatimukset2019(DokumenttiBase docBase, Ammattitaitovaatimukset2019 ammattitaitovaatimukset2019) {
        if (ammattitaitovaatimukset2019 != null) {
            LokalisoituTeksti kohde = ammattitaitovaatimukset2019.getKohde();
            List<Ammattitaitovaatimus2019> vaatimukset = ammattitaitovaatimukset2019.getVaatimukset();
            List<Ammattitaitovaatimus2019Kohdealue> kohdealueet = ammattitaitovaatimukset2019.getKohdealueet();

            if (!ObjectUtils.isEmpty(vaatimukset) || !ObjectUtils.isEmpty(kohdealueet)) {
                if (kohde != null && !ObjectUtils.isEmpty(vaatimukset)) {
                    addTeksti(docBase, getTextString(docBase, kohde), "p");
                }

                Element listaEl = docBase.getDocument().createElement("ul");
                docBase.getBodyElement().appendChild(listaEl);

                vaatimukset.forEach(vaatimus -> {
                    Element vaatimusEl = docBase.getDocument().createElement("li");
                    String rivi = getTextString(docBase, vaatimus.getVaatimus());
                    vaatimusEl.setTextContent(rivi);
                    listaEl.appendChild(vaatimusEl);
                });

                kohdealueet.forEach(alue -> {
                    Element alueEl = docBase.getDocument().createElement("div");
                    docBase.getBodyElement().appendChild(alueEl);

                    LokalisoituTeksti kuvaus = alue.getKuvaus();
                    if (kuvaus != null) {
                        Element kuvausEl = docBase.getDocument().createElement("strong");
                        kuvausEl.setTextContent(getTextString(docBase, kuvaus));
                        alueEl.appendChild(kuvausEl);
                    }

                    if (!ObjectUtils.isEmpty(alue.getVaatimukset())) {

                        Element kohdeEl = docBase.getDocument().createElement("p");
                        if (kohde != null) {
                            kohdeEl.setTextContent(getTextString(docBase, kohde));
                        } else {
                            kohdeEl.setTextContent(messages.translate("docgen.info.opiskelija", docBase.getKieli()));
                        }
                        alueEl.appendChild(kohdeEl);

                        Element alueListaEl = docBase.getDocument().createElement("ul");
                        alue.getVaatimukset().forEach(vaatimus -> {
                            Element vaatimusEl = docBase.getDocument().createElement("li");
                            Ammattitaitovaatimus2019Dto vaatimusDto = mapper.map(vaatimus, Ammattitaitovaatimus2019Dto.class);
                            String rivi = getTextString(docBase, vaatimusDto.getVaatimus());
                            vaatimusEl.setTextContent(rivi);
                            alueListaEl.appendChild(vaatimusEl);
                        });
                        alueEl.appendChild(alueListaEl);
                    }

                });
            }
        }
    }

    private void addAmmattitaitovaatimuksenKohdealue(DokumenttiBase docBase,
                                                     Ammattitaitovaatimukset2019Dto ammattitaitovaatimukset2019Dto) {
        DokumenttiTaulukko taulukko = new DokumenttiTaulukko();
        StringBuilder vaatimuksenKohteetBuilder = new StringBuilder();
        ammattitaitovaatimukset2019Dto.getKohdealueet().forEach(ammattitaitovaatimuksenKohde -> {
            if (ammattitaitovaatimukset2019Dto.getKohde() != null) {
                vaatimuksenKohteetBuilder.append("<h6>");
                vaatimuksenKohteetBuilder.append(getTextString(docBase, ammattitaitovaatimukset2019Dto.getKohde()));
                vaatimuksenKohteetBuilder.append("</h6>");
            }

            if (ammattitaitovaatimuksenKohde.getKuvaus() != null) {
                vaatimuksenKohteetBuilder.append("<p>");
                vaatimuksenKohteetBuilder.append(getTextString(docBase, ammattitaitovaatimuksenKohde.getKuvaus()));
                vaatimuksenKohteetBuilder.append("</p>");
            }

            vaatimuksenKohteetBuilder.append("<ul>");
            ammattitaitovaatimuksenKohde.getVaatimukset().forEach(vaatimus -> {
                vaatimuksenKohteetBuilder.append("<li>");
                vaatimuksenKohteetBuilder.append(getTextString(docBase, vaatimus.getVaatimus()));
                if (vaatimus.getKoodi() != null) {
                    vaatimuksenKohteetBuilder.append(" (");
                    vaatimuksenKohteetBuilder.append(vaatimus.getKoodi().getArvo());
                    vaatimuksenKohteetBuilder.append(")");
                }
                vaatimuksenKohteetBuilder.append("</li>");
            });
            vaatimuksenKohteetBuilder.append("</ul>");
        });

        DokumenttiRivi rivi = new DokumenttiRivi();
        rivi.addSarake(vaatimuksenKohteetBuilder.toString());
        taulukko.addRivi(rivi);

        taulukko.addToDokumentti(docBase);
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

        if (opintokokonaisuus.getTavoitteidenKuvaus() != null) {
            addTeksti(docBase, messages.translate("docgen.tavoitteiden-kuvaus", docBase.getKieli()), "h6");
            addTeksti(docBase, getTextString(docBase, opintokokonaisuus.getTavoitteidenKuvaus()), "div");
        }

        addTeksti(docBase, getTextString(docBase, opintokokonaisuus.getOpetuksenTavoiteOtsikko()), "h6");
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

        if (opintokokonaisuus.getArvioinninKuvaus() != null) {
            addTeksti(docBase, messages.translate("docgen.arvioinnin-kuvaus", docBase.getKieli()), "h6");
            addTeksti(docBase, getTextString(docBase, opintokokonaisuus.getArvioinninKuvaus()), "div");
        }

        addTeksti(docBase, messages.translate("docgen.osaamisen-arvioinnin-kriteerit", docBase.getKieli()), "h6");
        Element arvioinnitEl = docBase.getDocument().createElement("ul");

        opintokokonaisuus.getArvioinnit().forEach(arviointi -> {
            Element arviointiEl = docBase.getDocument().createElement("li");
            String rivi = getTextString(docBase, arviointi.getArviointi());
            arviointiEl.setTextContent(rivi);
            arvioinnitEl.appendChild(arviointiEl);
        });
        docBase.getBodyElement().appendChild(arvioinnitEl);
    }

    private void addLaajaalainenOsaaminen(DokumenttiBase docBase, SisaltoViite lapsi) {
        TuvaLaajaAlainenOsaaminenDto tuvaLaajaAlainenOsaaminenDto = mapper.map(lapsi.getTuvaLaajaAlainenOsaaminen(), TuvaLaajaAlainenOsaaminenDto.class);
        addTeksti(docBase, getTextString(docBase, tuvaLaajaAlainenOsaaminenDto.getTeksti()), "div");
    }

    private void addKoulutuksenosa(DokumenttiBase docBase, SisaltoViite lapsi) {
        fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutuksenOsaDto perusteenOsaDto = null;
        if (lapsi.getPerusteenOsaId() != null) {
            perusteenOsaDto = (fi.vm.sade.eperusteet.amosaa.dto.peruste.KoulutuksenOsaDto) eperusteetService.getPerusteenOsa(docBase.getOpetussuunnitelma().getPeruste().getId(), lapsi.getPerusteenOsaId());
        }
        KoulutuksenOsaDto koulutuksenOsaDto = mapper.map(lapsi.getKoulutuksenosa(), KoulutuksenOsaDto.class);

        addTeksti(docBase, getTextString(docBase, perusteenOsaDto != null ? perusteenOsaDto.getKuvaus() : koulutuksenOsaDto.getKuvaus()), "div");

        addTeksti(docBase, messages.translate("docgen.tavoitteet.title", docBase.getKieli()), "h5");
        Element tavoitteetEl = docBase.getDocument().createElement("ul");
        Stream.concat(
                perusteenOsaDto != null ? Stream.of(perusteenOsaDto.getTavoitteet()) : Stream.of(koulutuksenOsaDto.getTavoitteet()),
                Stream.of(koulutuksenOsaDto.getPaikallinenTarkennus() != null ? koulutuksenOsaDto.getPaikallinenTarkennus().getTavoitteet() : Collections.<LokalisoituTekstiDto>emptyList()))
                .flatMap(Collection::stream)
                .forEach(tavoite -> {
                    Element tavoiteEl = docBase.getDocument().createElement("li");
                    String rivi = getTextString(docBase, tavoite);
                    tavoiteEl.setTextContent(rivi);
                    tavoitteetEl.appendChild(tavoiteEl);
                });
        docBase.getBodyElement().appendChild(tavoitteetEl);

        if (koulutuksenOsaDto.getPaikallinenTarkennus() != null) {
            addTeksti(docBase, getTextString(docBase, koulutuksenOsaDto.getPaikallinenTarkennus().getTavoitteetKuvaus()), "div");

            if (!koulutuksenOsaDto.getPaikallinenTarkennus().getLaajaalaisetosaamiset().isEmpty()) {
                addTeksti(docBase, messages.translate("docgen.laaja-alainen-osaaminen.title", docBase.getKieli()), "h5");
                koulutuksenOsaDto.getPaikallinenTarkennus().getLaajaalaisetosaamiset().forEach(lao -> {
                    addTeksti(docBase, getTextString(docBase, lao.getNimi()), "h6");
                    addTeksti(docBase, getTextString(docBase, lao.getLaajaAlaisenOsaamisenKuvaus()), "div");
                });
            }

            addTeksti(docBase, messages.translate("docgen.keskeinen-sisalto.title", docBase.getKieli()), "h5");
            addTeksti(docBase, getTextString(docBase, perusteenOsaDto != null ? perusteenOsaDto.getKeskeinenSisalto() : koulutuksenOsaDto.getKeskeinenSisalto()), "div");
            addTeksti(docBase, getTextString(docBase, koulutuksenOsaDto.getPaikallinenTarkennus().getKeskeinenSisalto()), "div");
            addTeksti(docBase, messages.translate("docgen.arviointi.title", docBase.getKieli()), "h5");
            addTeksti(docBase, getTextString(docBase, perusteenOsaDto != null ? perusteenOsaDto.getArvioinninKuvaus() : koulutuksenOsaDto.getArvioinninKuvaus()), "div");
            addTeksti(docBase, getTextString(docBase, koulutuksenOsaDto.getPaikallinenTarkennus().getArvioinninKuvaus()), "div");

            if (!CollectionUtils.isEmpty(koulutuksenOsaDto.getPaikallinenTarkennus().getKoulutuksenJarjestajat())) {
                addTeksti(docBase, messages.translate("docgen.koulutuksen-jarjestajat.title", docBase.getKieli()), "h5");

                koulutuksenOsaDto.getPaikallinenTarkennus().getKoulutuksenJarjestajat().forEach(koulutuksenJarjestajaDto -> {
                    addTeksti(docBase, getTextString(docBase, koulutuksenJarjestajaDto.getNimi()), "h5");
                    addTeksti(docBase, messages.translate("docgen.toteutusuunnitelman-tai-koulutuksen-jarjestajan-verkkosivut.title", docBase.getKieli()), "h6");

                    Element linkkiDiv = docBase.getDocument().createElement("div");
                    Element koulutuksenjarjestajanUrl = docBase.getDocument().createElement("a");
                    koulutuksenjarjestajanUrl.setTextContent(getTextString(docBase, koulutuksenJarjestajaDto.getUrl()));
                    koulutuksenjarjestajanUrl.setAttribute("href", getTextString(docBase, koulutuksenJarjestajaDto.getUrl()));
                    linkkiDiv.appendChild(koulutuksenjarjestajanUrl);
                    docBase.getBodyElement().appendChild(linkkiDiv);

                    addTeksti(docBase, messages.translate("docgen.kaytannon-toteutus.title", docBase.getKieli()), "h6");
                    addTeksti(docBase, getTextString(docBase, koulutuksenJarjestajaDto.getKuvaus()), "div");
                });
            }
        }
    }

    private void addKotoLaajaAlainenOsaaminen(DokumenttiBase docBase, SisaltoViite lapsi) {
        KotoLaajaAlainenOsaaminenDto perusteenOsaDto = (KotoLaajaAlainenOsaaminenDto) eperusteetService.getPerusteenOsa(docBase.getOpetussuunnitelma().getPeruste().getId(), lapsi.getPerusteenOsaId());
        addTeksti(docBase, getTextString(docBase, perusteenOsaDto.getYleiskuvaus()), "div");

        perusteenOsaDto.getOsaamisAlueet().forEach(osaamisalue -> {
            addTeksti(docBase, getTextString(docBase, new LokalisoituTekstiDto(osaamisalue.getKoodi().getNimi())), "h6");
            addTeksti(docBase, getTextString(docBase, osaamisalue.getKuvaus()), "div");
        });

        addTeksti(docBase, messages.translate("docgen.laaja-alaisen-osaamisen-paikallinen-tarkennus", docBase.getKieli()), "h6");
        addTeksti(docBase, getTextString(docBase, lapsi.getKotoLaajaAlainenOsaaminen().getTeksti()), "div");
    }

    private void addKotoKielitaitotaso(DokumenttiBase docBase, SisaltoViite lapsi) {
        KotoKielitaitotasoDto perusteenOsaDto = (KotoKielitaitotasoDto) eperusteetService.getPerusteenOsa(docBase.getOpetussuunnitelma().getPeruste().getId(), lapsi.getPerusteenOsaId());
        addTeksti(docBase, getTextString(docBase, perusteenOsaDto.getKuvaus()), "div");

        Map<String, KotoTaitotaso> taitotasoMap = lapsi.getKotoKielitaitotaso().getTaitotasot().stream().collect(Collectors.toMap((taitotaso -> taitotaso.getKoodiUri()), (taitotaso -> taitotaso)));
        addKotoTaitotasot(docBase, taitotasoMap, perusteenOsaDto.getTaitotasot(), "docgen.tavoitteet.title");

        addKotoTaitotasoLaajaAlaisetOsaamiset(docBase, lapsi.getKotoKielitaitotaso().getLaajaAlaisetOsaamiset());
    }

    private void addKotoOpinto(DokumenttiBase docBase, SisaltoViite lapsi) {
        KotoOpintoDto perusteenOsaDto = (KotoOpintoDto) eperusteetService.getPerusteenOsa(docBase.getOpetussuunnitelma().getPeruste().getId(), lapsi.getPerusteenOsaId());
        addTeksti(docBase, getTextString(docBase, perusteenOsaDto.getKuvaus()), "div");

        Map<String, KotoTaitotaso> taitotasoMap = lapsi.getKotoOpinto().getTaitotasot().stream().collect(Collectors.toMap((KotoTaitotaso::getKoodiUri), (taitotaso -> taitotaso)));
        addKotoTaitotasot(docBase, taitotasoMap, perusteenOsaDto.getTaitotasot(), "docgen.tavoitteet-ja-sisallot.title");

        addKotoTaitotasoLaajaAlaisetOsaamiset(docBase, lapsi.getKotoOpinto().getLaajaAlaisetOsaamiset());
    }

    private void addKotoTaitotasoLaajaAlaisetOsaamiset(DokumenttiBase docBase, List<KotoTaitotasoLaajaAlainenOsaaminen> laajaAlaisetOsaamiset) {
        List<SisaltoViiteDto> laajaAlaisetViitteet = svService.getSisaltoviitteet(docBase.getOpetussuunnitelma().getKoulutustoimija().getId(), docBase.getOpetussuunnitelma().getId(), SisaltoTyyppi.KOTO_LAAJAALAINENOSAAMINEN);

        Map<String, KotoLaajaAlaisenOsaamisenAlueDto> perusteenLaot = laajaAlaisetViitteet.stream()
                .map(laoViite -> eperusteetService.getPerusteenOsa(docBase.getOpetussuunnitelma().getPeruste().getId(), laoViite.getPerusteenOsaId()))
                .map(perusteenOsaDto -> ((KotoLaajaAlainenOsaaminenDto) perusteenOsaDto))
                .map(KotoLaajaAlainenOsaaminenDto::getOsaamisAlueet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap((lao -> lao.getKoodi().getUri()), (lao -> lao)));

        if (!laajaAlaisetOsaamiset.isEmpty()) {
            addTeksti(docBase, messages.translate("docgen.laaja-alainen-osaaminen.title", docBase.getKieli()), "h5");
        }

        laajaAlaisetOsaamiset.forEach(lao -> {
            KotoLaajaAlaisenOsaamisenAlueDto perusteenLao = perusteenLaot.get(lao.getKoodiUri());
            addTeksti(docBase, getTextString(docBase, new LokalisoituTekstiDto(perusteenLao.getKoodi().getNimi())), "h6");
            addTeksti(docBase, getTextString(docBase, perusteenLao.getKuvaus()), "div");

            addTeksti(docBase, getTextString(docBase, lao.getTeksti()), "div");
        });
    }

    private void addKotoTaitotasot(DokumenttiBase docBase, Map<String, KotoTaitotaso> taitotasoMap, List<KotoTaitotasoDto> taitotasot, String tavoiteTitle) {

        taitotasot.forEach(taitotaso -> {

            addTeksti(docBase, getTextString(docBase, new LokalisoituTekstiDto(taitotaso.getNimi().getNimi())), "h5");

            String tavoitteet = getTextString(docBase, taitotaso.getTavoitteet());
            addTextWithTopic(tavoitteet, tavoiteTitle, docBase);

            if (taitotasoMap.get(taitotaso.getNimi().getUri()) != null) {
                KotoTaitotaso opsTaitotaso = taitotasoMap.get(taitotaso.getNimi().getUri());

                if (opsTaitotaso.getTavoiteTarkennus() != null) {
                    String tavoiteTarkennus = getTextString(docBase, opsTaitotaso.getTavoiteTarkennus());
                    addTextWithTopic(tavoiteTarkennus, "docgen.tavoitteiden-paikallinen-tarkennus.title", docBase);
                }

                if (opsTaitotaso.getSisaltoTarkennus() != null) {
                    String sisaltoTarkennus = getTextString(docBase, opsTaitotaso.getSisaltoTarkennus());
                    addTextWithTopic(sisaltoTarkennus, "docgen.sisaltojen-paikallinen-tarkennus.title", docBase);
                }
            }

            String kielenkayttotarkoitus = getTextString(docBase, taitotaso.getKielenkayttotarkoitus());
            String aihealueet = getTextString(docBase, taitotaso.getAihealueet());
            String viestintataidot = getTextString(docBase, taitotaso.getViestintataidot());
            String opiskelijantaidot = getTextString(docBase, taitotaso.getOpiskelijantaidot());

            String opiskelijanTyoelamataidot = getTextString(docBase, taitotaso.getOpiskelijanTyoelamataidot());
            String suullinenVastaanottaminen = getTextString(docBase, taitotaso.getSuullinenVastaanottaminen());
            String suullinenTuottaminen = getTextString(docBase, taitotaso.getSuullinenTuottaminen());
            String vuorovaikutusJaMediaatio = getTextString(docBase, taitotaso.getVuorovaikutusJaMediaatio());

            if (StringUtils.isNotEmpty(kielenkayttotarkoitus)
                    || StringUtils.isNotEmpty(aihealueet)
                    || StringUtils.isNotEmpty(viestintataidot)
                    || StringUtils.isNotEmpty(opiskelijantaidot)
                    || StringUtils.isNotEmpty(opiskelijanTyoelamataidot)
                    || StringUtils.isNotEmpty(suullinenVastaanottaminen)
                    || StringUtils.isNotEmpty(suullinenTuottaminen)
                    || StringUtils.isNotEmpty(vuorovaikutusJaMediaatio)) {
                addTeksti(docBase, messages.translate("docgen.keskeiset-sisallot.title", docBase.getKieli()), "h5");
            }

            addTextWithTopic(kielenkayttotarkoitus, "docgen.kielenkayttotarkoitus.title", docBase);
            addTextWithTopic(aihealueet, "docgen.aihealueet.title", docBase);
            addTextWithTopic(viestintataidot, "docgen.viestintataidot.title", docBase);
            addTextWithTopic(opiskelijantaidot, "docgen.opiskelijantaidot.title", docBase);

            addTextWithTopic(opiskelijanTyoelamataidot, "docgen.opiskelijan_tyoelamataidot.title", docBase);
            addTextWithTopic(suullinenVastaanottaminen, "docgen.suullinen_vastaanottaminen.title", docBase);
            addTextWithTopic(suullinenTuottaminen, "docgen.suullinen_tuottaminen.title", docBase);
            addTextWithTopic(vuorovaikutusJaMediaatio, "docgen.vuorovaikutus_ja_mediaatio.title", docBase);
        });
    }

    private void addTextWithTopic(String text, String translationKey, DokumenttiBase docBase) {
        if (StringUtils.isNotEmpty(text)) {
            addTeksti(docBase, messages.translate(translationKey, docBase.getKieli()), "h6");
            addTeksti(docBase, text, "div");
        }
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

                if (StringUtils.isEmpty(id)) {
                    continue;
                }

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

    private void buildKansilehti(DokumenttiBase docBase, Long opsId, Kieli kieli) {
        byte[] image = dokumenttiKuvaService.getImage(opsId, "kansikuva", kieli);

        if (image == null) {
            return;
        }

        Element head = docBase.getHeadElement();
        Element kansikuva = docBase.getDocument().createElement("kansikuva");
        Element kuva = docBase.getDocument().createElement("img");

        String base64 = Base64.getEncoder().encodeToString(image);
        kuva.setAttribute("src", "data:image/jpg;base64," + base64);

        kansikuva.appendChild(kuva);
        head.appendChild(kansikuva);
    }

    private void buildYlatunniste(DokumenttiBase docBase, Long opsId, Kieli kieli) {
        byte[] image = dokumenttiKuvaService.getImage(opsId, "ylatunniste", kieli);

        if (image == null) {
            return;
        }

        Element head = docBase.getHeadElement();
        Element ylatunniste = docBase.getDocument().createElement("ylatunniste");
        Element kuva = docBase.getDocument().createElement("img");

        String base64 = Base64.getEncoder().encodeToString(image);
        kuva.setAttribute("src", "data:image/jpg;base64," + base64);

        ylatunniste.appendChild(kuva);
        head.appendChild(ylatunniste);
    }

    private void buildAlatunniste(DokumenttiBase docBase, Long opsId, Kieli kieli) {
        byte[] image = dokumenttiKuvaService.getImage(opsId, "alatunniste", kieli);

        if (image == null) {
            return;
        }

        Element head = docBase.getHeadElement();
        Element alatunniste = docBase.getDocument().createElement("alatunniste");
        Element kuva = docBase.getDocument().createElement("img");

        String base64 = Base64.getEncoder().encodeToString(image);
        kuva.setAttribute("src", "data:image/jpg;base64," + base64);

        alatunniste.appendChild(kuva);
        head.appendChild(alatunniste);
    }
}
