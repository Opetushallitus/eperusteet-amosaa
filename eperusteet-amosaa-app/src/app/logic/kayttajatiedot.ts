module Kayttajatiedot {
    export const parsiEsitysnimi = (kayttajatieto) => kayttajatieto.sukunimi
        ? kayttajatieto.kutsumanimi + " " + kayttajatieto.sukunimi
        : KaannaService.kaanna("muokkaajaa-ei-loytynyt") + " (" + kayttajatieto.oidHenkilo + ")";
}
