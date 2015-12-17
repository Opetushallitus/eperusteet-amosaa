CREATE SEQUENCE hibernate_sequence;

CREATE TABLE kommentti (
    id bigint NOT NULL,
    luoja character varying(255),
    muokattu timestamp without time zone,
    luotu timestamp without time zone,
    muokkaaja character varying(255),
    nimi character varying(255),
    opetussuunnitelmaid bigint,
    parentid bigint,
    ylinid bigint,
    poistettu boolean,
    sisalto character varying(1024)
);

CREATE TABLE koodistokoodi (
    id bigint NOT NULL,
    koodiarvo character varying(255),
    koodiuri character varying(255)
);

CREATE TABLE liite (
    id uuid NOT NULL,
    data oid NOT NULL,
    luotu timestamp without time zone,
    nimi character varying(1024),
    tyyppi character varying(255) NOT NULL
);

CREATE TABLE lokalisoituteksti (
    id bigint NOT NULL
);

CREATE TABLE lokalisoituteksti_teksti (
    lokalisoituteksti_id bigint NOT NULL,
    kieli character varying(255),
    teksti text
);


CREATE TABLE lukko (
    id bigint NOT NULL,
    haltija_oid character varying(255),
    luotu timestamp without time zone,
    vanhenemisaika integer NOT NULL
);

CREATE TABLE ohje (
    id bigint NOT NULL,
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    kohde uuid,
    teksti_id bigint,
    tyyppi character varying(255) NOT NULL
);

CREATE TABLE ohje_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    revend integer,
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    kohde uuid,
    teksti_id bigint,
    tyyppi character varying(255)
);

CREATE TABLE opetussuunnitelma (
    id bigint NOT NULL,
    kuvaus_id bigint,
    nimi_id bigint,
    tekstit_id bigint,
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    tila character varying(255) NOT NULL,
    tyyppi character varying(255) NOT NULL,
    perusteendiaarinumero character varying(255),
    pohja_id bigint,
    koulutustyyppi character varying(255),
    paatospaivamaara timestamp without time zone,
    esikatseltavissa boolean
);

CREATE TABLE opetussuunnitelma_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    revend integer,
    kuvaus_id bigint,
    nimi_id bigint,
    tekstit_id bigint,
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    tila character varying(255),
    tyyppi character varying(255),
    perusteendiaarinumero character varying(255),
    pohja_id bigint,
    koulutustyyppi character varying(255),
    paatospaivamaara timestamp without time zone,
    esikatseltavissa boolean
);

CREATE TABLE opetussuunnitelma_julkaisukielet (
    opetussuunnitelma_id bigint NOT NULL,
    julkaisukielet character varying(255)
);

CREATE TABLE opetussuunnitelma_julkaisukielet_aud (
    rev integer NOT NULL,
    opetussuunnitelma_id bigint NOT NULL,
    julkaisukielet character varying(255) NOT NULL,
    revtype smallint,
    revend integer
);

CREATE TABLE opetussuunnitelma_koodistokoodi (
    opetussuunnitelma_id bigint NOT NULL,
    kunnat_id bigint NOT NULL
);

CREATE TABLE opetussuunnitelma_koodistokoodi_aud (
    rev integer NOT NULL,
    opetussuunnitelma_id bigint NOT NULL,
    kunnat_id bigint NOT NULL,
    revtype smallint,
    revend integer
);

CREATE TABLE opetussuunnitelma_liite (
    opetussuunnitelma_id bigint NOT NULL,
    liite_id uuid NOT NULL
);

CREATE TABLE opetussuunnitelma_liite_aud (
    rev integer NOT NULL,
    opetussuunnitelma_id bigint NOT NULL,
    liite_id uuid NOT NULL,
    revtype smallint,
    revend integer
);

CREATE TABLE opetussuunnitelma_organisaatiot (
    opetussuunnitelma_id bigint NOT NULL,
    organisaatiot character varying(255)
);

CREATE TABLE opetussuunnitelma_organisaatiot_aud (
    rev integer NOT NULL,
    opetussuunnitelma_id bigint NOT NULL,
    organisaatiot character varying(255) NOT NULL,
    revtype smallint,
    revend integer
);

CREATE TABLE revinfo (
    rev integer NOT NULL,
    revtstmp bigint,
    kommentti character varying(1000),
    muokkaajaoid character varying(255)
);

CREATE TABLE tekstikappale (
    id bigint NOT NULL,
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    tila character varying(255) NOT NULL,
    nimi_id bigint,
    teksti_id bigint,
    tunniste uuid,
    pakollinen boolean,
    valmis boolean
);

CREATE TABLE tekstikappale_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    revend integer,
    luoja character varying(255),
    luotu timestamp without time zone,
    muokattu timestamp without time zone,
    muokkaaja character varying(255),
    tila character varying(255),
    nimi_id bigint,
    teksti_id bigint,
    tunniste uuid,
    pakollinen boolean,
    valmis boolean
);

CREATE TABLE tekstikappaleviite (
    id bigint NOT NULL,
    omistussuhde character varying(255) NOT NULL,
    tekstikappale_id bigint,
    vanhempi_id bigint,
    lapset_order integer,
    pakollinen boolean,
    valmis boolean
);

CREATE TABLE tekstikappaleviite_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    revend integer,
    omistussuhde character varying(255),
    tekstikappale_id bigint,
    vanhempi_id bigint,
    pakollinen boolean,
    valmis boolean
);

CREATE TABLE tekstiosa (
    id bigint NOT NULL,
    otsikko_id bigint,
    teksti_id bigint
);

CREATE TABLE tekstiosa_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    revend integer,
    otsikko_id bigint,
    teksti_id bigint
);

CREATE TABLE termi (
    id bigint NOT NULL,
    ops_id bigint NOT NULL,
    avain text NOT NULL,
    termi_id bigint,
    selitys_id bigint
);

CREATE TABLE termi_aud (
    id bigint,
    avain text,
    ops_id bigint,
    termi_id bigint,
    selitys_id bigint,
    rev integer,
    revtype smallint,
    revend integer
);

ALTER TABLE ONLY kommentti ADD CONSTRAINT kommentti_pkey PRIMARY KEY (id); 
ALTER TABLE ONLY koodistokoodi ADD CONSTRAINT koodistokoodi_pkey PRIMARY KEY (id);
ALTER TABLE ONLY liite ADD CONSTRAINT liite_pkey PRIMARY KEY (id); 
ALTER TABLE ONLY lokalisoituteksti ADD CONSTRAINT lokalisoituteksti_pkey PRIMARY KEY (id); 
ALTER TABLE ONLY lukko ADD CONSTRAINT lukko_pkey PRIMARY KEY (id); 
ALTER TABLE ONLY ohje ADD CONSTRAINT ohje_pkey PRIMARY KEY (id); 
ALTER TABLE ONLY ohje_aud ADD CONSTRAINT ohje_aud_pkey PRIMARY KEY (id, rev); 
ALTER TABLE ONLY opetussuunnitelma ADD CONSTRAINT opetussuunnitelma_pkey PRIMARY KEY (id); 
ALTER TABLE ONLY opetussuunnitelma_aud ADD CONSTRAINT opetussuunnitelma_aud_pkey PRIMARY KEY (id, rev); 
ALTER TABLE ONLY opetussuunnitelma_julkaisukielet_aud ADD CONSTRAINT opetussuunnitelma_julkaisukielet_aud_pkey PRIMARY KEY (rev, opetussuunnitelma_id, julkaisukielet); 
ALTER TABLE ONLY opetussuunnitelma_koodistokoodi ADD CONSTRAINT opetussuunnitelma_koodistokoodi_pkey PRIMARY KEY (opetussuunnitelma_id, kunnat_id); 
ALTER TABLE ONLY opetussuunnitelma_koodistokoodi_aud ADD CONSTRAINT opetussuunnitelma_koodistokoodi_aud_pkey PRIMARY KEY (rev, opetussuunnitelma_id, kunnat_id); 
ALTER TABLE ONLY opetussuunnitelma_liite ADD CONSTRAINT opetussuunnitelma_liite_pkey PRIMARY KEY (opetussuunnitelma_id, liite_id); 
ALTER TABLE ONLY opetussuunnitelma_liite_aud ADD CONSTRAINT opetussuunnitelma_liite_aud_pkey PRIMARY KEY (rev, opetussuunnitelma_id, liite_id); 
ALTER TABLE ONLY opetussuunnitelma_organisaatiot_aud ADD CONSTRAINT opetussuunnitelma_koulut_aud_pkey PRIMARY KEY (rev, opetussuunnitelma_id, organisaatiot); 
ALTER TABLE ONLY revinfo ADD CONSTRAINT revinfo_pkey PRIMARY KEY (rev); 
ALTER TABLE ONLY tekstikappale ADD CONSTRAINT tekstikappale_pkey PRIMARY KEY (id); 
ALTER TABLE ONLY tekstikappale_aud ADD CONSTRAINT tekstikappale_aud_pkey PRIMARY KEY (id, rev); 
ALTER TABLE ONLY tekstikappaleviite ADD CONSTRAINT tekstikappaleviite_pkey PRIMARY KEY (id); 
ALTER TABLE ONLY tekstikappaleviite_aud ADD CONSTRAINT tekstikappaleviite_aud_pkey PRIMARY KEY (id, rev); 
ALTER TABLE ONLY tekstiosa ADD CONSTRAINT tekstiosa_pkey PRIMARY KEY (id); 
ALTER TABLE ONLY tekstiosa_aud ADD CONSTRAINT tekstiosa_aud_pkey PRIMARY KEY (id, rev); 
ALTER TABLE ONLY termi ADD CONSTRAINT termi_pkey PRIMARY KEY (id);

ALTER TABLE ONLY lokalisoituteksti_teksti ADD CONSTRAINT fk_lokalisoituteksti_id FOREIGN KEY (lokalisoituteksti_id) REFERENCES lokalisoituteksti(id); 
ALTER TABLE ONLY ohje ADD CONSTRAINT fk_hw19w1na8qehidjetsw9wobrx FOREIGN KEY (teksti_id) REFERENCES lokalisoituteksti(id); 
ALTER TABLE ONLY ohje_aud ADD CONSTRAINT fk_7yv0l2iob8mugl35rure6fos1 FOREIGN KEY (rev) REFERENCES revinfo(rev); 
ALTER TABLE ONLY ohje_aud ADD CONSTRAINT fk_f48ffqqfl2low007owdugtksf FOREIGN KEY (revend) REFERENCES revinfo(rev); 
ALTER TABLE ONLY opetussuunnitelma ADD CONSTRAINT fk_opetussuunnitelma_lokalisoituteksti_kuvaus FOREIGN KEY (kuvaus_id) REFERENCES lokalisoituteksti(id); 
ALTER TABLE ONLY opetussuunnitelma ADD CONSTRAINT fk_opetussuunnitelma_lokalisoituteksti_nimi FOREIGN KEY (nimi_id) REFERENCES lokalisoituteksti(id); 
ALTER TABLE ONLY opetussuunnitelma ADD CONSTRAINT fk_opetussuunnitelma_tekstikappaleviite FOREIGN KEY (tekstit_id) REFERENCES tekstikappaleviite(id); 
ALTER TABLE ONLY opetussuunnitelma ADD CONSTRAINT fk_tnfxq12pi5iq9h0v8g9cal80y FOREIGN KEY (pohja_id) REFERENCES opetussuunnitelma(id); 
ALTER TABLE ONLY opetussuunnitelma_aud ADD CONSTRAINT fk_opetussuunnitelma_aud_revinfo_rev FOREIGN KEY (rev) REFERENCES revinfo(rev); 
ALTER TABLE ONLY opetussuunnitelma_aud ADD CONSTRAINT fk_opetussuunnitelma_aud_revinfo_revend FOREIGN KEY (revend) REFERENCES revinfo(rev); 
ALTER TABLE ONLY opetussuunnitelma_julkaisukielet ADD CONSTRAINT fk_il7kmuh52hmlr3d2r04buywd0 FOREIGN KEY (opetussuunnitelma_id) REFERENCES opetussuunnitelma(id); 
ALTER TABLE ONLY opetussuunnitelma_julkaisukielet_aud ADD CONSTRAINT fk_8oegas5p3n5f6msqhmlgjxj8p FOREIGN KEY (revend) REFERENCES revinfo(rev); 
ALTER TABLE ONLY opetussuunnitelma_julkaisukielet_aud ADD CONSTRAINT fk_9mh1nop9pvgrsrxy0ko6a7hjr FOREIGN KEY (rev) REFERENCES revinfo(rev); 
ALTER TABLE ONLY opetussuunnitelma_koodistokoodi ADD CONSTRAINT fk_dsfpjvbl9wo0c0sjnp1bec4t3 FOREIGN KEY (kunnat_id) REFERENCES koodistokoodi(id); 
ALTER TABLE ONLY opetussuunnitelma_koodistokoodi ADD CONSTRAINT fk_kb775ur3ap0e6vw4bvch0thyh FOREIGN KEY (opetussuunnitelma_id) REFERENCES opetussuunnitelma(id); 
ALTER TABLE ONLY opetussuunnitelma_koodistokoodi_aud ADD CONSTRAINT fk_2xtr6786qv934w1j1iarttq49 FOREIGN KEY (revend) REFERENCES revinfo(rev); 
ALTER TABLE ONLY opetussuunnitelma_koodistokoodi_aud ADD CONSTRAINT fk_33wo3fu3s0jl5allvpsqafk0x FOREIGN KEY (rev) REFERENCES revinfo(rev); 
ALTER TABLE ONLY opetussuunnitelma_liite ADD CONSTRAINT fk_k9qfrela3cfnnettr4aryvs0f FOREIGN KEY (opetussuunnitelma_id) REFERENCES opetussuunnitelma(id); 
ALTER TABLE ONLY opetussuunnitelma_liite ADD CONSTRAINT fk_n5yqt8i556xum7c63wyd8l7se FOREIGN KEY (liite_id) REFERENCES liite(id); 
ALTER TABLE ONLY opetussuunnitelma_liite_aud ADD CONSTRAINT fk_1cf2y5my1vxf9gylil6vbtwjl FOREIGN KEY (revend) REFERENCES revinfo(rev); 
ALTER TABLE ONLY opetussuunnitelma_liite_aud ADD CONSTRAINT fk_7ki7ulrar35ev4rpechrfpodx FOREIGN KEY (rev) REFERENCES revinfo(rev); 
ALTER TABLE ONLY opetussuunnitelma_organisaatiot ADD CONSTRAINT fk_prljq4o9chadb5p4fb4kaoer0 FOREIGN KEY (opetussuunnitelma_id) REFERENCES opetussuunnitelma(id); 
ALTER TABLE ONLY opetussuunnitelma_organisaatiot_aud ADD CONSTRAINT fk_6lcqv3d2kx97yv1m1otfkjd73 FOREIGN KEY (revend) REFERENCES revinfo(rev); 
ALTER TABLE ONLY opetussuunnitelma_organisaatiot_aud ADD CONSTRAINT fk_ppo45oanktb47ds837291hw1h FOREIGN KEY (rev) REFERENCES revinfo(rev); 
ALTER TABLE ONLY tekstikappale ADD CONSTRAINT fk_tekstikappale_lokalisoituteksti_nimi FOREIGN KEY (nimi_id) REFERENCES lokalisoituteksti(id); 
ALTER TABLE ONLY tekstikappale ADD CONSTRAINT fk_tekstikappale_lokalisoituteksti_teksti FOREIGN KEY (teksti_id) REFERENCES lokalisoituteksti(id); 
ALTER TABLE ONLY tekstikappale_aud ADD CONSTRAINT fk_tekstikappale_aud_revinfo_rev FOREIGN KEY (rev) REFERENCES revinfo(rev); 
ALTER TABLE ONLY tekstikappale_aud ADD CONSTRAINT fk_tekstikappale_aud_revinfo_revend FOREIGN KEY (revend) REFERENCES revinfo(rev); 
ALTER TABLE ONLY tekstikappaleviite ADD CONSTRAINT fk_tekstikappaleviite_tekstikappale FOREIGN KEY (tekstikappale_id) REFERENCES tekstikappale(id); 
ALTER TABLE ONLY tekstikappaleviite ADD CONSTRAINT fk_tekstikappaleviite_vanhempi FOREIGN KEY (vanhempi_id) REFERENCES tekstikappaleviite(id); 
ALTER TABLE ONLY tekstikappaleviite_aud ADD CONSTRAINT fk_tekstikappaleviite_aud_revinfo_rev FOREIGN KEY (rev) REFERENCES revinfo(rev); 
ALTER TABLE ONLY tekstikappaleviite_aud ADD CONSTRAINT fk_tekstikappaleviite_aud_revinfo_revend FOREIGN KEY (revend) REFERENCES revinfo(rev); 
ALTER TABLE ONLY tekstiosa ADD CONSTRAINT fk_oa7juph0c39rc1mu1y0l0xdeb FOREIGN KEY (teksti_id) REFERENCES lokalisoituteksti(id); 
ALTER TABLE ONLY tekstiosa ADD CONSTRAINT fk_spsm22lrit456xp9k0yc1m9kj FOREIGN KEY (otsikko_id) REFERENCES lokalisoituteksti(id); 
ALTER TABLE ONLY tekstiosa_aud ADD CONSTRAINT fk_j0crlc392kycsuget13pkggtx FOREIGN KEY (rev) REFERENCES revinfo(rev); 
ALTER TABLE ONLY tekstiosa_aud ADD CONSTRAINT fk_ok5uqqli0gjnktsx8e7lxy3a7 FOREIGN KEY (revend) REFERENCES revinfo(rev); 
ALTER TABLE ONLY termi ADD CONSTRAINT termi_ops_id_fkey FOREIGN KEY (ops_id) REFERENCES opetussuunnitelma(id); 
ALTER TABLE ONLY termi ADD CONSTRAINT termi_selitys_id_fkey FOREIGN KEY (selitys_id) REFERENCES lokalisoituteksti(id); 
ALTER TABLE ONLY termi ADD CONSTRAINT termi_termi_id_fkey FOREIGN KEY (termi_id) REFERENCES lokalisoituteksti(id);
ALTER TABLE ONLY termi_aud ADD CONSTRAINT termi_aud_ops_id_fkey FOREIGN KEY (ops_id) REFERENCES opetussuunnitelma(id); 
ALTER TABLE ONLY termi_aud ADD CONSTRAINT termi_aud_selitys_id_fkey FOREIGN KEY (selitys_id) REFERENCES lokalisoituteksti(id); 
ALTER TABLE ONLY termi_aud ADD CONSTRAINT termi_aud_termi_id_fkey FOREIGN KEY (termi_id) REFERENCES lokalisoituteksti(id); 
