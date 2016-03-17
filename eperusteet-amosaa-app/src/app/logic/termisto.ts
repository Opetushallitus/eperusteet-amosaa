interface LocalisedString {
    fi?: String,
    sv?: String
}

interface Kasite {
    avain: String,
    termi: LocalisedString,
    selitys: LocalisedString,
    alaviite?: Boolean
}

namespace Termisto {

    const termistoViestit= {
        serverError: (err:any)=> KaannaService.kaanna("palvelin-virhe") + " " + err.toString(),
        validationError: ()=> KaannaService.kaanna("termi-puuttuu"),
        postSuccess: ()=> KaannaService.kaanna("kasite-tallenettu")
    };

    export const parseOne = (revision) => ({
        id: revision.numero,
        muokkaaja: revision.muokkaajaOid, // TODO: Hae käyttäjän nimi organisaatiopalvelusta promisena
        kommentti: revision.kommentti,
        date: new Date(revision.pvm)
    });

    const makeKey = (termi: LocalisedString) => {
        let key = KaannaService.kaanna(termi).replace(/[^a-zA-Z0-9]/g, '') || 'avain';
        return key + (new Date()).getTime();
    };

    const validate = (termi: LocalisedString) => !!KaannaService.kaanna(termi).trim();

    const readyToPost = (kasite: Kasite) => {
        if (validate(kasite.termi)) {
            if (!kasite.avain) {
                kasite.avain =  makeKey(kasite.termi);
            }
            return kasite;
        }
        NotifikaatioService.varoitus(termistoViestit.validationError);
        throw new Error("missing term");
        return;
    };

    const handleResponse = (kasite: Kasite) => {
        NotifikaatioService.onnistui("Uusi käsite on tallenettu");
        return kasite;
    };

    const handleError = (err: String) => NotifikaatioService.varoitus(termistoViestit.serverError(err));

    export const sort = (kasitteet: any, order: boolean) =>
        _.sortBy(kasitteet, (kasite: Kasite) => kasite.alaviite && kasite.alaviite === order);

    export const makeBlankKasite = (): Kasite => ({
        avain: '',
        termi: {},
        selitys: {},
        alaviite: false
    });

    export const post = (kasitteet: any, kasite: Kasite) => {
        let post = readyToPost(kasite);
        return kasitteet.post(post).then(handleResponse).catch(handleError);
    }
};