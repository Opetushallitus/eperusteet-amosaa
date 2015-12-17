module Opetussuunnitelmat {
    export const parsiPerustiedot = (opetussuunnitelmat, urlGenerator: (id: Number) => string) => _(opetussuunnitelmat)
        .map(p => ({
            id: p.id,
            nimi: p.nimi,
            kuvaus: p.kuvaus,
            $$url: urlGenerator(p.id)
        }))
        .value();
}
