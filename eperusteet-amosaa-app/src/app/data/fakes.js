var Fake;
(function (Fake) {
    var createSisalto = function (id, tyyppi, nimi, lapset) {
        if (lapset === void 0) { lapset = []; }
        return ({
            id: id,
            nimi: { fi: nimi },
            tyyppi: tyyppi,
            lapset: lapset
        });
    };
    Fake.Kayttajaprofiili = function (id) {
        return {
            koulutustoimijat: [1],
            opetussuunnitelmat: [1]
        };
    };
    Fake.YhteisetOsat = function () { return ({
        6: {
            id: 6,
            julkaisukielet: ["fi"],
            kuvaus: "Jotain yhteisiä juttuja on",
            luoja: "test",
            luotu: 1450162628037,
            muokattu: 1450162654256,
            muokkaaja: "test",
            nimi: {
                fi: "Kokkolan kokkikoulun yhteiset"
            },
            tila: "luonnos",
            paatospaivamaara: null,
            esikatseltavissa: true,
            pohja: 1
        },
        5: {
            id: 5,
            julkaisukielet: ["fi"],
            kuvaus: "Jotain ihan älytöntä",
            luoja: "test",
            luotu: 1450162628037,
            muokattu: 1450162654256,
            muokkaaja: "test",
            nimi: {
                fi: "Kuun sirkuslaitoksen asiat"
            },
            tila: "luonnos",
            paatospaivamaara: null,
            esikatseltavissa: true,
            pohja: 1
        }
    }); };
    Fake.Koulutustoimijat = function () { return [{
            id: 1,
            organisaatiot: [{
                    oid: "1.2.246.562.10.1234",
                    nimi: {
                        fi: "Kuun sirkuslaitos"
                    }
                }],
            kunnat: [],
            kuvaus: {
                fi: "Vuonna 2127 haluttiin huikeampia sirkustaiteilijoita toimimaan pienemmässä painovoimassa."
            },
            yhteinenOsa: 5
        }, {
            id: 2,
            organisaatiot: [{
                    oid: "1.2.246.562.10.1235",
                    tyypit: ["Koulutustoimija"],
                    nimi: {
                        fi: "Kokkolan kokkikoulu"
                    }
                }],
            kunnat: [],
            kuvaus: {
                fi: "Ideat loppuu"
            },
            yhteinenOsa: 6
        }]; };
    Fake.Opetussuunnitelmat = function (koulutustoimijaId) { return ({
        3: {
            id: 3,
            julkaisukielet: ["fi"],
            organisaatiot: ["1.2.246.562.10.1234"],
            kuvaus: "Jotain ihan älytöntä",
            luoja: "test",
            luotu: 1450162628037,
            muokattu: 1450162654256,
            muokkaaja: "test",
            nimi: {
                fi: "Avaruusjonglöörin tutkinto"
            },
            perusteenDiaarinumero: "x/y/1",
            tila: "luonnos",
            tyyppi: "ops",
            koulutustyyppi: "koulutustyyppi_16",
            paatospaivamaara: null,
            esikatseltavissa: true,
            peruste: 33829
        },
        4: {
            id: 4,
            julkaisukielet: ["fi"],
            organisaatiot: ["1.2.246.562.10.1234", "1.2.246.562.10.1235"],
            kuvaus: "",
            luoja: "test",
            luotu: 1450162628037,
            muokattu: 1450162654256,
            muokkaaja: "test",
            nimi: {
                fi: "Matalan gravitaation kokkitutkinto"
            },
            perusteenDiaarinumero: "x/y/2",
            tila: "luonnos",
            tyyppi: "ops",
            koulutustyyppi: "koulutustyyppi_16",
            paatospaivamaara: null,
            esikatseltavissa: true,
            peruste: 617
        }
    }); };
})(Fake || (Fake = {}));
//# sourceMappingURL=fakes.js.map