interface LocalisedString {
    fi?: String;
    sv?: String;
}

interface Kasite {
    avain?: String;
    termi: LocalisedString;
    selitys: LocalisedString;
    alaviite?: Boolean;
}

namespace Termisto {
    export const parseOne = revision => ({
        id: revision.numero,
        muokkaaja: revision.muokkaajaOid, // TODO: Hae käyttäjän nimi organisaatiopalvelusta promisena
        kommentti: revision.kommentti,
        date: new Date(revision.pvm)
    });

    export const sort = (kasitteet: Array<Kasite>, order: boolean) =>
        _.sortBy(kasitteet, (kasite: Kasite) => kasite.alaviite && kasite.alaviite === order);

    export const makeBlankKasite = (): Kasite => ({
        termi: { fi: "" },
        selitys: { fi: "" },
        alaviite: false
    });
}
