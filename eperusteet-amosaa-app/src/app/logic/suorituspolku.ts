namespace Suorituspolku {
    export const pakollinen = _.memoize((osa) => !osa.osaamisala && (osa.pakollinen || _.any(osa.osat, pakollinen)));

    // const export pakollinen = (osa) => true;
};
