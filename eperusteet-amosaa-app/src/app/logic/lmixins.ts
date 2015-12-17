declare module _ {
    interface OwnMixins {
        print<T>(array: T): T;
        print<T>(): T;
    }

    interface LoDashStatic extends OwnMixins { }
    interface LoDashImplicitArrayWrapper<T> extends OwnMixins { }
    interface LoDashImplicitObjectWrapper<T> extends OwnMixins { }
}

_.mixin({
    print: (array) => {
        _.each(array, (v, k) => console.log(_.clone(k), _.clone(v)));
        return array;
    }
});
