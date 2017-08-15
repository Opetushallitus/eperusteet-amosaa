namespace Perusteet {
    // Choose ops over others
    export const getSuoritustapa = (ops, suoritustavat) => {
        const result: any = _.size(suoritustavat) === 1
            ? _.first(suoritustavat)
            : _.find(suoritustavat, { suoritustapakoodi: ops.suoritustapa });
        if (result.suoritustapakoodi === "naytto") {
            result.laajuusYksikko = "";
        }
        return result;
    };

    export const getTutkinnonOsat = (peruste) => peruste.tutkinnonOsat;

    export const getRakenne = (suoritustapa) => suoritustapa.rakenne;

    export const getTosaViitteet = (suoritustapa) => suoritustapa.tutkinnonOsaViitteet;

    export const isSiirtymalla = (peruste) => peruste.siirtymaPaattyy && peruste.siirtymaPaattyy < Date.now();

    export const isVanhentunut = (peruste) => !isSiirtymalla(peruste)
        && peruste.voimassaoloLoppuu
        && peruste.voimassaoloLoppuu < Date.now();

    export const isTuleva = (peruste) => peruste.voimassaoloAlkaa && peruste.voimassaoloAlkaa > Date.now();
}
