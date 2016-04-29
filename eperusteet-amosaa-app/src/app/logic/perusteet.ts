namespace Perusteet {
    // Choose ops over others
    export const getSuoritustapa = (peruste) =>
        _.size(peruste.suoritustavat) === 1
            ? _.first(peruste.suoritustavat)
            : _.find(peruste.suoritustavat, { suoritustapakoodi: "ops" });

    export const getTutkinnonOsat = (peruste) => peruste.tutkinnonOsat;

    export const getRakenne = (suoritustapa) => suoritustapa.rakenne;

    export const getTosaViitteet = (suoritustapa) => suoritustapa.tutkinnonOsaViitteet;
};
