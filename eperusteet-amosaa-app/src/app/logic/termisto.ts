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
    export const sort = (kasitteet: any, order: boolean) => {
        return _.sortBy(kasitteet, (kasite: Kasite) => kasite.alaviite && kasite.alaviite === order);
    };
    const makeKey = (termi: LocalisedString) => {
        let key = KaannaService.kaanna(termi).replace(/[^a-zA-Z0-9]/g, '') || 'avain';
        return key + (new Date()).getTime();
    };
    const validate = (termi: LocalisedString) => {
        return !!KaannaService.kaanna(termi).trim();
    };
    const readyToPost = (kasite: Kasite) => {
        if (!validate(kasite.termi)) {
            NotifikaatioService.varoitus(termistoViestit.validationError);
            return null;
        }
        if (!kasite.avain) {
            kasite.avain =  makeKey(kasite.termi);
        }
        return kasite;
    };
    const handleResponse = (kasite: Kasite) => {
        NotifikaatioService.onnistui("Uusi käsite on tallenettu");
        return kasite;
    };
    const handleError = (err: String) => {
        NotifikaatioService.varoitus(termistoViestit.serverError(err));
    };
    export const post = (kasitteet: any, kasite: Kasite) => {
        let post = readyToPost(kasite);
        return post ? kasitteet.post(post).then(handleResponse).catch(handleError) : false;
    }
};