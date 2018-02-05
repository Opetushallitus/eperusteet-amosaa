namespace Osat {
    export const deinit = (osa, koulutustoimija) => {
        return true;
    };

    export const isTutke2 = (osa) => {
        return _.isObject(osa) && (osa.tyyppi === "tutke2" || osa.tyyppi === "reformi_tutke2");
    };
}
