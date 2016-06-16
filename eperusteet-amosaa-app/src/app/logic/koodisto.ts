namespace Koodisto {
    export const parseRawKoodisto = (koodit) => _(koodit)
        .compact()
        .map((koodi: any) => ({
            arvo: koodi.koodiArvo,
            uri: koodi.koodiUri,
            nimi: _.zipObject(
                _.map(koodi.metadata, (meta: any) => meta.kieli.toLowerCase()),
                _.map(koodi.metadata, "nimi"))
        }))
        .value();

    export const paikallinenToFull = (koulutustoimija, koodiArvo: string) =>
        "paikallinen_tutkinnonosa_" + koulutustoimija.organisaatio + "_" + koodiArvo;
};
