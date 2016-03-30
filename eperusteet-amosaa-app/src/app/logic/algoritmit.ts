namespace Algoritmit {
    export const match = (input, to) => {
        let vertailu = KaannaService.kaanna(to + '');
        return vertailu.toLowerCase().indexOf(input.toLowerCase()) !== -1;
    };
    export const traverse = (objekti, lapsienAvain, cb, depth = 0) => {
        if (!objekti) {
            return;
        }
        _.forEach(objekti[lapsienAvain], function(solmu, index) {
            if (!cb(solmu, depth, index, objekti[lapsienAvain], objekti)) {
                solmu.$$traverseParent = objekti;
                traverse(solmu, lapsienAvain, cb, depth + 1);
            }
        });
    };
};