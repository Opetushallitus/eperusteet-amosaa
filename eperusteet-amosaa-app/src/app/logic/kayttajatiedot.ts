namespace Kayttajatiedot {
    export const parsiEsitysnimi = (kayttajatieto) => {
        if (kayttajatieto) {
            return (kayttajatieto.sukunimi && kayttajatieto.kutsumanimi)
                ? kayttajatieto.kutsumanimi + " " + kayttajatieto.sukunimi
                : KaannaService.kaanna("muokkaajaa-ei-loytynyt") + " (" + kayttajatieto.oidHenkilo + ")";
        }
        else {
            return KaannaService.kaanna("tuntematon-kayttaja");
        }
    }
}
