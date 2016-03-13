namespace Termisto {
    const termistoViestit= {
        serverError: ()=> KaannaService.kaanna("palvelin-virhe"),
        validationError: ()=> KaannaService.kaanna("termi-puuttuu"),
        postSuccess: ()=> KaannaService.kaanna("kasite-tallenettu")
    };
    const makeKey = (termi) => {
        let key = KaannaService.kaanna(termi).replace(/[^a-zA-Z0-9]/g, '') || 'avain';
        return key + (new Date()).getTime();
    };
    const validate = (termi) => {
        return !!KaannaService.kaanna(termi).trim();
    };
    const readyToPost = (newKasite) => {
        console.log("here");
        if (!validate(newKasite.termi)) {
            NotifikaatioService.varoitus(termistoViestit.serverError);
            return false;
        }
        if (!newKasite.avain) {
            newKasite.avain =  makeKey(newKasite.termi);
        }
        return newKasite;
    };
    const handleResponse = (kasite) => {
        NotifikaatioService.onnistui("Uusi käsite on tallenettu");
        return kasite;
    };
    const handleError = (err) => {
        NotifikaatioService.varoitus(termistoViestit.validationError, err);
    };
    export const post = (kasitteet, newKasite) => {
        let post = readyToPost(newKasite);
        return post ? kasitteet.post(post).then(handleResponse).catch(handleError) : false;
    }
};