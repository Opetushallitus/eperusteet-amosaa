namespace Suorituspolku {
    export const pakollinen = _.memoize((osa) => !osa.osaamisala && (osa.pakollinen || _.some(osa.osat, pakollinen)));

    export const calculateRealAmount = (ops, tree, tosat, poistetut) => {
        const result = {};
        const shouldCount = (node) => !poistetut[node.tunniste] || !poistetut[node.tunniste].piilotettu;
        const isRyhma = (node) => !node._tutkinnonOsaViite;

        const getKoko = (node) => isRyhma(node)
            ? (node.rooli !== "määrittelemätön"
              ? _.property("muodostumisSaanto.koko.minimi")(node) || 0
              : _.property("muodostumisSaanto.koko.maksimi")(node) || 0)
            : tosat[node._tutkinnonOsaViite] ? 1 : 0;

        const getLaajuus = (node) => isRyhma(node)
            ? (node.rooli !== "määrittelemätön"
              ? _.property("muodostumisSaanto.laajuus.minimi")(node) || 0
              : _.property("muodostumisSaanto.laajuus.maksimi")(node) || 0)
            : tosat[node._tutkinnonOsaViite].laajuus || 0;

        const calculateAmounts = (node, laajuusTaiKoko) => _(node.osat)
            .filter(shouldCount)
            .map(laajuusTaiKoko)
            .compact()
            .reduce((acc, min: any) => (min || 0) + acc);

        Algoritmit.traverse(tree, "osat", (node) => {
            if (node.rooli && node.rooli !== "määrittelemätön") {
                node.$$laskettuLaajuus = calculateAmounts(node, getLaajuus);
                node.$$laskettuKoko = calculateAmounts(node, getKoko);
                const laajuus: any = _.property("muodostumisSaanto.laajuus")(node);
                const koko: any = _.property("muodostumisSaanto.koko")(node);
                node.$$valid =
                    (!laajuus || !laajuus.minimi || node.$$laskettuLaajuus >= laajuus.minimi)
                    && (!koko || !koko.minimi || node.$$laskettuKoko >= koko.minimi);
            }
        });
        return result;
    };

    // const export pakollinen = (osa) => true;
};
