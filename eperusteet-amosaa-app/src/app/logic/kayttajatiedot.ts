module Kayttajatiedot {
    export const parsiEsitysnimi = (kayttajatieto) => kayttajatieto.sukunimi
        ? kayttajatieto.kutsumanimi + " " + kayttajatieto.sukunimi
        : kayttajatieto.oidHenkilo;
        // : KaannaService.kaanna("muokkaajaa-ei-loytynyt");
}
