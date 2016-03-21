namespace Tekstikappaleet {
    export const uniikit = (tekstikappaleviitteet: Array<any>) =>
        _.indexBy(tekstikappaleviitteet, "id");

    export const teeRakenne = (tekstikappaleviitteet, id: number | string, depth = 0) => ({
        id: id,
        $$depth: depth,
        $$obj: tekstikappaleviitteet[id],
        lapset: _.map(tekstikappaleviitteet[id].lapset, (lapsiId: number) =>
                        teeRakenne(tekstikappaleviitteet, lapsiId, depth + 1))
    });
};
