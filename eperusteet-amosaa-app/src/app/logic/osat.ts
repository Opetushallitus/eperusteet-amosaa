namespace Osat {
    export const deinit = (osa, koulutustoimija) => {
        return true;
    };

    export const isTutke2 = ({ tyyppi }) => {
        return tyyppi === "tutke2" || tyyppi === "reformi_tutke2";
    };
}
