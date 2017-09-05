namespace Koulutustoimijat {
    export const opetushallitus = _.constant("1.2.246.562.10.00000000001");

    export const isOpetushallitus = koulutustoimija => koulutustoimija.organisaatio === opetushallitus();
}
