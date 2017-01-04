namespace Suorituspolku {
    export const pakollinen = _.memoize((osa) => !osa.osaamisala && (osa.pakollinen || _.some(osa.osat, pakollinen)));

    export const calculateRealAmount = (ops, tree, tosat, poistetut) => {
        const result = {};
        const shouldCount = (node) => !poistetut[node.tunniste] || !poistetut[node.tunniste].piilotettu;
        const isRyhma = (node) => !node._tutkinnonOsaViite;

        const calculateSubstituteLaajuus = (node) => _(node.osat)
            .map(getLaajuus)
            .reduce(sum);

        const getKoko = (node) => isRyhma(node)
            ? (node.rooli !== "määrittelemätön"
              ? _.property("muodostumisSaanto.koko.minimi")(node) || 0
              : _.property("muodostumisSaanto.koko.maksimi")(node) || 0)
            : tosat[node._tutkinnonOsaViite] ? 1 : 0;

        const getLaajuus = (node) => {
            if (isRyhma(node)) {
                const minimi = _.property("muodostumisSaanto.laajuus.minimi")(node);
                const maksimi = _.property("muodostumisSaanto.laajuus.maksimi")(node);
                const laajuus = maksimi || minimi;

                if (laajuus) {
                    return laajuus;
                }
                else {
                    const lastenLaajuus = _(node.osat || [])
                        .filter(shouldCount)
                        .map(getLaajuus)
                        .reduce(sum);
                    return lastenLaajuus;
                }
            }
            else {
                return tosat[node._tutkinnonOsaViite].laajuus || 0;
            }
        }

        const sum = (acc, min: any) => (min || 0) + acc;

        const calculateAmounts = (node, laajuusTaiKoko) => _(node.osat)
            .filter(shouldCount)
            .map(laajuusTaiKoko)
            .compact()
            .reduce(sum);

        (function recur(node) {
            let osatValidit = true;
            _.each(node.osat || [], (osa) => {
                if (!recur(osa)) {
                    osatValidit = false;
                }
            });

            if (tree.osat[2].tunniste === node.tunniste) {
                console.log(node.rooli && node.rooli !== "määrittelemätön", node)
            }

            if (node.rooli && node.rooli !== "määrittelemätön") {
                // Käytetään ryhmän oman muodostumisen laskemiseen
                node.$$laskettuLaajuus = calculateAmounts(node, getLaajuus);

                // Käytetään ryhmän oman muodostumisen laskemiseen
                node.$$laskettuKoko = calculateAmounts(node, getKoko);

                // Validoidaan ryhmät
                const laajuus = _.property("muodostumisSaanto.laajuus")(node);
                const koko = _.property("muodostumisSaanto.koko")(node);

                const laajuusValidi = () => !laajuus || !laajuus.minimi || node.$$laskettuLaajuus >= laajuus.minimi;
                const kokoValidi = () => !koko || !koko.minimi || node.$$laskettuKoko >= koko.minimi;

                node.$$valid = laajuusValidi() && kokoValidi() && osatValidit;

                return node.$$valid || !shouldCount(node);
            }
            return true;
        })(tree);

        return result;
    };

    // const export pakollinen = (osa) => true;
};
