namespace Algoritmit {
    export const match = (input, to) => {
        let vertailu = KaannaService.kaanna(to);
        const result =  _.isEmpty(input) || vertailu.toLowerCase().indexOf(input.toLowerCase()) !== -1;
        // if (result) {
        //     console.log(result, input, vertailu);
        // }
        if (!to) {
            console.log(to)
        }
        return result;
    };

    export const traverseUp = (obj, cb, parentKey = "$$traverseParent") => {
        let current = obj[parentKey];
        if (current) {
            cb(current);
            traverseUp(current, cb, parentKey);
        }
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
