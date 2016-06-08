namespace Koodisto {
    export const parseRawKoodisto = (koodit) => _(koodit)
        .map(koodi => ({
            arvo: koodi.koodiArvo,
            uri: koodi.koodiUri,
            nimi: _.zipObject(
                _.map(koodi.metadata, (meta: any) => meta.kieli.toLowerCase()),
                _.map(koodi.metadata, "nimi"))
        }))
        .value();
};
