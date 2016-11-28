declare module _ {
    interface OwnMixins {
        callAndGive<F>(x: F, ...args: any[]): F;
        print(x: any): any;
        spy<T>(x: T): T;
        print(): any;
        matchStrings(search: string, target: string): boolean;
        fromPairs(x: Array<any>): any;
        append<T>(x: Array<T>, el: T): Array<T>;
        overwrite(to: Object, from: Object): void;
        overwriteData(to: Object, from: Object): void;
        setRemove(from: Object, what: Object): void;
        cset<O, P, V>(o: O, p: P): (v: V) => void;
        fromPairs(): any;
        flattenBy<T>(root: T, field: string): T[];
    }

    interface LoDashStatic extends OwnMixins { }
    interface LoDashImplicitArrayWrapper<T> extends OwnMixins { }
    interface LoDashImplicitObjectWrapper<T> extends OwnMixins { }
}

_.mixin({
    append: (arr, el) => {
        let result = _.clone(arr);
        result.push(el);
        return result;
    },
    matchStrings: (search: string = "", target: string = "") =>
        !search || (target && target.toLocaleLowerCase().indexOf(search.toLowerCase()) !== -1),
    callAndGive: (f: Function, ...args) => {
        f.apply(undefined, args);
        return f;
    },
    cset: (obj, path) => (value) => _.set(obj, path, value),
    setRemove: (from, what) => {
        _.each(what, (field, k) => {
            if (from.hasOwnProperty(k)) {
                delete from[k];
            }
        });
    },
    overwriteData: (to, from) => {
        _.each(to, (field, k) => {
            if (to.hasOwnProperty(k) && !_.isFunction(to[k])) {
                delete to[k];
            }
        });
        _.merge(to, from);
    },
    overwrite: (to, from) => {
        _.each(to, (field, k) => {
            if (to.hasOwnProperty(k)) {
                delete to[k];
            }
        });
        _.merge(to, from);
    },
    spy: (obj) => {
        return obj;
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
    },
    flattenBy: (root: any, field: string = "lapset") => {
        const result = [];
        const pusher = (node) => {
            result.push(node);
            _.each(node[field], child => { pusher(child); });
        };
        pusher(root);
        return result;
    }
});
