declare module _ {
    interface OwnMixins {
        callAndGive<F>(x: F, ...args: any[]): F;
        print(x: any): any;
        print(): any;
        matchStrings(search: string, target: string): boolean;
        fromPairs(x: Array<any>): any;
        fromPairs(): any;
    }

    interface LoDashStatic extends OwnMixins { }
    interface LoDashImplicitArrayWrapper<T> extends OwnMixins { }
    interface LoDashImplicitObjectWrapper<T> extends OwnMixins { }
}

_.mixin({
    matchStrings: (search: string = "", target: string = "") =>
        !search || (target && target.toLocaleLowerCase().indexOf(search.toLowerCase()) !== -1),
    callAndGive: (f: Function, ...args) => {
        f.apply(undefined, args);
        return f;
    },
    fromPairs: (pairs) => {
        let obj: any = {};
        _.each(pairs, (pair) => {
            if (pair) {
                obj[pair[0]] = pair[1];
            }
        });
        return obj;
    },
    print: (array) => {
        _.each(array, (v, k) => console.log(_.clone(k), _.clone(v)));
        return array;
    }
});
