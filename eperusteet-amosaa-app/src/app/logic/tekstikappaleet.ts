namespace Tekstikappaleet {
    export const rakenna = (tekstikappaleviitteet: Array<any>, rootId: number | string) => {
        const tkvMap = _.indexBy(tekstikappaleviitteet, "id");

        const root = tkvMap[rootId] || {
            lapset: []
        };

        return _(root.lapset)
            .map(tkvId => _.merge(tkvMap[tkvId], { $$depth: 0 }))
            .value();
    };
};
