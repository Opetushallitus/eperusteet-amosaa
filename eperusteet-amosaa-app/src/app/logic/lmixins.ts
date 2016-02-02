declare module _ {
    interface OwnMixins {
        callAndGive<F>(a: F, ...any): F;
        print(any): any;
        print(): any;
        fromPairs(a: Array<any>): any;
        fromPairs(): any;
    }

    interface LoDashStatic extends OwnMixins { }
    interface LoDashImplicitArrayWrapper<T> extends OwnMixins { }
    interface LoDashImplicitObjectWrapper<T> extends OwnMixins { }
}

_.mixin({
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
