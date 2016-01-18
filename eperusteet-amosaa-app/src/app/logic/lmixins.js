_.mixin({
    fromPairs: function (pairs) {
        var obj = {};
        _.each(pairs, function (pair) {
            if (pair) {
                obj[pair[0]] = pair[1];
            }
        });
        return obj;
    },
    print: function (array) {
        _.each(array, function (v, k) { return console.log(_.clone(k), _.clone(v)); });
        return array;
    }
});
//# sourceMappingURL=lmixins.js.map