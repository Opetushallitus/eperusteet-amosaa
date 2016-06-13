module Opetussuunnitelmat {
    export const parsiPerustiedot = (opetussuunnitelmat, urlGenerator: (id: number) => string) => _(opetussuunnitelmat)
        .map(p => ({
            id: p.id,
            nimi: p.nimi,
            kuvaus: p.kuvaus,
            $$url: urlGenerator(p.id)
        }))
        .value();

    export const sallitutSisaltoTyypit = (ops) => {
        if (ops.tyyppi === "ops" || ops.tyyppi === "kooste") {
            return ["tekstikappale", "tutkinnonosa", "suorituspolku"]; // "tutkinnonosaryhma" ei tarvita toistaiseksi
        }
        else if (ops.tyyppi === "yleinen") {
            return ["tekstikappale", "tutkinnonosa", "tutkinnonosaryhma"];
        }
        else {
            return ["tekstikappale"];
        }
    };
}
