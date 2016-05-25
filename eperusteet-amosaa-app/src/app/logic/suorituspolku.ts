namespace Suorituspolku {
    export const pakollinen = _.memoize((osa) => !osa.osaamisala && (osa.pakollinen || _.some(osa.osat, pakollinen)));

    export const calculateRealAmount = (tree, tosat, poistetut) => {
        const result = {};
        const shouldCount = (node) => !poistetut[node.tunniste] && node.rooli !== "määrittelemätön";
        const isRyhma = (node) => !node._tutkinnonOsaViite;
        const getLaajuus = (node) => isRyhma(node)
            ? _.property("muodostumisSaanto.laajuus.minimi")(node) || 0
            : tosat[node._tutkinnonOsaViite].laajuus || 0;

        Algoritmit.traverse(tree, "osat", (node) => {
            if (node.rooli && node.rooli !== "määrittelemätön") {
                node.$$laskettuLaajuus = _(node.osat)
                    .filter(shouldCount)
                    .map(getLaajuus)
                    .compact()
                    .reduce((acc: number, min: number) => (min || 0) + acc);

                const laajuus = _.property("muodostumisSaanto.laajuus")(node);
                node.$$valid = !laajuus || !laajuus.minimi || node.$$laskettuLaajuus >= laajuus.minimi;
            }
        });
        return result;
    };

    // const export pakollinen = (osa) => true;
};
